package in.jvapps.system_alert_window.utils;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import in.jvapps.system_alert_window.models.Margin;
import in.jvapps.system_alert_window.models.Padding;

public class UiBuilder {

    public static TextView getTextView(Context context, Map<String, Object> textMap) {
        if (textMap == null) return null;
        TextView textView = new TextView(context);
        textView.setText((String) textMap.get("text"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, NumberUtils.getFloat(textMap.get("fontSize")));
        textView.setTextColor(NumberUtils.getInt(textMap.get("textColor")));
        Padding padding = getPadding(context, textMap.get("padding"));
        textView.setPadding(padding.getLeft(), padding.getTop(), padding.getRight(), padding.getBottom());
        return textView;
    }

    public static Padding getPadding(Context context, Object object) {
        @SuppressWarnings("unchecked")
        Map<String, Object> paddingMap = (Map<String, Object>) object;
        if (paddingMap == null) {
            return new Padding(0, 0, 0, 0, context);
        }
        return new Padding(paddingMap.get("left"), paddingMap.get("top"), paddingMap.get("right"), paddingMap.get("bottom"), context);
    }

    public static Margin getMargin(Context context, Object object) {
        @SuppressWarnings("unchecked")
        Map<String, Object> marginMap = (Map<String, Object>) object;
        if (marginMap == null) {
            return new Margin(0, 0, 0, 0, context);
        }
        return new Margin(marginMap.get("left"), marginMap.get("top"), marginMap.get("right"), marginMap.get("bottom"), context);
    }

    public static Button getButtonView(Context context, Map<String, Object> buttonMap) {
        if (buttonMap == null) return null;
        Button button = new Button(context);
        TextView buttonText = getTextView(context, Commons.getMapFromObject(buttonMap, "text"));
        assert buttonText != null;
        button.setText(buttonText.getText());
        button.setTextSize(Commons.getSpFromPixels(context, buttonText.getTextSize()));
        button.setTextColor(buttonText.getTextColors());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                Commons.getPixelsFromDp(context, (int) buttonMap.get("width")),
                Commons.getPixelsFromDp(context, (int) buttonMap.get("width")));
        Margin buttonMargin = getMargin(context, buttonMap.get("margin"));
        params.setMargins(buttonMargin.getLeft(), buttonMargin.getTop(), buttonMargin.getRight(), buttonMargin.getBottom());
        button.setLayoutParams(params);
        Padding padding = getPadding(context, buttonMap.get("padding"));
        button.setPadding(padding.getLeft(), padding.getTop(), padding.getRight(), padding.getBottom());
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(NumberUtils.getInt(buttonMap.get("fillColor")));
        gd.setCornerRadius(NumberUtils.getFloat(buttonMap.get("borderRadius")));
        int borderWidth = NumberUtils.getInt(buttonMap.get("borderWidth"));
        gd.setStroke(borderWidth, NumberUtils.getInt(buttonMap.get("borderColor")));
        button.setBackground(gd);
        return button;
    }
}
