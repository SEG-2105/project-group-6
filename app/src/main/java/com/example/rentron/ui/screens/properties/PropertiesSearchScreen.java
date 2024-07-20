package com.example.rentron.ui.screens.properties;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.rentron.R;
import com.example.rentron.ui.core.UIScreen;

public class PropertiesSearchScreen extends UIScreen {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_property_screen);

        attachOnClickListeners();

    }

    private void attachOnClickListeners() {

        //Header Buttons
        ImageButton backButton = (ImageButton) findViewById(R.id.back_btn3);
        ImageButton cartButton = (ImageButton) findViewById(R.id.cartBtn);

        backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cartButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
