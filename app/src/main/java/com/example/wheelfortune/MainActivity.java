package com.example.wheelfortune;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText itemName;
    private EditText itemChance;
    private Button addItemButton;
    private Button spinButton;
    private FrameLayout wheelContainer;
    private WheelView wheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemName = findViewById(R.id.itemName);
        itemChance = findViewById(R.id.itemChance);
        addItemButton = findViewById(R.id.addItemButton);
        spinButton = findViewById(R.id.spinButton);
        wheelContainer = findViewById(R.id.wheelContainer);

        wheelView = new WheelView(this, null);
        wheelContainer.addView(wheelView);

        addItemButton.setOnClickListener(v -> {
            String name = itemName.getText().toString();
            float chance = Float.parseFloat(itemChance.getText().toString());
            wheelView.addItem(name, chance);
            itemName.setText("");
            itemChance.setText("");
        });

        spinButton.setOnClickListener(v -> {
            wheelView.spin();
        });
    }
}