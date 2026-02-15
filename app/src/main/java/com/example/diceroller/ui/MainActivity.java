package com.example.diceroller.ui;



import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
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
    private RecyclerView histogramRecycler;
    private HistogramAdapter histogramAdapter;


    private TextView stateText;
    private TextView rollCount;



    private Button undoBtn;
    private Button newRollBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        diceRoller = new DiceRoller(new Dice(6));
        histogramRecycler = findViewById(R.id.histogramRecycler);

        histogramRecycler.setLayoutManager(new LinearLayoutManager(this));

        histogramAdapter = new HistogramAdapter();
        histogramRecycler.setAdapter(histogramAdapter);


        rollCount = findViewById(R.id.rollCountText);
        undoBtn = findViewById(R.id.undoBtn);
        newRollBtn = findViewById(R.id.newRollBtn);

        undoBtn.setOnClickListener(v -> {
            diceRoller.undo();
            updateHistogram();
        });

        newRollBtn.setOnClickListener(v -> {
            String text = rollCount.getText().toString();
            int value = Integer.parseInt(text);
            diceRoller.rollMany(value);
            updateHistogram();
        });
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


}