package org.lsmr.vending.frontend1;

/**
 * Instances of this class represent cans (or bottles) of pop.
 */
public class Pop implements Deliverable {
    private String name;

    /**
     * Basic constructor.
     * 
     * @param name
     *            The name of the pop (i.e., its brand). Cannot be null. Cannot
     *            be an empty string.
     * @throws IllegalArgumentException
     *             If the argument is null or an empty string.
     */
    public Pop(String name) {
	if(name == null || name.length() == 0)
	    throw new IllegalArgumentException("The argument cannot be null or an empty string");

	this.name = name;
    }

    /**
     * Accessor for the name of the pop.
     * 
     * @return The name of the pop. Should never be null or an empty string.
     */
    public String getName() {
	return name;
    }
    
    @Override
    public String toString() {
	return getName();
    }
}
