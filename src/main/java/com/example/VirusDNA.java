package main.java.com.example;

/**
 * @class   VirusDNA
 * @brief   Models a DNA virus, extending the Virus class and adding specific
 *          attributes and methods.
 * @extends Virus
 */
public class VirusDNA extends Virus {

    /**
     * @brief   Creates a DNA-type virus.
     *
     * @param   name                    Name of the virus.
     * @param   family                  Family of the virus.
     * @param   probabilityOfIllness    Probability of developing the illness.
     * @param   incubationTime          Incubation time of the virus.
     * @param   latencyTime             Latency time of the virus.
     * @param   durationOfIllness       Average duration of the illness (in time
     *                                  units).
     * @param   durationOfInfection     Average duration of the infection (in
     *                                  time units).
     * @param   durationOfImmunity      Average duration of the immunity period
     *                                  (in time units).
     * @param   mortalityRate           Mortality rate of the virus.
     * @param   contagionRate           Contagion rate of the virus.
     *
     * @pre     --
     * @post    Creates a DNA-type virus with the given parameters.
     */
    public VirusDNA(
            String name, Family family, double probabilityOfIllness,
            int incubationTime, int latencyTime, int durationOfIllness,
            int durationOfInfection, int durationOfImmunity, double mortalityRate,
            double contagionRate) {
        super(name, family, probabilityOfIllness, incubationTime, latencyTime,
                durationOfIllness, durationOfInfection, durationOfImmunity,
                mortalityRate, contagionRate);
    }
}
