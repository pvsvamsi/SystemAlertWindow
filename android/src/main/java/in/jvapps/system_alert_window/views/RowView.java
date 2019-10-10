package in.jvapps.system_alert_window.views;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import in.jvapps.system_alert_window.models.Padding;
import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.UiBuilder;

public class RowView {
    private Map<String, Object> rowMap;
    private Context context;

    public RowView(Context context, Map<String, Object> rowMap) {
        this.context = context;
        this.rowMap = rowMap;
    }

    public LinearLayout getView() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> columnsMap = (List<Map<String, Object>>) rowMap.get("columns");
        Padding padding = UiBuilder.getPadding(context, Commons.getMapFromObject(rowMap, "padding"));
        linearLayout.setPadding(padding.getLeft(), padding.getTop(), padding.getRight(), padding.getBottom());
        if (columnsMap != null) {
            for (int i = 0; i < columnsMap.size(); i++) {
                Map<String, Object> eachColumn = columnsMap.get(i);
                TextView textView = UiBuilder.getTextView(context, Commons.getMapFromObject(eachColumn,"text"));
                linearLayout.addView(textView);
            }
        }
        return linearLayout;
    }
}
