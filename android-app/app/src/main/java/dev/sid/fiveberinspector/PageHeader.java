package dev.sid.fiveberinspector;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.*;

/** Compact shared navigation: the page title leads, with no separate branding row. */
final class PageHeader extends LinearLayout {
  PageHeader(Activity activity, String title, String backLabel, Runnable back, Runnable settings) {
    super(activity);
    setOrientation(HORIZONTAL);
    setGravity(Gravity.CENTER_VERTICAL);
    setBackgroundColor(MainActivity.BG);
    setPadding(dp(24), dp(8), dp(24), dp(8));
    setMinimumHeight(dp(64));
    if (back != null)
      addView(slot(activity, "back", backLabel, back), new LayoutParams(dp(48), dp(48)));
    TextView heading = label(activity, title, 22, MainActivity.TEXT);
    heading.setAccessibilityHeading(true);
    LayoutParams titleParams = new LayoutParams(0, -2, 1);
    if (back != null) titleParams.leftMargin = dp(8);
    if (settings != null) titleParams.rightMargin = dp(8);
    addView(heading, titleParams);
    if (settings != null)
      addView(slot(activity, "settings", s(R.string.ui_settings), settings), new LayoutParams(dp(48), dp(48)));
  }
  private View slot(Activity activity, String glyph, String description, Runnable click) {
    if (click == null) {
      View spacer = new View(activity);
      spacer.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
      return spacer;
    }
    ImageButton button = new ImageButton(activity);
    button.setImageDrawable(new UiIcon(glyph,MainActivity.TEXT));
    button.setContentDescription(description);button.setPadding(dp(12),dp(12),dp(12),dp(12));
    button.setMinimumWidth(0);button.setMinimumHeight(0);button.setStateListAnimator(null);button.setElevation(0);
    android.util.TypedValue ripple = new android.util.TypedValue();
    activity.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, ripple, true);
    button.setBackgroundResource(ripple.resourceId);
    button.setOnClickListener(v -> click.run());
    return button;
  }
  private TextView label(Activity activity, String text, int size, int color) {
    TextView label = new TextView(activity);
    label.setText(text);label.setTextSize(size);label.setTextColor(color);
    label.setTypeface(null, Typeface.BOLD);label.setLineSpacing(dp(3), 1);
    return label;
  }
  static void applyInsets(View root) {
    root.setOnApplyWindowInsetsListener((view, insets) -> {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        android.graphics.Insets bars = insets.getInsets(
            WindowInsets.Type.systemBars() | WindowInsets.Type.ime());
        view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
      } else {
        // Android 9/10: legacy system insets include the resized keyboard area.
        view.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
            insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
      }
      return insets;
    });
  }
  private String s(int id) { return getResources().getString(id); }
  private String s(int id, Object... args) { return getResources().getString(id, args); }
  private int dp(float value) { return Math.round(getResources().getDisplayMetrics().density * value); }
}
