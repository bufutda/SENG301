package seng301.assn1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lsmr.vending.frontend1.Coin;
import org.lsmr.vending.frontend1.Deliverable;
import org.lsmr.vending.frontend1.IVendingMachineFactory;
import org.lsmr.vending.frontend1.Pop;
import org.lsmr.vending.frontend1.ScriptProcessor;
import org.lsmr.vending.frontend1.parser.ParseException;

/**
 * This class allows a set of scripts to be read in, executed, and evaluated for
 * correctness. It acts as the frontend of the vending machine system. The
 * backend is not yet implemented. For the methods in the class, each has a
 * "TODO" comment to indicate that you are to replace or to add its
 * implementation.
 * 
 * <p>
 * The frontend (in the package org.lsmr.vending.frontend1) includes a parser
 * for the scripts. Although I have given you its source code, it is a bit
 * complicated; I would suggest that you ignore its internals. In addition, the
 * frontend contains classes to represent coins and pops as well as a factory
 * interface and a class called {@link ScriptProcessor}. DO NOT modify the
 * contents of the frontend in any way. All your changes will happen in the
 * seng301.assn1 package. You should replace the "TODO" comments with
 * implementations. You can add additional classes as you see fit.
 * 
 * <h2>Factory</h2>
 * 
 * The factory keeps track of every vending machine it creates, numbered
 * sequentially from 0.
 * 
 * <h2>Vending machines</h2>
 * 
 * Construction. The vending machines have a set of one or more selection
 * buttons and operate on a set of one or more coin kinds (these details cannot
 * be changed once the machine is constructed). Each coin kind has a unique,
 * positive integer value.
 * 
 * <p>
 * Configuration. Each of the selection buttons corresponds to a kind of pop.
 * The name of each kind of pop and its price can be specified (and changed).
 * Selection buttons can share the same name or the same price or both.
 * 
 * <p>
 * Loading and unloading. The machine can be loaded with a set of coins (for
 * change) and a set of pops (to be sold). These can be unloaded from the
 * machine at any time, along with any money that has been used to buy pops.
 * Coins that the customer has entered before pressing a button will not be
 * unloaded.
 * 
 * <p>
 * Purchasing. Purchasing occurs by inserting an appropriate number of coins
 * into the machine and pressing the appropriate button. If the value of the
 * coins is sufficient to pay for the pop, the pop is dispensed and any change
 * owing is returned. If the desired kind of pop is empty, nothing is returned
 * (including change) and the state does not change. If the cost is higher than
 * the value entered, nothing is returned and the state does not change. There
 * is no coin return button on this version of the machine.
 * 
 * <p>
 * Extracting pop and change. Pops and change are delivered to a delivery chute.
 * These need to be extracted explicitly, else they remain there, accumulating.
 * 
 * <P>
 * Checking. The contents extracted from the delivery chute and the contents
 * extracted from inside the machine can be checked against expectations. The
 * frontend is implemented to deal with this when the commands are issued to do
 * so.
 * 
 * <p>
 * Physical limits. This simulation does not need to be realistic, in that no
 * physical limits are imposed. An effectively boundless number of coins and
 * pops can exist, be moved around, and be stored.
 * 
 * <h2>The Scripts</h2>
 * 
 * Scripts support a simple language consisting of 10 commands.
 * 
 * <p>
 * In the syntax below, tokens are specified as surrounded by quotation marks
 * and as the special token kinds &lt;STRING&gt; and &lt;INTEGER&gt;. Zero or
 * more characters of whitespace (i.e., blank spaces, tabs, carriage returns,
 * etc.) can appear between tokens, as in Java. A &lt;STRING&gt; represents a
 * string literal, which is any sequence of characters surrounded by quotation
 * marks; a backslash is used as an escape character to permit certain special
 * characters to be used in a manner identical to Java. An &lt;INTEGER&gt;
 * represents an integer literal which is either the numeral 0 or any numeral in
 * the range 1-9 followed by zero or more numerals each in the range 0-9, with
 * an optional minus sign.
 *
 * <h3>The construct command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <p>
 * 
 * <pre>
 * "construct" "(" &lt;INTEGER&gt; { "," &lt;INTEGER&gt; } ";" &lt;INTEGER&gt; ")"
 * </pre>
 * 
 * <p>
 * This command takes a sequence of 1 or more comma-separated integers each
 * representing a valid coin kind; there must be at least one valid coin kind.
 * The integer represents the value of the coin kind; each coin kind must have a
 * unique value. Each value must be a positive integer. The final,
 * semicolon-separated integer represents the number of selection buttons; it
 * must be a positive integer.
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * The purpose of this command is to create a vending machine object with the
 * specified characteristics. The created vending machine remains current until
 * and unless another vending machine is created. It is an error to issue any
 * other command prior to construction of a vending machine. The vending machine
 * is identified with an index number in the order of creation, starting from 0.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * construct(1; 1) // 1 coin kind of value "1"; 1 selection button
 * </pre>
 * 
 * <pre>
 * construct(10, 1, 3; 5) // 3 coin kinds of values "10", "1", "3" <b>in that order</b>; 5 selection buttons
 * </pre>
 * 
 * <pre>
 * construct(1, 3, 10; 5) // 3 coin kinds of values "1", "3", "10" <b>in that order</b>; 5 selection buttons
 * </pre>
 * 
 * <pre>
 * construct(1 ; 1) configure(" " ; 1)
 * </pre>
 * 
 * <p>
 * <b>Incorrect examples:</b>
 * 
 * <pre>
 * construct() // error: no coin kinds, no selection buttons
 * </pre>
 * 
 * <pre>
 * construct(0; 0) // error: coin values are not positive, selection button count is not positive
 * </pre>
 * 
 * <pre>
 * construct(1 2 3; 4) // error: commas missing
 * </pre>
 * 
 * <pre>
 * construct(1, 2, 3; 4 // error: closing parenthesis missing
 * </pre>
 * 
 * <pre>
 * configure([0] "a" ; 100) construct(1; 1) // command issue prior to first construct
 * </pre>
 * 
 * <h3>The configure command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "configure" "(" "[" &lt;INTEGER&gt; "]" &lt;STRING&gt; ","  &lt;INTEGER&gt; [ ";" &lt;STRING&gt; ","  &lt;INTEGER&gt; { ";" &lt;STRING&gt; ","  &lt;INTEGER&gt; } ] ")"
 * </pre>
 * 
 * This command takes a sequence of 1 or more semicolon-separated pairs. Each
 * pair consists of a string and an integer. Each string must be non-empty. Each
 * integer must be positive.
 * 
 * <p>
 * <b>Purpose:</b>
 * 
 * <p>
 * The purpose of this command is to configure a vending machine with the names
 * of the products and the prices that correspond to each selection button. It
 * is an error if the number of pairs specified is not identical to the number
 * of selection buttons specified in the most recent construct command.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * configure([0] "a", 1)
 * </pre>
 * 
 * <pre>
 * configure([0] " ", 1; " ", 1)
 * </pre>
 * 
 * <p>
 * <b>Incorrect examples:</b>
 * 
 * <pre>
 * configure() // error: no strings
 * </pre>
 * 
 * <pre>
 * configure([0] a) // error: no quotation marks
 * </pre>
 * 
 * <pre>
 * construct(1; 1) configure([0] " ", 1; " ", 1) // error: too many pairs
 * </pre>
 * 
 * <h3>The coin-load command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "coin-load" "(" "[" &lt;INTEGER&gt; "]" &lt;INTEGER&gt; ";" &lt;INTEGER&gt; "," &lt;INTEGER&gt; ")"
 * </pre>
 * 
 * This command takes three integers, beyond the initial VM index. The first
 * number is an index into the coin kinds, as specified in the construct
 * command. The second is the value of the coins to load there, and the third is
 * the number of coins of that value to load there. Note that it is legal to
 * load the wrong coins into an index intended for other coins. The VM should
 * not worry about analyzing the values of coins that are stored at locations
 * where there is an expectation of the value. (This is intended to simulate
 * human error on the part of a technician who has loaded the machine.)
 * 
 * <p>
 * <b>Purpose:</b>
 * 
 * <p>
 * The purpose of this command is to load the vending machine with a set of
 * coins for change.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * construct(1; 1) coin-load([0] 1, 0) // 0 coins of value "1", not so useful but legal
 * </pre>
 * 
 * <pre>
 * construct(3, 2, 1; 2) coin-load([0] 0; 3, 1) coin-load([0] 1; 2, 0) coin-load([0] 2; 1, 0) // 1 coin of value "3", none of "2" and none of "1"
 * </pre>
 * 
 * <pre>
 * construct(3, 2, 1; 2) coin-load([0] 1; 3, 1) // 1 coin of value "3", but placed at the location for coins of value "2" ... trouble for someone!
 * </pre>
 * 
 * <p>
 * <b>Incorrect examples:</b>
 * 
 * <pre>
 * coin-load([0] 0) // error: only one integer
 * </pre>
 * 
 * <pre>
 * coin-load([0] -1; -1, -1) // error: negative values are not allowed
 * </pre>
 * 
 * <h3>The pop-load command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "pop-load" "(" "[" &lt;INTEGER&gt; "]" &lt;INTEGER&gt; ";" &lt;STRING&gt; "," &lt;INTEGER&gt; ")"
 * </pre>
 * 
 * This command takes three values, beyond the initial VM index. The first value
 * is a non-negative integer indicating the index into the pop kinds where the
 * pops will be stored. The second is the (brand) name of the pops to be used
 * for the pops to be stored there, and the third is the non-negative number of
 * pops to be stored there.
 * 
 * <p>
 * <b>Purpose:</b>
 * 
 * <p>
 * The purpose of this command is to load the vending machine with a set of pop
 * cans.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * construct(1; 1) pop-load([0] 0; "foo", 1) // 1 foo pop to be stored at location 0
 * </pre>
 * 
 * <pre>
 * construct(3, 2, 1; 2) pop-load([0] 1; " ", 1) // 1 " " pop to be stored at location 1
 * </pre>
 * 
 * <p>
 * <b>Incorrect examples:</b>
 * 
 * <pre>
 * pop-load([0] 0) // error: too few arguments
 * </pre>
 * 
 * <pre>
 * pop-load([0] -1; " ", -1) // error: negative values are not allowed
 * </pre>
 * 
 * <pre>
 * construct(3, 2, 1; 2) pop-load([0] 1; "foo") // error: the quantity of pop not specified
 * </pre>
 * 
 * <h3>The unload command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "unload" "(" "[" &lt;INTEGER&gt; "]" ")"
 * </pre>
 * 
 * <p>
 * <b>Purpose:</b>
 * 
 * <p>
 * This command causes the total value of remaining unused coins, total value of
 * stored payment coins, and individual names of unsold pops to be unloaded from
 * the interior of the machine (for checking). The one integer is an index of
 * the VM.
 * 
 * <h3>The extract command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "extract" "(" "[" &lt;INTEGER&gt; "]" ")"
 * </pre>
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * This command causes the current contents of the delivery chute to be removed
 * from the vending machine (for checking).
 * 
 * <h3>The insert command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "insert" "(" "[" &lt;INTEGER&gt; "]" &lt;INTEGER&gt; ")"
 * </pre>
 * 
 * An error will occur if the integer is not positive. The coin will immediately
 * be deposited in the delivery chute if its value does not correspond to a coin
 * kind supported by the current vending machine.
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * The purpose of this command is to insert a coin of the specified value into
 * the machine.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * construct(1; 1) insert([0] 1)
 * </pre>
 * 
 * <pre>
 * construct(5; 1) insert([0] 1) // the coin is immediately returned
 * </pre>
 * 
 * <p>
 * <b>Incorrect examples:</b>
 * 
 * <pre>
 * insert([0] 0) // error: non-positive coin
 * </pre>
 * 
 * <pre>
 * insert([0]) // error: no coin
 * </pre>
 * 
 * <h3>The press command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "press" "(" "[" &lt;INTEGER&gt; "]" &lt;INTEGER&gt; ")"
 * </pre>
 * 
 * After the initial VM index, the other integer has to be non-negative. It is
 * an error if the integer is greater than or equal to the number of pop
 * selection buttons for the current vending machine.
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * The purpose of this command is to simulate the press of a pop selection
 * button.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * construct(1; 1) press([0] 0)
 * </pre>
 * 
 * <p>
 * <b>Incorrect examples:</b>
 * 
 * <pre>
 * press() // Error: no VM index, no button indicated
 * </pre>
 * 
 * <pre>
 * press([1] -1) // Error: number is negative
 * </pre>
 * 
 * <pre>
 * construct(1 ; 1) press([0] 1) // Error: button number is out of range
 * </pre>
 * 
 * <h3>The CHECK_DELIVERY command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "CHECK_DELIVERY" "(" &lt;INTEGER&gt; { "," &lt;STRING&gt; } ")"
 * </pre>
 * 
 * This command is used to check whether your vending machine behaves as
 * expected. This command does not communicate with your vending machine, but
 * checks whether it has already delivered what is expected. It operates on the
 * most recently delivered materials, from whichever VM; hence, it takes no VM
 * index.
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * The integer indicates the expected total value of all coins delivered (for
 * example, as change). The sequence of strings indicates the kinds of pop that
 * are expected to have been delivered.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * CHECK_DELIVERY(0)
 * </pre>
 * 
 * <pre>
 * CHECK_DELIVERY(0, &quot;Coke&quot;)
 * </pre>
 * 
 * <h3>The CHECK_TEARDOWN command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "CHECK_TEARDOWN" "(" &lt;INTEGER&gt; ";" &lt;INTEGER&gt; [ ";" &lt;STRING&gt; { "," &lt;STRING&gt; } ] ")"
 * </pre>
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * This command is used to check what is still inside a vending machine (the one
 * that has most recently been unloaded). The first number indicates the total
 * value of the change still remaining for use. The second number indicates the
 * total value of the coins entered as payment. Note that some coins can be "in
 * limbo": entered in the machine but not yet used for a purchase; such coins
 * are not unloaded and cannot be checked. The two numbers are separated by a
 * semicolon. A sequence of comma-separated strings can follow these numbers. If
 * present, it is separated by a semicolon. Each string represents a kind of pop
 * that is expected to be found.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * CHECK_TEARDOWN(0; 0; "Coke", "Water")
 * </pre>
 * 
 * <pre>
 * CHECK_TEARDOWN(1; 100)
 * </pre>
 */
