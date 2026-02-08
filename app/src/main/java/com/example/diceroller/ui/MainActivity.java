package com.example.diceroller.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import com.example.diceroller.R;
import com.example.diceroller.logic.Dice;
import com.example.diceroller.logic.DiceRoller;

public class MainActivity extends AppCompatActivity {

    private DiceRoller diceRoller;

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
        diceRoller = new DiceRoller(new Dice(6));

        stateText = findViewById(R.id.stateText);
        valueInput = findViewById(R.id.valueInput);
        countInput = findViewById(R.id.countInput);

        rerollEqualBtn = findViewById(R.id.rerollEqualBtn);
        rerollAboveBtn = findViewById(R.id.rerollAboveBtn);
        deleteEqualBtn = findViewById(R.id.deleteEqualBtn);
        deleteAboveBtn = findViewById(R.id.deleteAboveBtn);
        undoBtn = findViewById(R.id.undoBtn);
        newRollBtn = findViewById(R.id.newRollBtn);

        rerollEqualBtn.setOnClickListener(v -> {
            diceRoller.rerollEqual(getDiceValue());
            renderState();
        });

        rerollAboveBtn.setOnClickListener(v -> {
            diceRoller.rerollAboveOrEqual(getDiceValue());
            renderState();
        });

        deleteEqualBtn.setOnClickListener(v -> {
            diceRoller.deleteEqual(getDiceValue());
            renderState();
        });

        deleteAboveBtn.setOnClickListener(v -> {
            diceRoller.deleteAboveOrEqual(getDiceValue());
            renderState();
        });

        undoBtn.setOnClickListener(v -> {
            diceRoller.undo();
            renderState();
        });

        newRollBtn.setOnClickListener(v -> {
            //diceRoller.clear();
            diceRoller.rollMany(getDiceCount());
            renderState();
        });

    }

    private void renderState() {
        stateText.setText(diceRoller.getRolls().toString());
    }

    private int getDiceValue() {
        String text = valueInput.getText().toString().trim();

        if (text.isEmpty()) {
            return 1;
        }

        try {
            int value = Integer.parseInt(text);
            if (value < 1) {
                return 1;
            }
            if (value > 6) {
                return 6;
            }
            return value;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private int getDiceCount() {
        String text = countInput.getText().toString().trim();

        if (text.isEmpty()) {
            return 1;
        }

        try {
            int count = Integer.parseInt(text);
            if (count < 1) {
                return 1;
            }
            if (count > 100) {
                return 100;
            }
            return count;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

}