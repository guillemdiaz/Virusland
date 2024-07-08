package main.java.com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @class   Simulator
 * @brief   Initiates the simulation.
 */
public class Simulator {

    /** @brief List of regions. */
    private static Map<String, Region> a_regions;
    /** @brief List of vaccines. */
    private static Map<String, Vaccine> a_vaccines;
    /** @brief List of families. */
    static Map<String, Family> a_families;
    /** @brief List of viruses. */
    private static Map<String, Virus> a_viruses;
    /** @brief List of DNA viruses. */
    private static Map<String, VirusDNA> a_virusesDNA;
    /** @brief List of RNA viruses. */
    private static Map<String, VirusRNA> a_virusesRNA;
    /** @brief List of viruses within each Region. */
    private final Map<Region, List<Virus>> a_region_viruses;
    /** @brief Simulation step. */
    private int a_step;

    /**
     * @brief   Constructor of the Simulator class.
     *
     * @pre     --
     * @post    Initializes the Maps as empty and sets the current step number
     *          to 0.
     */
    Simulator() {
        a_regions = new HashMap<>();
        a_vaccines = new HashMap<>();
        a_virusesDNA = new HashMap<>();
        a_virusesRNA = new HashMap<>();
        a_viruses = new HashMap<>();
        a_families = new HashMap<>();
        this.a_region_viruses = new HashMap<>();
        this.a_step = 0;
    }

    /**
     * @brief   Reads the initial state file and infects each region with the
     *          corresponding virus.
     *
     * @param   filepath    Path to the "initialState.txt" file
     * @throws  IOException If the file does not exist.
     *
     * @pre     --
     * @post    Adds the specified virus to each region as indicated in the
     *          "initialState.txt" file.
     *          Also saves this information in the Map a_region_viruses using
     *          the method AddVirusToRegion();
     */
    public void initializeSimulation(String filepath) throws IOException {
        List<Pair<String, List<Pair<String, Integer>>>> virus_regions;
        File selectedFile = new File(filepath);
        virus_regions = processInitialStateFiles(selectedFile);

        for (Pair<String, List<Pair<String, Integer>>> pair : virus_regions) {
            String r = pair.first;
            Region region = a_regions.get(r);
            if (region == null) {
                System.err.println("Region not found: " + r);
                continue;
            }
            for (Pair<String, Integer> pair2 : pair.second) {
                String v = pair2.first;
                Integer p_sick = pair2.second;
                Virus virus = a_viruses.get(v);
                if (virus == null) {
                    System.err.println("Virus not found: " + v);
                    continue;
                }
                region.initializeVirusStatistics(virus);
                region.infect(virus, p_sick);
                AddVirusToRegion(region, virus);
            }
        }
    }

    /**
     * @brief   Simulates a step of the simulator.
     *
     * @pre     --
     * @post    Updates the information for each region in the Map
     *          a_region_viruses for each virus within it.
     */
    public void simulate() {
        for (Map.Entry<Region, List<Virus>> r_Entry : a_region_viruses.entrySet()) {
            List<Virus> mutations = new ArrayList<>();
            Region r = r_Entry.getKey();
            r.updateRegion(mutations);
            for (Virus v : mutations) {
                AddVirusToRegion(r, v);
            }
        }
        a_step++;
    }

    /**
     * @brief   Adds a virus to the list of viruses contained within each region.
     *
     * @param   r   Region to which the virus is added.
     * @param   v   Virus to add.
     */
    private void AddVirusToRegion(Region r, Virus v) {
        if (a_region_viruses.get(r) == null) { // If the region is not in the list, add it with the specified virus
            List<Virus> l_Viruses = new ArrayList<>();
            l_Viruses.add(v);
            a_region_viruses.put(r, l_Viruses);
        } else { // If the region is already in the list, add the specified virus to it
            List<Virus> l_Viruses = a_region_viruses.get(r);
            if (!l_Viruses.contains(v)) { // Only add the virus if it is not already in the list.
                a_region_viruses.get(r).add(v);
            }
        }
    }

    /**
     * @brief   Adds a vaccine to a region for a percentage of the population.
     *
     * @param   r               Region where the vaccine is applied.
     * @param   v               Vaccine to add within the region.
     * @param   p_vaccinated    Percentage of vaccinated population within
     *                          the region.
     *
     * @pre     --
     * @post    Vaccinated region r with vaccine v for p_vaccinated% of the
     *          population.
     */
    public void vaccinateRegion(Region r, Vaccine v, Integer p_vaccinated) {
        Region r_vaccinate = a_regions.get(r.getName());
        Vaccine vaccine = a_vaccines.get(v.getName());
        r_vaccinate.vaccinate(vaccine, p_vaccinated);
    }

    /**
     * @brief   Confines a region with the parameters specified in the
     *          confinement class.
     *
     * @param   r   Region to confine.
     * @param   c   Parameters of the confinement to apply.
     *
     * @pre     --
     * @post    Confines region r with the parameters of confinement c.
     */
    public void confine(Region r, Confinement c) {
        r.applyHardLockdown(c);
    }