public class VendingMachineFactory implements IVendingMachineFactory {
	/**
	 * An ArrayList of vending machines
	 */
	private ArrayList<VendingMachine> machines;

	/**
	 * This is the method that is called to run your program.
	 * 
	 * @param args
	 *            This is formal parameter is required, but is ignored. Don't
	 *            use it.
	 * @throws ParseException
	 *             If a script is in the wrong syntax.
	 * @throws IOException
	 *             If a script file cannot be found or read.
	 */
	public static void main(String[] args) throws ParseException, IOException {
		new ScriptProcessor("good-script", new VendingMachineFactory(), true);
		new ScriptProcessor("bad-script1", new VendingMachineFactory(), true);
		new ScriptProcessor("bad-script2", new VendingMachineFactory(), true);
	}

	/**
	 * Basic constructor.
	 */
	public VendingMachineFactory() {
		machines = new ArrayList<VendingMachine>();
	}

	@Override
	public List<Deliverable> extractFromDeliveryChute(int vmIndex) throws IndexOutOfBoundsException {
		if (vmIndex < 0 || vmIndex > machines.size() - 1) {
			throw new IndexOutOfBoundsException("the selected vending machine is not yet constructed");
		}
		List<Deliverable> res = new ArrayList<Deliverable>();
		Coin retc = machines.get(vmIndex).grabCoin();
		while (retc != null) {
			res.add(retc);
			retc = machines.get(vmIndex).grabCoin();
		}
		Pop retp = machines.get(vmIndex).grabPop();
		while (retp != null) {
			res.add(retp);
			retp = machines.get(vmIndex).grabPop();
		}
		return res;
	}

