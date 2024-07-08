package java.com.example;

import main.java.com.example.Family;
import main.java.com.example.VirusRNA;

import java.util.HashMap;
import java.util.Map;

/**
 * @class   VirusTest
 *
 * @brief   Class that contains the tests for the VirusRNA class.
 *
 * @details Executes various tests to verify the correct functioning of the
 *          VirusRNA class, including mutation behavior.
 */
class VirusTest {
    /** @brief  Map of virus families. */
    static Map<String, Family> families;
    /** @brief  Map of RNA viruses. */
    static Map<String, VirusRNA> VirusRNAMap;

    /**
     * @brief   Main method to run the VirusRNA tests.
     *
     * @pre     --
     * @post    Initializes virus families and RNA viruses, performs mutations,
     *          and prints the results.
     */
    public static void main(String[] args) {
        families = new HashMap<>();
        VirusRNAMap = new HashMap<>();

        Family family;
        family = new Family("Orthomyxoviridae", 20);
        families.put("Orthomyxoviridae", family);
        family = new Family("Coronaviridae", 25);
        families.put("Coronaviridae", family);
        family = new Family("Hepadnaviridae", 0);
        families.put("Hepadnaviridae", family);

        VirusRNA VirusRNA;
        VirusRNA = new VirusRNA("Flu", families.get("Orthomyxoviridae"),
                0.8, 2, 1, 5, 5, 30, 0.05, 0.3, 0.002, 0.007);
        VirusRNAMap.put("Flu", VirusRNA);
        VirusRNA = new VirusRNA("Covid19", families.get("Coronaviridae"),
                0.75, 3, 2, 7, 6, 50, 0.1, 0.4, 0.001, 0.005);
        VirusRNAMap.put("Covid19", VirusRNA);
        VirusRNA = new VirusRNA("SARS-CoV", families.get("Coronaviridae"),
                0.9, 1, 1, 5, 5, 40, 0.1, 0.3, 0.002, 0.007);
        VirusRNAMap.put("SARS-CoV", VirusRNA);

        main.java.com.example.VirusRNA newVirusRNA = VirusRNAMap.get("Flu").mutateDueToCopyError();
        System.out.println("Name: " + newVirusRNA.getName() + " main.java.com.example.Family: " +
                newVirusRNA.getFamily().getName() +
                " Disease Probability: " + newVirusRNA.getDiseaseProbability() +
                " Incubation Time: " + newVirusRNA.getIncubationTime() +
                " Latency Time: " + newVirusRNA.getLatencyTime() +
                " Infection Duration: " + newVirusRNA.getInfectionDuration() +
                " Immunity Duration: " + newVirusRNA.getImmunityDuration() +
                " Mortality Rate: " + newVirusRNA.getMortalityRate() +
                " Contagion Rate: " + newVirusRNA.getContagionRate() +
                " Mutation Probability Coincidence: " +
                newVirusRNA.getMutationProbabilityCoincidence() +
                " Mutation Probability Copy Error: " +
                newVirusRNA.getMutationProbabilityCopyError());

        System.out.println();

        VirusRNA newVirusRNA2 = VirusRNAMap.get("Covid19").mutateByCoincidence(VirusRNAMap.get(
                "SARS-CoV"));
        System.out.println("Name: " + newVirusRNA2.getName() + " main.java.com.example.Family: " +
                newVirusRNA2.getFamily().getName() +
                " Disease Probability: " + newVirusRNA2.getDiseaseProbability() +
                " Incubation Time: " + newVirusRNA2.getIncubationTime() +
                " Latency Time: " + newVirusRNA2.getLatencyTime() +
                " Infection Duration: " + newVirusRNA2.getInfectionDuration() +
                " Immunity Duration: " + newVirusRNA2.getImmunityDuration() +
                " Mortality Rate: " + newVirusRNA2.getMortalityRate() +
                " Contagion Rate: " + newVirusRNA2.getContagionRate() +
                " Mutation Probability Coincidence: " + newVirusRNA2.getMutationProbabilityCoincidence() +
                " Mutation Probability Copy Error: " + newVirusRNA2.getMutationProbabilityCopyError());
    }
}
