package main.java.com.example;

/**
 * @class   main.java.com.example.Vaccine
 * @brief   A vaccine.
 *
 * @details Contains attributes such as the name, effectiveness percentage,
 *          duration, and the virus it is intended for.
 */
public class Vaccine {
    /** @brief  The name of the vaccine. */
    private final String name;
    /** @brief  The type of the vaccine. */
    private final String type;
    /** @brief  The virus the vaccine is intended for. */
    private final Virus targetVirus;
    /** @brief  The effectiveness percentage of the vaccine. */
    private final Integer effectiveness;
    /** @brief  The activation time of the vaccine. */
    private final int activationTime;
    /** @brief  The duration of the vaccine. */
    private final int duration;
    /** @brief  The percentage reduction in the mortality rate among the
                infected. */
    private int mortalityRateReduction;
    /** @brief  The percentage reduction in the duration of the disease. */
    private int diseaseDurationReduction;
    /** @brief  The percentage reduction in the probability of developing the
                disease. */
    private int diseaseProbabilityReduction;
    /** @brief  The percentage reduction in the contagion rate. */
    private int contagionRateReduction;

    /**
     * @brief   Creates an inhibiting vaccine from the given parameters.
     *
     * @param   name            The name of the vaccine.
     * @param   type            The type of the vaccine (inhibiting or
     *                          attenuating).
     * @param   targetVirus     The virus the vaccine is intended for.
     * @param   effectiveness   The effectiveness percentage of the vaccine.
     * @param   activationTime  The activation time of the vaccine.
     * @param   duration        The duration of the vaccine's effect.
     */
    public Vaccine(String name, String type, Virus targetVirus, Integer effectiveness,
                   int activationTime, int duration) {
        this.name = name;
        this.type = type;
        this.targetVirus = targetVirus;
        this.effectiveness = effectiveness;
        this.activationTime = activationTime;
        this.duration = duration;
        this.diseaseProbabilityReduction = 100;
        this.mortalityRateReduction = 0;
        this.diseaseDurationReduction = 0;
        this.contagionRateReduction = 0;
    }

    /**
     * @brief   Creates an attenuating vaccine from the given parameters.
     *
     * @param   name                        The name of the vaccine.
     * @param   type                        The type of the vaccine (inhibiting
     *                                      or attenuating).
     * @param   targetVirus                 The virus the vaccine is intended for.
     * @param   activationTime              The activation time of the vaccine.
     * @param   duration                    The duration of the vaccine's effect.
     * @param   mortalityRateReduction      The percentage reduction in mortality
     *                                      rate.
     * @param   diseaseDurationReduction    The percentage reduction in disease
     *                                      duration.
     * @param   diseaseProbabilityReduction The percentage reduction in disease
     *                                      probability.
     * @param   contagionRateReduction      The percentage reduction in
     *                                      contagion rate.
     */
    public Vaccine(String name, String type, Virus targetVirus,
                   int activationTime, int duration,
                   Integer mortalityRateReduction,
                   Integer diseaseDurationReduction,
                   Integer diseaseProbabilityReduction,
                   Integer contagionRateReduction) {
        this(name, type, targetVirus, null, activationTime, duration);
        this.mortalityRateReduction = mortalityRateReduction;
        this.diseaseDurationReduction = diseaseDurationReduction;
        this.diseaseProbabilityReduction = diseaseProbabilityReduction;
        this.contagionRateReduction = contagionRateReduction;
    }

    /** @return the name of the vaccine. */
    public String getName() {
        return name;
    }

    /** @return the type of the vaccine. */
    public String getType() {
        return type;
    }

    /** @return the expected effectiveness percentage of the vaccine. */
    public Integer getEffectiveness() {
        return effectiveness;
    }

    /** @return the duration of the vaccine's effect in time units. */
    public int getDuration() {
        return duration;
    }

    /**
     * @return the time needed for the vaccine to take effect in the specified
     *         time units.
     */
    public int getActivationTime() {
        return activationTime;
    }

    /** @return the virus the vaccine is intended for. */
    public Virus getTargetVirus() {
        return targetVirus;
    }

    /** @return the percentage reduction in mortality rate. */
    public int getMortalityRateReduction() {
        return mortalityRateReduction;
    }

    /** @return the percentage reduction in disease duration. */
    public int getDiseaseDurationReduction() {
        return diseaseDurationReduction;
    }

    /** @return the percentage reduction in the probability of disease. */
    public int getDiseaseProbabilityReduction() {
        return diseaseProbabilityReduction;
    }

    /** @return the percentage reduction in contagion rate. */
    public int getContagionRateReduction() {
        return contagionRateReduction;
    }
}
