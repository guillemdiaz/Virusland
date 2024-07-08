package main.java.com.example;

import java.util.*;

/**
 * @class   Region
 * @brief   Class that models a region of the world.
 *
 * @details Contains attributes such as the region's name, its population, its
 *          surface area, and statistics related to each present virus.
 */
public class Region {
    /** @brief  The name of the region. */
    private final String name;
    /** @brief  Number of inhabitants of the region. */
    private int inhabitants;
    /** @brief  Internal mobility rate of the region. */
    private final double internalMobility;
    /** @brief  Reduced mobility rate of the region if any. */
    private Pair<Integer, Integer> reducedMobility;
    /** @brief  Percentage of external mobility for each neighboring region. */
    private final Map<Region, Integer> neighboringRegions;
    /** @brief  Closures applied to neighboring regions. */
    private final Map<Region, Boolean> regionClosures;
    /** @brief  Statistics of each virus in the region. */
    private final Map<Virus, Statistics> virusStatistics;
    /** @brief  Map of vaccines applied to the region. */
    private final Map<Vaccine, List<Pair<Integer, Pair<Integer, Integer>>>> vaccinated;
    /** @brief  Copies of viruses that have had vaccine effects applied to them. */
    private final List<Virus> virusCopies;
    /** @brief  Region states for each virus at each step of the simulation. */
    private final Map<Virus, List<RegionState>> states;
    /** @brief  Steps of the simulation in time units. */
    private int simulationStep;

    /**
     * @brief   Creates a region from the given parameters.
     *
     * @param   name                The name of the region.
     * @param   numInhabitants      The number of inhabitants of the region at
     *                              the initial step.
     * @param   internalMobility    The internal mobility rate of the region.
     */
    public Region(String name, int numInhabitants, double internalMobility) {
        this.name = name;
        this.neighboringRegions = new HashMap<>();
        this.regionClosures = new HashMap<>();
        this.inhabitants = numInhabitants;
        this.internalMobility = internalMobility;
        this.reducedMobility = new Pair<>(0, 0);
        this.virusStatistics = new HashMap<>();
        this.vaccinated = new HashMap<>();
        this.virusCopies = new ArrayList<>();
        this.states = new HashMap<>();
        this.simulationStep = 0;
    }

    /** @return the name of the region. */
    public String getName() {
        return name;
    }

    /**
     * @brief   Adds a neighboring region to the list of neighboring regions.
     *
     * @param   region      The region to add.
     * @param   percentage  The percentage of the population that travels to the
     *                      neighboring region.
     * @throws  IllegalArgumentException    if the region to add is null or the
     *                                      same.
     *
     * @pre     The added region is not null and not the same current region.
     * @post    The added region is in the list of neighboring regions of the
     *          current region and the map of region closures has been updated.
     */
    public void addNeighboringRegion(Region region, int percentage) {
        if (region == null || region.equals(this))
            throw new IllegalArgumentException("The added region cannot be " +
                    "null or the same as the current region");
        neighboringRegions.put(region, percentage);
        regionClosures.put(region, false);
    }

    /** @return  The map of neighboring regions of the region. */
    public Map<Region, Integer> getNeighboringRegions() {
        return neighboringRegions;
    }

    /**
     * @param   region  The region to check.
     *
     * @return  true if they are neighbors.
     */
    public boolean isNeighboringWith(Region region) {
        return neighboringRegions.containsKey(region);
    }

    /** @return the number of inhabitants of the region. */
    public int getNumInhabitants() {
        return inhabitants;
    }

    /**
     * @brief   Infects a number of people for a given virus.
     *
     * @details If the virus already exists in the region, the number of people
     *          infected by the virus is updated. If the virus does not exist in
     *          the region, a new entry is added to the count.
     *
     * @param   virus           The virus for which to update the number of
     *                          infected people.
     * @param   numToInfect     The number of new infections to add to the count
     *                          for the virus.
     *
     * @throws  IllegalArgumentException    if the number of infections to add
     *                                      is negative.
     *
     * @pre     numToInfect is a non-negative integer.
     * @post    The count of infected by the virus and the maps of infections
     *          and latents of the region have been updated.
     */
    public void infect(Virus virus, int numToInfect) {
        if (numToInfect < 0)
            throw new IllegalArgumentException("The number of infections to " +
                    "add cannot be negative.");
        if (numToInfect != 0) {
            int possibleInfection =
                    inhabitants - getNumInfected(virus) - getNumImmune(virus);
            numToInfect = Math.min(numToInfect, possibleInfection);
            directInfect(virus, numToInfect);
        }
    }

