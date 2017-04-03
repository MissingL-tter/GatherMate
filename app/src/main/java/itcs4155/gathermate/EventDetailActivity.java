package itcs4155.gathermate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Dillon on 3/31/2017.
 */

public class EventDetailActivity extends AppCompatActivity {
    TextView nameView;
    TextView descView;
    TextView locationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Intent intent = getIntent();
        String name = intent.getStringExtra("detailName");
        String desc = intent.getStringExtra("detailDesc");
        String loc = intent.getStringExtra("locationDetail");
        nameView = (TextView) findViewById(R.id.detailName);
        nameView.setText(name);
        descView = (TextView) findViewById(R.id.detailDesc);
        descView.setText(desc);
        locationView = (TextView) findViewById(R.id.locationDetail);
        locationView.setText(loc);
    }
}
