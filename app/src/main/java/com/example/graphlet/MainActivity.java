package com.example.graphlet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private Button buttonAlgorithmVisualizer;
    private Button buttonGraphManagement;
    private Button buttonQuizSection;
    private ImageView buttonClose;
    private ImageView buttonHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAlgorithmVisualizer = findViewById(R.id.button_algorithm_visualizer);
        buttonGraphManagement = findViewById(R.id.button_graph_management);
        buttonQuizSection = findViewById(R.id.button_quiz_section);
        buttonClose = findViewById(R.id.button_close);
        buttonHelp = findViewById(R.id.button_help);

        buttonAlgorithmVisualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(MainActivity.this, AlgorithmVisualizerActivity.class);
                 startActivity(intent);
            }
        });

        buttonGraphManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GraphManagementActivity.class);
                startActivity(intent);
            }
        });

        buttonQuizSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to open Quiz Section Activity
                // Intent intent = new Intent(MainActivity.this, QuizSectionActivity.class);
                // startActivity(intent);
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });

        buttonHelp.setOnClickListener(new View.OnClickListener() { // Add this block
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HelpMenuActivity.class);
                startActivity(intent);
            }
        });
    }
}