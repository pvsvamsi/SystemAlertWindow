package in.jvapps.system_alert_window.views;

import android.content.Context;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import in.jvapps.system_alert_window.models.Padding;
import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.UiBuilder;

import static in.jvapps.system_alert_window.utils.Constants.*;

public class FooterView {
    private Map<String, Object> footerMap;
    private Context context;

    public FooterView(Context context, Map<String, Object> footerMap) {
        this.context = context;
        this.footerMap = footerMap;
    }

    public LinearLayout getView() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        Padding footerPadding = UiBuilder.getPadding(context, footerMap.get(KEY_PADDING));
        linearLayout.setPadding(footerPadding.getLeft(), footerPadding.getTop(), footerPadding.getRight(), footerPadding.getBottom());
        linearLayout.setLayoutParams(params);
        if ((boolean) footerMap.get(KEY_IS_SHOW_FOOTER)) {
            Map<String, Object> textMap = Commons.getMapFromObject(footerMap, KEY_TEXT);
            Map<String, Object> buttonMap = Commons.getMapFromObject(footerMap, KEY_BUTTON);
            TextView textView = UiBuilder.getTextView(context, textMap);
            Button buttonView = UiBuilder.getButtonView(context, buttonMap);
            String buttonPosition = (String) footerMap.get(KEY_BUTTON_POSITION);
            if (textView != null) {
                if (buttonView != null) {
                    if ("leading".equals(buttonPosition)) {
                        linearLayout.addView(buttonView);
                        linearLayout.addView(textView);
                    } else {
                        linearLayout.addView(textView);
                        linearLayout.addView(buttonView);
                    }
                } else {
                    linearLayout.addView(textView);
                }
            } else {
                linearLayout.addView(buttonView);
                linearLayout.setGravity(Commons.getGravity(buttonPosition, Gravity.CENTER));
            }
        }
        return linearLayout;
    }
}
