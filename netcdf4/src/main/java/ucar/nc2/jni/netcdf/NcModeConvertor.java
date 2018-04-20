/*
 * Copyright (c) 1998-2018 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.jni.netcdf;

import java.util.EnumSet;
import ucar.nc2.jni.netcdf.NcMode;
import static ucar.nc2.jni.netcdf.Nc4prototypes.*;

public final class NcModeConvertor {

  private NcModeConvertor(){
  }
  
  static public final int compute(int modeInit, EnumSet<NcMode> mode){
    int ret = modeInit;
    
    if (mode.contains(NcMode.NC_NOWRITE)) ret |= NC_NOWRITE;
    if (mode.contains(NcMode.NC_WRITE)) ret |= NC_WRITE;
    if (mode.contains(NcMode.NC_CLOBBER)) ret |= NC_CLOBBER;
    if (mode.contains(NcMode.NC_NOCLOBBER)) ret |= NC_NOCLOBBER;
    if (mode.contains(NcMode.NC_DISKLESS)) ret |= NC_DISKLESS;
    if (mode.contains(NcMode.NC_MMAP)) ret |= NC_MMAP;
    if (mode.contains(NcMode.NC_CLASSIC_MODEL)) ret |= NC_CLASSIC_MODEL;
    if (mode.contains(NcMode.NC_64BIT_OFFSET)) ret |= NC_64BIT_OFFSET;
    if (mode.contains(NcMode.NC_SHARE)) ret |= NC_SHARE;
    if (mode.contains(NcMode.NC_NETCDF4)) ret |= NC_NETCDF4;
    if (mode.contains(NcMode.NC_MPIIO)) ret |= NC_MPIIO;
    if (mode.contains(NcMode.NC_MPIPOSIX)) ret |= NC_MPIPOSIX;
    if (mode.contains(NcMode.NC_PNETCDF)) ret |= NC_PNETCDF;
    if (mode.contains(NcMode.NC_INMEMORY)) ret |= NC_INMEMORY;
    
    return ret;
  }
    
  static public final int compute(EnumSet<NcMode> mode){
    int ret =0x0000;
    compute(ret, mode);
    return ret;
  }
  
}