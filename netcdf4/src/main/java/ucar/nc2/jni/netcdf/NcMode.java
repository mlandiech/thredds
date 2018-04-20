/*
 * Copyright (c) 1998-2018 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.jni.netcdf;

public enum NcMode {

  NC_NOWRITE,       /**< value : 0 */
  NC_WRITE,         /**< value : 1 */
  NC_CLOBBER,       /**< value : 0. Destroy existing file. Mode flag for nc_create(). */
  NC_NOCLOBBER,     /**< value : 0x0004. Don't destroy existing file. Mode flag for nc_create(). */
  NC_DISKLESS,      /**< value : 0x0008. Create a diskless file. Mode flag for nc_open() or nc_create(). */
  NC_MMAP,          /**< value : 0x0010. Use diskless file with mmap. Mode flag for nc_open() or nc_create(). */
  NC_CLASSIC_MODEL, /**< value : 0x0100. Enforce classic model. Mode flag for nc_create(). */
  NC_64BIT_OFFSET,  /**< value : 0x0200. Use large (64-bit) file offsets. Mode flag for nc_create(). */
  NC_SHARE,         /**< value : 0x0800. Share updates, limit caching. Use this in mode flags for both nc_create() and nc_open(). */
  NC_NETCDF4,       /**< value : 0x1000. Use netCDF-4/HDF5 format. Mode flag for nc_create(). */
  NC_MPIIO,         /**< value : 0x2000. Turn on MPI I/O. Use this in mode flags for both nc_create() and nc_open(). */
  NC_MPIPOSIX,      /**< value : 0x4000. Turn on MPI POSIX I/O. Use this in mode flags for both nc_create() and nc_open(). */
  NC_PNETCDF,       /**< value : NC_MPIIO. Use parallel-netcdf library. Mode flag for nc_open(). */
  NC_INMEMORY       /**< value : 0x8000. Read from memory. Mode flag for nc_open() or nc_create() => NC_DISKLESS */

}