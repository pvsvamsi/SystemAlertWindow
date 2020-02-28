package in.jvapps.system_alert_window.views;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.jvapps.system_alert_window.models.Decoration;
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
        Decoration decoration = UiBuilder.getDecoration(context, footerMap.get(KEY_DECORATION));
        if (decoration != null) {
            GradientDrawable gd = UiBuilder.getGradientDrawable(decoration);
            linearLayout.setBackground(gd);
        }
        if ((boolean) footerMap.get(KEY_IS_SHOW_FOOTER)) {
            Map<String, Object> textMap = Commons.getMapFromObject(footerMap, KEY_TEXT);
            List<Map<String, Object>> buttonsMap = Commons.getMapListFromObject(footerMap, KEY_BUTTONS_LIST);
            TextView textView = UiBuilder.getTextView(context, textMap);
            List<Button> buttonsView = new ArrayList<>();
            for (Map<String, Object> buttonMap : buttonsMap) {
                buttonsView.add(UiBuilder.getButtonView(context, buttonMap));
            }
            String buttonsPosition = (String) footerMap.get(KEY_BUTTONS_LIST_POSITION);
            if (textView != null) {
                if (buttonsView.size() > 0) {
                    if ("leading".equals(buttonsPosition)) {
                        for (Button buttonView : buttonsView) {
                            linearLayout.addView(buttonView);
                        }
                        linearLayout.addView(textView);
                    } else {
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                1.0f
                        );
                        textView.setLayoutParams(param);
                        linearLayout.addView(textView);
                        for (Button buttonView : buttonsView) {
                            linearLayout.addView(buttonView);
                        }
                    }
                } else {
                    linearLayout.addView(textView);
                }
            } else {
                for (Button buttonView : buttonsView) {
                    linearLayout.addView(buttonView);
                }
                linearLayout.setGravity(Commons.getGravity(buttonsPosition, Gravity.FILL));
            }
        }
        return linearLayout;
    }
}
