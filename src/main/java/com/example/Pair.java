package main.java.com.example;

/**
 * @class   Pair
 * @brief   Class that implements a pair of two elements, one of type S and
 *          the other of type T.
 *
 * @details It is immutable, meaning that once a Pair object is created, it is
 *          not possible to modify its attributes.
 *
 * @param   <S> Type of the first parameter element.
 * @param   <T> Type of the second parameter element.
 */
public class Pair<S, T> {
    /** @brief  The first element of the pair. */
    public S first;
    /** @brief  The second element of the pair. */
    public T second;

    /**
     * @brief   Constructs a pair from the given values.
     *
     * @param   first   The first element of the pair of type S.
     * @param   second  The second element of the pair of type T.
     */
    public Pair(S first, T second) {
        this.first = first;
        this.second = second;
    }

    /**
     * @param   o   Object to compare with the current pair.
     *
     * @return  True if the two objects are equal.
     */
    @Override
    public boolean equals(Object o) {
        boolean r = false;
        if (o instanceof Pair<?, ?>) {
            Pair<?, ?> p = (Pair<?, ?>) o;
            r = this.first.equals(p.first) && this.second.equals(p.second);
        }
        return r;
    }

    /** @return The hash value of the pair. */
    @Override
    public int hashCode() {
        return this.first.hashCode() + this.second.hashCode();
    }
}
