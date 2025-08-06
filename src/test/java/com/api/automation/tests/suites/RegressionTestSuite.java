package com.api.automation.tests.suites;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Regression Test Suite - Contains comprehensive tests for full functionality
 */
@Suite
@SelectPackages("com.api.automation.tests")
@IncludeTags("regression")
public class RegressionTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
