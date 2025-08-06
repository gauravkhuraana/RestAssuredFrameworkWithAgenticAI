package com.api.automation.tests.suites;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Smoke Test Suite - Contains critical tests that must pass
 */
@Suite
@SelectPackages("com.api.automation.tests.smoke")
@IncludeTags("smoke")
public class SmokeTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
}
