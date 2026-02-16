package com.example.diceroller.ui;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diceroller.R;
import com.example.diceroller.logic.Dice;
import com.example.diceroller.logic.DiceRoller;
import com.example.diceroller.logic.RerollResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int MIN_DICE_COUNT = 1;
    private static final int MAX_DICE_COUNT = 100;

    private DiceRoller diceRoller;
    private HistogramAdapter histogramAdapter;

    private RecyclerView histogramRecycler;
    private LinearLayout bottomPanel;

    private TextView rollCount;
    private Button minusBtn, plusBtn, plusFiveBtn, plusTenBtn;
    private Button undoBtn, newRollBtn, rerollSelectedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initLogic();
        initViews();
        setupRecycler();
        setupBottomPanelInsets();
        setupButtons();

        refreshAll();
    }

    // =========================
    // INITIALIZATION
    // =========================

    private void initLogic() {
        diceRoller = new DiceRoller(new Dice(10));
    }

    private void initViews() {
        bottomPanel = findViewById(R.id.bottomPanel);
        histogramRecycler = findViewById(R.id.histogramRecycler);

        rollCount = findViewById(R.id.rollCountText);

        minusBtn = findViewById(R.id.minusBtn);
        plusBtn = findViewById(R.id.plusBtn);
        plusFiveBtn = findViewById(R.id.plusFiveBtn);
        plusTenBtn = findViewById(R.id.plusTenBtn);

        undoBtn = findViewById(R.id.undoBtn);
        newRollBtn = findViewById(R.id.newRollBtn);
        rerollSelectedBtn = findViewById(R.id.rerollSelectedBtn);
    }

    private void setupRecycler() {
        histogramAdapter = new HistogramAdapter();
        histogramRecycler.setLayoutManager(new LinearLayoutManager(this));
        histogramRecycler.setAdapter(histogramAdapter);

        histogramAdapter.setOnSelectionChangedListener(count -> {
            if (count > 0) showRerollButton();
            else hideRerollButton();
        });
    }

    private void setupBottomPanelInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(bottomPanel, (view, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            params.bottomMargin = systemBars.bottom;
            view.setLayoutParams(params);

            return insets;
        });

        bottomPanel.post(() -> {
            int panelHeight = bottomPanel.getHeight() + dpToPx(20);
            histogramRecycler.setPadding(
                    histogramRecycler.getPaddingLeft(),
                    histogramRecycler.getPaddingTop(),
                    histogramRecycler.getPaddingRight(),
                    panelHeight
            );
        });
    }

    private void setupButtons() {

        minusBtn.setOnClickListener(v -> changeRollCount(-1));
        plusBtn.setOnClickListener(v -> changeRollCount(1));
        plusFiveBtn.setOnClickListener(v -> changeRollCount(5));
        plusTenBtn.setOnClickListener(v -> changeRollCount(10));

        minusBtn.setOnLongClickListener(v -> {
            rollCount.setText(String.valueOf(MIN_DICE_COUNT));
            updateRollButtonsState(MIN_DICE_COUNT);
            return true;
        });

        undoBtn.setOnClickListener(v -> handleUndo());
        newRollBtn.setOnClickListener(v -> handleNewRoll());
        rerollSelectedBtn.setOnClickListener(v -> handleReroll());
    }

    // =========================
    // ACTION HANDLERS
    // =========================

    private void handleUndo() {
        diceRoller.undo();
        histogramAdapter.clearHistory();
        histogramAdapter.clearIncrements();
        refreshAll();
    }

    private void handleNewRoll() {
        int value = getRollCount();
        histogramAdapter.clearHistory();
        histogramAdapter.clearIncrements();
        diceRoller.rollMany(value);
        refreshAll();
    }

    private void handleReroll() {

        histogramAdapter.clearIncrements();
        histogramAdapter.clearHistory();

        List<Integer> selected = histogramAdapter.getSelectedValues();
        if (selected.isEmpty()) return;

        RerollResult result = diceRoller.reroll(selected);

        Map<Integer, Integer> increments = new HashMap<>();
        for (int value : result.getNewValues()) {
            increments.put(value, increments.getOrDefault(value, 0) + 1);
        }

        histogramAdapter.clearSelection();
        histogramAdapter.setData(buildHistogramItems());
        histogramAdapter.setIncrements(increments);

        List<Integer> oldVals = result.getOldValues();
        List<Integer> newVals = result.getNewValues();

        for (int i = 0; i < oldVals.size(); i++) {
            String message = oldVals.get(i) + " → " + newVals.get(i);
            histogramAdapter.addInfoMessage(message);
        }

        hideRerollButton();
    }

    // =========================
    // UI HELPERS
    // =========================

    private void refreshAll() {
        updateHistogram();
        updateUndoState();
        updateRollButtonsState(getRollCount());
    }

    private void updateHistogram() {
        histogramAdapter.setData(buildHistogramItems());
    }

    private List<HistogramItem> buildHistogramItems() {
        int[] counts = diceRoller.getHistogram();
        List<HistogramItem> list = new ArrayList<>();

        for (int i = 0; i < counts.length; i++) {
            list.add(new HistogramItem(i + 1, counts[i]));
        }

        return list;
    }

    private int getRollCount() {
        return Integer.parseInt(rollCount.getText().toString());
    }

    private void changeRollCount(int delta) {
        int value = getRollCount() + delta;

        if (value < MIN_DICE_COUNT) value = MIN_DICE_COUNT;
        if (value > MAX_DICE_COUNT) value = MAX_DICE_COUNT;

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

    private void updateUndoState() {
        undoBtn.setEnabled(diceRoller.canUndo());
    }

    private void showRerollButton() {
        if (rerollSelectedBtn.getVisibility() == View.VISIBLE) return;

        rerollSelectedBtn.setVisibility(View.VISIBLE);
        rerollSelectedBtn.setAlpha(0f);
        rerollSelectedBtn.setTranslationY(40);

        rerollSelectedBtn.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(200)
                .start();
    }

    private void hideRerollButton() {
        if (rerollSelectedBtn.getVisibility() == View.GONE) return;

        rerollSelectedBtn.animate()
                .alpha(0f)
                .translationY(40)
                .setDuration(200)
                .withEndAction(() -> rerollSelectedBtn.setVisibility(View.GONE))
                .start();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
