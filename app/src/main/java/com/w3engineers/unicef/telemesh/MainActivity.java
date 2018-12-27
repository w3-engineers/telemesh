package com.w3engineers.unicef.telemesh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Functionality added
        TextView helloTextView = findViewById(R.id.hello_text);

        InfoParser infoParser = new InfoParser();

        helloTextView.setText(infoParser.getHelloText());
    }
}
