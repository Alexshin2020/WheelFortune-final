package com.example.wheelfortune;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WheelView wheelView;
    private TextView resultTV;
    private Button addSectorButton, resetBtn, removeSectorButton;
    private EditText sectorNameEditText;
    private List<String> nameList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private TextView historyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        wheelView = findViewById(R.id.wheelview);
        resultTV = findViewById(R.id.winnerVal);
        addSectorButton = findViewById(R.id.addSectorButton);
        resetBtn = findViewById(R.id.resetBtn);
        removeSectorButton = findViewById(R.id.removeSectorButton);
        sectorNameEditText = findViewById(R.id.sectorNameEditText);
        historyTextView = findViewById(R.id.historyTextView);

        // Изначальные сектора
        nameList.add("100");
        nameList.add("200");
        nameList.add("300");
        nameList.add("400");
        nameList.add("500");
        nameList.add("600");

        wheelView.setData(nameList);
        wheelView.setRoundItemSelectedListener(new WheelView.RoundItemSelectedListener() {
            @Override
            public void onRoundItemSelected(int index, String selectedItem) {
                resultTV.setText(selectedItem); // Отображаем результат
                updatePrizeAndHistory(selectedItem); // Сохраняем и обновляем историю
            }
        });

        addSectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sectorName = sectorNameEditText.getText().toString();
                if (!sectorName.isEmpty()) {
                    nameList.add(sectorName);
                    wheelView.setData(nameList);
                    sectorNameEditText.setText("");
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.reset();
                resultTV.setText("");
            }
        });

        removeSectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameList.size() > 0) {
                    nameList.remove(nameList.size() - 1);
                    wheelView.setData(nameList);
                } else {
                    Toast.makeText(MainActivity.this, "Нечего удалять!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateHistoryTextView();
    }

    private void updatePrizeAndHistory(String prize) {
        long rowId = dbHelper.addSpin(prize);
        if (rowId == -1) {
            Log.e("MainActivity", "Failed to add spin to history");
        } else {
            Log.d("MainActivity", "Spin added to history. Row ID: " + rowId);
        }
        updateHistoryTextView();
    }

    private void updateHistoryTextView() {
        List<SpinHistoryItem> history = dbHelper.getAllSpins();
        StringBuilder historyText = new StringBuilder();
        for (SpinHistoryItem item : history) {
            historyText.append(item.getDateTime()).append(": ").append(item.getPrize()).append("\n");
        }
        historyTextView.setText(historyText.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    public void spin(View view) {
        wheelView.spin();
    }
}