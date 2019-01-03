package com.w3engineers.unicef.telemesh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button helloButton = findViewById(R.id.show);

        // Functionality added
        final TextView helloTextView = findViewById(R.id.hello_text);

        helloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoParser infoParser = new InfoParser();
                helloTextView.setText(infoParser.getHelloText());
            }
        });

        Log.e("MainActivity","Simple commit to check every things works well..");
    }
}
