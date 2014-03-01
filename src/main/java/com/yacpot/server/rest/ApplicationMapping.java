package com.yacpot.server.rest;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationMapping {

  private String contextPath;

  private final List<MappingEntry> resourceMappings;

  public ApplicationMapping() {
    this.contextPath = StringUtils.EMPTY;
    this.resourceMappings = new ArrayList<>();
  }

  public void registerResource(@NotNull Object resource) {
    for (Method method : resource.getClass().getMethods()) {
      ResourceMapping resourceMapping = method.getAnnotation(ResourceMapping.class);
      if (resourceMapping == null) continue;

      resourceMappings.add(new MappingEntry(resourceMapping, resource, method));
    }
  }

  public ProcessingResult resolve(String path) throws MappingException {
    List<MappingSolution> solutions = findSolutionsFor(path);

    if (solutions.size() > 0) {
      return solutions.get(0).execute();
    }

    return ProcessingResult.OkResult;
  }

  private List<MappingSolution> findSolutionsFor(String path) {
    String pathToMatch = path;
    if (path.startsWith(contextPath)) {
      pathToMatch = StringUtils.substringAfter(path, contextPath);
    }

    List<MappingSolution> result = new ArrayList<>();
    for (MappingEntry mapping : resourceMappings) {
      Matcher matcher = mapping.getPattern().matcher(pathToMatch);
      if (!matcher.matches()) continue;

      result.add(new MappingSolution(mapping, pathToMatch, matcher.toMatchResult()));
    }
    return result;
  }

  public ApplicationMapping contextPath(String contextPath) {
    this.contextPath = contextPath;
    return this;
  }

  private final class MappingEntry {
    private final Object resourceInstance;
    private final Method method;
    private final Pattern pattern;

    private MappingEntry(@NotNull ResourceMapping resourceMapping, @NotNull Object resourceInstance, @NotNull Method method) {
      this.resourceInstance = resourceInstance;
      this.method = method;
      this.pattern = Pattern.compile(resourceMapping.pattern());
    }

    public Object getResourceInstance() {
      return resourceInstance;
    }

    public Method getMethod() {
      return method;
    }

    public Pattern getPattern() {
      return pattern;
    }
  }

  private final class MappingSolution {
    private MappingEntry mappingEntry;
    private String path;
    private MatchResult matchResult;

    private MappingSolution(MappingEntry mappingEntry, String path, MatchResult matchResult) {
      this.path = path;
      this.matchResult = matchResult;
      this.mappingEntry = mappingEntry;
    }

    public ProcessingResult execute() throws MappingException {
      Method method = mappingEntry.getMethod();
      // Check return type is ProcessingResult
      if (!method.getReturnType().isAssignableFrom(ProcessingResult.class)) {
        throw new MappingException("Return type of method must be ProcessingResult.");
      }

      // Check method signature
      Class<?>[] signature = method.getParameterTypes();
      int numberOfParamGroups = matchResult.groupCount();
      if (signature.length != numberOfParamGroups) {
        throw new MappingException(MessageFormat.format("Number of method parameters ({0}) is not equals the matched groups in the pattern ({1}).", signature.length, numberOfParamGroups));
      }
      boolean allStringParameters = true;
      for (Class<?> clazz : signature) {
        if (!clazz.equals(String.class)) {
          allStringParameters = false;
        }
      }
      if (!allStringParameters) {
        throw new MappingException("The type of all parameters must be String.");
      }

      // fill parameter array with matched groups
      // first match is group(1)
      Object[] parameters = new String[numberOfParamGroups];
      for (int i = 0; i < numberOfParamGroups; i++) {
        parameters[i] = matchResult.group(i + 1);
      }

      // Instantiate the class and call the method
      try {
        return (ProcessingResult) method.invoke(mappingEntry.getResourceInstance(), parameters);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new MappingException("Problem instantiating Resource object: " + e.getLocalizedMessage(), e);
      }
    }

  }
}
