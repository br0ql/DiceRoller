package com.example.diceroller.logic;

import java.util.List;
public class RerollResult {

    private final List<Integer> oldValues;
    private final List<Integer> newValues;

    public RerollResult(List<Integer> oldValues, List<Integer> newValues) {
        this.oldValues = oldValues;
        this.newValues = newValues;
    }

    public List<Integer> getOldValues() {
        return oldValues;
    }

    public List<Integer> getNewValues() {
        return newValues;
    }
}