    /**
     * @brief   Releases a region.
     *
     * @param   r   Region to release.
     *
     * @pre     --
     * @post    Releases region r.
     */
    public void release(Region r) {
        r.relaxLockdown();
    }

    /**
     * @brief   Applies a closure between two regions to limit movement
     *          between them.
     *
     * @param   r   Region 1 to apply the closure.
     * @param   x   Region 2 to apply the closure.
     *
     * @pre     --
     * @post    Applies the closure between region r and region x.
     */
    public void applyClosure(Region r, Region x) {
        List<Region> closure_region = new ArrayList<>();
        closure_region.add(x);
        r.applyClosure(closure_region);
    }

    /**
     * @brief   Opens the flow of people between two regions.
     *
     * @param   r   Region 1 to open the movement flow.
     * @param   x   Region 2 to open the movement flow.
     *
     * @pre     --
     * @post    Opens the flow of movement of people between region r and
     *          region x.
     */
    public void openClosure(Region r, Region x) {
        List<Region> closure_region = new ArrayList<>();
        closure_region.add(x);
        r.relaxClosure(closure_region);
    }

    /** @return  The current simulation step. */
    public Integer getSimulationStep() { return a_step; }

    /** @return  The map of all regions. */
    public Map<String, Region> getRegions() { return a_regions; }

    /** @return  The map with the list of viruses within each region. */
    public Map<Region, List<Virus>> getRegionViruses() {
        return a_region_viruses;
    }

    /** @return  The map of all vaccines. */
    public Map<String, Vaccine> getVaccines() { return a_vaccines; }

    /**
     * @brief   Loads the data from the selected file.
     *
     * @param   filePath    Path to the selected file.
     * @throws  IOException If the selected file does not exist.
     *
     * @pre     --
     * @post    Processes the selected file and loads the corresponding data
     *          into the attributes a_regions, a_viruses, or a_vaccines.
     */
    public static void chooseFile(String filePath) throws IOException {
        File selectedFile = new File(filePath);
        String fileName = selectedFile.getName();
        if (fileName.startsWith("reg")) {
            processRegionFile(selectedFile);
        } else if (fileName.startsWith("vir")) {
            processVirusFile(selectedFile);
        } else if (fileName.startsWith("vac")) {
            processVaccineFile(selectedFile);
        } else if (fileName.startsWith("initial")) {
            processInitialStateFiles(selectedFile);
        } else {
            System.out.println("Unsupported file type: " + fileName);
        }
    }

