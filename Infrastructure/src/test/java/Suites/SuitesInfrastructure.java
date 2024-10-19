package Suites;

import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

public class SuitesInfrastructure {


    // around 10 seconds
    @Suite
    @SelectPackages("org.cris6h16")
    @ExcludeTags({"with-spring-context"})
    public static class NoContextTests {

    }

    // around 20 seconds
    @Suite
    @SelectPackages("org.cris6h16")
    public static class AllTests {

    }

}
