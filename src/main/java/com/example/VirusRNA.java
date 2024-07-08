package main.java.com.example;

import java.util.Random;

/**
 * @class   VirusRNA
 * @brief   Models an RNA virus
 * @extends Virus
 */
public class VirusRNA extends Virus {

    /** @brief Probability of mutation due to copy error. */
    private double mutationProbabilityCopyError;

    /** @brief Probability of mutation due to coincidence. */
    private double mutationProbabilityCoincidence;

    /**
     * @brief   Creates a copy of the given RNA virus.
     *
     * @param   virus   The RNA virus to copy.
     */
    public VirusRNA(VirusRNA virus) {
        super(virus.getName(), virus.getFamily(), virus.getDiseaseProbability(),
                virus.getIncubationTime(), virus.getLatencyTime(),
                virus.getDiseaseDuration(), virus.getInfectionDuration(),
                virus.getImmunityDuration(), virus.getMortalityRate(),
                virus.getContagionRate());
        this.mutationProbabilityCopyError = virus.getMutationProbabilityCopyError();
        this.mutationProbabilityCoincidence = virus.getMutationProbabilityCoincidence();
    }

    /**
     * @brief   Creates an RNA-type virus.
     *
     * @param   name                        Name of the virus
     * @param   family                      Family of the virus
     * @param   probabilityOfIllness        Probability of developing the illness
     * @param   incubationTime              Incubation time of the virus
     * @param   latencyTime                 Latency time of the virus
     * @param   durationOfIllness           Average duration of the illness (in time units)
     * @param   durationOfInfection         Average duration of the infection (in time units)
     * @param   durationOfImmunity          Average duration of the immunity period (in time units)
     * @param   mortalityRate               Mortality rate of the virus
     * @param   contagionRate               Contagion rate of the virus
     * @param   mutationProbabilityCopyError Probability of mutation due to copy error
     * @param   mutationProbabilityCoincidence Probability of mutation due to coincidence
     *
     * @pre     --
     * @post    Creates an RNA-type virus with the given parameters.
     */
    public VirusRNA(String name, Family family, double probabilityOfIllness, int incubationTime, int latencyTime, int durationOfIllness, int durationOfInfection, int durationOfImmunity, double mortalityRate, double contagionRate, double mutationProbabilityCopyError, double mutationProbabilityCoincidence) {
        super(name, family, probabilityOfIllness, incubationTime, latencyTime, durationOfIllness, durationOfInfection, durationOfImmunity, mortalityRate, contagionRate);
        this.mutationProbabilityCopyError = mutationProbabilityCopyError;
        this.mutationProbabilityCoincidence = mutationProbabilityCoincidence;
    }

    /** @return  Mutation probability due to copy error. */
    public double getMutationProbabilityCopyError() {
        return mutationProbabilityCopyError;
    }

    /** @return  Mutation probability due to coincidence. */
    public double getMutationProbabilityCoincidence() {
        return mutationProbabilityCoincidence;
    }

    /**
     * @param   probability Probability of mutation due to coincidence
     *
     * @pre     --
     * @post    Updates the mutation probability due to coincidence with the
     *          given value.
     */
    public void setMutationProbabilityCoincidence(Double probability) {
        this.mutationProbabilityCoincidence = probability;
    }

    /**
     * @param   probability Probability of mutation due to copy error
     *
     * @pre     --
     * @post    Updates the mutation probability due to copy error with the
     *          given value.
     */
    public void setMutationProbabilityCopyError(Double probability) {
        this.mutationProbabilityCopyError = probability;
    }

