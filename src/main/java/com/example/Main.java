package main.java.com.example;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        Simulator s = new Simulator();

        System.out.println("Enter the folder name containing the files:");
        String folderName = sc.nextLine();
        String dir = "res" + File.separator + folderName;  // Set the directory to the selected folder

        System.out.println("Processing files in folder: " + dir);
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();

        String simulationFileName = "";
        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String filePath = file.getAbsolutePath();
                if (file.getName().startsWith("ini")) {
                    simulationFileName = filePath;
                } else {
                    Simulator.chooseFile(filePath);
                }
            }
        }

        s.initializeSimulation(simulationFileName);

        // Initialize GraphVisualizer
        GraphVisualizer graphVisualizer = new GraphVisualizer(s.getRegions());
        graphVisualizer.display();

        showOptions();
        System.out.print("Option: ");
        String option = sc.next();
        while (!option.equals("0")) {
            switch (option) {
                case "1":
                    Vaccine v = chooseVaccine(s.getVaccines(), sc);
                    List<Pair<Region, Integer>> regionList = chooseRegionsForVaccine(s.getRegions(), sc);
                    for (Pair<Region, Integer> r : regionList) {
                        s.vaccinateRegion(r.first, v, r.second);
                    }
                    break;
                case "2":
                    Pair<Region, Region> regionsPair = chooseRegionsToCloseFlow(s.getRegions(), sc, true);
                    s.applyClosure(regionsPair.first, regionsPair.second);
                    graphVisualizer.addEdge(regionsPair.first.getName(), regionsPair.second.getName());
                    break;
                case "3":
                    Pair<Region, Region> regionPair = chooseRegionsToCloseFlow(s.getRegions(), sc, false);
                    s.openClosure(regionPair.first, regionPair.second);
                    graphVisualizer.removeEdge(regionPair.first.getName(), regionPair.second.getName());
                    break;
                case "4":
                    Pair<Region, Pair<Integer, Integer>> confinementData = chooseRegionToConfine(s.getRegions(), sc);
                    Region rConf = confinementData.first;
                    Integer time = confinementData.second.first;
                    Integer mobilityReduction = confinementData.second.second;
                    Confinement c = new Confinement(time, mobilityReduction);
                    s.confine(rConf, c);
                    break;
                case "5":
                    Region rDesc = chooseRegionToRelease(s.getRegions(), sc);
                    s.release(rDesc);
                    break;
                case "6":
                    s.simulate();
                    break;
                case "7":
                    System.out.print("Number of steps to simulate: ");
                    int steps = sc.nextInt();
                    for (int i = 0; i < steps; i++) {
                        s.simulate();
                    }
                    break;
                case "8":
                    showCurrentInformation(s);
                    break;
                case "9":
                    showTotalInformation(s);
                    break;
                case "m":
                    showOptions();
                    break;
                default:
                    System.out.println("Incorrect Option.");
            }
            graphVisualizer.updateGraph(s);
            System.out.print("Option: ");
            option = sc.next();
        }
    }

    /**
     * @brief   Displays the options that can be chosen.
     *
     * @pre     --
     * @post    Prints the available options to the console.
     */
    public static void showOptions() {
        System.out.println("\n");
        System.out.println("********************* OPTIONS ************************");
        System.out.println("* 1. Introduce vaccine to regions                    *");
        System.out.println("* 2. Close flow of people between two regions        *");
        System.out.println("* 3. Open flow of people between two regions         *");
        System.out.println("* 4. Confine a region                                *");
        System.out.println("* 5. Release a region                                *");
        System.out.println("* 6. Simulate 1 step                                 *");
        System.out.println("* 7. Simulate n steps                                *");
        System.out.println("* 8. Show current step information                   *");
        System.out.println("* 9. Show total information                          *");
        System.out.println("* m. Show Options                                    *");
        System.out.println("*                                                    *");
        System.out.println("* 0. End                                             *");
        System.out.println("******************************************************");
    }

    /**@param   vaccines    Map of all vaccines in the simulator.
     * @param   sc          Scanner.
     *
     * @return  The selected vaccine from the available options.
     */
    public static Vaccine chooseVaccine(Map<String, Vaccine> vaccines, Scanner sc) {
        Vector<String> vaccinesVector = new Vector<>();
        int i = 1;
        for (String vaccineString : vaccines.keySet()) {
            System.out.println("[" + i + "] - " + vaccineString);
            vaccinesVector.add(vaccineString);
            i++;
        }
        System.out.print("Number of the vaccine: ");
        int vaccineInteger = sc.nextInt();
        return vaccines.get(vaccinesVector.elementAt(vaccineInteger - 1));
    }

    /**
     * @brief   Choose multiple regions to apply a vaccine
     *
     * @param   regions Map of all regions in the simulator
     * @param   sc      Scanner
     *
     * @return  List of the selected regions with the percentage of population
     *          to vaccinate
     *
     * @pre     regions map is not null and sc is a valid Scanner object.
     * @post    Returns a list of regions and their respective vaccination percentages.
     */
    public static List<Pair<Region, Integer>> chooseRegionsForVaccine(Map<String, Region> regions, Scanner sc) {
        List<Pair<Region, Integer>> regionsList = new ArrayList<>();
        Vector<String> regionsVector = new Vector<>();
        int i = 1;
        for (String regionString : regions.keySet()) {
            System.out.println("[" + i + "] - " + regionString);
            regionsVector.add(regionString);
            i++;
        }
        System.out.println("Choose the region number to apply the vaccine and " +
                "the percentage of the affected population (* to finish):");
        System.out.print("Region: ");
        while (sc.hasNext()) {
            if (sc.hasNextInt()) {
                int regionInteger = sc.nextInt();
                System.out.print("Percentage of population: ");
                Integer p_population = sc.nextInt();
                Pair<Region, Integer> regionPair =
                        new Pair<>(regions.get(regionsVector.elementAt(regionInteger - 1)), p_population);
                regionsList.add(regionPair);
                System.out.print("Region: ");
            } else {
                String input = sc.next();
                if (input.equals("*")) {
                    break;
                } else {
                    System.out.println("Invalid input. Enter a region number " +
                            "or '*' to finish.");
                }
            }
        }
        return regionsList;
    }

    /**
     * @param   regions Map of all regions in the simulator.
     * @param   sc      Scanner.
     * @param   close   True if you want to close the flow, False if you want
     *                  to open it.
     *
     * @return  The two selected regions to either close or open the flow
     *          between them.
     */
    public static Pair<Region, Region> chooseRegionsToCloseFlow(Map<String, Region> regions, Scanner sc, Boolean close) {
        Pair<Region, Region> regionPair;
        Region r1 = new Region(null, 0, 0), r2 = new Region(null, 0, 0);
        Vector<String> regionsVector = new Vector<>();
        int i = 1;
        for (String regionString : regions.keySet()) {
            System.out.println("[" + i + "] - " + regionString);
            regionsVector.add(regionString);
            i++;
        }
        if (close) System.out.println("Choose the numbers of the two regions you" +
                " want to close the flow between: ");
        else System.out.println("Choose the numbers of the two regions you want " +
                "to open the flow between: ");
        for (int j = 0; j < 2; j++) {
            System.out.print("Region " + (j + 1) + ": ");
            int regionInteger = sc.nextInt();

            if (j == 0) r1 = regions.get(regionsVector.elementAt(regionInteger - 1));
            else r2 = regions.get(regionsVector.elementAt(regionInteger - 1));
        }
        regionPair = new Pair<>(r1, r2);
        return regionPair;
    }

    /**
     * @param   regions Map of all regions in the simulator.
     * @param   sc      Scanner.
     *
     * @return  The region to confine with the time and mobility reduction to
     *          apply.
     */
    public static Pair<Region, Pair<Integer, Integer>> chooseRegionToConfine(Map<String, Region> regions, Scanner sc) {
        Region r;
        Vector<String> regionsVector = new Vector<>();
        int i = 1;
        for (String regionString : regions.keySet()) {
            System.out.println("[" + i + "] - " + regionString);
            regionsVector.add(regionString);
            i++;
        }
        System.out.println("Choose the number of the region you want to confine:");
        System.out.print("Region: ");
        int regionInteger = sc.nextInt();
        r = regions.get(regionsVector.elementAt(regionInteger - 1));
        System.out.println("Number of steps the confinement will last:");
        Integer time = sc.nextInt();

        System.out.println("Reduction in mobility to apply:");
        Integer mobilityReduction = sc.nextInt();

        Pair<Integer, Integer> p = new Pair<>(time, mobilityReduction);

        return new Pair<>(r, p);
    }

    /**
     * @param   regions Map of all regions in the simulator.
     * @param   sc      Scanner.
     *
     * @return  The selected region to release.
     */
    public static Region chooseRegionToRelease(Map<String, Region> regions,
                                               Scanner sc) {
        Region r;
        Vector<String> regionsVector = new Vector<>();
        int i = 1;
        for (String regionString : regions.keySet()) {
            System.out.println("[" + i + "] - " + regionString);
            regionsVector.add(regionString);
            i++;
        }
        System.out.println("Choose the number of the region you want to " +
                "release:");
        System.out.print("Region: ");
        int regionInteger = sc.nextInt();
        r = regions.get(regionsVector.elementAt(regionInteger - 1));

        return r;
    }

    /**
     * @brief   Shows the current step information of the simulator for all
     *          regions.
     *
     * @param   s   Simulator
     *
     * @pre     s is not null.
     * @post    Prints the current step information for all regions in the
     *          simulator.
     */
    public static void showCurrentInformation(Simulator s) {
        Map<Region, List<Virus>> virusRegions = s.getRegionViruses();
        System.out.println("Current step information:");
        if (virusRegions.isEmpty()) {
            System.out.println("No virus information available.");
        } else {
            for (Map.Entry<Region, List<Virus>> virRegEntry : virusRegions.entrySet()) {
                Region r = virRegEntry.getKey();
                for (Virus v : virRegEntry.getValue()) {
                    System.out.println(r.regionState(v, s.getSimulationStep()));
                }
            }
        }
    }

    /**
     * @brief   Shows the total information of all regions in the simulator
     *
     * @param   s   Simulator
     *
     * @pre     s is not null.
     * @post    Prints the total information for all regions in the simulator.
     */
    public static void showTotalInformation(Simulator s) {
        Map<Region, List<Virus>> virusRegions = s.getRegionViruses();
        System.out.println("Total information:");
        if (virusRegions.isEmpty()) {
            System.out.println("No virus information available.");
        } else {
            for (Map.Entry<Region, List<Virus>> virRegEntry : virusRegions.entrySet()) {
                Region r = virRegEntry.getKey();
                for (Virus v : virRegEntry.getValue()) {
                    System.out.println(r.getRegionTotals(v));
                }
            }
        }
    }
}
