package Suites;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
        "org.cris6h16.Adapters.In.Rest.Facades",
        "org.cris6h16.Adapters.In.Rest",
        "org.cris6h16.Adapters.Out.SpringData",
        "org.cris6h16.Adapters.Out.SpringData.Entities",
        "org.cris6h16.Config.SpringBoot.Controllers",
        "org.cris6h16.Config.SpringBoot.Redis",
        "org.cris6h16.Config.SpringBoot.Security.Filters",
        "org.cris6h16.Config.SpringBoot.Security.UserDetails",
        "org.cris6h16.Config.SpringBoot.Security",
        "org.cris6h16.Config.SpringBoot.Services",
        "org.cris6h16.Config.SpringBoot.Services.Email",
        "org.cris6h16.Config.SpringBoot.Utils",
})
public class TestSuitesA {
}
