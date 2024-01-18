package org.smartdata.model;

import static org.smartdata.conf.SmartConfKeys.SMART_IGNORED_PATH_TEMPLATES_KEY;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;

public class IgnoredPathsManager {
    private static final String IGNORED_PATH_TEMPLATES_DELIMITER = ",";

    private final Matcher patternMatcher;

    public IgnoredPathsManager(Configuration configuration) {
        this(getIgnorePatterns(configuration));
    }

    public IgnoredPathsManager(List<String> ignoredPathPatterns) {
        StringJoiner patternBuilder = new StringJoiner("|", "(", ")");
        ignoredPathPatterns.forEach(patternBuilder::add);

        Pattern pattern = Pattern.compile(patternBuilder.toString());
        this.patternMatcher = pattern.matcher("");
    }

    public boolean shouldIgnore(String absolutePath) {
        return shouldIgnoreInternal(absolutePath)
            || shouldIgnoreInternal(FilenameUtils.getName(absolutePath));
    }

    private boolean shouldIgnoreInternal(String stringToMatch) {
        patternMatcher.reset(stringToMatch);
        return patternMatcher.matches();
    }

    private static List<String> getIgnorePatterns(Configuration configuration) {
        String rawIgnoreTemplates = configuration.get(SMART_IGNORED_PATH_TEMPLATES_KEY);
        return StringUtils.isNotBlank(rawIgnoreTemplates)
            ? Arrays.asList(rawIgnoreTemplates.split(IGNORED_PATH_TEMPLATES_DELIMITER))
            : Collections.emptyList();
    }
}
