package totoro.project.interaction.interactiontestproject;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import totoro.project.interaction.interactiontestproject.common.CommonUtil;
import totoro.project.interaction.interactiontestproject.databinding.SettingActivityBinding;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

  SettingActivityBinding binding;

  private String screenHideHeight;
  private String beforeTestCount;
  private String beforeTest2TargetX;
  private String beforeTest2TargetY;
  private String beforeTest2ButtonSize;
  private String aBaseX;
  private String aBaseY;
  private String aButtonSize;
  private String aButtonRadius;
  private String aButtonDegree;
  private String aTestCount;
  private String bButtonSize;
  private String bButtonInterval;

  private Pair<Integer, Integer> rootScreenSize;
  private Pair<Integer, Integer> screenSize;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.setting_activity);

    // Populates screen size.
    final View content = findViewById(android.R.id.content);
    content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        //Remove it here unless you want to get this callback for EVERY
        //layout pass, which can get you into infinite loops if you ever
        //modify the layout from within this method.
        content.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        //Now you can get the width and height from content
        rootScreenSize = Pair.create(content.getMeasuredWidth(), content.getMeasuredHeight());
        screenSize = Pair.create(content.getMeasuredWidth(), content.getMeasuredHeight());

        validateSharedPreferences();
        binding.saveButton.setOnClickListener(SettingActivity.this);
      }
    });
  }

  private void getBindingStrings() {
    screenHideHeight = binding.screenHideHeight.getText().toString();
    beforeTestCount = binding.beforeTestCount.getText().toString();
    beforeTest2TargetX = binding.beforeTest2TargetX.getText().toString();
    beforeTest2TargetY = binding.beforeTest2TargetY.getText().toString();
    beforeTest2ButtonSize = binding.beforeTest2ButtonSize.getText().toString();
    aBaseX = binding.aBaseX.getText().toString();
    aBaseY = binding.aBaseY.getText().toString();
    aButtonDegree = binding.aButtonDegree.getText().toString();
    aButtonSize = binding.aButtonSize.getText().toString();
    aButtonRadius = binding.aButtonRadius.getText().toString();
    aTestCount = binding.aTestCount.getText().toString();
    bButtonSize = binding.bButtonSize.getText().toString();
    bButtonInterval = binding.bButtonInterval.getText().toString();
  }

  @Override
  public void onClick(View view) {
    if (view.getId() != R.id.save_button) {
      throw new RuntimeException("Invalid clicked view: " + view.toString());
    }
    getBindingStrings();
    // Data들이 모두 설정되었는지 체크.
    if (screenHideHeight.isEmpty() || beforeTestCount.isEmpty() || beforeTest2TargetX.isEmpty()
        || beforeTest2TargetY.isEmpty() || beforeTest2ButtonSize.isEmpty() || aBaseX.isEmpty()
        || aBaseY.isEmpty()
        || aButtonSize.isEmpty() || aButtonRadius.isEmpty() || aButtonDegree.isEmpty()
        || aTestCount.isEmpty() || bButtonSize.isEmpty() || bButtonInterval.isEmpty()) {
      Toast.makeText(
          this, getResources().getString(R.string.incomplete_input_error), Toast.LENGTH_SHORT)
          .show();
      return;
    }
    // Data들이 모두 integer인지 확인.
    if (!checkFloat(screenHideHeight) || !checkInt(beforeTestCount)
        || !checkFloat(beforeTest2TargetX) || !checkFloat(beforeTest2ButtonSize)
        || !checkFloat(beforeTest2TargetY) || !checkFloat(aBaseX) || !checkFloat(aBaseY)
        || !checkFloat(aButtonSize) || !checkFloat(aButtonDegree) || !checkFloat(aButtonRadius)
        || !checkInt(aTestCount) || !checkFloat(bButtonSize) || !checkFloat(bButtonInterval)) {
      Toast.makeText(
          this, getResources().getString(R.string.non_integer_error), Toast.LENGTH_SHORT)
          .show();
      return;
    }
    SharedPreferences.Editor editor =
        getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE).edit();

    // TODO(totoro): mm -> px로 변환시켜서 넣어야 함.
    float screenHideHeightPixel = CommonUtil.toPixel(Float.valueOf(screenHideHeight), getResources().getDisplayMetrics());
    screenSize = Pair.create(rootScreenSize.first, (int) (rootScreenSize.second - screenHideHeightPixel));
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_SCREEN_HIDE_HEIGHT, screenHideHeightPixel);

    System.out.println("Saved creen size: " + screenSize.first + ", " + screenSize.second);

    editor.putInt(KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_COUNT, Integer.valueOf(beforeTestCount));

    Pair<Float, Float> originalBeforeTest2TargetPosition = CommonUtil.toOriginalPosition(
        Pair.create(
            CommonUtil.toPixel(Float.valueOf(beforeTest2TargetX), getResources().getDisplayMetrics()),
            CommonUtil.toPixel(Float.valueOf(beforeTest2TargetY), getResources().getDisplayMetrics())),
        CommonUtil.toPixel(Float.valueOf(beforeTest2ButtonSize), getResources().getDisplayMetrics()));
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_2_TARGET_X, originalBeforeTest2TargetPosition.first);
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_2_TARGET_Y, originalBeforeTest2TargetPosition.second);
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_2_BUTTON_SIZE,
        CommonUtil.toPixel(Float.valueOf(beforeTest2ButtonSize), getResources().getDisplayMetrics()));
    System.out.println("SHARED_PREFERENCES beforeTest2ButtonSize: " + getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE).getFloat(KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_2_BUTTON_SIZE, 0));

    Pair<Float, Float> originalABasePosition = CommonUtil.toOriginalPosition(
        Pair.create(
            CommonUtil.toPixel(Float.valueOf(aBaseX), getResources().getDisplayMetrics()),
            CommonUtil.toPixel(Float.valueOf(aBaseY), getResources().getDisplayMetrics())),
        CommonUtil.toPixel(Float.valueOf(aButtonSize), getResources().getDisplayMetrics()));
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_X, originalABasePosition.first);
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_Y, originalABasePosition.second);
    System.out.println("Setting value a base: " + Float.valueOf(aBaseX) + ", " + Float.valueOf(aBaseY));
    System.out.println("SHARED_PREFERENCES original position millimeter: "
        + CommonUtil.toMillimeter(originalABasePosition.first, getResources().getDisplayMetrics())
        + ", " + CommonUtil.toMillimeter(originalABasePosition.second, getResources().getDisplayMetrics()));
    System.out.println("SHARED_PREFERENCES_SETTING_A_BASE_X: " + getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE).getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_X, 0));
    System.out.println("SHARED_PREFERENCES_SETTING_A_BASE_Y: " + getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE).getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_Y, 0));

    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_SIZE,
        CommonUtil.toPixel(Float.valueOf(aButtonSize), getResources().getDisplayMetrics()));
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_RADIUS,
        CommonUtil.toPixel(Float.valueOf(aButtonRadius), getResources().getDisplayMetrics()));
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_DEGREE, Float.valueOf(aButtonDegree));
    editor.putInt(KeyMap.SHARED_PREFERENCES_SETTING_A_TEST_COUNT, Integer.valueOf(aTestCount));
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_B_BUTTON_SIZE,
        CommonUtil.toPixel(Float.valueOf(bButtonSize), getResources().getDisplayMetrics()));
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_B_BUTTON_INTERVAL,
        CommonUtil.toPixel(Float.valueOf(bButtonInterval), getResources().getDisplayMetrics()));
    editor.apply();

    finish();
  }

  private boolean checkInt(String str) {
    try {
      Integer.valueOf(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private boolean checkFloat(String str) {
    try {
      Float.valueOf(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private void validateSharedPreferences() {
    SharedPreferences sharedPreferences =
        getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE);
    float screenHideHeightPixel = sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_SCREEN_HIDE_HEIGHT, 0);
    screenHideHeight = String.valueOf(
        CommonUtil.toMillimeter(
            screenHideHeightPixel, getResources().getDisplayMetrics()));
    binding.screenHideHeight.setText(screenHideHeight);
    screenSize = Pair.create(rootScreenSize.first, (int) (rootScreenSize.second - screenHideHeightPixel));

    beforeTestCount = String.valueOf(
        sharedPreferences.getInt(KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_COUNT, 0));
    binding.beforeTestCount.setText(String.valueOf(beforeTestCount));

    Pair<Float, Float> centerBeforeTest2TargetPosition = CommonUtil.toCenterPosition(
        Pair.create(
            sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_2_TARGET_X, 0),
            sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_2_TARGET_Y, 0)),
        sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_2_BUTTON_SIZE, 0));
    beforeTest2TargetX = String.valueOf(CommonUtil.toMillimeter(centerBeforeTest2TargetPosition.first, getResources().getDisplayMetrics()));
    binding.beforeTest2TargetX.setText(beforeTest2TargetX);
    beforeTest2TargetY = String.valueOf(CommonUtil.toMillimeter(centerBeforeTest2TargetPosition.second, getResources().getDisplayMetrics()));
    binding.beforeTest2TargetY.setText(beforeTest2TargetY);
    beforeTest2ButtonSize = String.valueOf(
        CommonUtil.toMillimeter(
            sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_2_BUTTON_SIZE, 0),
            getResources().getDisplayMetrics()));
    binding.beforeTest2ButtonSize.setText(beforeTest2ButtonSize);
    System.out.println("validate beforeTest2ButtonSize: " + beforeTest2ButtonSize);

    Pair<Float, Float> centerBasePosition = CommonUtil.toCenterPosition(
        Pair.create(
            sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_X, 0),
            sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_Y, 0)),
        sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_SIZE, 0));
    aBaseX = String.valueOf(CommonUtil.toMillimeter(centerBasePosition.first, getResources().getDisplayMetrics()));
    binding.aBaseX.setText(aBaseX);
    aBaseY = String.valueOf(CommonUtil.toMillimeter(centerBasePosition.second, getResources().getDisplayMetrics()));
    binding.aBaseY.setText(aBaseY);
    aButtonSize = String.valueOf(
        CommonUtil.toMillimeter(
            sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_SIZE, 0),
            getResources().getDisplayMetrics()));
    binding.aButtonSize.setText(aButtonSize);
    aButtonRadius = String.valueOf(
        CommonUtil.toMillimeter(
            sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_RADIUS, 0),
            getResources().getDisplayMetrics()));
    binding.aButtonRadius.setText(aButtonRadius);
    aButtonDegree = String.valueOf(
        sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_DEGREE, 0));
    binding.aButtonDegree.setText(aButtonDegree);
    aTestCount = String.valueOf(
        sharedPreferences.getInt(KeyMap.SHARED_PREFERENCES_SETTING_A_TEST_COUNT, 0));
    binding.aTestCount.setText(String.valueOf(aTestCount));
    bButtonSize = String.valueOf(
        CommonUtil.toMillimeter(
            sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_B_BUTTON_SIZE, 0),
            getResources().getDisplayMetrics()));
    binding.bButtonSize.setText(String.valueOf(bButtonSize));
    bButtonInterval = String.valueOf(
        CommonUtil.toMillimeter(
            sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_B_BUTTON_INTERVAL, 0),
            getResources().getDisplayMetrics()));
    binding.bButtonInterval.setText(String.valueOf(bButtonInterval));
  }
}
