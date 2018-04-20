/*
 * Copyright (c) 1998-2018 University Corporation for Atmospheric Research/Unidata
 * See LICENSE for license information.
 */

package ucar.nc2.jni.netcdf;

import java.util.ArrayList;
import java.util.List;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class NcMemioStructure extends Structure {

  public SizeT size;

  public Pointer memory;

  public int flags;

  @Override
  protected List<String> getFieldOrder() {
    List<String> fields = new ArrayList<>();
    fields.add("size");
    fields.add("memory");
    fields.add("flags");
    return fields;
  }
}