    /**
     * @brief   Infects people based on the internal mobility of the
     *          region and the contagion rate of a virus.
     *
     * @param   virus   The virus for which people are infected.
     *
     * @pre     virus is not null.
     * @post    A number of people have been infected based on the contagion rate
     *          of the region, internal mobility, and the percentage of infected.
     *          Mutations of the virus due to copy error and coincidence have also
     *          been generated.
     */
    private void infectByProbability(Virus virus, List<Virus> mutations) {
        double infectedPercentage = (double) getNumInfected(virus) / inhabitants;
        double coincidenceProbability;
        if (reducedMobility.second != 0)
            coincidenceProbability = (1 - Math.pow((1 - infectedPercentage *
                    virus.getContagionRate()), reducedMobility.first));
        else
            coincidenceProbability = (1 - Math.pow((1 - infectedPercentage *
                    virus.getContagionRate()), internalMobility));
        int numToInfect = (int) ((inhabitants - getNumInfected(virus) -
                getNumImmune(virus)) * coincidenceProbability);
        if (numToInfect < 0)
            numToInfect = 0;
        if (virus instanceof VirusRNA) {
            int copyErrorMutations =
                    (int) (infectedPercentage * virus.getContagionRate() *
                            ((VirusRNA) virus).getMutationProbabilityCopyError());
            for (int i = 0; i < copyErrorMutations; i++) {
                Virus mutation = ((VirusRNA) virus).mutateDueToCopyError();
                initializeVirusStatistics(mutation);
                directInfect(mutation, numToInfect);
                mutations.add(mutation);
            }
            List<Virus> sameFamilyViruses = new ArrayList<>();
            for (Virus virusB : virusStatistics.keySet())
                if (virus.sameFamily(virusB))
                    sameFamilyViruses.add(virusB);
            int familyInfected = 0;
            for (Virus familyVirus : sameFamilyViruses)
                familyInfected += getNumInfected(familyVirus);
            for (Virus familyVirus : sameFamilyViruses) {
                int p = familyInfected / getNumInhabitants();
                int probabilityInfectedTwoViruses =
                        (getNumInfected(familyVirus) / getNumInhabitants()) *
                                ((getNumInfected(familyVirus) - 1) / (getNumInhabitants() - 1)) * p;
                int coincidenceErrorMutations = (int) (infectedPercentage *
                        virus.getContagionRate() * probabilityInfectedTwoViruses *
                        ((VirusRNA) virus).getMutationProbabilityCoincidence());
                for (int i = 0; i < coincidenceErrorMutations; i++) {
                    Virus mutation =
                            ((VirusRNA) virus).mutateByCoincidence((VirusRNA) familyVirus);
                    initializeVirusStatistics(mutation);
                    directInfect(mutation, numToInfect);
                    mutations.add(mutation);
                }
            }
        }
        directInfect(virus, numToInfect);
    }

    /**
     * @brief   Directly infects a number of people in the region.
     *
     * @param   virus           The virus for which to infect.
     * @param   numToInfect     The number of infections to add.
     *
     * @pre     virus is not null.
     * @post    The maps of incubated infections and latents have been updated
     *          with numToInfect and the respective time, as well as the total
     *          number of infected for the virus.
     */
    private void directInfect(Virus virus, int numToInfect) {
        initializeVirusStatistics(virus);
        Pair<Integer, Integer> incubationPair =
                new Pair<>(numToInfect, virus.getIncubationTime());
        List<Pair<Integer, Integer>> incubationPairs =
                virusStatistics.get(virus).getInfected();
        incubationPairs.add(incubationPair);
        Pair<Integer, Integer> latencyPair = new Pair<>(numToInfect,
                virus.getLatencyTime());
        List<Pair<Integer, Integer>> latencyPairs =
                virusStatistics.get(virus).getLatents();
        latencyPairs.add(latencyPair);
        virusStatistics.get(virus).increaseInfected(numToInfect);
    }

    /**
     * @param   virus   The virus for which to get the current number of infected.
     *
     * @details An inhabitant is considered infected if they are in the incubation
     *          state for the virus.
     *
     * @return  the number of infected by this specific virus at the current step.
     */
    public int getNumInfected(Virus virus) {
        if (!virusStatistics.containsKey(virus)) {
            return 0; // or handle the error as needed
        }
        List<Pair<Integer, Integer>> pairs =
                virusStatistics.get(virus).getInfected();
        int infected = 0;
        for (Pair<Integer, Integer> pair : pairs)
            infected += pair.first;
        return infected;
    }

    /**
     * @brief   Initializes the virus statistics for a given virus if not
     *          already present.
     *
     * @param   virus   The virus for which statistics need to be initialized.
     *
     * @pre     virus is not null.
     * @post    If the virus was not already present, a new entry is added to
     *          the virusStatistics map.
     */
    public void initializeVirusStatistics(Virus virus) {
        if (!virusStatistics.containsKey(virus)) {
            Statistics statistics = new Statistics();
            virusStatistics.put(virus, statistics);
        }
    }

    /**
     * @param   virus   The virus for which to get the total number of infected.
     *
     * @return  the number of infected by this virus in the region since the
     *          simulation started.
     */
    private int getTotalNumInfected(Virus virus) {
        return virusStatistics.get(virus).getTotalInfected();
    }

    /**
     * @brief   Contagions a virus to a specific number of inhabitants.
     *
     * @param   virus           The virus to be transmitted.
     * @param   numToContagion  The number of inhabitants to be infected.
     *
     * @pre     --
     * @post    The map of infected has been updated.
     */
    private void contagion(Virus virus, int numToContagion) {
        Pair<Integer, Integer> contagionPair =
                new Pair<>(numToContagion, virus.getInfectionDuration());
        List<Pair<Integer, Integer>> infectedVirus =
                virusStatistics.get(virus).getContagious();
        infectedVirus.add(contagionPair);
        virusStatistics.get(virus).increaseContagious(numToContagion);
    }

    /**
     * @param   virus   The virus for which to get the current number of infected.
     *
     * @return  the number of infected by this specific virus at the current step.
     */
    private int getNumContagious(Virus virus) {
        List<Pair<Integer, Integer>> pairs =
                virusStatistics.get(virus).getContagious();
        int infected = 0;
        for (Pair<Integer, Integer> pair : pairs)
            infected += pair.first;
        return infected;
    }

    /**
     * @param   virus   The virus for which to get the total number of infected.
     *
     * @return  the number of infected by this virus in the region since the
     *          simulation started.
     */
    private int getTotalNumContagious(Virus virus) {
        return virusStatistics.get(virus).getTotalContagious();
    }