    /**
     * @return  A new instance of the Virus class representing the
     *          mutated virus due to copy error with a name assigned according
     *          to the described logic.
     */
    public VirusRNA mutateDueToCopyError() {
        VirusRNA mutatedVirus = new VirusRNA(this);

        // Assign the same family to the mutation.
        Family family = this.getFamily();
        mutatedVirus.setFamily(family);

        // Assign a name to the mutated virus.
        String originalVirusName = this.getName();
        int indexNumerator = originalVirusName.lastIndexOf('1');
        if (indexNumerator == -1) {
            // The original virus name does not have a numerator.
            mutatedVirus.setName(originalVirusName + "1");
        } else {
            // The original virus name has a numerator.
            String baseOriginalVirusName = originalVirusName.substring(0, indexNumerator);
            int originalVirusNumerator = Integer.parseInt(originalVirusName.substring(indexNumerator));
            int mutatedVirusNumerator = originalVirusNumerator + 1;
            mutatedVirus.setName(baseOriginalVirusName + mutatedVirusNumerator);
        }

        Random r = new Random();

        // Apply formula for each parameter.
        mutatedVirus.setDiseaseProbability(this.getDiseaseProbability() *
                applyVariation(r,
                family));

        mutatedVirus.setIncubationTime((int) (this.getIncubationTime() *
                applyVariation(r, family)));

        mutatedVirus.setLatencyTime((int) (this.getLatencyTime() *
                applyVariation(r, family)));

        mutatedVirus.setDiseaseDuration((int) (this.getDiseaseDuration() *
                applyVariation(r
                , family)));

        mutatedVirus.setInfectionDuration((int) (this.getInfectionDuration() *
                applyVariation(r, family)));

        mutatedVirus.setInfectionDuration((int) (this.getImmunityDuration() *
                applyVariation(r, family)));

        mutatedVirus.setMortalityRate(this.getMortalityRate() *
                applyVariation(r, family));

        mutatedVirus.setContagionRate(this.getContagionRate() *
                applyVariation(r, family));

        mutatedVirus.setMutationProbabilityCoincidence
                (this.getMutationProbabilityCoincidence() * applyVariation(r, family));

        mutatedVirus.setMutationProbabilityCopyError
                (this.getMutationProbabilityCopyError() * applyVariation(r, family));

        return mutatedVirus;
    }

    /**
     * @param   r       Random value between 0 and 1.
     * @param   family  The family of the Virus.
     *
     * @return  A double with the variation.
     */
    private double applyVariation(Random r, Family family) {
        double variation;
        variation = 1 + (r.nextDouble() * 2 - 1) * family.getMaxVariationPercentage() / 100;
        return variation;
    }

    /**
     * @param   virusA  the virus A with which the mutation by coincidence occurs
     *
     * @return  A new instance of Virus class representing the mutated virus
     *          C with a name assigned according to the described logic,
     *          resulting from the mutation by coincidence of this virus (B) and
     *          virusA.
     */
    public VirusRNA mutateByCoincidence(VirusRNA virusA) {
        if (virusA.getFamily() != this.getFamily()) {
            throw new IllegalArgumentException("The viruses are not from the same family.");
        }

        VirusRNA virusC = new VirusRNA(this);

        // Assign the same family to virusC.
        virusC.setFamily(virusA.getFamily());

        // Set the name of the mutated virus.
        String mutationName = virusA.getName() + "_" + this.getName();
        virusC.setName(mutationName);

        Random r = new Random();
        double p;

        // Apply formula for each parameter.
        p = r.nextDouble();
        virusC.setDiseaseProbability(p * virusA.getDiseaseProbability()
                + (1 - p) * this.getDiseaseProbability());

        p = r.nextDouble();
        virusC.setIncubationTime((int) (p * virusA.getIncubationTime()
                + (1 - p) * this.getIncubationTime()));

        p = r.nextDouble();
        virusC.setLatencyTime((int) (p * virusA.getLatencyTime()
                + (1 - p) * this.getLatencyTime()));

        p = r.nextDouble();
        virusC.setDiseaseDuration((int) (p * virusA.getDiseaseDuration()
                + (1 - p) * this.getDiseaseDuration()));

        p = r.nextDouble();
        virusC.setInfectionDuration((int) (p * virusA.getInfectionDuration()
                + (1 - p) * this.getInfectionDuration()));

        p = r.nextDouble();
        virusC.setImmunityDuration((int) (p * virusA.getImmunityDuration()
                + (1 - p) * this.getImmunityDuration()));

        p = r.nextDouble();
        virusC.setMortalityRate(p * virusA.getMortalityRate()
                + (1 - p) * this.getMortalityRate());

        p = r.nextDouble();
        virusC.setContagionRate(p * virusA.getContagionRate()
                + (1 - p) * this.getContagionRate());

        p = r.nextDouble();
        virusC.setMutationProbabilityCopyError
                (p * virusA.getMutationProbabilityCopyError()
                        + (1 - p) * this.getMutationProbabilityCopyError());

        p = r.nextDouble();
        virusC.setMutationProbabilityCoincidence
                (p * virusA.getMutationProbabilityCoincidence()
                        + (1 - p) * this.getMutationProbabilityCoincidence());

        return virusC;
    }
}
