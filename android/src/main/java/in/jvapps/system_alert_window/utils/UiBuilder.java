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

public class UiBuilder {

    private static UiBuilder _instance;
    private final SystemAlertWindowPlugin systemAlertWindowPlugin = new SystemAlertWindowPlugin();

    private UiBuilder() {
    }

    public static UiBuilder getInstance() {
        if (_instance == null) {
            _instance = new UiBuilder();
        }
        return _instance;
    }

    public TextView getTextView(Context context, Map<String, Object> textMap) {
        if (textMap == null) return null;
        TextView textView = new TextView(context);
        textView.setText((String) textMap.get(Constants.KEY_TEXT));
        textView.setTypeface(textView.getTypeface(), Commons.getFontWeight((String) textMap.get(Constants.KEY_FONT_WEIGHT), Typeface.NORMAL));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, NumberUtils.getFloat(textMap.get(Constants.KEY_FONT_SIZE)));
        textView.setTextColor(NumberUtils.getInt(textMap.get(Constants.KEY_TEXT_COLOR)));
        Padding padding = getPadding(context, textMap.get(Constants.KEY_PADDING));
        textView.setPadding(padding.getLeft(), padding.getTop(), padding.getRight(), padding.getBottom());
        return textView;
    }

    public Padding getPadding(Context context, Object object) {
        @SuppressWarnings("unchecked")
        Map<String, Object> paddingMap = (Map<String, Object>) object;
        if (paddingMap == null) {
            return new Padding(0, 0, 0, 0, context);
        }
        return new Padding(paddingMap.get(Constants.KEY_LEFT), paddingMap.get(Constants.KEY_TOP), paddingMap.get(Constants.KEY_RIGHT), paddingMap.get(Constants.KEY_BOTTOM), context);
    }

    public Margin getMargin(Context context, Object object) {
        @SuppressWarnings("unchecked")
        Map<String, Object> marginMap = (Map<String, Object>) object;
        if (marginMap == null) {
            return new Margin(0, 0, 0, 0, context);
        }
        return new Margin(marginMap.get(Constants.KEY_LEFT), marginMap.get(Constants.KEY_TOP), marginMap.get(Constants.KEY_RIGHT), marginMap.get(Constants.KEY_BOTTOM), context);
    }

    public Decoration getDecoration(Context context, Map<String, Object> decorationMap) {
        if (decorationMap == null) {
            return null;
        }
        return new Decoration(decorationMap.get(Constants.KEY_START_COLOR), decorationMap.get(Constants.KEY_END_COLOR),
                decorationMap.get(Constants.KEY_BORDER_WIDTH), decorationMap.get(Constants.KEY_BORDER_RADIUS),
                decorationMap.get(Constants.KEY_BORDER_COLOR), context);
    }

    public Button getButtonView(Context context, Map<String, Object> buttonMap) {
        if (buttonMap == null) return null;
        Button button = new Button(context);
        TextView buttonText = getTextView(context, Commons.getMapFromObject(buttonMap, Constants.KEY_TEXT));
        assert buttonText != null;
        button.setText(buttonText.getText());
        final Object tag = buttonMap.get(Constants.KEY_TAG);
        button.setTag(tag);
        button.setTextSize(Commons.getSpFromPixels(context, buttonText.getTextSize()));
        button.setTextColor(buttonText.getTextColors());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            button.setElevation(10);
        @SuppressWarnings("ConstantConditions")
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                Commons.getPixelsFromDp(context, (int) ((double) buttonMap.get(Constants.KEY_WIDTH))),
                Commons.getPixelsFromDp(context, (int) ((double) buttonMap.get(Constants.KEY_HEIGHT))),
                1.0f);
        Margin buttonMargin = getMargin(context, buttonMap.get(Constants.KEY_MARGIN));
        params.setMargins(buttonMargin.getLeft(), buttonMargin.getTop(), buttonMargin.getRight(), Math.min(buttonMargin.getBottom(), 4));
        button.setLayoutParams(params);
        Padding padding = getPadding(context, buttonMap.get(Constants.KEY_PADDING));
        button.setPadding(padding.getLeft(), padding.getTop(), padding.getRight(), padding.getBottom());
        Decoration decoration = getDecoration(context, Commons.getMapFromObject(buttonMap, Constants.KEY_DECORATION));
        if (decoration != null) {
            GradientDrawable gd = getGradientDrawable(decoration);
            button.setBackground(gd);
        }
        button.setOnClickListener(v -> {
            if (!systemAlertWindowPlugin.sIsIsolateRunning.get()) {
                systemAlertWindowPlugin.startCallBackHandler(context);
            }
            systemAlertWindowPlugin.invokeCallBack(context, Constants.CALLBACK_TYPE_ONCLICK, tag);
        });
        return button;
    }

    public GradientDrawable getGradientDrawable(Decoration decoration) {
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
