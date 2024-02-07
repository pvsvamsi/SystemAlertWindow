package in.jvapps.system_alert_window;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.Constants;
import in.jvapps.system_alert_window.utils.NumberUtils;
import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.FlutterEngineGroup;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.android.FlutterFragment;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.android.FlutterTextureView;


import static in.jvapps.system_alert_window.utils.Constants.INTENT_EXTRA_PARAMS_MAP;


public class BubbleActivity extends AppCompatActivity {

    private  LinearLayout bubbleLayout;
    private HashMap<String, Object> paramsMap;
    private FlutterView flutterView;
    private FlutterEngine flutterEngine;

    private Context mContext;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        mContext = this;
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            paramsMap = (HashMap<String, Object>) intent.getSerializableExtra(INTENT_EXTRA_PARAMS_MAP);
            FlutterEngineGroup enn = new FlutterEngineGroup(mContext);
            DartExecutor.DartEntrypoint dEntry = new DartExecutor.DartEntrypoint(
                    FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                    "overlayMain");
            flutterEngine = enn.createAndRunEngine(mContext, dEntry);
            FlutterEngineCache.getInstance().put(Constants.FLUTTER_CACHE_ENGINE, flutterEngine);
            configureUI();
        }
    }

    protected void onResume() {
        super.onResume();
        flutterEngine.getLifecycleChannel().appIsResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        flutterEngine.getLifecycleChannel().appIsInactive();
    }

    @Override
    protected void onStop() {
        super.onStop();
        flutterEngine.getLifecycleChannel().appIsPaused();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flutterEngine.getLifecycleChannel().appIsDetached();
    }

    void configureUI(){
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL); // Set the orientation if needed

        flutterEngine.getLifecycleChannel().appIsResumed();
        flutterView = new FlutterView(getApplicationContext(), new FlutterTextureView(getApplicationContext()));
        flutterView.attachToFlutterEngine(Objects.requireNonNull(FlutterEngineCache.getInstance().get(Constants.FLUTTER_CACHE_ENGINE)));
        flutterView.setFitsSystemWindows(true);
        flutterView.setFocusable(true);
        flutterView.setFocusableInTouchMode(true);
        flutterView.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        flutterView.setLayoutParams(params);
        linearLayout.addView(flutterView);
        setContentView(linearLayout);
    }
}