	@Override
	public void insertCoin(int vmIndex, Coin coin) throws IndexOutOfBoundsException, NullPointerException {
		if (vmIndex < 0 || vmIndex > machines.size() - 1) {
			throw new IndexOutOfBoundsException("the selected vending machine is not yet constructed");
		}
		if (coin == null) {
			throw new NullPointerException("coin cannot be null");
		}
		machines.get(vmIndex).putCoin(coin);
	}

	@Override
	public void pressButton(int vmIndex, int value) throws IndexOutOfBoundsException {
		if (vmIndex < 0 || vmIndex > machines.size() - 1) {
			throw new IndexOutOfBoundsException("the selected vending machine is not yet constructed");
		}
		if (value < 0 || value > machines.get(vmIndex).getSelectionButtonCount() - 1) {
			throw new IndexOutOfBoundsException("the button does not exist");
		}
		machines.get(vmIndex).purchase(value);
	}

	@Override
	public int constructNewVendingMachine(List<Integer> coinKinds, int selectionButtonCount)
			throws IllegalArgumentException, IllegalStateException {
		if (machines.add(new VendingMachine(coinKinds, selectionButtonCount))) { // true
																					// if
																					// OK
			return machines.size() - 1; // index of new machine
		} else {
			return -1; // error occurred
		}
	}

