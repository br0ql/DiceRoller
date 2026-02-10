package com.example.diceroller.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import com.example.diceroller.R;
import com.example.diceroller.logic.Dice;
import com.example.diceroller.logic.DiceRoller;


public class MainActivity extends AppCompatActivity {
    private static final int MIN_DICE_VALUE = 1;
    private static final int MAX_DICE_VALUE = 6;
    private static final int MIN_DICE_COUNT = 1;
    private static final int MAX_DICE_COUNT = 100;

    private DiceRoller diceRoller;
    private HistogramView histogramView;

    private TextView stateText;
    private EditText valueInput;
    private EditText countInput;

    private Button rerollEqualBtn;
    private Button rerollAboveBtn;
    private Button deleteEqualBtn;
    private Button deleteAboveBtn;
    private Button undoBtn;
    private Button newRollBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        diceRoller = new DiceRoller(new Dice(20));
        histogramView = findViewById(R.id.histogramView);

        stateText = findViewById(R.id.stateText);
        valueInput = findViewById(R.id.valueInput);
        countInput = findViewById(R.id.countInput);

        rerollEqualBtn = findViewById(R.id.rerollEqualBtn);
        rerollAboveBtn = findViewById(R.id.rerollAboveBtn);
        deleteEqualBtn = findViewById(R.id.deleteEqualBtn);
        deleteAboveBtn = findViewById(R.id.deleteAboveBtn);
        undoBtn = findViewById(R.id.undoBtn);
        undoBtn.setEnabled(false);
        newRollBtn = findViewById(R.id.newRollBtn);

        rerollEqualBtn.setOnClickListener(v -> {
            diceRoller.rerollEqual(getDiceValue());
            renderState();
            updateResultsUI();

        });

        rerollAboveBtn.setOnClickListener(v -> {
            diceRoller.rerollAboveOrEqual(getDiceValue());
            renderState();
            updateResultsUI();
        });

        deleteEqualBtn.setOnClickListener(v -> {
            diceRoller.deleteEqual(getDiceValue());
            renderState();
            updateResultsUI();
        });

        deleteAboveBtn.setOnClickListener(v -> {
            diceRoller.deleteAboveOrEqual(getDiceValue());
            renderState();
            updateResultsUI();
        });

        undoBtn.setOnClickListener(v -> {
            diceRoller.undo();
            renderState();
            updateResultsUI();
        });

        newRollBtn.setOnClickListener(v -> {
            diceRoller.rollMany(getDiceCount());
            renderState();
            updateResultsUI();
        });

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        renderState();

    }

    private void renderState() {
        int[] histogram = diceRoller.getHistogram();

        if (diceRoller.getRolls().isEmpty()) {
            stateText.setText(
                    "No rolls yet\n\n" +
                            "Use \"New roll\" to start rolling dice."
            );
            updateUndoState();
            return;
        }

        StringBuilder sb = new StringBuilder();
        int numberOfRolls = 0;

        for (int i = 1; i < histogram.length; i++) {
            numberOfRolls += histogram[i];
            sb.append("#")
                    .append(i)
                    .append(": ")
                    .append(histogram[i])
                    .append("\n");
        }
        sb.append("Total: ")
                .append(numberOfRolls);

        stateText.setText(sb.toString());
        updateUndoState();
    }
    private int getDiceValue() {
        String text = valueInput.getText().toString().trim();

        if (text.isEmpty()) {
            valueInput.setError("Value required");
            return MIN_DICE_VALUE;
        }

        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            valueInput.setError("Invalid number");
            return MIN_DICE_VALUE;
        }

        if (value < MIN_DICE_VALUE || value > MAX_DICE_VALUE) {
            valueInput.setError(
                    "Value must be between " +
                            MIN_DICE_VALUE + " and " + MAX_DICE_VALUE
            );
            return clamp(value, MIN_DICE_VALUE, MAX_DICE_VALUE);
        }

        valueInput.setError(null);
        return value;
    }


    private int getDiceCount() {
        String text = countInput.getText().toString().trim();

        if (text.isEmpty()) {
            countInput.setError("Number of dice required");
            return MIN_DICE_COUNT;
        }

        int count;
        try {
            count = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            countInput.setError("Invalid number");
            return MIN_DICE_COUNT;
        }

        if (count < MIN_DICE_COUNT || count > MAX_DICE_COUNT) {
            countInput.setError(
                    "Number of dice must be between " +
                            MIN_DICE_COUNT + " and " + MAX_DICE_COUNT
            );
            return clamp(count, MIN_DICE_COUNT, MAX_DICE_COUNT);
        }

        countInput.setError(null);
        return count;
    }


    private void updateUndoState() {
        undoBtn.setEnabled(diceRoller.canUndo());
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private void updateResultsUI() {
        histogramView.setData(diceRoller.getHistogram());
        stateText.setText(diceRoller.getRolls().toString());
    }
}