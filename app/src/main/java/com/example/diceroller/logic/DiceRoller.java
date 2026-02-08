package com.example.diceroller.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DiceRoller {
    private Dice dice;
    // Typ List bo ArrayList jest wariantem listy. W ten sposob
    // w razie czego mozna uzyc pozniej ArrayList lub LinkedList bez zmiany kodu tutaj.
    private List<Integer> rolls;
    // Obiekt Deque ktory jest stosem zbudowanym z List<Integer>.
    private Deque<List<Integer>> history = new ArrayDeque<>();
    // Maksymalne długość stosu.
    private static final int MAX_HISTORY = 5;

    public DiceRoller(Dice dice) {
        this.dice = dice;
        this.rolls = new ArrayList<>();
    }

    public void rollMany(int numberOfRolls) {
        saveState();
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

    public void clear() {
        saveState();
        rolls.clear();
    }

    // Method that saves the current state of the rolls list.
    // It will also keep the size of stack to be < 5.
    public void saveState() {
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

    public int[] getHistogram() {
        int[] counts = new int[dice.getSides() + 1];
        for (int result : rolls) {
            counts[result]++;
        }
        return counts;
    }

    public List<Integer> getRolls() {
        return rolls;
    }
}

