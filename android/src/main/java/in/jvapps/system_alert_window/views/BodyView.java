package in.jvapps.system_alert_window.views;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import in.jvapps.system_alert_window.models.Padding;
import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.NumberUtils;
import in.jvapps.system_alert_window.utils.UiBuilder;

import static in.jvapps.system_alert_window.utils.Constants.KEY_BACKGROUND_COLOR;
import static in.jvapps.system_alert_window.utils.Constants.KEY_COLUMNS;
import static in.jvapps.system_alert_window.utils.Constants.KEY_PADDING;
import static in.jvapps.system_alert_window.utils.Constants.KEY_ROWS;
import static in.jvapps.system_alert_window.utils.Constants.KEY_TEXT;

public class BodyView {
    private Map<String, Object> bodyMap;
    private Context context;

    public BodyView(Context context, Map<String, Object> bodyMap) {
        this.context = context;
        this.bodyMap = bodyMap;
    }

    public LinearLayout getView() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(NumberUtils.getInt(bodyMap.get(KEY_BACKGROUND_COLOR)));
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Padding padding = UiBuilder.getPadding(context, bodyMap.get(KEY_PADDING));
        linearLayout.setPadding(padding.getLeft(), padding.getTop(), padding.getRight(), padding.getBottom());
        linearLayout.setBackgroundColor(NumberUtils.getInt(bodyMap.get(KEY_BACKGROUND_COLOR)));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rowsMap = (List<Map<String, Object>>) bodyMap.get(KEY_ROWS);
        if (rowsMap != null) {
            for (int i = 0; i < rowsMap.size(); i++) {
                Map<String, Object> row = rowsMap.get(i);
                linearLayout.addView(createRow(row));
            }
        }
        return linearLayout;
    }

    private View createRow(Map<String, Object> rowMap) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        Padding padding = UiBuilder.getPadding(context, rowMap.get(KEY_PADDING));
        linearLayout.setPadding(padding.getLeft(), padding.getTop(), padding.getRight(), padding.getBottom());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> columnsMap = (List<Map<String, Object>>) rowMap.get(KEY_COLUMNS);
        if (columnsMap != null) {
            for (int j = 0; j < columnsMap.size(); j++) {
                Map<String, Object> column = columnsMap.get(j);
                TextView textView = UiBuilder.getTextView(context, Commons.getMapFromObject(column, KEY_TEXT));
                linearLayout.addView(textView);
            }
        }
        return linearLayout;
    }
}
