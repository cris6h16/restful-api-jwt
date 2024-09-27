package Suites;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
        "org.cris6h16.Adapters.In.Rest.Facades",
        "org.cris6h16.Adapters.Out.SpringData",
        "org.cris6h16.Config.SpringBoot.Security.Filters",
})
public class TestSuitesA {
}
