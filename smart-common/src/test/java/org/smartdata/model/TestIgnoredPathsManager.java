package org.smartdata.model;

import static org.smartdata.conf.SmartConfKeys.SMART_IGNORED_PATH_TEMPLATES_KEY;

import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestIgnoredPathsManager {

    @Parameterized.Parameter(0)
    public String ignoreTemplates;

    @Parameterized.Parameter(1)
    public String pathToCheck;

    @Parameterized.Parameter(2)
    public boolean expectedResult;

    @Parameterized.Parameters()
    public static Object[] parameters() {
        return new Object[][] {
            {"path/files/.*,path/another_dir/", "path/files/test_file.txt", true},
            {"path/another_dir/.*,test_file.txt", "path/files/test_file.txt", true},
        };
    }

    @Test
    public void testShouldIgnorePath() {
        Configuration configuration = new Configuration();
        configuration.set(SMART_IGNORED_PATH_TEMPLATES_KEY, ignoreTemplates);
        IgnoredPathsManager ignoredPathsManager = new IgnoredPathsManager(configuration);

        Assert.assertEquals(expectedResult, ignoredPathsManager.shouldIgnore(pathToCheck));
    }
}