	@Override
	public void configureVendingMachine(int vmIndex, List<String> popNames, List<Integer> popCosts)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		if (vmIndex < 0 || vmIndex > machines.size() - 1) {
			throw new IndexOutOfBoundsException("the selected vending machine is not yet constructed");
		}
		if (popNames == null || popCosts == null) {
			throw new IllegalArgumentException("arguments may not be null");
		}
		if (popNames.size() != machines.get(vmIndex).getSelectionButtonCount()
				|| popCosts.size() != machines.get(vmIndex).getSelectionButtonCount()) {
			throw new IllegalArgumentException("data lists do not match the number of buttons constructed");
		}
		Iterator<String> itN = popNames.iterator();
		Iterator<Integer> itC = popCosts.iterator();
		while (itN.hasNext() && itC.hasNext()) {
			machines.get(vmIndex).setButton(itN.next(), itC.next());
		}
	}

	@Override
	public void loadCoins(int vmIndex, int coinKindIndex, Coin... coins)
			throws IndexOutOfBoundsException, NullPointerException, IllegalArgumentException {
		if (vmIndex < 0 || vmIndex > machines.size() - 1) {
			throw new IndexOutOfBoundsException("the selected vending machine is not yet constructed");
		}
		if (coinKindIndex < 0 || coinKindIndex > machines.get(vmIndex).getCoinTypeCount() - 1) {
			throw new IndexOutOfBoundsException("the coinKindIndex is out of bounds");
		}
		if (coins == null) {
			throw new NullPointerException("coin array cannot be null");
		}
		for (int i = 0; i < coins.length; i++) {
			if (coins[i] == null) {
				throw new NullPointerException("coin cannot be null");
			}
			machines.get(vmIndex).addCoin(coinKindIndex, coins[i]);
		}
	}

	@Override
	public void loadPops(int vmIndex, int popKindIndex, Pop... pops)
			throws IndexOutOfBoundsException, NullPointerException, IllegalArgumentException {
		if (vmIndex < 0 || vmIndex > machines.size() - 1) {
			throw new IndexOutOfBoundsException("the selected vending machine is not yet constructed");
		}
		if (popKindIndex < 0 || popKindIndex > machines.get(vmIndex).getSelectionButtonCount() - 1) {
			throw new IndexOutOfBoundsException("the selected pop kind does not exist");
		}
		if (pops == null) {
			throw new NullPointerException("pop array cannot be null");
		}
		for (int i = 0; i < pops.length; i++) {
			if (pops[i] == null) {
				throw new NullPointerException("pop cannot be null");
			}
			machines.get(vmIndex).addPop(popKindIndex, pops[i]);
		}
	}

	@Override
	public List<List<?>> unloadVendingMachine(int vmIndex) {
		if (vmIndex < 0 || vmIndex > machines.size() - 1) {
			throw new IndexOutOfBoundsException("the selected vending machine is not yet implemented");
		}
		List<List<?>> ret = new ArrayList<List<?>>();
		ret.add(machines.get(vmIndex).unloadCoins("change"));
		ret.add(machines.get(vmIndex).unloadCoins("purchase"));
		ret.add(machines.get(vmIndex).unloadPops());
		return ret;
	}
}
