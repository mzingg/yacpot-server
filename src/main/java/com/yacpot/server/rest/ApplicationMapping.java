package com.yacpot.server.rest;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationMapping<T extends Task> {

  private final List<MappingEntry> resourceMappings;

  public ApplicationMapping() {
    this.resourceMappings = new ArrayList<>();
  }

  public void registerResource(@NotNull Object resource) {
    for (Method method : resource.getClass().getMethods()) {
      ResourceMapping resourceMapping = method.getAnnotation(ResourceMapping.class);
      if (resourceMapping == null) continue;

      resourceMappings.add(new MappingEntry(resourceMapping, resource, method));
    }
  }

  public TaskResult resolve(@NotNull T task) throws MappingException {
    List<MappingSolution> solutions = findSolutionsFor(task);

    // TODO: How to handle multiple solutions?
    if (solutions.size() > 0) {
      return solutions.get(0).execute();
    }

    return TaskResult.NotFoundResult;
  }

  protected List<MappingSolution> findSolutionsFor(T task) {
    List<MappingSolution> result = new ArrayList<>();
    for (MappingEntry mapping : resourceMappings) {
      if (mapping.appliesTo(task)) continue;
      result.add(new MappingSolution(task, mapping));
    }
    return result;
  }

  protected final class MappingEntry {
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

    public boolean appliesTo(T task) {
      Operation[] operations = resourceMapping().supportsOperations();
      if (operations.length > 0 && !ArrayUtils.contains(operations, task.getOperation()))
        return true;

      Matcher matcher = pattern().matcher(task.getPath());
      if (!matcher.matches()) {
        return true;
      }

      // first match is group(1)
      MatchResult result = matcher.toMatchResult();
      for (int idx = 1; idx <= result.groupCount(); idx++) {
        task.addParameter(matcher.group(idx));
      }

      return false;
    }
  }

  protected final class MappingSolution {
    private final T task;
    private final MappingEntry mappingEntry;

    private MappingSolution(T task, MappingEntry mappingEntry) {
      this.task = task;
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

      if (signature.length != task.getPathParameterCount() + startIndex) {
        throw new MappingException(MessageFormat.format("Number of method parameters ({0}) is not equals the matched groups in the pattern ({1}).", signature.length, task.getPathParameterCount() + startIndex));
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
        parameters[0] = task;
      }
      // fill addParameter array with matched groups
      for (int i = startIndex; i < signature.length; i++) {
        parameters[i] = task.getPathParameter(i - startIndex);
      }

      // Instantiate the class and call the method
      try {
        return (TaskResult) method.invoke(mappingEntry.resourceInstance(), parameters);
      } catch (Exception e) {
        throw new MappingException("Error calling resource method: " + e.getLocalizedMessage(), e);
      }
    }

  }
}
