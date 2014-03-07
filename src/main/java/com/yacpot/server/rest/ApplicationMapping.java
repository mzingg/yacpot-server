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

  public TaskResult resolve(String path) throws MappingException {
    String pathToMatch = path;
    if (StringUtils.isNotBlank(contextPath) && !path.startsWith(contextPath)) {
      throw new MappingException("When contextPath is set all paths must start with this contextpath (" + contextPath + ") for resolving.");
    }
    if (StringUtils.isNotBlank(contextPath) && path.startsWith(contextPath)) {
      pathToMatch = StringUtils.substringAfter(path, contextPath);
    }

    List<MappingSolution> solutions = findSolutionsFor(pathToMatch);

    // TODO: How to handle multiple solutions?
    if (solutions.size() > 0) {
      return solutions.get(0).execute();
    }

    return TaskResult.OkResult;
  }

  private List<MappingSolution> findSolutionsFor(String path) {
    List<MappingSolution> result = new ArrayList<>();
    for (MappingEntry mapping : resourceMappings) {
      Matcher matcher = mapping.pattern().matcher(path);
      if (!matcher.matches()) continue;

      result.add(new MappingSolution(mapping, path, matcher.toMatchResult()));
    }
    return result;
  }

  public ApplicationMapping contextPath(String contextPath) {
    this.contextPath = contextPath;
    return this;
  }

  private final class MappingEntry {
    private final ResourceMapping resourceMapping;
    private final Object resourceInstance;
    private final Method method;
    private final Pattern pattern;

    private MappingEntry(@NotNull ResourceMapping resourceMapping, @NotNull Object resourceInstance, @NotNull Method method) {
      this.resourceMapping = resourceMapping;
      this.resourceInstance = resourceInstance;
      this.method = method;
      this.pattern = Pattern.compile(resourceMapping.pattern());
    }

    public ResourceMapping resourceMapping() {
      return resourceMapping;
    }

    public Object resourceInstance() {
      return resourceInstance;
    }

    public Method method() {
      return method;
    }

    public Pattern pattern() {
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

    public TaskResult execute() throws MappingException {
      Method method = mappingEntry.method();
      // Check return type is TaskResult
      if (!method.getReturnType().isAssignableFrom(TaskResult.class)) {
        throw new MappingException("Return type of method must be TaskResult.");
      }

      // Check method signature
      Class<?>[] signature = method.getParameterTypes();
      // Check if the first argument is of type Task and adjust flags accordingly
      int startIndex = 0;
      boolean requestsTaskParameter = false;
      if (signature.length > 0 && Task.class.isAssignableFrom(signature[0])) {
        requestsTaskParameter = true;
        startIndex = 1;
      }

      if (signature.length != matchResult.groupCount() + startIndex) {
        throw new MappingException(MessageFormat.format("Number of method parameters ({0}) is not equals the matched groups in the pattern ({1}).", signature.length,  matchResult.groupCount() + startIndex));
      }

      boolean allStringParameters = true;
      for (int idx = startIndex; idx < signature.length; idx++) {
        Class<?> clazz = signature[idx];
        if (!clazz.equals(String.class)) {
          allStringParameters = false;
        }
      }
      if (!allStringParameters) {
        throw new MappingException("The type of all parameters must be String.");
      }

      Object[] parameters = new Object[signature.length];
      if (requestsTaskParameter) {
        parameters[0] = new Task() {};
      }
      // fill parameter array with matched groups
      // first match is group(1)
      for (int i = startIndex; i < signature.length; i++) {
        parameters[i] = matchResult.group(i - startIndex + 1);
      }

      // Instantiate the class and call the method
      try {
        return (TaskResult) method.invoke(mappingEntry.resourceInstance(), parameters);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new MappingException("Problem instantiating Resource object: " + e.getLocalizedMessage(), e);
      }
    }

  }
}
