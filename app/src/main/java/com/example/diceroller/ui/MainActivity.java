package com.example.diceroller.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TextView;

import com.example.diceroller.R;
import com.example.diceroller.logic.Dice;
import com.example.diceroller.logic.DiceRoller;

public class MainActivity extends AppCompatActivity {

    private DiceRoller diceRoller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        diceRoller = new DiceRoller(new Dice(6));

        Button rollBtn = findViewById(R.id.rollButton);
        TextView resultText = findViewById(R.id.resultText);

        rollBtn.setOnClickListener(v -> {
            diceRoller.clear();
            diceRoller.rollMany(5);

            resultText.setText(diceRoller.getRolls().toString());
        });
    }
}