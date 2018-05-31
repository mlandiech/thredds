/*
 * Copyright (c) 1998-2018 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.jni.netcdf;

import com.sun.jna.Memory;

import ucar.nc2.jni.netcdf.SizeT;
import ucar.nc2.jni.netcdf.NcMemioStructure;

/**
 * TODO
 *
 * @author 
 * @since 
 * @see 
 */
public class NcMemio {

  public NcMemioStructure struct = null;

  //////////////////////////////////////////////////
  // Constructor(s)

  /**
   * Constructor
   */
  public NcMemio() {
      this.struct = new NcMemioStructure();
      this.struct.size = null;
      this.struct.memory = null;
      this.struct.flags = 0;
  }

  /**
   * Constructor. Create new Memory buffer
   * 
   * @param size
   *      Memory buffer size
   */
  public NcMemio(final long size) {
    this.struct = new NcMemioStructure();
    this.struct.size = new SizeT(size);
    this.struct.memory = new Memory(this.struct.size.longValue());
    this.struct.flags = 0;
  }

  /**
   * Constructor. Create new Memory buffer from existing byte array
   * 
   * @param buffer
   *      Existing byte array
   */
  public NcMemio(byte[] buffer) {
    this.struct = new NcMemioStructure();
    this.struct.size = new SizeT(buffer.length);
    this.struct.memory = new Memory(this.struct.size.longValue());
    this.struct.memory.write(0,buffer,0,buffer.length);
    this.struct.flags = 0;
  }
  
  /**
   * Constructor. Create new Memory buffer from existing Memory Structure
   * 
   * @param struct
   *      Existing Memory Structure
   */
  public NcMemio(NcMemioStructure struct) {
    this.struct = struct;
  }
  
  /**
   * Return internal memory structure
   * 
   * @return The internal memory structure
   */
  public NcMemioStructure getStructure(){
    return struct;
  }
  
  /**
   * Set internal memory structure
   * 
   * @param The new internal memory structure
   */
  public void setStructure(NcMemioStructure struct){
    this.struct = struct;
  }
  
  /**
   * Return memory object
   * 
   * @return The memory buffer size
   */
  protected Memory getMemory() {
    Memory ret = null;
    if (this.struct != null){
      ret = (Memory)this.struct.memory;
    }
    return ret;
  }
  
  /**
   * Check Memory validity
   * 
   * @return If the memory is valid
   */
  public final boolean isValid(){
    boolean ret = true;
    if (this.getMemory() == null) {
      ret = false;
    }
    return ret;
  }
  
  /**
   * Return memory buffer size
   * 
   * @return The memory buffer size
   */
  public final long getSize() {
    long ret = 0;
    if (this.getMemory() != null) {
      ret = this.getMemory().size();
    }
    return ret;
  }
  

  /**
   * Return memory buffer size
   * 
   * @return The memory buffer size
   */
  public byte[] getBytes() {
    byte[] ret = null;
    if (this.struct != null && this.getMemory() != null && this.getSize() > 0) {
      ret = this.getMemory().getByteArray(0, (int)getSize());
    }
    return ret;
  }

}
