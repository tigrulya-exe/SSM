package org.smartdata.hdfs.action;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.tools.DistCp;
import org.apache.hadoop.tools.DistCpOptions;
import org.apache.hadoop.tools.OptionsParser;
import org.smartdata.action.annotation.ActionSignature;

@ActionSignature(
    actionId = "distCp",
    displayName = "DistCp",
    usage = DistCpAction.FILE_PATH + " $path "
        + DistCpAction.TARGET_ARG + " $target "
        + " [additional options from " +
        "https://hadoop.apache.org/docs/stable/hadoop-distcp/DistCp.html#Command_Line_Options]"
)
public class DistCpAction extends HdfsAction {

    public static final String SOURCE_PATHS_DELIMITER = ",";
    public static final String TARGET_ARG = "-target";

    public static final String SOURCE_PATH_LIST_FILE = "-f";

    public static final Set<String> NON_DISTCP_OPTIONS = Sets.newHashSet(FILE_PATH, TARGET_ARG);

    private DistCpOptions options;

    @Override
    public void init(Map<String, String> args) {
        super.init(args);
        validateRequiredOptions(args);

        List<String> rawArgs = new ArrayList<>();

        if (!args.containsKey(SOURCE_PATH_LIST_FILE)) {
            rawArgs.addAll(parseSourcePaths(args.get(FILE_PATH)));
        }
        rawArgs.add(args.get(TARGET_ARG));

        args.entrySet()
            .stream()
            .filter(entry -> !NON_DISTCP_OPTIONS.contains(entry.getKey()))
            .map(entry -> mapOptionToStr(entry.getKey(), entry.getValue()))
            .forEach(rawArgs::add);

        options = OptionsParser.parse(rawArgs.toArray(new String[0]));
    }

    @Override
    protected void execute() throws Exception {
        DistCp distCp = new DistCp(getContext().getConf(), options);
        try (Job copyJob = distCp.execute()) {
            distCp.waitForJobCompletion(copyJob);
        }
    }

    DistCpOptions getOptions() {
        return options;
    }

    private List<String> parseSourcePaths(String sourcePaths) {
        return Arrays.asList(sourcePaths.split(SOURCE_PATHS_DELIMITER));
    }

    private void validateRequiredOptions(Map<String, String> args) {
        if (!args.containsKey(FILE_PATH) && !args.containsKey(SOURCE_PATH_LIST_FILE)) {
            throw new IllegalArgumentException("Source paths not provided, please provide either "
                + FILE_PATH + " either " + SOURCE_PATH_LIST_FILE + " argument");
        }

        if (!args.containsKey(TARGET_ARG)) {
            throw new IllegalArgumentException("Required argument not present: " + TARGET_ARG);
        }
    }

    private String mapOptionToStr(String key, String value) {
        if (value.isEmpty()) {
            return key;
        }
        return key + " " + value;
    }
}
