package com.example.diceroller.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


public class DiceRoller {
    private final Dice dice;
    // List type is a supertype and includes ArrayList for potential further use.
    private List<Integer> rolls;
    // Deque object that is a list composed of integers
    private final Deque<List<Integer>> history = new ArrayDeque<>();
    // Max stack height
    private static final int MAX_HISTORY = 5;

    public DiceRoller(Dice dice) {
        this.dice = dice;
        this.rolls = new ArrayList<>();
    }

    public void rollMany(int numberOfRolls) {
        saveState();
        rolls.clear();
        for (int i = 0; i < numberOfRolls; i++) {
            rolls.add(dice.roll());
        }
    }

    public void rerollEqual(int result) {
        saveState();
        for (int i = 0; i < rolls.size(); i++) {
            if (rolls.get(i) == result) {
                rolls.set(i, dice.roll());
            }
        }
    }

    public void rerollAboveOrEqual(int result) {
        saveState();
        for (int i = 0; i < rolls.size(); i++) {
            if (rolls.get(i) >= result) {
                rolls.set(i, dice.roll());
            }
        }
    }

    public void deleteEqual(int result) {
        saveState();
        rolls.removeIf(r -> (r == result));
    }

    public void deleteAboveOrEqual(int result) {
        saveState();
        rolls.removeIf(r -> (r >= result));
    }

    public RerollResult reroll(List<Integer> valuesToReroll) {
        saveState();

        Set<Integer> rerollSet = new HashSet<>(valuesToReroll);

        List<Integer> oldValues = new ArrayList<>();
        List<Integer> newValues = new ArrayList<>();

        for (int i = 0; i < rolls.size(); i++) {
            int currentValue = rolls.get(i);

            if (rerollSet.contains(currentValue)) {
                oldValues.add(currentValue);

                int newValue = dice.roll();
                rolls.set(i, newValue);

                newValues.add(newValue);
            }
        }
        return new RerollResult(oldValues, newValues);
    }

    public void delete(List<Integer> valuesToDelete) {

        saveState();

        Set<Integer> deleteSet = new HashSet<>(valuesToDelete);

        rolls.removeIf(deleteSet::contains);
    }


    public void clear() {
        saveState();
        rolls.clear();
    }

    // Method that saves the current state of the rolls list.
    // It will also keep the size of stack to be < 5.
    private void saveState() {
        // Pushing copy of rolls list to the top of the stack.
        history.push(new ArrayList<>(rolls));

        // Removing last copy on the stack when stack size bigger than allowed.
        if (history.size() > MAX_HISTORY) {
            history.removeLast();
        }
    }

    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }
        rolls = history.pop();
        return true;
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }


    public int[] getHistogram() {
        int[] histogram = new int[dice.getSides()];
        for (int result : rolls) {
            histogram[result - 1]++;
        }
        return histogram;
    }

    public List<Integer> getRolls() {
        return new ArrayList<>(rolls);
    }

    public void restoreRolls(List<Integer> rolls) {
        this.rolls = new ArrayList<>(rolls);
    }
}

