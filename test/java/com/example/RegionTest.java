package java.com.example;

import main.java.com.example.Pair;
import main.java.com.example.Region;
import org.junit.jupiter.api.*;

/**
 * @class   RegionTest
 *
 * @brief   Class that contains the tests for the mRegion class.
 *
 * @details Executes various tests to verify the correct functioning of the
 *          Region class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegionTest {
    /** @brief Instance A of the Region class. */
    Region regionA = new Region("Region A", 1000000, 3);
    /** @brief Instance B of the Region class. */
    Region regionB = new Region("Region B", 500000, 0.5);
    /** @brief Instance C of the Region class. */
    Region regionC = new Region("Region C", 700000, 2);
    /** @brief Counter for the number of tests passed. */
    private static int testsPassed = 0;
    /** @brief Counter for the total number of tests. */
    private static int totalTests = 0;

    /**
     * @brief   Executed before each test to increment the test counter.
     *
     * @pre     --
     * @post    The test counter is incremented by 1.
     */
    @BeforeEach
    public void beforeEachTest() {
        totalTests++;
    }

    /**
     * @brief   Executed after each test to display the name of the executed test.
     *
     * @param   testInfo    Information about the test.
     *
     * @pre     --
     * @post    Information about the executed test is displayed.
     */
    @AfterEach
    public void afterEachTest(TestInfo testInfo) {
        String testName = "TEST " + testInfo.getDisplayName();
        String padding = " ".repeat(Math.max(0, 16 - testName.length()));
        System.out.println("<<<<<< " + testName + padding + "EXECUTED >>>>>>");
    }

    /**
     * @brief   Executed before all tests to display the name of the class
     *          being tested.
     *
     * @pre     --
     * @post    The region is correctly initialized.
     */
    @BeforeAll
    public static void initTests() {
        System.out.println("[TESTING CLASS " +
                Pair.class.getName().toUpperCase() + "]");
    }

    /**
     * @brief   Executed after all tests to display the number of tests passed.
     *
     * @pre     --
     * @post    The final test results are displayed.
     */
    @AfterAll
    public static void finalTests() {
        System.out.println("[TOTAL TESTS: " + totalTests + " | TESTS PASSED: "
                + testsPassed + "]");
        System.out.println(testsPassed == totalTests ? "[OK]" : "[FAILED]");
    }

    /**
     * @brief   Test for the functions name() and numInhabitants() of the
     *          Region class.
     *
     * @pre     The region is correctly configured.
     * @post    The functions name() and numInhabitants() are verified to
     *          work correctly.
     */
    @Test
    @Order(1)
    @DisplayName("name() and numInhabitants()")
    public void testNameAndInhabitants() {
        Assertions.assertEquals("Region A", regionA.getName());
        Assertions.assertEquals(1000000, regionA.getNumInhabitants());
        testsPassed++;
    }

    /**
     * @brief   Test for the functions neighboringRegions() and
     *          addNeighboringRegion() of the Region class.
     *
     * @pre     The region is correctly configured.
     * @post    The functions neighboringRegions() and addNeighboringRegion()
     *          are verified to work correctly.
     */
    @Test
    @Order(2)
    @DisplayName("neighboringRegions() and addNeighboringRegion()")
    public void testNeighboringRegions() {
        regionA.addNeighboringRegion(regionB, 30);
        regionA.addNeighboringRegion(regionC, 20);
        Assertions.assertTrue(regionA.getNeighboringRegions().containsKey(regionB));
        Assertions.assertTrue(regionA.getNeighboringRegions().containsKey(regionC));
        Assertions.assertEquals(30, (int) regionA.getNeighboringRegions().get(regionB));
        Assertions.assertEquals(20, (int) regionA.getNeighboringRegions().get(regionC));
        testsPassed++;
    }
}
