
import org.testng.annotations.Test;

public class MixSuite {
    // Test from Regression
    @Test
    public void mixRegressionTest() {
        System.out.println("🎯 Mix Test 1: [Regression] Login functionality");
    }

    // Tests from Nightly
    @Test
    public void mixNightlyTest1() {
        System.out.println("🎯 Mix Test 2: [Nightly] Database backup");
    }

    @Test
    public void mixNightlyTest2() {
        System.out.println("🎯 Mix Test 3: [Nightly] Report generation");
    }

    // Test from Sanity
    @Test
    public void mixSanityTest() {
        System.out.println("🎯 Mix Test 4: [Sanity] Home page loading");
    }
}