    /**
     * @brief   Makes a certain percentage of the population sick.
     *
     * @param   virus       The virus for which to make inhabitants sick.
     * @param   percentage  The percentage of inhabitants to be made sick.
     *
     * @pre     percentage is an integer between 0 and 100.
     * @post    A random number of inhabitants not already sick from the virus
     *          have been made sick, according to the specified percentage.
     */
    public void makeSick(Virus virus, int percentage) {
        if (percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("The percentage must be an " +
                    "integer between 0 and 100");
        if (percentage == 0)
            makeSickByProbability(virus);
        else {
            int numToMakeSick =
                    (int) Math.round((percentage / 100.0) * inhabitants);
            directMakeSick(virus, numToMakeSick);
        }
    }

    /**
     * @brief   Directly makes a certain number of inhabitants sick with a given
     *          virus.
     *
     * @param   virus           The virus that will cause the illness.
     * @param   numToMakeSick   The number of inhabitants to be made sick.
     *
     * @pre     --
     * @post    A list of inhabitants sick from the given virus has been created
     *          and the total for the same virus has been incremented.
     */
    private void directMakeSick(Virus virus, int numToMakeSick) {
        Statistics statistics = new Statistics();
        virusStatistics.put(virus, statistics);
        int possibleSickness = inhabitants - getNumVaccinated(virus) -
                getNumImmune(virus) - getNumSick(virus);
        numToMakeSick = Math.min(numToMakeSick, possibleSickness);
        if (numToMakeSick <= 0)
            throw new IllegalArgumentException("Not enough susceptibles to " +
                    "make sick.");
        Pair<Integer, Integer> pair = new Pair<>(numToMakeSick,
                virus.getDiseaseDuration());
        List<Pair<Integer, Integer>> pairs =
                virusStatistics.get(virus).getSick();
        pairs.add(pair);
        virusStatistics.get(virus).increaseSick(numToMakeSick);
        infect(virus, numToMakeSick);
        contagion(virus, numToMakeSick);
    }

    /**
     * @brief   Makes inhabitants infected with a virus sick according to the
     *          probability rate of the virus.
     *
     * @param   virus   The virus that will cause the illness.
     *
     * @pre     --
     * @post    A number of inhabitants have been made sick based on the
     *          probability of illness of the virus and the map of statistics
     *          has been updated.
     */
    private void makeSickByProbability(Virus virus) {
        List<Pair<Integer, Integer>> infectedPairs =
                virusStatistics.get(virus).getInfected();
        int numToMakeSick = 0;
        for (int i = 0; i < infectedPairs.size(); i++) {
            Pair<Integer, Integer> pair = infectedPairs.get(i);
            int probabilityOfIllness = (int) (virus.getDiseaseProbability() *
                    pair.first);
            numToMakeSick += probabilityOfIllness;
            pair.first -= probabilityOfIllness;
            infectedPairs.set(i, pair);
        }
        int possibleSickness = inhabitants - getNumVaccinated(virus) -
                getNumImmune(virus) - getNumSick(virus);
        if (possibleSickness < 0)
            possibleSickness = 0;
        numToMakeSick = Math.min(numToMakeSick, possibleSickness);
        Pair<Integer, Integer> pair = new Pair<>(numToMakeSick,
                virus.getDiseaseDuration());
        List<Pair<Integer, Integer>> sickPairs =
                virusStatistics.get(virus).getSick();
        sickPairs.add(pair);
        virusStatistics.get(virus).increaseSick(numToMakeSick);
    }

    /**
     * @param   virus   The virus for which to get the number of sick.
     *
     * @return  the number of sick from the last simulation step.
     */
    private int getNumSick(Virus virus) {
        List<Pair<Integer, Integer>> pairs =
                virusStatistics.get(virus).getSick();
        int sick = 0;
        for (Pair<Integer, Integer> pair : pairs)
            sick += pair.first;
        return sick;
    }

    /**
     * @param   virus   The virus for which to get the total number of sick.
     *
     * @return  the number of inhabitants sick from the specified virus in the
     *          region since the simulation started. If the region has no sick
     *          from this virus, returns 0.
     */
    private int getTotalNumSick(Virus virus) {
        return virusStatistics.get(virus).getTotalSick();
    }

    /**
     * @brief   Immunizes a specific number of inhabitants from a specific virus
     *          and viruses from the same family.
     *
     * @param   virus           The virus to immunize against.
     * @param   numToImmunize   The number of inhabitants to immunize.
     *
     * @pre     --
     * @post    A number of inhabitants have been immunized against all viruses
     *          from the same family and the map of statistics has been updated.
     */
    private void immunize(Virus virus, int numToImmunize) {
        List<Virus> sameFamilyViruses = new ArrayList<>();
        for (Virus immuneVirus : virusStatistics.keySet())
            if (virus.sameFamily(immuneVirus))
                sameFamilyViruses.add(immuneVirus);
        for (Virus familyVirus : sameFamilyViruses) {
            Pair<Integer, Integer> immunityPair =
                    new Pair<>(numToImmunize,
                            familyVirus.getImmunityDuration());
            List<Pair<Integer, Integer>> immuneVirus =
                    virusStatistics.get(familyVirus).getImmune();
            immuneVirus.add(immunityPair);
        }
    }

    /**
     * @param   virus   The virus for which to get the number of immune.
     *
     * @return  the number of immune from the last simulation step.
     */
    private int getNumImmune(Virus virus) {
        List<Pair<Integer, Integer>> pairs =
                virusStatistics.get(virus).getImmune();
        int immune = 0;
        for (Pair<Integer, Integer> pair : pairs)
            immune += pair.first;
        return immune;
    }

    /**
     * @param   virus   The virus for which to get the number of deaths.
     *
     * @throws  IllegalArgumentException    if the virus is not present in the
     *                                      region or not enough steps have been
     *                                      simulated.
     *
     * @return  the number of deaths from the last simulation step.
     */
    private int getNumDeaths(Virus virus) {
        if (!virusStatistics.containsKey(virus))
            throw new IllegalArgumentException("The virus is not present in " +
                    "the region or not enough steps have been simulated.");
        return virusStatistics.get(virus).getDeaths();
    }

    /**
     * @param   virus   The virus for which to get the total number of deaths.
     *
     * @return  the number of inhabitants who have died in the region from the
     *          specified virus since the simulation started.
     */
    private int getTotalNumDeaths(Virus virus) {
        List<RegionState> virusStates = states.get(virus);
        if (virusStates == null)
            return 0;
        int totalDeaths = 0;
        for (RegionState regionState : virusStates)
            totalDeaths += regionState.deaths;
        return totalDeaths;
    }

    /**
     * @param   virus   The virus for which to get the total number of cured.
     *
     * @throws  IllegalArgumentException    if the virus is not present in the
     *                                      region or not enough steps have been
     *                                      simulated.
     *
     * @return  the number of cured in the region from the specified virus since
     *          the simulation started.
     */
    private int getTotalNumCured(Virus virus) {
        if (!virusStatistics.containsKey(virus))
            throw new IllegalArgumentException("The virus is not present in " +
                    "the region.");
        return virusStatistics.get(virus).getCured();
    }

    /**
     * @brief   Vaccinates a percentage of the population in the region.
     *
     * @param   vaccine      main.java.com.example.Vaccine to be used to
     *                       vaccinate the population.
     * @param   percentage   The percentage of the population to be vaccinated.
     *
     * @pre     percentage is an integer between 0 and 100.
     * @post    A percentage of inhabitants have been vaccinated with the
     *          specified vaccine and the map of vaccinated has been updated.
     */
    public void vaccinate(Vaccine vaccine, int percentage) {
        int numToVaccinate = (int) Math.round((percentage / 100.0) * inhabitants);
        if (vaccine.getEffectiveness() != null)
            numToVaccinate *= (int) (vaccine.getEffectiveness() / 100.0);
        Pair<Integer, Pair<Integer, Integer>> vaccinationPair =
                new Pair<>(numToVaccinate,
                        new Pair<>(vaccine.getActivationTime(), vaccine.getDuration()));
        List<Pair<Integer, Pair<Integer, Integer>>> vaccinatedList =
                vaccinated.getOrDefault(vaccine, new ArrayList<>());
        vaccinatedList.add(vaccinationPair);
        vaccinated.put(vaccine, vaccinatedList);
        for (Virus virus : virusStatistics.keySet())
            if (virus.getName().startsWith(vaccine.getTargetVirus().getName()))
                virusStatistics.get(virus).increaseVaccinated(numToVaccinate);
    }

    /**
     * @param   virus   The virus for which to get the number of vaccinated.
     *
     * @return  the number of vaccinated from the last simulation step.
     */
    private int getNumVaccinated(Virus virus) {
        int numVaccinated = 0;
        for (Vaccine vaccine : vaccinated.keySet()) {
            if (virus.equals(vaccine.getTargetVirus())) {
                List<Pair<Integer, Pair<Integer, Integer>>> pairs =
                        vaccinated.get(vaccine);
                for (Pair<Integer, Pair<Integer, Integer>> pair : pairs)
                    numVaccinated += pair.first;
                break;
            }
        }
        return numVaccinated;
    }

    /**
     * @param   virus   The virus for which to get the total number of vaccinated.
     *
     * @throws  IllegalArgumentException    if the virus is not present in the
     *                                      region or not enough steps have been
     *                                      simulated.
     *
     * @return  the number of inhabitants vaccinated in the region from the
     *          specified virus since the simulation started.
     */
    private int getTotalNumVaccinated(Virus virus) {
        if (!virusStatistics.containsKey(virus))
            throw new IllegalArgumentException("The virus is not present in " +
                    "the region or not enough steps have been simulated.");
        return virusStatistics.get(virus).getTotalVaccinated();
    }

    /**
     * @param   virus   The virus for which to calculate the mortality rate in
     *                  the region.
     *
     * @return  the mortality rate of the virus in the region.
     */
    private double calculateMortalityRate(Virus virus) {
        if (getNumDeaths(virus) < 1)
            return 0.00;
        double mortalityRate =
                (double) getTotalNumDeaths(virus) / getTotalNumInfected(virus);
        String strMortalityRate =
                String.format("%.2f", mortalityRate).replace(",", ".");
        return Double.parseDouble(strMortalityRate);
    }

    /**
     * @param   virus   The virus for which to calculate the transmission rate
     *                  of the region.
     *
     * @return  The transmission rate of the region for a specific virus.
     */
    private double calculateTransmissionRate(Virus virus) {
        if (!states.containsKey(virus))
            return 0.00;
        double transmissionRate =
                (double) getNumContagious(virus) / getRegionState(virus,
                        simulationStep).infected;
        String strTransmissionRate =
                String.format("%.2f", transmissionRate).replace(",", ".");
        return Double.parseDouble(strTransmissionRate);
    }

    /** @return The number of inhabitants arriving in the region in a unit of time. */
    private int externalPopulation() {
        int externalPopulation = 0;
        for (Region neighboringRegion : neighboringRegions.keySet()) {
            if (!regionClosures.get(neighboringRegion)) {
                Map<Region, Integer> neighbors = neighboringRegion.getNeighboringRegions();
                if (neighbors != null) {
                    Integer percentage = neighbors.get(this);
                    if (percentage != null) {
                        int neighborPopulation = neighboringRegion.getNumInhabitants();
                        externalPopulation += (percentage * neighborPopulation) / 100;
                    }
                }
            }
        }
        return externalPopulation;
    }

    /**
     * @brief   Updates the state of the region and its inhabitants.
     *
     * @details At each step of the simulation, the infected, latents, and
     *          contagious are updated, as well as the number of sick, immune,
     *          and vaccinated inhabitants. A new region state is also created
     *          for each present virus.
     *
     * @pre     --
     * @post    The state of the inhabitants has been updated and a new region
     *          state has been created for each present virus. The simulation
     *          step has been incremented by one unit and the list of mutations
     *          has been updated as well as the map of statistics for each virus.
     */
    public void updateRegion(List<Virus> mutations) {
        if (simulationStep % 2 == 0)
            inhabitants += externalPopulation();
        else
            inhabitants -= externalPopulation();
        updateLatents();
        updateContagious(mutations);
        updateInfected();
        updateSick();
        updateImmune();
        updateVaccinated();
        for (Virus virus : virusStatistics.keySet()) {
            RegionState state = new RegionState(simulationStep, inhabitants,
                    getNumInfected(virus), getNumContagious(virus), getNumImmune(virus),
                    getNumSick(virus), getNumDeaths(virus), getNumVaccinated(virus),
                    calculateTransmissionRate(virus), calculateMortalityRate(virus));
            states.computeIfAbsent(virus, k -> new ArrayList<>()).add(state);
        }
        if (reducedMobility.second != 0) {
            reducedMobility.second -= 1;
            if (reducedMobility.second == 0)
                relaxClosure(new ArrayList<>(neighboringRegions.keySet()));
        }
        simulationStep++;
    }

    /**
     * @brief   Updates the inhabitants in latent state for each virus.
     *
     * @details For each virus, decreases the latency time of each latent by 1
     *          unit. If a group of latents has reached the end of their latency
     *          period, it is removed from the map of latents and passes to the
     *          group of contagious.
     *
     * @pre     --
     * @post    The map of latents and the map of contagious for each virus have
     *          been updated.
     */
    private void updateLatents() {
        for (Virus virus : virusStatistics.keySet()) {
            List<Pair<Integer, Integer>> latentsVirus =
                    virusStatistics.get(virus).getLatents();
            Iterator<Pair<Integer, Integer>> itr = latentsVirus.iterator();
            while (itr.hasNext()) {
                Pair<Integer, Integer> pair = itr.next();
                if (pair.second != 0)
                    pair.second -= 1;
                else {
                    contagion(virus, pair.first);
                    itr.remove();
                }
            }
        }
    }

    /**
     * @brief   Updates the inhabitants contagious for each virus.
     *
     * @details For each virus, decreases the contagion time of each group of
     *          contagious by one unit, removing it if the duration has been
     *          surpassed, or updating the remaining duration if it has not yet
     *          ended. Also calls the infection method to update the state of
     *          infected people based on the virus and its transmission rate in
     *          the region.
     *
     * @pre     --
     * @post    The map of contagious has been updated as described. Additionally,
     *          the infection method has been called for each virus.
     */
    private void updateContagious(List<Virus> mutations) {
        for (Virus virus : virusStatistics.keySet()) {
            infectByProbability(virus, mutations);
            List<Pair<Integer, Integer>> contagiousVirus =
                    virusStatistics.get(virus).getContagious();
            Iterator<Pair<Integer, Integer>> itr = contagiousVirus.iterator();
            while (itr.hasNext()) {
                Pair<Integer, Integer> pair = itr.next();
                if (pair.second != 0)
                    pair.second -= 1;
                else
                    itr.remove();
            }
        }
    }

    /**
     * @brief   Updates the inhabitants infected for each virus.
     *
     * @details For each virus, decreases the incubation time of each infected
     *          by one unit. If an infected has reached the end of their
     *          incubation period, it is removed from the map of infected.
     *          Additionally, a number of infected inhabitants are made sick
     *          based on the probability of illness of the virus.
     *
     * @pre     --
     * @post    The map of infected for each virus has been updated and a certain
     *          number of infected inhabitants have been made sick or immunized.
     */
    private void updateInfected() {
        for (Virus virus : virusStatistics.keySet()) {
            makeSickByProbability(virus);
            List<Pair<Integer, Integer>> infectedVirus =
                    virusStatistics.get(virus).getInfected();
            Iterator<Pair<Integer, Integer>> itr = infectedVirus.iterator();
            while (itr.hasNext()) {
                Pair<Integer, Integer> pair = itr.next();
                if (pair.second != 0)
                    pair.second -= 1;
                else {
                    immunize(virus, pair.first);
                    virusStatistics.get(virus).setCured(getTotalNumCured(virus) + pair.first);
                    itr.remove();
                }
            }
        }
    }

    /**
     * @brief   Updates the inhabitants sick due to a specific virus.
     *
     * @details For each sick inhabitant, checks the possibility of death based
     *          on the mortality rate of the virus. Decreases the remaining
     *          days to recovery by one unit, and if the duration of the
     *          illness has passed, cures them. Subsequently, immunizes the
     *          inhabitants who have recovered from the illness.
     *
     * @pre     --
     * @post    The map of sick has been updated and the number of inhabitants
     *          who have recovered has been immunized. The number of deaths and
     *          recovered for each virus has been counted, and the number of
     *          inhabitants in the region has been updated.
     */
    private void updateSick() {
        for (Virus virus : virusStatistics.keySet()) {
            List<Pair<Integer, Integer>> sickVirus =
                    virusStatistics.get(virus).getSick();
            int numDeathsVirus = 0;
            for (int i = 0; i < sickVirus.size(); i++) {
                Pair<Integer, Integer> pair = sickVirus.get(i);
                int deathProbability = (int) (virus.getMortalityRate() * pair.first);
                numDeathsVirus += deathProbability;
                inhabitants -= deathProbability;
                pair.first -= deathProbability;
                sickVirus.set(i, pair);
            }
            virusStatistics.get(virus).setDeaths(numDeathsVirus);
            Iterator<Pair<Integer, Integer>> itr = sickVirus.iterator();
            while (itr.hasNext()) {
                Pair<Integer, Integer> pair = itr.next();
                if (pair.second != 0)
                    pair.second -= 1;
                else {
                    immunize(virus, pair.first);
                    virusStatistics.get(virus).setCured(getTotalNumCured(virus) + pair.first);
                    itr.remove();
                }
            }
        }
    }

    /**
     * @brief   Updates the inhabitants immunized by the virus.
     *
     * @details For each virus, reduces the remaining immunity time by one unit.
     *          If the immunity time has reached zero, the inhabitant is no
     *          longer immunized.
     *
     * @pre     --
     * @post    The map of immune for each virus has been updated.
     */
    private void updateImmune() {
        for (Virus virus : virusStatistics.keySet()) {
            List<Pair<Integer, Integer>> immuneVirus =
                    virusStatistics.get(virus).getImmune();
            Iterator<Pair<Integer, Integer>> itr = immuneVirus.iterator();
            while (itr.hasNext()) {
                Pair<Integer, Integer> pair = itr.next();
                if (pair.second != 0)
                    pair.second -= 1;
                else
                    itr.remove();
            }
        }
    }

    /**
     * @brief   Updates the number of vaccinated for each virus and applies the
     *          effects if necessary.
     *
     * @details If the activation time of the vaccine has not yet ended, the
     *          remaining time is reduced by one unit. If the activation time has
     *          ended, the effects of the vaccine are applied. If the duration of
     *          the vaccine has not yet ended, the remaining duration time is
     *          reduced by one unit. If the duration of the vaccine has ended,
     *          the vaccine is removed from the vaccinated and its effects
     *          are no longer applied.
     *
     * @pre     --
     * @post    The map of vaccinated has been updated with the new activation
     *          and duration times of the vaccines and the effects of the
     *          vaccines have been applied or ceased to be applied as
     *          appropriate.
     */
    private void updateVaccinated() {
        for (Vaccine vaccine : vaccinated.keySet()) {
            List<Pair<Integer, Pair<Integer, Integer>>> vaccinatedVirus = vaccinated.get(vaccine);
            Iterator<Pair<Integer, Pair<Integer, Integer>>> itr = vaccinatedVirus.iterator();
            while (itr.hasNext()) {
                Pair<Integer, Pair<Integer, Integer>> pair = itr.next();
                if (pair.second.first != 0) {
                    pair.second.first -= 1;
                    if (pair.second.first == 0)
                        applyVaccineEffects(vaccine);
                } else {
                    if (pair.second.second != 0)
                        pair.second.second -= 1;
                    else {
                        itr.remove();
                        removeVaccineEffects(vaccine);
                    }
                }
            }
        }
    }

    /**
     * @brief   Applies the effects of a vaccine.
     *
     * @details The effects are applied to the virus the vaccine is targeted at
     *          and its mutations.
     *
     * @param   vaccine  The vaccine to apply to the viruses.
     *
     * @pre     --
     * @post    The effects of the vaccine have been applied to the corresponding
     *          viruses and the map of statistics has been updated, as well as
     *          the list of virus copies.
     */
    private void applyVaccineEffects(Vaccine vaccine) {
        for (Virus virus : virusStatistics.keySet())
            if (virus.getName().startsWith(vaccine.getTargetVirus().getName())) {
                Virus vaccinatedVirus;
                if (virus instanceof VirusDNA)
                    vaccinatedVirus = new VirusDNA(virus.getName(), virus.getFamily(),
                            virus.getDiseaseProbability() *
                                    (100 - vaccine.getDiseaseProbabilityReduction()) / 100,
                            virus.getIncubationTime(), virus.getLatencyTime(),
                            virus.getDiseaseDuration() *
                                    (100 - vaccine.getDiseaseDurationReduction()) / 100,
                            virus.getInfectionDuration(), virus.getImmunityDuration(),
                            virus.getMortalityRate() *
                                    (100 - vaccine.getMortalityRateReduction()) / 100,
                            virus.getContagionRate() *
                                    (100 - vaccine.getContagionRateReduction()) / 100);
                else
                    vaccinatedVirus = new VirusRNA(virus.getName(), virus.getFamily(),
                            virus.getDiseaseProbability() *
                                    (100 - vaccine.getDiseaseProbabilityReduction()) / 100,
                            virus.getIncubationTime(), virus.getLatencyTime(),
                            virus.getDiseaseDuration() *
                                    (100 - vaccine.getDiseaseDurationReduction()) / 100,
                            virus.getInfectionDuration(), virus.getImmunityDuration(),
                            virus.getMortalityRate() *
                                    (100 - vaccine.getMortalityRateReduction()) / 100,
                            virus.getContagionRate() *
                                    (100 - vaccine.getContagionRateReduction()) / 100,
                            ((VirusRNA) virus).getMutationProbabilityCopyError(),
                            ((VirusRNA) virus).getMutationProbabilityCoincidence());
                Statistics originalStatistics = virusStatistics.get(virus);
                virusCopies.add(virus);
                virusStatistics.remove(virus);
                virusStatistics.put(vaccinatedVirus, originalStatistics);
            }
    }

    /**
     * @brief   Removes the effects of a previously applied vaccine for a series
     *          of specific viruses.
     *
     * @param   vaccine  The vaccine for which the effects are being removed.
     *
     * @pre     The vaccine must be of type "attenuating".
     * @post    The viruses affected by the vaccine return to their original
     *          attributes and have been removed from the list of virus copies.
     */
    private void removeVaccineEffects(Vaccine vaccine) {
        if (!vaccine.getType().equals("attenuating"))
            return;
        for (Virus virus : virusCopies) {
            if (virus.getName().startsWith(vaccine.getTargetVirus().getName())) {
                Statistics statistics = virusStatistics.get(virus);
                virusStatistics.remove(virus);
                virusStatistics.put(virus, statistics);
                virusCopies.remove(virus);
            }
        }
    }

    /**
     * @brief   Applies a closure between the current region and a specific number
     *          of regions.
     *
     * @details This method applies a closure between the current region and a set
     *          of neighboring regions, passed as a parameter. If a hard lockdown
     *          is being applied, the list of regions may include all neighboring
     *          regions. To avoid infinite recursion, a list of visited regions is
     *          maintained.
     *
     * @param   regions the list of regions for which the closure will be applied.
     *
     * @throws  IllegalArgumentException    if the list of regions is empty.
     *
     * @pre     The list of regions passed as a parameter is not empty.
     * @post    The closure between the current region and the neighboring regions
     *          passed as a parameter has been applied. If a hard lockdown is
     *          being applied, the closure between the current region and all
     *          neighboring regions has been applied. The map of closures for all
     *          affected regions has been updated.
     */
    public void applyClosure(List<Region> regions) {
        if (regions == null)
            throw new IllegalArgumentException("The list of regions cannot be " +
                    "null");
        if (regions.isEmpty())
            return;
        if (simulationStep % 2 == 0)
            inhabitants -= externalPopulation();
        Set<Region> visited = new HashSet<>();
        for (Region region : regions)
            if (isNeighboringWith(region) && !region.equals(this)) {
                regionClosures.put(region, true);
                if (!visited.contains(region)) {
                    visited.add(region);
                    List<Region> regionsToVisit = new ArrayList<>(regions);
                    regionsToVisit.remove(this);
                    region.applyClosure(regionsToVisit);
                }
            }
    }

    /**
     * @brief   Relaxes (stops applying) the closure to a number of specified
     *          regions.
     *
     * @details This method relaxes the closure in the regions passed as a
     *          parameter, setting the corresponding value in the map of closures
     *          to false. It also propagates this closure change to all
     *          neighboring regions, recursively applying the relaxClosure method.
     *          A list of visited regions is used to avoid infinite recursion.
     *
     * @param   regions the list of regions for which to relax the closure.
     *
     * @throws  IllegalArgumentException    if the list of regions is empty.
     *
     * @pre     The parameter regions is not null.
     * @post    The closure in the specified regions has been relaxed and this
     *          closure change has been propagated to all neighboring regions.
     *          Consequently, the map of closures has been updated.
     */
    public void relaxClosure(List<Region> regions) {
        if (regions == null)
            throw new IllegalArgumentException("The list of regions cannot be " +
                    "null");
        if (regions.isEmpty())
            return;
        Set<Region> visited = new HashSet<>();
        for (Region region : regions)
            if (isNeighboringWith(region) && !region.equals(this)) {
                regionClosures.put(region, false);
                if (!visited.contains(region)) {
                    visited.add(region);
                    List<Region> regionsToVisit = new ArrayList<>(regions);
                    regionsToVisit.remove(this);
                    region.applyClosure(regionsToVisit);
                }
            }
    }

    /**
     * @brief   Applies a hard lockdown to the region.
     *
     * @details Modifies the reduced internal mobility and applies a closure with
     *          all neighboring regions.
     *
     * @param   confinement object containing the mobility reduction to apply
     *                      and the time to apply it.
     *
     * @pre     --
     * @post    The reduced internal mobility of the region has been modified
     *          and a closure has been applied to the region and all neighboring
     *          regions in relation to this.
     */
    public void applyHardLockdown(Confinement confinement) {
        reducedMobility = new Pair<>(confinement.getMobilityReduction(),
                confinement.getDuration());
        applyClosure(new ArrayList<>(neighboringRegions.keySet()));
    }

    /**
     * @brief   Relaxes (stops applying) the lockdown in a region.
     *
     * @details Modifies the reduced internal mobility and relaxes the
     *          closure (if it exists) with all neighboring regions.
     *
     * @pre     --
     * @post    The reduced internal mobility of the region is equal to 0 and the
     *          closure (if applied) has been relaxed in all neighboring regions.
     */
    public void relaxLockdown() {
        reducedMobility = new Pair<>(0, 0);
        relaxClosure(new ArrayList<>(neighboringRegions.keySet()));
    }

    /**
     * @param   virus           The virus for which to get the state of the region.
     * @param   simulationStep  The simulation step for which to get the state of
     *                          the region.
     *
     * @throws  IllegalArgumentException    if the virus is not present in the
     *                                      region.
     * @throws  IndexOutOfBoundsException   if the value of simulationStep is
     *                                      greater than the number of saved
     *                                      states.
     *
     * @return  a string with the state of the region for a given virus at a
     *          specific simulation step.
     */
    public String regionState(Virus virus, int simulationStep) {
        if (!states.containsKey(virus))
            throw new IllegalArgumentException("There is no state of the region " +
                    "for this virus.");
        List<RegionState> virusStates = states.get(virus);
        if (simulationStep < 0 || simulationStep > virusStates.size())
            throw new IndexOutOfBoundsException("Invalid simulation step.");
        StringBuilder sb = new StringBuilder();
        sb.append("******************************************************\n");
        sb.append("REGION ").append(name.toUpperCase()).append(" SITUATION FOR THE VIRUS ")
                .append(virus.getName().toUpperCase()).append(" AT SIMULATION STEP ").append(simulationStep - 1)
                .append("\n");
        RegionState state = virusStates.get(simulationStep - 1);
        sb.append(state);
        return sb.toString();
    }

    /**
     * @param   virus           The virus for which to get the state of the
     *                          region.
     * @param   simulationStep  The simulation step for which to get the state.
     *
     * @throws  IllegalArgumentException    if the virus is not present in the
     *                                      region.
     * @throws  IndexOutOfBoundsException   if the value of simulationStep is
     *                                      greater than the number of saved
     *                                      states.
     *
     * @return  the state of the region for the given virus and simulation step.
     */
    private RegionState getRegionState(Virus virus, int simulationStep) {
        if (!states.containsKey(virus))
            throw new IllegalArgumentException("There is no state of the region " +
                    "for this virus.");
        List<RegionState> virusStates = states.get(virus);
        if (simulationStep < 0 || simulationStep > virusStates.size())
            throw new IndexOutOfBoundsException("Invalid simulation step.");
        return virusStates.get(simulationStep - 1);
    }

    /**
     * @param   virus           The virus for which to get the totals of the
     *                          region.
     *
     * @throws  IllegalArgumentException    if there is no state for this specific
     *                                      virus.
     *
     * @return  a string with the totals of the region for a given virus.
     */
    public String getRegionTotals(Virus virus) {
        if (!states.containsKey(virus))
            throw new IllegalArgumentException("There is no state of the region " +
                    "for this virus.");
        StringBuilder sb = new StringBuilder();
        sb.append("******************************************************\n");
        sb.append("TOTALS FOR REGION ").append(name.toUpperCase()).append(" FOR THE VIRUS ")
                .append(virus.getName().toUpperCase()).append("\n");
        RegionState state = new RegionState(simulationStep, inhabitants,
                getTotalNumInfected(virus), getTotalNumContagious(virus),
                getTotalNumCured(virus), getTotalNumSick(virus),
                getTotalNumDeaths(virus), getTotalNumVaccinated(virus),
                calculateTransmissionRate(virus), calculateMortalityRate(virus));
        sb.append(state);
        return sb.toString();
    }

    /** @brief  Class representing the state of a region at a specific moment
     *          in the simulation for a specific virus. */
    private static class RegionState {
        /** @brief  Simulation step in which the region is. */
        private final int simulationStep;
        /** @brief  Number of inhabitants of the region. */
        private final int numInhabitants;
        /** @brief  Number of inhabitants infected in the region. */
        private final int infected;
        /** @brief  Number of inhabitants contagious in the region. */
        private final int contagious;
        /** @brief  Number of inhabitants sick in the region. */
        private final int sick;
        /** @brief  Number of inhabitants immune in the region. */
        private final int immune;
        /** @brief  Number of inhabitants dead in the region. */
        private final int deaths;
        /** @brief  Number of inhabitants vaccinated in the region. */
        private final int vaccinated;
        /** @brief  Transmission rate of the virus in the region. */
        private final double transmissionRate;
        /** @brief  Mortality rate of the virus in the region. */
        private final double mortalityRate;

        /**
         * @brief   Constructor of the RegionState class.
         *
         * @param   simulationStep      The simulation step in which the region
         *                              is.
         * @param   numInhabitants      The number of inhabitants of the region.
         * @param   infected            The number of inhabitants infected in
         *                              the region.
         * @param   contagious          The number of inhabitants contagious in
         *                              the region.
         * @param   sick                The number of inhabitants sick in the
         *                              region.
         * @param   immune              The number of inhabitants immune in the
         *                              region.
         * @param   deaths              The number of inhabitants dead in the
         *                              region.
         * @param   vaccinated          The number of inhabitants vaccinated in
         *                              the region.
         * @param   transmissionRate    The transmission rate of the virus in
         *                              the region.
         * @param   mortalityRate       The mortality rate of the virus in the
         *                              region.
         */
        private RegionState(int simulationStep, int numInhabitants, int infected,
                            int contagious, int immune, int sick,
                            int deaths, int vaccinated, double transmissionRate,
                            double mortalityRate) {
            this.simulationStep = simulationStep;
            this.numInhabitants = numInhabitants;
            this.infected = infected;
            this.contagious = contagious;
            this.sick = sick;
            this.immune = immune;
            this.deaths = deaths;
            this.vaccinated = vaccinated;
            this.transmissionRate = transmissionRate;
            this.mortalityRate = mortalityRate;
        }

        /** @return a string with the current state information of the region. */
        @Override
        public String toString() {
            String separator =
                    "******************************************************";
            String format = "* %-28s %21s *\n";
            return separator + "\n" +
                    String.format(format, "Simulation step", simulationStep) +
                    String.format(format, "Number of inhabitants", numInhabitants) +
                    String.format(format, "Number of infected", infected) +
                    String.format(format, "Number of contagious", contagious) +
                    String.format(format, "Number of sick", sick) +
                    String.format(format, "Number of immune", immune) +
                    String.format(format, "Number of deaths", deaths) +
                    String.format(format, "Number of vaccinated", vaccinated) +
                    String.format(format, "Transmission rate", transmissionRate + "%") +
                    String.format(format, "Mortality rate", mortalityRate + "%") +
                    separator + "\n";
        }
    }
}
