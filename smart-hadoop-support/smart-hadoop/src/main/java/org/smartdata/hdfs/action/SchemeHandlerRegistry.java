package org.smartdata.hdfs.action;

import org.smartdata.utils.PathUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static org.smartdata.utils.PathUtil.getScheme;
import static org.smartdata.utils.PathUtil.isAbsoluteRemotePath;

public class SchemeHandlerRegistry {
  private final Map<Predicate<String>, ThrowingRunnable> schemeHandlers;
  private ThrowingRunnable defaultHandler;

  public SchemeHandlerRegistry() {
    this.schemeHandlers = new LinkedHashMap<>();
  }

  public SchemeHandlerRegistry onLocalPath(ThrowingRunnable handler) {
    schemeHandlers.put(path -> !isAbsoluteRemotePath(path), handler);
    return this;
  }

  public SchemeHandlerRegistry onRemotePath(ThrowingRunnable handler) {
    schemeHandlers.put(PathUtil::isAbsoluteRemotePath, handler);
    return this;
  }

  public SchemeHandlerRegistry onSchemes(ThrowingRunnable handler, String... schemes) {
    schemeHandlers.put(SchemePredicate.forSchemes(schemes), handler);
    return this;
  }

  public SchemeHandlerRegistry onSchemesExcluding(ThrowingRunnable handler, String... excludedSchemes) {
    schemeHandlers.put(SchemePredicate.forSchemesExcluding(excludedSchemes), handler);
    return this;
  }

  public SchemeHandlerRegistry defaultHandler(ThrowingRunnable handler) {
    this.defaultHandler = handler;
    return this;
  }

  public void executeForPath(String path) throws Exception {
    for (Map.Entry<Predicate<String>, ThrowingRunnable> entry : schemeHandlers.entrySet()) {
      if (entry.getKey().test(path)) {
        entry.getValue().run();
        break;
      }
    }

    if (defaultHandler == null) {
      throw new IllegalArgumentException("Path is not supported: " + path);
    }
    defaultHandler.run();
  }

  private static class SchemePredicate implements Predicate<String> {

    private final Set<String> schemes;
    private final boolean shouldBeOneOfSchemes;

    private SchemePredicate(boolean shouldBeOneOfSchemes, String... schemes) {
      this.shouldBeOneOfSchemes = shouldBeOneOfSchemes;
      this.schemes = new HashSet<>(Arrays.asList(schemes));
    }

    @Override
    public boolean test(String path) {
      return getScheme(path)
          .filter(scheme -> schemes.contains(scheme) == shouldBeOneOfSchemes)
          .isPresent();
    }

    private static SchemePredicate forSchemes(String... schemes) {
      return new SchemePredicate(true, schemes);
    }

    private static SchemePredicate forSchemesExcluding(String... schemes) {
      return new SchemePredicate(false, schemes);
    }
  }

  public interface ThrowingRunnable {
    void run() throws Exception;
  }
}