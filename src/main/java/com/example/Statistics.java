package main.java.com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * @class   Statistics
 * @brief   Stores all the statistics maps.
 */
public class Statistics {

    /** @brief  Incubation of the infected for each virus. */
    private final List<Pair<Integer, Integer>> infected;
    /** @brief  The total number of infected since the simulation started. */
    private Integer totalInfected;
    /** @brief  Latency of the infected for each virus. */
    private final List<Pair<Integer, Integer>> latents;
    /** @brief  Contagion of the infected for each virus. */
    private final List<Pair<Integer, Integer>> contagious;
    /** @brief  The total number of contagious since the simulation started. */
    private Integer totalContagious;
    /** @brief  Sick people for each virus. */
    private final List<Pair<Integer, Integer>> sick;
    /** @brief  The total number of sick people since the simulation started. */
    private  Integer totalSick;
    /** @brief  Immune people for each virus. */
    private final List<Pair<Integer, Integer>> immune;
    /** @brief Number of deaths for each virus. */
    private Integer deaths;
    /** @brief  Number of cured people for each virus. */
    private Integer cured;
    /** @brief  The total number of vaccinated people since the simulation started. */
    private Integer totalVaccinated;

    public Statistics() {
        infected = new ArrayList<>();
        totalInfected = 0;
        latents = new ArrayList<>();
        contagious = new ArrayList<>();
        totalContagious = 0;
        sick = new ArrayList<>();
        totalSick = 0;
        immune = new ArrayList<>();
        deaths = 0;
        cured = 0;
        totalVaccinated = 0;
    }

    /** @return A list of the infected for a virus. */
    public List<Pair<Integer, Integer>> getInfected() { return infected; }

    /** @return The total infected for a virus. */
    public Integer getTotalInfected() { return totalInfected; }

    /** @return A list of the latency of the infected for a virus. */
    public List<Pair<Integer, Integer>> getLatents() { return latents; }

    /** @return A list of the contagious for a virus. */
    public List<Pair<Integer, Integer>> getContagious() { return contagious; }

    /** @return The total contagious. */
    public Integer getTotalContagious() { return totalContagious; }

    /** @return A list of the sick for a virus. */
    public List<Pair<Integer, Integer>> getSick() { return sick; }

    /** @return The total sick. */
    public Integer getTotalSick() { return totalSick; }

    /** @return A list of the immune for a virus. */
    public List<Pair<Integer, Integer>> getImmune() { return immune; }

    /** @return The total number of deaths. */
    public Integer getDeaths() { return deaths; }

    /** @return The number of cured people for the virus. */
    public Integer getCured() { return cured; }

    /** @return The total number of vaccinated. */
    public Integer getTotalVaccinated() { return totalVaccinated; }

    /**
     * @param   totalInfected   Integer with the total of infected people.
     *
     * @pre:    --
     * @post:   Updates the total number of infected people.
     */
    void increaseInfected(Integer totalInfected) {
        this.totalInfected += totalInfected;
    }

    /**
     * @param   totalContagious Integer with the total of contagious people.
     *
     * @pre:    --
     * @post:   Updates the total number of contagious.
     */
    void increaseContagious(Integer totalContagious) {
        this.totalContagious += totalContagious;
    }

    /**
     * @param   totalSick   Integer with the total of sick people.
     *
     * @pre:    --
     * @post:   Updates the total number of sick people.
     */
    void increaseSick(Integer totalSick) { this.totalSick += totalSick; }

    /**
     * @param   deaths  Integer with the total deaths.
     *
     * @pre:    --
     * @post:   Updates the total number of deaths.
     */
    void setDeaths(Integer deaths) { this.deaths = deaths; }

    /**
     * @param   cured   Integer with the total cured.
     *
     * @pre:    --
     * @post:   Updates the total number of cured people.
     */
    void setCured(Integer cured) { this.cured = cured; }

    /**
     * @param   totalVaccinated Integer with the total vaccinated.
     *
     * @pre:    --
     * @post:   Updates the total number of vaccinated people.
     */
    void increaseVaccinated(Integer totalVaccinated) {
        this.totalVaccinated += totalVaccinated;
    }
}
