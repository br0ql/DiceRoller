package com.example.diceroller.ui;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import android.content.SharedPreferences;

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
    private int diceCount = 10;
    private int numberOfSides = 10;
    private static final String KEY_DICE_COUNT = "key_dice_count";
    private static final String KEY_DICE_SIDES = "key_dice_sides";
    private static final String KEY_ROLLS = "key_rolls";
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_FIRST_RUN = "isFirstRun";

    private DiceRoller diceRoller;
    private Dice dice;

    private HistogramAdapter histogramAdapter;

    private RecyclerView histogramRecycler;
    private LinearLayout bottomPanel;
    private Button minusBtn, rollCount, plusBtn, plusFiveBtn, plusTenBtn;
    private Button undoBtn, newRollBtn, rerollSelectedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        restoreLogic(savedInstanceState);
        initLogic();
        restoreDiceRollerRolls(savedInstanceState);
        initViews();
        setupRecycler();
        setupBottomPanelInsets();
        setupButtons();
        refreshAll();
        checkFirstRun();
    }

    // =========================
    // INITIALIZATION
    // =========================

    private void initLogic() {
        dice = new Dice(numberOfSides);
        diceRoller = new DiceRoller(dice);
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
        rerollSelectedBtn.setVisibility(View.GONE);
    }

    private void setupRecycler() {
        histogramAdapter = new HistogramAdapter();
        histogramRecycler.setLayoutManager(new LinearLayoutManager(this));
        histogramRecycler.setAdapter(histogramAdapter);

        histogramAdapter.setOnSelectionChangedListener(count -> {
            if (count > 0) {
                showRerollButton();
            } else {
                hideRerollButton();
            }
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
        changeRollCount(0);
        minusBtn.setOnClickListener(v -> changeRollCount(-1));
        plusBtn.setOnClickListener(v -> changeRollCount(1));
        plusFiveBtn.setOnClickListener(v -> changeRollCount(5));
        plusTenBtn.setOnClickListener(v -> changeRollCount(10));

        minusBtn.setOnLongClickListener(v -> {
            diceCount = MIN_DICE_COUNT;
            rollCount.setText(getString(R.string.dice_count_label, diceCount, dice.getSides()));
            updateRollButtonsState(MIN_DICE_COUNT);
            return true;
        });

        rollCount.setOnLongClickListener(v -> {
            showDicePickerDialog();
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
        histogramAdapter.clearSelection();
        refreshAll();
    }

    private void handleNewRoll() {
        histogramAdapter.clearHistory();
        histogramAdapter.clearIncrements();
        diceRoller.rollMany(diceCount);
        refreshAll();
    }

    private void handleReroll() {

        histogramAdapter.clearIncrements();
        histogramAdapter.clearHistory();

        List<Integer> selected = histogramAdapter.getSelectedValues();
        if (selected.isEmpty()) return;

        RerollResult result = diceRoller.reroll(selected);

        Map<Integer, Integer> increments = new HashMap<>();
        for (int oldValue : result.getOldValues()) {
            int current =  increments.getOrDefault(oldValue, 0);
            increments.put(oldValue, current - 1);

        }

        for (int newValue : result.getNewValues()) {
            int current = increments.getOrDefault(newValue, 0);
            increments.put(newValue, current + 1);
        }

        histogramAdapter.clearSelection();
        histogramAdapter.setData(buildHistogramItems());
        histogramAdapter.setIncrements(increments);

        List<Integer> oldVals = result.getOldValues();
        List<Integer> newVals = result.getNewValues();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < oldVals.size(); i++) {
            sb.append(oldVals.get(i))
                    .append(" → ")
                    .append(newVals.get(i));

            if (i < oldVals.size() - 1) {
                sb.append("  |  ");
            }
        }

        histogramAdapter.addInfoMessage(sb.toString());

        hideRerollButton();
    }

    // =========================
    // UI HELPERS
    // =========================

    private void refreshAll() {
        updateHistogram();
        updateUndoState();
        histogramAdapter.clearSelection();
        hideRerollButton();
        updateRollButtonsState(diceCount);
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

    private void changeRollCount(int delta) {
        diceCount += delta;

        if (diceCount < MIN_DICE_COUNT) diceCount = MIN_DICE_COUNT;
        if (diceCount > MAX_DICE_COUNT) diceCount = MAX_DICE_COUNT;

        rollCount.setText(getString(R.string.dice_count_label, diceCount, dice.getSides()));
        updateRollButtonsState(diceCount);
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
    private void showDicePickerDialog() {

        final String[] diceOptions = {"D4", "D6", "D8", "D10", "D12", "D20"};
        final int[] diceValues = {4, 6, 8, 10, 12, 20};

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.select_dice_title))
                .setItems(diceOptions, (dialog, which) -> {

                    numberOfSides = diceValues[which];

                    initLogic();

                    rollCount.setText(
                            getString(R.string.dice_count_label, diceCount, numberOfSides)
                    );

                    histogramAdapter.clearSelection();
                    histogramAdapter.clearHistory();
                    histogramAdapter.clearIncrements();
                    refreshAll();
                })
                .show();
    }
    // Checking if app is being run for the first time
    private void checkFirstRun() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean(KEY_FIRST_RUN, true);

        if (isFirstRun) {
            showIntroDialog();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_FIRST_RUN, false);
            editor.apply();
        }
    }

    // Message to show when app opening the app for the first time
    private void showIntroDialog() {

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.intro_title))
                .setMessage(getString(R.string.intro_message))
                .setPositiveButton(getString(R.string.intro_button_positive), null)
                .setCancelable(false)
                .show();
    }

    // Saving app state when device rotates to landscape mode
    @Override
    protected void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Saving die info for logic initialization
        outState.putInt(KEY_DICE_COUNT, diceCount);
        outState.putInt(KEY_DICE_SIDES, numberOfSides);

        //Saving results array
        outState.putIntegerArrayList(KEY_ROLLS, new ArrayList<>(diceRoller.getRolls()));
    }

    // Helper method to restore app logic in onCreate, when rotation to landscape mode happens
    private void restoreLogic (Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            diceCount = savedInstanceState.getInt(KEY_DICE_COUNT, 10);
            numberOfSides = savedInstanceState.getInt(KEY_DICE_SIDES, 10);
        }

    }

    // Helper method to restore last roll results in onCreate, when rotation to landscape mode happens
    private void restoreDiceRollerRolls (Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            ArrayList<Integer> savedRolls = savedInstanceState.getIntegerArrayList(KEY_ROLLS);
            if (savedRolls != null) {
                diceRoller.restoreRolls(savedRolls);
            }
        }
    }



}
