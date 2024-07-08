package main.java.com.example;

/**
 * @class   Confinement
 * @brief
 */
public class Confinement {
    /** @brief  duration of the confinement. */
    private final int duration;
    /** @brief  reduction of the mobility in the region in question. */
    private final int mobilityReduction;

    /**
     * @brief   Confinement measures in a region.
     *
     * @param   duration            duration of the confinement.
     * @param   mobilityReduction   reduction of the mobility in the region in
     *                              question.
     */
    Confinement(int duration, int mobilityReduction){
        this.duration = duration;
        this.mobilityReduction = mobilityReduction;
    }

    /**
     * @return  The duration of the confinement.
     */
    public int getDuration(){
        return duration;
    }

    /**
     * @return  The mobility reduction of the confinement.
     */
    public int getMobilityReduction(){
        return mobilityReduction;
    }
}
