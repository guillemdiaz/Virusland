package java.com.example;

import main.java.com.example.Pair;
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.ArrayList;

/**
 * @class   PairTest.
 *
 * @brief   Class that contains the tests for the Pair class.
 *
 * @details Executes various tests to verify the correct functioning of the
 *          Pair class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PairTest
{
    /** @brief  Different instances of the Pair class. */
    private static List<Pair<Integer, Integer>> pairs;
    /** @brief  Counter for the number of tests passed. */
    private static int testsPassed = 0;
    /** @brief  Counter for the total number of tests. */
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
     * @brief   Executed before all tests to display the name of the class being tested.
     *
     * @pre     --
     * @post    The pair is correctly initialized.
     */
    @BeforeAll
    public static void initTests() {
        pairs = new ArrayList<>();
        pairs.add(new Pair<>(1, 2));
        pairs.add(new Pair<>(2, 3));
        pairs.add(new Pair<>(1, 2));
        pairs.add(new Pair<>(30, null));
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
     * @brief   Test for the equals() function of the Pair class.
     *
     * @pre     The pairs are correctly configured.
     * @post    The equals() function is verified to work correctly.
     */
    @Test
    @Order(1)
    @DisplayName("equals(o)")
    public void testEquals() {
        Assertions.assertEquals(pairs.get(0), pairs.get(0));
        Assertions.assertTrue(pairs.get(0).equals(pairs.get(2)) &&
                pairs.get(2).equals(pairs.get(0)));
        Assertions.assertNotEquals(pairs.get(0), pairs.get(1));
        Assertions.assertNotEquals(null, pairs.get(0));
        Assertions.assertNotEquals(pairs.get(0), new Pair<>(3, 5));
        testsPassed++;
    }

    /**
     * @brief   Test for the hashCode() function of the Pair class.
     *
     * @pre     The pairs are correctly configured.
     * @post    The hashCode() function is verified to work correctly.
     */
    @Test
    @Order(2)
    @DisplayName("hashCode()")
    public void testHashCode() {
        Assertions.assertEquals(pairs.get(0).hashCode(), pairs.get(2).hashCode());
        Assertions.assertNotEquals(pairs.get(0).hashCode(), pairs.get(1).hashCode());
        testsPassed++;
    }
}