package com.example.diceroller.ui;



import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.view.ViewGroup;

import android.widget.LinearLayout;


import com.example.diceroller.R;
import com.example.diceroller.logic.Dice;
import com.example.diceroller.logic.DiceRoller;

public class MainActivity extends AppCompatActivity {
    private static final int MIN_DICE_VALUE = 1;
    private static final int MAX_DICE_VALUE = 6;
    private static final int MIN_DICE_COUNT = 1;
    private static final int MAX_DICE_COUNT = 100;

    private DiceRoller diceRoller;
    private RecyclerView histogramRecycler;
    private HistogramAdapter histogramAdapter;

    private TextView rollCount;
    private Button minusBtn;
    private Button plusBtn;
    private Button plusFiveBtn;
    private Button plusTenBtn;
    private Button undoBtn;
    private Button newRollBtn;

    private LinearLayout bottomPanel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        bottomPanel = findViewById(R.id.bottomPanel);

        //        Bottom panel position for different types of navigation
        ViewCompat.setOnApplyWindowInsetsListener(bottomPanel, (view, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            params.bottomMargin = systemBars.bottom;
            view.setLayoutParams(params);

            return insets;
        });


        diceRoller = new DiceRoller(new Dice(10));
        histogramRecycler = findViewById(R.id.histogramRecycler);

        histogramRecycler.setLayoutManager(new LinearLayoutManager(this));

        histogramAdapter = new HistogramAdapter();
        histogramRecycler.setAdapter(histogramAdapter);

        // Recyclerview position
        bottomPanel.post(() -> {

            int panelHeight = bottomPanel.getHeight() + dpToPx(20);

            histogramRecycler.setPadding(
                    histogramRecycler.getPaddingLeft(),
                    histogramRecycler.getPaddingTop(),
                    histogramRecycler.getPaddingRight(),
                    panelHeight
            );
        });



        rollCount = findViewById(R.id.rollCountText);
        undoBtn = findViewById(R.id.undoBtn);
        newRollBtn = findViewById(R.id.newRollBtn);
        minusBtn = findViewById(R.id.minusBtn);
        plusBtn = findViewById(R.id.plusBtn);
        plusFiveBtn = findViewById(R.id.plusFiveBtn);
        plusTenBtn = findViewById(R.id.plusTenBtn);


        undoBtn.setOnClickListener(v -> {
            diceRoller.undo();
            updateHistogram();
            updateUndoState();
        });

        newRollBtn.setOnClickListener(v -> {
            String text = rollCount.getText().toString();
            int value = Integer.parseInt(text);
            diceRoller.rollMany(value);
            updateHistogram();
            updateUndoState();
        });

        // Long press minusBtn to reset rollCount to MIN_DICE_COUNT
        minusBtn.setOnLongClickListener(v -> {

            rollCount.setText(String.valueOf(MIN_DICE_COUNT));
            updateRollButtonsState(MIN_DICE_COUNT);

            return true; // ważne!
        });

        minusBtn.setOnClickListener(v -> changeRollCount(-1));
        plusBtn.setOnClickListener(v -> changeRollCount(1));
        plusFiveBtn.setOnClickListener(v -> changeRollCount(5));
        plusTenBtn.setOnClickListener(v -> changeRollCount(10));

        // Updating roll buttons based on initial values already set in xml file
        int initialValue = Integer.parseInt(rollCount.getText().toString());
        updateRollButtonsState(initialValue);
        updateUndoState();
        updateHistogram();
    }

    private void updateUndoState() {
        undoBtn.setEnabled(diceRoller.canUndo());
    }
    private void updateHistogram() {

        int[] counts = diceRoller.getHistogram();

        List<HistogramItem> list = new ArrayList<>();

        for (int i = 0; i < counts.length; i++) {
            list.add(new HistogramItem(i + 1, counts[i]));
        }

        histogramAdapter.setData(list);
    }

    // Method that is uded within recyclerview position method to offes end of list to above the bottom panel.
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // Method for validating rollCountText value
    private void changeRollCount(int delta) {
        int value = Integer.parseInt(rollCount.getText().toString());

        value += delta;

        if (value < MIN_DICE_COUNT) {
            value = MIN_DICE_COUNT;
        }

        if (value > MAX_DICE_COUNT) {
            value = MAX_DICE_COUNT;
        }

        rollCount.setText(String.valueOf(value));
        updateRollButtonsState(value);
    }

    private void updateRollButtonsState(int value) {
        boolean canDecrease = value > MIN_DICE_COUNT;
        minusBtn.setEnabled(canDecrease);
        minusBtn.setAlpha(canDecrease ? 1f : 0.4f);

        boolean canIncrease = value < MAX_DICE_COUNT;
        plusBtn.setEnabled(canIncrease);
        plusBtn.setAlpha(canIncrease ? 1f : 0.4f);
        plusFiveBtn.setEnabled(canIncrease);
        plusFiveBtn.setAlpha(canIncrease ? 1f : 0.4f);
        plusTenBtn.setEnabled(canIncrease);
        plusTenBtn.setAlpha(canIncrease ? 1f : 0.4f);

    }



}