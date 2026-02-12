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
    private EditText rollCount;



    private Button undoBtn;
    private Button newRollBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        diceRoller = new DiceRoller(new Dice(6));
        histogramView = findViewById(R.id.histogramView);

//        stateText = findViewById(R.id.stateText);

        TextView rollCount = findViewById(R.id.rollCountText);
        int value = Integer.parseInt(rollCount.getText().toString());

        undoBtn = findViewById(R.id.undoBtn);
        undoBtn.setEnabled(false);
        newRollBtn = findViewById(R.id.newRollBtn);

        undoBtn.setOnClickListener(v -> {
            diceRoller.undo();
            renderState();
            updateResultsUI();
        });

        newRollBtn.setOnClickListener(v -> {
            diceRoller.rollMany(value);
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
//            stateText.setText(
//                    "No rolls yet\n\n" +
//                            "Use \"New roll\" to start rolling dice."
//            );
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

//        stateText.setText(sb.toString());
        updateUndoState();
    }

    private void updateUndoState() {
        undoBtn.setEnabled(diceRoller.canUndo());
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private void updateResultsUI() {
        histogramView.setData(diceRoller.getHistogram());
//        stateText.setText(diceRoller.getRolls().toString());
    }
}