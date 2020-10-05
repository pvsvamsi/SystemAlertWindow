package in.jvapps.system_alert_window;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.views.BodyView;
import in.jvapps.system_alert_window.views.FooterView;
import in.jvapps.system_alert_window.views.HeaderView;

import static in.jvapps.system_alert_window.utils.Constants.INTENT_EXTRA_PARAMS_MAP;
import static in.jvapps.system_alert_window.utils.Constants.KEY_BODY;
import static in.jvapps.system_alert_window.utils.Constants.KEY_FOOTER;
import static in.jvapps.system_alert_window.utils.Constants.KEY_HEADER;

public class BubbleActivity extends AppCompatActivity {

    private  LinearLayout bubbleLayout;
    private HashMap<String, Object> paramsMap;

    private Context mContext;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        mContext = this;
        bubbleLayout = findViewById(R.id.bubbleLayout);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paramsMap = (HashMap<String, Object>) intent.getSerializableExtra(INTENT_EXTRA_PARAMS_MAP);
            configureUI();
        }
    }

    void configureUI(){
        Map<String, Object> headersMap = Commons.getMapFromObject(paramsMap, KEY_HEADER);
        Map<String, Object> bodyMap = Commons.getMapFromObject(paramsMap, KEY_BODY);
        Map<String, Object> footerMap = Commons.getMapFromObject(paramsMap, KEY_FOOTER);
        LinearLayout headerView = new HeaderView(mContext, headersMap).getView();
        LinearLayout bodyView = new BodyView(mContext, bodyMap).getView();
        LinearLayout footerView = new FooterView(mContext, footerMap).getView();

        bubbleLayout.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        bubbleLayout.setLayoutParams(params);
        bubbleLayout.addView(headerView);
        bubbleLayout.addView(bodyView);
        bubbleLayout.addView(footerView);
    }
}
