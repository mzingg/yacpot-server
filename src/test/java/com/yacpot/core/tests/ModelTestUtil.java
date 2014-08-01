package com.yacpot.core.tests;

import com.yacpot.core.model.GenericModel;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class ModelTestUtil {

  public static String toJoinedString(Collection<? extends GenericModel> orderedList) {
    List<String> result = orderedList.stream().map(GenericModel::getLabel).collect(Collectors.toList());
    return StringUtils.join(result, ",");
  }

}
