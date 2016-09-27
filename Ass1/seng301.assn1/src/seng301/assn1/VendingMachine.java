package seng301.assn1;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayDeque;
import org.lsmr.vending.frontend1.Coin;
import org.lsmr.vending.frontend1.Pop;

public class VendingMachine {
	private List<Integer> coinTypes;
	private List<Integer> sortedCoins;
	private ArrayDeque<Coin> deliveryChute_change;
	private ArrayDeque<Pop> deliveryChute_pop;
	private HashMap<Integer, HashMap<String, ArrayDeque<Coin>>> coinInventory;
	private HashMap<String, ArrayDeque<Pop>> popInventory;
	private int selectionButtonCount;
	private List<VmButton> buttons;
	private int coinSlot;

	public class VmButton {
		private String name;
		private int cost;

		public VmButton(String name, Integer cost) throws IllegalArgumentException {
			if (name == null || cost == null) {
				throw new IllegalArgumentException("arguments may not be null");
			}
			if (name.isEmpty() || name.length() < 3) { // name is always wrapped
														// in quotes?
				throw new IllegalArgumentException("pop name must be at least one character");
			}
			if (cost <= 0) {
				throw new IllegalArgumentException("pop cost must be positive");
			}
			this.setName(name);
			this.setCost(cost);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getCost() {
			return cost;
		}

		public void setCost(int cost) {
			this.cost = cost;
		}
	}

	/**
	 * Basic Constructor
	 */
	public VendingMachine(List<Integer> coinTypes, int selectionButtonCount)
			throws IllegalArgumentException, IllegalStateException {
		coinInventory = new HashMap<Integer, HashMap<String, ArrayDeque<Coin>>>();
		popInventory = new HashMap<String, ArrayDeque<Pop>>();
		deliveryChute_change = new ArrayDeque<Coin>();
		deliveryChute_pop = new ArrayDeque<Pop>();
		coinSlot = 0;
		buttons = new ArrayList<VmButton>();
		this.coinTypes = coinTypes;
		Iterator<Integer> it = coinTypes.iterator();
		while (it.hasNext()) {
			Integer cur = it.next();
			if (coinTypes.indexOf(cur) != coinTypes.lastIndexOf(cur)) {
				throw new IllegalStateException("Duplicate Coin Type: " + cur);
			}
			if (cur <= 0) {
				throw new IllegalArgumentException("A Coin Type cannot be 0 or negative: " + cur);
			}
			coinInventory.put(cur, new HashMap<String, ArrayDeque<Coin>>());
			coinInventory.get(cur).put("purchase", new ArrayDeque<Coin>());
			coinInventory.get(cur).put("change", new ArrayDeque<Coin>());
		}
		sortedCoins = new ArrayList<Integer>(coinTypes);
		Collections.sort(sortedCoins);
		Collections.reverse(sortedCoins);
		if (selectionButtonCount < 0) {
			throw new IllegalArgumentException("selectionButtonCount cannot be less than 0");
		}
		this.selectionButtonCount = selectionButtonCount;
	}

	public int getSelectionButtonCount() {
		return selectionButtonCount;
	}

	public void setButton(String name, Integer cost) throws IllegalArgumentException {
		buttons.add(new VmButton(name, cost));
		popInventory.put(name, new ArrayDeque<Pop>());
	}

	public int getCoinTypeCount() {
		return coinTypes.size();
	}

	public void addCoin(int coinKindIndex, Coin coin) throws IndexOutOfBoundsException, IllegalArgumentException {
		if (coinKindIndex < 0 || coinKindIndex > coinTypes.size() - 1) {
			throw new IndexOutOfBoundsException("coin index out of bounds");
		} else {
			if (coin.getValue() < 0) {
				throw new IllegalArgumentException("coin values cannot be negative");
			}
			coinInventory.get(coinTypes.get(coinKindIndex)).get("change").add(coin);
		}
	}

	public void addPop(int popKindIndex, Pop soda) throws IndexOutOfBoundsException, IllegalArgumentException {
		if (popKindIndex < 0 || popKindIndex > buttons.size() - 1) {
			throw new IndexOutOfBoundsException("pop index out of bounds");
		} else {
			if (soda.getName().isEmpty() || soda.getName().length() < 3) {
				throw new IllegalArgumentException("pop name cannot be an empty string");
			}
			popInventory.get(buttons.get(popKindIndex).getName()).add(soda);
		}
	}

	public void putCoin(Coin coin) {
		if (coinInventory.containsKey(coin.getValue())) {
			coinInventory.get(coin.getValue()).get("purchase").add(coin);
			coinSlot += coin.getValue();
		} else {
			deliveryChute_change.add(coin);
		}
	}

	public Pop grabPop() {
		return deliveryChute_pop.pollFirst();
	}

	public Coin grabCoin() {
		return deliveryChute_change.pollFirst();
	}

	public List<Coin> unloadCoins(String bank) {
		ArrayList<Coin> ret = new ArrayList<Coin>();
		for (HashMap<String, ArrayDeque<Coin>> denomination : coinInventory.values()) {
			ret.addAll(denomination.get(bank));
			denomination.get(bank).clear();
		}
		return ret;
	}

	public List<Pop> unloadPops() {
		ArrayList<Pop> ret = new ArrayList<Pop>();
		for (ArrayDeque<Pop> popList : popInventory.values()) {
			ret.addAll(popList);
			popList.clear();
		}
		return ret;
	}

	public void purchase(int buttonIndex) throws IndexOutOfBoundsException {
		if (buttonIndex < 0 || buttonIndex > buttons.size() - 1) {
			throw new IndexOutOfBoundsException("button doesn't exist");
		}
		int cost = buttons.get(buttonIndex).getCost();
		if (cost > coinSlot) {
			// not enough money
		} else {
			if (popInventory.get(buttons.get(buttonIndex).getName()).isEmpty()) {
				// no more pop
			} else {
				deliveryChute_pop.add(popInventory.get(buttons.get(buttonIndex).getName()).pop());
				int dueChange = coinSlot - cost;
				Iterator<Integer> it = sortedCoins.iterator();
				while (it.hasNext()) {
					int denomination = it.next();
					ArrayDeque<Coin> changeSlot = coinInventory.get(denomination).get("change");
					while (dueChange >= denomination && !changeSlot.isEmpty()) {
						deliveryChute_change.add(changeSlot.pop());
						dueChange -= denomination;
					}
				}
				coinSlot = dueChange;
			}
		}
	}
}
