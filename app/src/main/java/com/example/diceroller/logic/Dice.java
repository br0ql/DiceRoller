package com.example.diceroller.logic;

import androidx.annotation.NonNull;

import java.util.Random;

public class Dice {

    private final int sides;
    private final Random random;

    public Dice(int numberOfSides) {
        if (numberOfSides < 2) {
            throw new IllegalArgumentException("Dice must have at least 2 sides");
        }
        this.sides = numberOfSides;
        this.random = new Random();
    }

    public int roll() {
        return random.nextInt(sides) + 1;
    }

    public int getSides() {
        return sides;
    }

    @NonNull
    @Override
    public String toString() {
        return "Number of sides = " + sides;
    }
}
