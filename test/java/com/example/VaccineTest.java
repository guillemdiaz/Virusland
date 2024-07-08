package java.com.example;

import main.java.com.example.*;
import org.junit.jupiter.api.*;

/**
 * @class   VaccineTest
 * @brief   Class that contains the tests for the Vaccine class.
 *
 * @details Executes various tests to verify the correct functioning of the
 *          Vaccine class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VaccineTest {
    /** @brief  Instance of the Vaccine class of the inhibitor type. */
    private static Vaccine inhibitorVaccine;
    /** @brief  Instance of the Vaccine class of the attenuator type. */
    private static Vaccine attenuatorVaccine;
    /** @brief  Counter for the number of tests passed. */
    private static int testsPassed = 0;
    /** @brief  Counter for the number of tests performed. */
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
        String padding = " ".repeat(Math.max(0, 36 - testName.length()));
        System.out.println("<<<<<< " + testName + padding + "EXECUTED >>>>>>");
    }

    /**
     * @brief   Executed before all tests to display the name of the class
     *          being tested.
     *
     * @pre     --
     * @post    The inhibitor and attenuator vaccines are correctly initialized.
     */
    @BeforeAll
    public static void initTests() {
        Family familyH = new Family("Hepadnaviridae", 0);
        Virus virusH = new VirusDNA("HepatitisB", familyH, 0.5, 6, 3, 2000,
                2000, 0, 0.4, 0.1);
        inhibitorVaccine = new Vaccine("VC_HepaB_T1", "inhibitor",
                virusH, 95, 4, 150);
        Family familyG = new Family("Orthomyxoviridae", 20);
        Virus virusG = new VirusRNA("Flu", familyG, 0.8, 2, 1, 5, 5, 30, 0.05,
                0.3, 0.002, 0.007);
        attenuatorVaccine = new Vaccine("VCFluA1", "attenuator",
                virusG, 5, 100, 70, 30, 40, 50);
        System.out.println("[TESTING CLASS " + Vaccine.class.getName() + "]");
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
     * @brief   Test for the name() function of the Vaccine class.
     *
     * @pre     The inhibitor and attenuator vaccines are correctly configured.
     * @post    The name() function is verified to return the vaccine name correctly.
     */
    @Test
    @Order(1)
    @DisplayName("name()")
    public void testName() {
        Assertions.assertEquals("VC_HepaB_T1", inhibitorVaccine.getName());
        Assertions.assertEquals("VCFluA1", attenuatorVaccine.getName());
        testsPassed++;
    }

    /**
     * @brief   Test for the type() function of the Vaccine class.
     *
     * @pre     The inhibitor and attenuator vaccines are correctly configured.
     * @post    The type() function is verified to return the vaccine type correctly.
     */
    @Test
    @Order(2)
    @DisplayName("type()")
    public void testType() {
        Assertions.assertEquals("inhibitor", inhibitorVaccine.getType());
        Assertions.assertEquals("attenuator", attenuatorVaccine.getType());
        Assertions.assertNotEquals(inhibitorVaccine.getType(), attenuatorVaccine.getType());
        testsPassed++;
    }

    /**
     * @brief   Test for the effectiveness() function of the Vaccine class.
     *
     * @pre     The inhibitor and attenuator vaccines are correctly configured.
     * @post    The effectiveness() function is verified to return the vaccine
     *          effectiveness correctly.
     */
    @Test
    @Order(3)
    @DisplayName("effectiveness()")
    public void testEffectiveness() {
        Assertions.assertEquals(95, inhibitorVaccine.getEffectiveness());
        Assertions.assertNull(attenuatorVaccine.getEffectiveness());
        int effectiveness = inhibitorVaccine.getEffectiveness();
        Assertions.assertTrue(effectiveness >= 0 && effectiveness <= 100);
        testsPassed++;
    }

    /**
     * @brief   Test for the activationTime() function of the Vaccine class.
     *
     * @pre     The inhibitor and attenuator vaccines are correctly configured.
     * @post    The activationTime() function is verified to return the activation
     *          time of the vaccine correctly.
     */
    @Test
    @Order(4)
    @DisplayName("activationTime()")
    public void testActivationTime() {
        Assertions.assertEquals(4, inhibitorVaccine.getActivationTime());
        Assertions.assertEquals(5, attenuatorVaccine.getActivationTime());
        testsPassed++;
    }

    /**
     * @brief   Test for the duration() function of the Vaccine class.
     *
     * @pre     The inhibitor and attenuator vaccines are correctly configured.
     * @post    The duration() function is verified to return the duration of the
     *          vaccine effect correctly.
     */
    @Test
    @Order(5)
    @DisplayName("duration()")
    public void testDuration() {
        Assertions.assertEquals(150, inhibitorVaccine.getDuration());
        Assertions.assertEquals(100, attenuatorVaccine.getDuration());
        testsPassed++;
    }

    /**
     * @brief   Test for the targetVirus() function of the Vaccine class.
     *
     * @pre     The inhibitor and attenuator vaccines are correctly configured.
     * @post    The targetVirus() function is verified to return the target virus
     *          of the vaccine correctly.
     */
    @Test
    @Order(6)
    @DisplayName("targetVirus()")
    public void testTargetVirus() {
        Family expectedFamilyH = new Family("Hepadnaviridae", 0);
        Virus expectedVirusH = new VirusDNA("HepatitisB", expectedFamilyH,
                0.5, 6, 3, 2000, 2000, 0, 0.4, 0.1);
        Family expectedFamilyG = new Family("Orthomyxoviridae", 20);
        Virus expectedVirusG = new VirusRNA("Flu", expectedFamilyG, 0.8, 2,
                1, 5, 5, 30, 0.05, 0.3, 0.002, 0.007);
        Assertions.assertEquals(expectedVirusH,
                inhibitorVaccine.getTargetVirus());
        Assertions.assertEquals(expectedVirusG,
                attenuatorVaccine.getTargetVirus());
        testsPassed++;
    }

    /**
     * @brief   Test for the mortalityRateReduction() function of the
     *          Vaccine class.
     *
     * @pre     The inhibitor and attenuator vaccines are correctly configured.
     * @post    The mortalityRateReduction() function is verified to return the
     *          percentage reduction in mortality rate correctly.
     */
    @Test
    @Order(7)
    @DisplayName("mortalityRateReduction()")
    public void testMortalityRateReduction() {
        Assertions.assertEquals(70,
                attenuatorVaccine.getMortalityRateReduction());
        Assertions.assertNull(inhibitorVaccine.getMortalityRateReduction());
        testsPassed++;
    }

    /**
     * @brief   Test for the illnessDurationReduction() function of the
     *          Vaccine class.
     *
     * @pre     The inhibitor and attenuator vaccines are correctly configured.
     * @post    The illnessDurationReduction() function is verified to return the
     *          percentage reduction in illness duration correctly.
     */
    @Test
    @Order(8)
    @DisplayName("illnessDurationReduction()")
    public void testIllnessDurationReduction() {
        Assertions.assertEquals(30,
                attenuatorVaccine.getDiseaseDurationReduction());
        Assertions.assertNull(inhibitorVaccine.getDiseaseDurationReduction());
        testsPassed++;
    }

    /**
     * @brief   Test for the illnessProbabilityReduction() function of the
     *          Vaccine class.
     *
     * @pre     The inhibitor and attenuator vaccines are correctly configured.
     * @post    The illnessProbabilityReduction() function is verified to return
     *          the percentage reduction in illness probability correctly.
     */
    @Test
    @Order(9)
    @DisplayName("illnessProbabilityReduction()")
    public void testIllnessProbabilityReduction() {
        Assertions.assertEquals(40, attenuatorVaccine.getDiseaseProbabilityReduction());
        Assertions.assertNull(inhibitorVaccine.getDiseaseProbabilityReduction());
        testsPassed++;
    }

    /**
     * @brief   Test for the contagionRateReduction() function of the
     *          Vaccine class.
     *
     * @pre     The inhibitor and attenuator vaccines are correctly configured.
     * @post    The contagionRateReduction() function is verified to return the
     *          percentage reduction in contagion rate correctly.
     */
    @Test
    @Order(10)
    @DisplayName("contagionRateReduction()")
    public void testContagionRateReduction() {
        Assertions.assertEquals(50,
                attenuatorVaccine.getContagionRateReduction());
        Assertions.assertNull(inhibitorVaccine.getContagionRateReduction());
        testsPassed++;
    }
}
