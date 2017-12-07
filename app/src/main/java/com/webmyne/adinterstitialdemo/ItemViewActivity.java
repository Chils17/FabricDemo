package com.webmyne.adinterstitialdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ItemViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        if (this.getIntent().hasExtra("item_id")) {
            // read the item id from the intent
            String itemId = this.getIntent().getStringExtra("item_id");

            // load the item associated with item id
            Log.i("MyTestApp", "Just deep linked with item id: " + itemId);
        }

    }
}
