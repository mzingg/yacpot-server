package com.yacpot.core.tests;

import com.yacpot.core.model.GenericModel;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ModelTestUtil {

  public static String toJoinedString(Collection<? extends GenericModel> orderedList) {
    List<String> result = new ArrayList<>();
    for (GenericModel model : orderedList) {
      result.add(model.getLabel());
    }
    return StringUtils.join(result, ",");
  }

}
