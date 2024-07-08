package main.java.com.example;

/**
 * @class   Family
 * @brief   Represents a family of related viruses.
 */
public class Family {
    /** @brief  name of the family */
    private final String name;
    /** @brief  maximum variation percentage of the family. */
    private int maxVariationPercentage;

    /**
     * @param   name    the name of the virus family.
     *
     * @pre:    --
     * @post:   Creates a family of viruses with the given name and an empty
     *          list of viruses.
     */
    public Family(String name, int maxVariationPercentage) {
        this.name = name;
        this.maxVariationPercentage = maxVariationPercentage;
    }

    /**
     * @return  The name of the virus family.
     */
    public String getName() {
        return name;
    }

    /**
     * @return  The maximum variation percentage of the family.
     */
    public int getMaxVariationPercentage() {
        return maxVariationPercentage;
    }

    /**
     * @pre:    --
     * @post:   Sets the maximum variation percentage of the family.
     *
     */
    public void setMaxVariationPercentage(int maxVariationPercentage) {
        this.maxVariationPercentage = maxVariationPercentage;
    }

}
