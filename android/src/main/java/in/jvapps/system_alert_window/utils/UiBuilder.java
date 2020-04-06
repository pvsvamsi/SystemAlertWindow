package in.jvapps.system_alert_window.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import in.jvapps.system_alert_window.SystemAlertWindowPlugin;
import in.jvapps.system_alert_window.models.Decoration;
import in.jvapps.system_alert_window.models.Margin;
import in.jvapps.system_alert_window.models.Padding;

import static in.jvapps.system_alert_window.utils.Constants.CALLBACK_TYPE_ONCLICK;
import static in.jvapps.system_alert_window.utils.Constants.KEY_BORDER_COLOR;
import static in.jvapps.system_alert_window.utils.Constants.KEY_BORDER_RADIUS;
import static in.jvapps.system_alert_window.utils.Constants.KEY_BORDER_WIDTH;
import static in.jvapps.system_alert_window.utils.Constants.KEY_BOTTOM;
import static in.jvapps.system_alert_window.utils.Constants.KEY_DECORATION;
import static in.jvapps.system_alert_window.utils.Constants.KEY_END_COLOR;
import static in.jvapps.system_alert_window.utils.Constants.KEY_FONT_SIZE;
import static in.jvapps.system_alert_window.utils.Constants.KEY_FONT_WEIGHT;
import static in.jvapps.system_alert_window.utils.Constants.KEY_HEIGHT;
import static in.jvapps.system_alert_window.utils.Constants.KEY_LEFT;
import static in.jvapps.system_alert_window.utils.Constants.KEY_MARGIN;
import static in.jvapps.system_alert_window.utils.Constants.KEY_PADDING;
import static in.jvapps.system_alert_window.utils.Constants.KEY_RIGHT;
import static in.jvapps.system_alert_window.utils.Constants.KEY_START_COLOR;
import static in.jvapps.system_alert_window.utils.Constants.KEY_TAG;
import static in.jvapps.system_alert_window.utils.Constants.KEY_TEXT;
import static in.jvapps.system_alert_window.utils.Constants.KEY_TEXT_COLOR;
import static in.jvapps.system_alert_window.utils.Constants.KEY_TOP;
import static in.jvapps.system_alert_window.utils.Constants.KEY_WIDTH;

public class UiBuilder {

    public static TextView getTextView(Context context, Map<String, Object> textMap) {
        if (textMap == null) return null;
        TextView textView = new TextView(context);
        textView.setText((String) textMap.get(KEY_TEXT));
        textView.setTypeface(textView.getTypeface(), Commons.getFontWeight((String) textMap.get(KEY_FONT_WEIGHT), Typeface.NORMAL));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, NumberUtils.getFloat(textMap.get(KEY_FONT_SIZE)));
        textView.setTextColor(NumberUtils.getInt(textMap.get(KEY_TEXT_COLOR)));
        Padding padding = getPadding(context, textMap.get(KEY_PADDING));
        textView.setPadding(padding.getLeft(), padding.getTop(), padding.getRight(), padding.getBottom());
        return textView;
    }

    public static Padding getPadding(Context context, Object object) {
        @SuppressWarnings("unchecked")
        Map<String, Object> paddingMap = (Map<String, Object>) object;
        if (paddingMap == null) {
            return new Padding(0, 0, 0, 0, context);
        }
        return new Padding(paddingMap.get(KEY_LEFT), paddingMap.get(KEY_TOP), paddingMap.get(KEY_RIGHT), paddingMap.get(KEY_BOTTOM), context);
    }

    public static Margin getMargin(Context context, Object object) {
        @SuppressWarnings("unchecked")
        Map<String, Object> marginMap = (Map<String, Object>) object;
        if (marginMap == null) {
            return new Margin(0, 0, 0, 0, context);
        }
        return new Margin(marginMap.get(KEY_LEFT), marginMap.get(KEY_TOP), marginMap.get(KEY_RIGHT), marginMap.get(KEY_BOTTOM), context);
    }

    public static Decoration getDecoration(Context context, Object object) {
        @SuppressWarnings("unchecked")
        Map<String, Object> decorationMap = (Map<String, Object>) object;
        if (decorationMap == null) {
            return null;
        }
        return new Decoration(decorationMap.get(KEY_START_COLOR), decorationMap.get(KEY_END_COLOR),
                decorationMap.get(KEY_BORDER_WIDTH), decorationMap.get(KEY_BORDER_RADIUS),
                decorationMap.get(KEY_BORDER_COLOR), context);
    }

    public static Button getButtonView(Context context, Map<String, Object> buttonMap) {
        if (buttonMap == null) return null;
        Button button = new Button(context);
        TextView buttonText = getTextView(context, Commons.getMapFromObject(buttonMap, KEY_TEXT));
        assert buttonText != null;
        button.setText(buttonText.getText());
        final Object tag = buttonMap.get(KEY_TAG);
        button.setTag(tag);
        button.setTextSize(Commons.getSpFromPixels(context, buttonText.getTextSize()));
        button.setTextColor(buttonText.getTextColors());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            button.setElevation(10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                Commons.getPixelsFromDp(context, (int) buttonMap.get(KEY_WIDTH)),
                Commons.getPixelsFromDp(context, (int) buttonMap.get(KEY_HEIGHT)),
                1.0f);
        Margin buttonMargin = getMargin(context, buttonMap.get(KEY_MARGIN));
        params.setMargins(buttonMargin.getLeft(), buttonMargin.getTop(), buttonMargin.getRight(), Math.min(buttonMargin.getBottom(), 4));
        button.setLayoutParams(params);
        Padding padding = getPadding(context, buttonMap.get(KEY_PADDING));
        button.setPadding(padding.getLeft(), padding.getTop(), padding.getRight(), padding.getBottom());
        Decoration decoration = getDecoration(context, buttonMap.get(KEY_DECORATION));
        if (decoration != null) {
            GradientDrawable gd = getGradientDrawable(decoration);
            button.setBackground(gd);
        }
        button.setOnClickListener(v -> {
            if (!SystemAlertWindowPlugin.sIsIsolateRunning.get()) {
                SystemAlertWindowPlugin.startCallBackHandler(context);
            }
            SystemAlertWindowPlugin.invokeCallBack(context, CALLBACK_TYPE_ONCLICK, tag);
        });
        return button;
    }

    public static GradientDrawable getGradientDrawable(Decoration decoration) {
        GradientDrawable gd = new GradientDrawable();
        if (decoration.isGradient()) {
            int[] colors = {decoration.getStartColor(), decoration.getEndColor()};
            gd.setColors(colors);
            gd.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        } else {
            gd.setColor(decoration.getStartColor());
        }
        gd.setCornerRadius(decoration.getBorderRadius());
        gd.setStroke(decoration.getBorderWidth(), decoration.getBorderColor());
        return gd;
    }

}
