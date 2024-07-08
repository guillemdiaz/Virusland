package main.java.com.example;

import java.util.Objects;

/**
 * @class   Virus
 * @brief   Models a virus
 */
public class Virus {

    /** @brief  Name of the virus. */
    private String name;
    /** @brief  Contagion rate of the virus. */
    private double contagionRate;
    /** @brief  Mortality rate of the virus. */
    private double mortalityRate;
    /** @brief  Incubation time of the virus. */
    private int incubationTime;
    /** @brief  Latency time of the virus. */
    private int latencyTime;
    /** @brief  Average duration of the disease (in time units). */
    private int diseaseDuration;
    /** @brief  Average duration of the infection (in time units). */
    private int infectionDuration;
    /** @brief  Average duration of the immunity period (in time units). */
    private int immunityDuration;
    /** @brief  Probability of developing the disease. */
    private double diseaseProbability;
    /** @brief  Family of the virus. */
    private Family family;

    /**
     * @pre     --
     * @post    Creates a virus with default parameter values.
     */
    public Virus() {
        this.name = "Unknown";
        this.contagionRate = 0.0;
        this.mortalityRate = 0.0;
        this.incubationTime = 0;
        this.latencyTime = 0;
        this.diseaseDuration = 0;
        this.infectionDuration = 0;
        this.immunityDuration = 0;
        this.diseaseProbability = 0.0;
    }

    /**
     * @param   name                Name of the virus.
     * @param   family              Family of the virus.
     * @param   diseaseProbability  Probability of developing the disease.
     * @param   incubationTime      Incubation time of the virus.
     * @param   latencyTime         Latency time of the virus.
     * @param   diseaseDuration     Average duration of the disease (in time
     *                              units).
     * @param   infectionDuration   Average duration of the infection (in
     *                              time units).
     * @param   immunityDuration    Average duration of the immunity period
     *                              (in time units).
     * @param   contagionRate       Contagion rate of the virus.
     * @param   mortalityRate       Mortality rate of the virus.
     *
     * @pre     --
     * @post    Creates a virus with the given parameters.
     */
    public Virus(String name, Family family, double diseaseProbability,
                 int incubationTime, int latencyTime, int diseaseDuration,
                 int infectionDuration, int immunityDuration, double mortalityRate,
                 double contagionRate) {
        this.name = name;
        this.family = family;
        this.diseaseProbability = diseaseProbability;
        this.incubationTime = incubationTime;
        this.latencyTime = latencyTime;
        this.diseaseDuration = diseaseDuration;
        this.infectionDuration = infectionDuration;
        this.immunityDuration = immunityDuration;
        this.contagionRate = contagionRate;
        this.mortalityRate = mortalityRate;
    }

    /**
     * @param   contagionRate   Contagion rate of the virus.
     *
     * @pre     0 <= contagionRate <= 1.
     * @post    Updates the contagion rate of the virus with the given value.
     */
    public void setContagionRate(double contagionRate) {
        this.contagionRate = contagionRate;
    }

    /**
     * @param   mortalityRate   Mortality rate of the virus.
     *
     * @pre     0 <= mortalityRate <= 1.
     * @post    Updates the mortality rate of the virus with the given value.
     */
    public void setMortalityRate(double mortalityRate) {
        this.mortalityRate = mortalityRate;
    }

    /**
     * @param   incubationTime Incubation time of the virus.
     *
     * @pre     incubationTime >= 0.
     * @post    Updates the incubation time of the virus with the given value.
     */
    public void setIncubationTime(int incubationTime) {
        this.incubationTime = incubationTime;
    }

    /**
     * @param   latencyTime Latency time of the virus.
     *
     * @pre     latencyTime >= 0.
     * @post    Updates the latency time of the virus with the given value.
     */
    public void setLatencyTime(int latencyTime) {
        this.latencyTime = latencyTime;
    }

    /**
     * @param   diseaseDuration Average duration of the disease (in time units).
     *
     * @pre     diseaseDuration >= 0.
     * @post    Updates the average duration of the disease with the given
     *          value.
     */
    public void setDiseaseDuration(int diseaseDuration) {
        this.diseaseDuration = diseaseDuration;
    }

    /**
     * @param   infectionDuration Average duration of the infection (in time
     *                            units).
     *
     * @pre     infectionDuration >= 0
     * @post    Updates the average duration of the infection with the given
     *          value.
     */
    public void setInfectionDuration(int infectionDuration) {
        this.infectionDuration = infectionDuration;
    }

    /**
     * @param   immunityDuration Average duration of the immunity (in time
     *                           units).
     *
     * @pre     immunityDuration >= 0.
     * @post    Updates the average duration of the immunity with the given
     *          value.
     */
    public void setImmunityDuration(int immunityDuration) {
        this.immunityDuration = immunityDuration;
    }

    /**
     * @param   diseaseProbability Probability of developing the disease.
     *
     * @pre     diseaseProbability >= 0.
     * @post    Updates the probability of developing the disease with the
     *          given value.
     */
    public void setDiseaseProbability(double diseaseProbability) {
        this.diseaseProbability = diseaseProbability;
    }

    /**
     * @param   family The family of the virus.
     *
     * @pre     --
     * @post    Updates the family of the virus.
     */
    public void setFamily(Family family) {
        this.family = family;
    }

    /**
     * @param   name    Name of the virus.
     *
     * @pre     --
     * @post    Updates the name of the virus.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return  The name of the virus. */
    public String getName() {
        return name;
    }

    /** @return  The family of the virus. */
    public Family getFamily() {
        return family;
    }

    /**
     * @param   virus   The virus to compare with
     *
     * @return  True if both viruses belong to the same family, false otherwise.
     */
    public boolean sameFamily(Virus virus) {
        return this.getFamily() == virus.getFamily();
    }

    /** @return  The contagion rate of the virus. */
    public double getContagionRate() {
        return contagionRate;
    }

    /** @return  The mortality rate of the virus. */
    public double getMortalityRate() {
        return mortalityRate;
    }

    /** @return  The probability of developing the disease. */
    public double getDiseaseProbability() {
        return diseaseProbability;
    }

    /** @return  The incubation time of the virus. */
    public int getIncubationTime() {
        return incubationTime;
    }

    /** @return  The latency time of the virus. */
    public int getLatencyTime() {
        return latencyTime;
    }

    /** @return  The average duration of the disease (in time units). */
    public int getDiseaseDuration() {
        return diseaseDuration;
    }

    /** @return  The average duration of the infection (in time units). */
    public int getInfectionDuration() {
        return infectionDuration;
    }

    /** @return  The average duration of the immunity period (in time units). */
    public int getImmunityDuration() {
        return immunityDuration;
    }

    /**
     * @param   o   The object to compare this Virus with
     *
     * @return  True if the objects are equal, False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Virus virus = (Virus) o;
        return Objects.equals(name, virus.name) && Objects.equals(family, virus.family);
    }

    /** @return  The hash value of this Virus. */
    @Override
    public int hashCode() {
        return Objects.hash(name, family);
    }
}