    /**
     * @brief   Processes the file containing the vaccines.
     *
     * @param   file        File containing the data of all vaccines.
     * @throws  IOException If the file does not exist.
     *
     * @pre     --
     * @post    Processes the file containing the vaccines and all its
     *          information, storing it in the Map a_vaccines.
     */
    private static void processVaccineFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        if (line.equals("vaccines")) {
            while ((line = reader.readLine()) != null) {
                if (line.equals("*")) {
                    continue;
                }
                String[] vaccineParts = line.split(" ", 3);
                if (vaccineParts.length < 2) {
                    continue; // Skip malformed lines
                }
                String vaccineName = vaccineParts[1];
                String type = reader.readLine().split(" ", 3)[1];
                Virus virus = a_viruses.get(reader.readLine().split(" ", 3)[1]);
                if (type.equals("attenuating")) {
                    int activationTime = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int duration = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int mortalityRateReduction = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int diseaseDurationReduction = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int diseaseProbabilityReduction = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int contagionRateReduction = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    Vaccine vaccine = new Vaccine(vaccineName, type, virus, activationTime, duration, mortalityRateReduction, diseaseDurationReduction, diseaseProbabilityReduction, contagionRateReduction);
                    a_vaccines.put(vaccineName, vaccine);
                } else if (type.equals("inhibiting")) {
                    Integer effectiveness = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int activationTime = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int duration = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    Vaccine vaccine = new Vaccine(vaccineName, type, virus, effectiveness, activationTime, duration);
                    a_vaccines.put(vaccineName, vaccine);
                }
            }
        }
        reader.close();
        System.out.println("Vaccines loaded");
    }


    /**
     * @brief   Processes the file containing the regions.
     *
     * @param   file        File containing the data of all regions.
     * @throws  IOException If the file does not exist.
     *
     * @pre     --
     * @post    Processes the file containing the regions and all their
     *          information, storing it in the Map a_regions.
     */
    private static void processRegionFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        if (line.equals("regions")) {
            line = reader.readLine();
            while (!line.split(" ", 2)[0].equals("limits_and_mobility")) {
                while (!line.equals("*")) {
                    String regionName = line.split(" ", 3)[1];
                    int inhabitants = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int internalMobility = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    Region region = new Region(regionName, inhabitants, internalMobility);
                    a_regions.put(regionName, region);
                    line = reader.readLine();
                }
                line = reader.readLine();
            }
            line = reader.readLine();
            while (line != null && !line.isEmpty()) {
                String regionName = line.split(" ", 3)[0];
                line = reader.readLine();
                while (line.charAt(0) != '*') {
                    String borderingRegionName = line.split(" ", 3)[0];
                    Region borderingRegion = a_regions.get(borderingRegionName);
                    int mobility = Integer.parseInt(line.split(" ", 3)[1].split("%", 2)[0]);
                    a_regions.get(regionName).addNeighboringRegion(borderingRegion, mobility);
                    line = reader.readLine();
                }
                line = reader.readLine();
            }
            reader.close();
            System.out.println("Regions loaded");
        }
    }

    /**
     * @brief   Processes the file containing the viruses.
     *
     * @param   file        File containing the data of all viruses.
     * @throws  IOException If the file does not exist.
     *
     * @pre     --
     * @post    Processes the file containing the viruses and all their
     *          information, storing it in the Map a_viruses.
     */
    private static void processVirusFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        String type = line;

        while (line != null) {
            if (line.equals("families") || line.equals("virus")) {
                type = line;
                line = reader.readLine();
            }
            if (type.equals("families")) {
                while (!line.equals("*")) {
                    String[] parts = line.split(" ", 3);
                    if (parts.length < 2) {
                        throw new IOException("Malformed line for family name: " + line);
                    }
                    String familyName = parts[1];
                    line = reader.readLine();
                    parts = line.split(" ", 3);
                    if (parts.length < 2) {
                        throw new IOException("Malformed line for max variation: " + line);
                    }
                    int maxVariation = Integer.parseInt(parts[1]);
                    Family family = new Family(familyName, maxVariation);
                    a_families.put(familyName, family);
                    line = reader.readLine();
                }
            } else if (type.equals("virus")) {
                while (!line.equals("*")) {
                    String[] parts = line.split(" ", 3);
                    if (parts.length < 2) {
                        throw new IOException("Malformed line for virus name: " + line);
                    }
                    String virusName = parts[1];
                    parts = reader.readLine().split(" ", 3);
                    if (parts.length < 2) {
                        throw new IOException("Malformed line for virus type: " + line);
                    }
                    String virusType = parts[1];
                    parts = reader.readLine().split(" ", 3);
                    if (parts.length < 2) {
                        throw new IOException("Malformed line for family name: " + line);
                    }
                    String familyName = parts[1];

                    double diseaseProbability = Double.parseDouble(reader.readLine().split(" ", 3)[1]);
                    int incubation = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int latency = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int diseaseDuration = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int contagionDuration = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    int immunityDuration = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                    double mortality = Double.parseDouble(reader.readLine().split(" ", 3)[1]);
                    double contagionRate = Double.parseDouble(reader.readLine().split(" ", 3)[1]);
                    Family family = a_families.get(familyName);

                    if (virusType.equals("RNA")) {
                        double copyMutationProbability = Double.parseDouble(reader.readLine().split(" ", 3)[1]);
                        double coincidenceMutationProbability = Double.parseDouble(reader.readLine().split(" ", 3)[1]);
                        VirusRNA virusRNA = new VirusRNA(virusName, family, diseaseProbability, incubation, latency,
                                diseaseDuration, contagionDuration, immunityDuration, mortality, contagionRate,
                                copyMutationProbability, coincidenceMutationProbability);
                        a_virusesRNA.put(virusName, virusRNA);
                        a_viruses.put(virusName, virusRNA);
                    } else if (virusType.equals("DNA")) {
                        VirusDNA virusDNA = new VirusDNA(virusName, family, diseaseProbability, incubation, latency,
                                diseaseDuration, contagionDuration, immunityDuration, mortality, contagionRate);
                        a_virusesDNA.put(virusName, virusDNA);
                        a_viruses.put(virusName, virusDNA);
                    }
                    line = reader.readLine();
                }
            }
            line = reader.readLine();
        }
        reader.close();
        System.out.println("Viruses loaded");
    }

    /**
     * @brief   Processes the file with the initial state data of the
     *          simulation.
     *
     * @param   file    File containing the initial state.
     * @throws  IOException If the file does not exist
     *
     * @return  A List of a Pair that contains the name of each region and a
     *          List that contains the name of each virus within the region
     *          and the percentage of sick people.
     */
    private static List<Pair<String, List<Pair<String, Integer>>>> processInitialStateFiles(File file) throws IOException {
        List<Pair<String, List<Pair<String, Integer>>>> virus_regions = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while (line.charAt(0) == '#') line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            String regionName = line.split(" ", 3)[1];
            line = reader.readLine();
            Pair<String, List<Pair<String, Integer>>> pair_region;
            List<Pair<String, Integer>> list_viruses = new ArrayList<>();
            if (line.equals("present_viruses")) line = reader.readLine();
            while (line.charAt(0) != '*') {
                String virusName = line.split(" ", 3)[1];
                Integer p_sick = Integer.parseInt(reader.readLine().split(" ", 3)[1]);
                Pair<String, Integer> pair_virus = new Pair<>(virusName, p_sick);
                list_viruses.add(pair_virus);

                line = reader.readLine();
            }
            pair_region = new Pair<>(regionName, list_viruses);
            virus_regions.add(pair_region);
            line = reader.readLine();
        }
        reader.close();
        System.out.println("Initial state loaded");
        return virus_regions;
    }
}
