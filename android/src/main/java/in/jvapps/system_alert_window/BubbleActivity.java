package in.jvapps.system_alert_window;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BubbleActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);

        textView = findViewById(R.id.textView);


    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if (getIntent() != null && getIntent().getExtras() != null) {

            String value = getIntent().getStringExtra("key");
            textView.setText(value);
        }*/
    }
}
