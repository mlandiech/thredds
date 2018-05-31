package ucar.nc2.jni.netcdf;

import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.ma2.MAMath;
import ucar.nc2.*;
import ucar.nc2.NCdumpW.WantValues;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.util.CompareNetcdf2;
import ucar.nc2.util.IO;
import ucar.nc2.write.Nc4ChunkingStrategyNone;
import ucar.unidata.util.test.TestDir;
import ucar.unidata.util.test.UnitTestCommon;
import ucar.unidata.util.test.category.NeedsCdmUnitTest;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Test copying files to netcdf4 with FileWriter2.
 * Compare original.
 *
 * @author caron
 * @since 7/27/12
 */
public class TestNc4IospInMemoryDiskless {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

    int countNotOK = 0;

    @Before
    public void setLibrary() {
        // Ignore this class's tests if NetCDF-4 isn't present.
        // We're using @Before because it shows these tests as being ignored.
        // @BeforeClass shows them as *non-existent*, which is not what we want.
        Assume.assumeTrue("NetCDF-4 C library not present.", Nc4Iosp.isClibraryPresent());
    }


    /////////////////////////////////////////////////

    public static void createTestFile(String fname) {

        byte[] buffer = null;
        try ( NetcdfFileWriter ncFileWriter = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf4, fname)) {
            // Create shared, unlimited Dimension
            ncFileWriter.addDimension(null, "x", 5);

            // Create a float Variable
            Variable arr = ncFileWriter.addVariable(null, "arr", DataType.FLOAT, "x");

            // Create file and exit redefine
            ncFileWriter.create();

            // Create an array of data and subset
            float[] data = new float[]{1.f, 2.f, 3.f, 4.f, 5.f, 6.f, 7.f, 8.f, 9.f, 10.f};
            Array arrData = Array.factory(DataType.FLOAT, new int[]{10}, data);
            Array subArr = arrData.sectionNoReduce(new int[]{1}, new int[]{5}, new int[]{2});

            // Write data to file
            ncFileWriter.write(arr, subArr);
            
            ncFileWriter.close();
            
        } catch ( InvalidRangeException | IOException e ) {
            Assert.fail(e.getMessage());
        }
    }
    @Test
    public void openInMemoryRead() throws IOException, InvalidRangeException {
        
        //////////////////////
        //  Create test file
        String fname = tempFolder.newFile().getAbsolutePath();
        createTestFile(fname);
        byte[] buffer = null;
        
        //////////////////////
        //  Stream test file
        File file = new File(fname);
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        try (InputStream in = new BufferedInputStream(new FileInputStream(fname))) {
            IO.copy(in, bos);
        }
        
        //////////////////////
        //  Open test file with in memory mode
        try (NetcdfFile ncFile = NetcdfFile.openInMemory("InMemReadTest.nc", bos.toByteArray(), "ucar.nc2.jni.netcdf.Nc4Iosp")) {
            Variable arr = ncFile.findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            Array arrData = arr.read();
            float[] data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{2.f, 4.f, 6.f, 8.f, 10.f},
                    data, 1e-6f);
            
            ncFile.close();
            buffer = ncFile.getInMemoryBuffer();
            
        } catch ( ClassNotFoundException | IllegalAccessException | InstantiationException e ) {
            Assert.fail(e.getMessage());
        }
        
        //////////////////////
        //  Open out buffer memory mode
        try (NetcdfFile ncFile = NetcdfFile.openInMemory("InMemReadTest.nc", buffer, "ucar.nc2.jni.netcdf.Nc4Iosp")) {
            Variable arr = ncFile.findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            Array arrData = arr.read();
            float[] data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{2.f, 4.f, 6.f, 8.f, 10.f},
                    data, 1e-6f);
            
            ncFile.close();
            buffer = ncFile.getInMemoryBuffer();
            
        } catch ( ClassNotFoundException | IllegalAccessException | InstantiationException e ) {
            Assert.fail(e.getMessage());
        }
        
        //////////////////////
        //  Make sure file has what we expect
        try (NetcdfFile ncFile = NetcdfFile.open(fname)) {
            Variable arr = ncFile.findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            Array arrData = arr.read();
            float[] data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{2.f, 4.f, 6.f, 8.f, 10.f},
                    data, 1e-6f);
        }
    }
    
    @Test
    public void openInMemoryWrite() throws IOException, InvalidRangeException {
        
        //////////////////////
        //  Create test file
        String fname = tempFolder.newFile().getAbsolutePath();
        createTestFile(fname);
        byte[] buffer = null;
        
        //////////////////////
        //  Open test file with in memory mode for writing
        try (NetcdfFileWriter ncFileWriter = NetcdfFileWriter.openExistingInMemory(fname)) {
            
            Variable arr = ncFileWriter.getNetcdfFile().findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            Array arrData = arr.read();
            float[] data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{2.f, 4.f, 6.f, 8.f, 10.f},
                    data, 1e-6f);
            
            // Create an array of data and subset
            data = new float[]{11.f, 12.f, 13.f, 14.f, 15.f, 16.f, 17.f, 18.f, 19.f, 20.f};
            arrData = Array.factory(DataType.FLOAT, new int[]{10}, data);
            Array subArr = arrData.sectionNoReduce(new int[]{1}, new int[]{5}, new int[]{2});

            // Write data to file
            ncFileWriter.write(arr, subArr);

            arr = ncFileWriter.getNetcdfFile().findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            arrData = arr.read();
            data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{12.f, 14.f, 16.f, 18.f, 20.f},
                    data, 1e-6f);
            
            ncFileWriter.close();
            buffer = ncFileWriter.getNetcdfFile().getInMemoryBuffer();
            
        } 
        
        //////////////////////
        //  Open out buffer with in memory mode for reading
        try (NetcdfFile ncFile = NetcdfFile.openInMemory("InMemReadTest.nc", buffer, "ucar.nc2.jni.netcdf.Nc4Iosp")) {
            Variable arr = ncFile.findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            Array arrData = arr.read();
            float[] data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{12.f, 14.f, 16.f, 18.f, 20.f},
                    data, 1e-6f);
            
            ncFile.close();
            buffer = ncFile.getInMemoryBuffer();
            
        } catch ( ClassNotFoundException | IllegalAccessException | InstantiationException e ) {
            Assert.fail(e.getMessage());
        }
        
        //////////////////////
        //  Make sure file has what we expect
        try (NetcdfFile ncFile = NetcdfFile.open(fname)) {
            Variable arr = ncFile.findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            Array arrData = arr.read();
            float[] data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{2.f, 4.f, 6.f, 8.f, 10.f},
                    data, 1e-6f);
        }
    }
    
    @Test
    public void openDisklessRead() throws IOException, InvalidRangeException {
        
        //////////////////////
        //  Create test file
        String fname = tempFolder.newFile().getAbsolutePath();
        createTestFile(fname);
        byte[] buffer = null;
        
        //////////////////////
        //  Open test file with diskless mode
        try (NetcdfFile ncFile = NetcdfFile.openDiskless(fname, "ucar.nc2.jni.netcdf.Nc4Iosp")) {
            Variable arr = ncFile.findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            Array arrData = arr.read();
            float[] data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{2.f, 4.f, 6.f, 8.f, 10.f},
                    data, 1e-6f);
            
        } catch ( ClassNotFoundException | IllegalAccessException | InstantiationException e ) {
            Assert.fail(e.getMessage());
        }
        
        //////////////////////
        //  Make sure file has what we expect
        try (NetcdfFile ncFile = NetcdfFile.open(fname)) {
            Variable arr = ncFile.findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            Array arrData = arr.read();
            float[] data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{2.f, 4.f, 6.f, 8.f, 10.f},
                    data, 1e-6f);
        }
    }
    
    @Test
    public void createInMemory() throws IOException, InvalidRangeException {
        
        byte[] buffer = null;
        String fname = tempFolder.newFile().getAbsolutePath();
        
        //////////////////////
        //  Create new file with in memory mode
        try ( NetcdfFileWriter ncFileWriter = NetcdfFileWriter.createNewInMemory(NetcdfFileWriter.Version.netcdf4, fname,10000)) {
            // Create shared, unlimited Dimension
            ncFileWriter.addDimension(null, "x", 5);

            // Create a float Variable
            Variable arr = ncFileWriter.addVariable(null, "arr", DataType.FLOAT, "x");

            // Create file and exit redefine
            ncFileWriter.create();

            // Create an array of data and subset
            float[] data = new float[]{1.f, 2.f, 3.f, 4.f, 5.f, 6.f, 7.f, 8.f, 9.f, 10.f};
            Array arrData = Array.factory(DataType.FLOAT, new int[]{10}, data);
            Array subArr = arrData.sectionNoReduce(new int[]{1}, new int[]{5}, new int[]{2});

            // Write data to file
            ncFileWriter.write(arr, subArr);
            
            ncFileWriter.close();
            buffer = ncFileWriter.getNetcdfFile().getInMemoryBuffer();
            
        } catch ( InvalidRangeException | IOException e ) {
            Assert.fail(e.getMessage());
        }
        
        //////////////////////
        //  Open out buffer with in memory mode for writing (modify value)
        try (NetcdfFileWriter ncFileWriter = NetcdfFileWriter.openExistingInMemory("WriteTest.nc",buffer)) {
            
            Variable arr = ncFileWriter.getNetcdfFile().findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            Array arrData = arr.read();
            float[] data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{2.f, 4.f, 6.f, 8.f, 10.f},
                    data, 1e-6f);
            
            // Create an array of data and subset
            data = new float[]{11.f, 12.f, 13.f, 14.f, 15.f, 16.f, 17.f, 18.f, 19.f, 20.f};
            arrData = Array.factory(DataType.FLOAT, new int[]{10}, data);
            Array subArr = arrData.sectionNoReduce(new int[]{1}, new int[]{5}, new int[]{2});

            // Write data to file
            ncFileWriter.write(arr, subArr);

            arr = ncFileWriter.getNetcdfFile().findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            arrData = arr.read();
            data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{12.f, 14.f, 16.f, 18.f, 20.f},
                    data, 1e-6f);
            
            ncFileWriter.close();
            ncFileWriter.getNetcdfFile().close();
            buffer = ncFileWriter.getNetcdfFile().getInMemoryBuffer();
            
        } 
        
        //////////////////////
        //  Open out buffer with in memory mode for reading
        try (NetcdfFile ncFile = NetcdfFile.openInMemory("InMemReadTest.nc", buffer, "ucar.nc2.jni.netcdf.Nc4Iosp")) {
            Variable arr = ncFile.findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            Array arrData = arr.read();
            float[] data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{12.f, 14.f, 16.f, 18.f, 20.f},
                    data, 1e-6f);
            
            ncFile.close();
            buffer = ncFile.getInMemoryBuffer();
            
        } catch ( ClassNotFoundException | IllegalAccessException | InstantiationException e ) {
            Assert.fail(e.getMessage());
        }
        
        //////////////////////
        //  Write buffer in file
        try (FileOutputStream fos = new FileOutputStream(fname)) {
            fos.write(buffer);
            fos.close();
        }
        
        //////////////////////
        //  Make sure file has what we expect
        try (NetcdfFile ncFile = NetcdfFile.open(fname)) {
            Variable arr = ncFile.findVariable(null, "arr");
            Assert.assertEquals(5, arr.getSize());
            Array arrData = arr.read();
            float[] data = (float[])arrData.get1DJavaArray(Float.class);
            Assert.assertEquals(5, data.length);
            Assert.assertArrayEquals(new float[]{12.f, 14.f, 16.f, 18.f, 20.f},
                    data, 1e-6f);
        }
    }
}
