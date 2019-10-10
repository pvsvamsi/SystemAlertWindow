package in.jvapps.system_alert_window.views;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import in.jvapps.system_alert_window.models.Padding;
import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.UiBuilder;

public class HeaderView {
    private Map<String, Object> headerMap;
    private Context context;

    public HeaderView(Context context, Map<String, Object> headerMap) {
        this.context = context;
        this.headerMap = headerMap;
    }

    public LinearLayout getView() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Map<String, Object> titleMap = Commons.getMapFromObject(headerMap,"title");
        Map<String, Object> subTitleMap = Commons.getMapFromObject(headerMap,"subTitle");
        Map<String, Object> buttonMap = Commons.getMapFromObject(headerMap,"button");
        Padding padding = UiBuilder.getPadding(context, headerMap.get("padding"));
        linearLayout.setPadding(padding.getLeft(), padding.getTop(), padding.getRight(), padding.getBottom());
        boolean isShowButton = (buttonMap != null);
        assert titleMap != null;
        View textColumn = createTextColumn(titleMap, subTitleMap);
        if (isShowButton) {
            String buttonPosition = (String) headerMap.get("buttonPosition");
            Button button = UiBuilder.getButtonView(context, buttonMap);
            if ("leading".equals(buttonPosition)) {
                linearLayout.addView(button);
                linearLayout.addView(textColumn);
            } else {
                linearLayout.addView(textColumn);
                linearLayout.addView(button);
            }
        } else {
            linearLayout.addView(textColumn);
        }

        return linearLayout;
    }

    private View createTextColumn(Map<String, Object> titleMap, Map<String, Object> subTitleMap) {
        TextView titleView = UiBuilder.getTextView(context, titleMap);
        if (subTitleMap != null) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(titleView);
            linearLayout.addView(UiBuilder.getTextView(context, subTitleMap));
            return linearLayout;
        }
        return titleView;
    }
}
