package org.lsmr.vending.frontend1;

import java.util.List;

/**
 * This interface specifies a set of event callbacks that your vending machine
 * implementation has to implement. The frontend calls these callbacks when the
 * corresponding script commands are encountered. The factory maintains a set of
 * vending machines, indexed from 0 to (number of vending machines - 1).
 */
public interface IVendingMachineFactory {
    /**
     * Constructs a vending machine that accepts a specified set of coin kinds
     * (positive integer denominations) and a certain number of pop selection
     * buttons. Coin kinds must have unique denominations. The constructed
     * vending machine is indexed with the next available number (i.e., the
     * first vending machine has index 0, the second has index 1, etc.). The
     * system starts WITHOUT any vending machine constructed by default.
     * 
     * @param coinKinds
     *            A list of the values to be used for valid coin kinds. The
     *            values must be unique. The values must be positive. Any order
     *            can be used, but the order will be used for later reference.
     * @param selectionButtonCount
     *            The number of selection buttons that the vending machine
     *            should have. Must be greater than 0.
     * @return The index of the vending machine just constructed.
     * @throws IllegalArgumentException
     *             if any of the coin kinds is not positive or if the selection
     *             button count is not positive
     * @throws IllegalStateException
     *             if the coin kinds do not have unique denominations
     */
    public int constructNewVendingMachine(List<Integer> coinKinds, int selectionButtonCount);

    /**
     * Configures the indicated vending machine to use the indicated names and
     * costs for the pop kinds related to the selection buttons at the indexed
     * position. For example, the string at position 0 of popNames is to be used
     * as the name of the pops loaded into the vending machine that would be
     * vended by selection button 0. Pop names and pop costs DO NOT need to be
     * unique.
     * 
     * @param vmIndex
     *            The index of the vending machine to configure.
     * @param popNames
     *            A list of the names to use for the pops. The length must equal
     *            the number of selection buttons in the machine. Each index
     *            position in the list corresponds to the selection button with
     *            the same index. Names must be legal names. The same name can
     *            be used for more than one position.
     * @param popCosts
     *            A list of the costs to use for the pops. The length must equal
     *            the number of selection buttons in the machine. Each index
     *            position in the list corresponds to the selection button with
     *            the same index. Costs must be greater than 0. The same cost
     *            can be used for more than one position.
     * @throws IndexOutOfBoundsException
     *             If the index is less than 0 or greater than or equal to the
     *             number of vending machines currently constructed.
     * @throws IllegalArgumentException
     *             if the arguments are null, if the number of elements in
     *             either list is not identical to the number of selection
     *             buttons for the vending machine, if a pop name is not a
     *             string at least 1 character long, or if a pop cost is not a
     *             positive integer.
     */
    public void configureVendingMachine(int vmIndex, List<String> popNames, List<Integer> popCosts);

    /**
     * A set of coins is ADDED to the indicated vending machine. Note that it is
     * legal to load coins of the wrong kind into the location intended for
     * another coin kind; this will lead to incorrect coins being returned at
     * times, but the vending machine will otherwise function normally.
     * 
     * @param vmIndex
     *            The index of the vending machine to use.
     * @param coinKindIndex
     *            The index of the coin kinds in which to add the coins.
     * @param coins
     *            A sequence of the coins to add to the machine.
     * @throws IndexOutOfBoundsException
     *             If the vmIndex is less than 0 or greater than or equal to the
     *             number of vending machines currently constructed; or if the
     *             coinKindIndex is less than 0 or greater than or equal to the
     *             number of coin kinds in the indicated vending machine.
     * @throws IllegalArgumentException
     *             If any of the counts are negative
     * @throws NullPointerException
     *             If any of the arguments is null
     */
    public void loadCoins(int vmIndex, int coinKindIndex, Coin... coins);

    /**
     * A set of pops is ADDED to the indicated vending machine. Note that it is
     * legal to load pops of the wrong brand into the location intended for
     * another pop brand; this will lead to incorrect pops being returned at
     * times, but the vending machine will otherwise function normally.
     * 
     * @param vmIndex
     *            The index of the vending machine to use.
     * @param popKindIndex
     *            The index of the pop kinds in which to add the pops.
     * @param pops
     *            A sequence of the pops to add to the machine.
     * @throws IndexOutOfBoundsException
     *             If the vmIndex is less than 0 or greater than or equal to the
     *             number of vending machines currently constructed; or if the
     *             popKindIndex is less than 0 or greater than or equal to the
     *             number of coin kinds in the indicated vending machine.
     * @throws IllegalArgumentException
     *             If any of the counts are negative
     * @throws NullPointerException
     *             If either of the arguments is null
     */
    public void loadPops(int vmIndex, int popKindIndex, Pop... pops);

    /**
     * Called to remove all coins and pops from the vending machine. The
     * returned list should have exactly three items in it in this order: a list
     * of the unused coins for change; a list of the coins used for payment; a
     * list of the unsold pops. Should not prevent the vending machine from
     * continuing to function.
     * 
     * @param vmIndex
     *            The index of the vending machine to use.
     * @return A list of the removed coins and pops, as described above.
     * @throws IndexOutOfBoundsException
     *             If the vmIndex is less than 0 or greater than or equal to the
     *             number of vending machines currently constructed.
     */
    public List<List<?>> unloadVendingMachine(int vmIndex);

    /**
     * Called to remove all coins and pops from the delivery chute of the
     * vending machine, returning them in whatever order is convenient (as
     * {@link Coin} and {@link Pop} instances). Should not prevent the vending
     * machine from continuing to function.
     * 
     * @param vmIndex
     *            The index of the vending machine to use.
     * @return A list of removed coins and pops from the delivery chute, as
     *         described above.
     * @throws IndexOutOfBoundsException
     *             If the vmIndex is less than 0 or greater than or equal to the
     *             number of vending machines currently constructed.
     */
    public List<Deliverable> extractFromDeliveryChute(int vmIndex);

    /**
     * Called to insert a coin in a vending machine.
     * 
     * @param vmIndex
     *            The index of the vending machine used.
     * @param coin
     *            The coin inserted in the machine. Cannot be null.
     * @throws IndexOutOfBoundsException
     *             If the vmIndex is less than 0 or greater than or equal to the
     *             number of vending machines currently constructed.
     * @throws NullPointerException
     *             If the coin argument is null.
     */
    public void insertCoin(int vmIndex, Coin coin);

    /**
     * Press the specified button on the specified vending machine. Buttons are
     * numbered starting from 0.
     * 
     * @param vmIndex
     *            The index of the vending machine used.
     * @param value
     *            The index of the button pressed
     * @throws IndexOutOfBoundsException
     *             If the vmIndex is less than 0 or greater than or equal to the
     *             number of vending machines currently constructed.
     */
    public void pressButton(int vmIndex, int value);
}
