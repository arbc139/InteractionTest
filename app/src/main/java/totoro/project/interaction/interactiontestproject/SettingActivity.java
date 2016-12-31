package totoro.project.interaction.interactiontestproject;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import totoro.project.interaction.interactiontestproject.common.CommonUtil;
import totoro.project.interaction.interactiontestproject.databinding.SettingActivityBinding;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

  SettingActivityBinding binding;

  private String screenHideHeight;
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
        setBindings();
      }
    });
  }

  private void setBindings() {
    binding.screenHideHeight.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        screenHideHeight = s.toString();
        System.out.println("screenHideHeight input: " + screenHideHeight);
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });
    binding.aBaseX.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        aBaseX = s.toString();
        System.out.println("A Base X input: " + aBaseX);
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });
    binding.aBaseY.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        aBaseY = s.toString();
        System.out.println("A Base Y input: " + aBaseY);
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });
    binding.aButtonDegree.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        aButtonDegree = s.toString();
        System.out.println("A Button Degree input: " + aButtonDegree);
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });
    binding.aButtonSize.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        aButtonSize = s.toString();
        System.out.println("A Button Size input: " + aButtonSize);
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });
    binding.aButtonRadius.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        aButtonRadius = s.toString();
        System.out.println("A Button Radius input: " + aButtonRadius);
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });
    binding.aTestCount.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        aTestCount = s.toString();
        System.out.println("A Test Count input: " + aTestCount);
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });
    binding.bButtonSize.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        bButtonSize = s.toString();
        System.out.println("bButtonSize input: " + bButtonSize);
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });
    binding.bButtonInterval.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        bButtonInterval = s.toString();
        System.out.println("bButtonInterval input: " + bButtonInterval);
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });
    binding.saveButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    if (view.getId() != R.id.save_button) {
      throw new RuntimeException("Invalid clicked view: " + view.toString());
    }
    // Data들이 모두 설정되었는지 체크.
    if (screenHideHeight.isEmpty() || aBaseX.isEmpty() || aBaseY.isEmpty()
        || aButtonSize.isEmpty() || aButtonRadius.isEmpty() || aButtonDegree.isEmpty()
        || aTestCount.isEmpty() || bButtonSize.isEmpty() || bButtonInterval.isEmpty()) {
      Toast.makeText(
          this, getResources().getString(R.string.incomplete_input_error), Toast.LENGTH_SHORT)
          .show();
      return;
    }
    // Data들이 모두 integer인지 확인.
    if (!checkFloat(screenHideHeight) || !checkFloat(aBaseX) || !checkFloat(aBaseY)
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

    Pair<Integer, Integer> originalABasePosition = CommonUtil.toOriginalPosition(
        Pair.create(
            Float.valueOf(CommonUtil.toPixelAppCoordinate(Float.valueOf(aBaseX), screenSize.first, getResources().getDisplayMetrics())).intValue(),
            Float.valueOf(CommonUtil.toPixelAppCoordinate(Float.valueOf(aBaseY), screenSize.second, getResources().getDisplayMetrics())).intValue()),
        (int) CommonUtil.toPixel(Float.valueOf(aButtonSize), getResources().getDisplayMetrics()));

    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_X, originalABasePosition.first);
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_Y, originalABasePosition.second);
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

    Pair<Integer, Integer> centerBasePosition = CommonUtil.toCenterPosition(Pair.create(
        Float.valueOf(sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_X, 0)).intValue(),
        Float.valueOf(sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_Y, 0)).intValue()),
        (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_SIZE, 0));
    aBaseX = String.valueOf(CommonUtil.toMillimeterCsvCoordinate(centerBasePosition.first, screenSize.first, getResources().getDisplayMetrics()));
    binding.aBaseX.setText(aBaseX);
    aBaseY = String.valueOf(CommonUtil.toMillimeterCsvCoordinate(centerBasePosition.second,screenSize.second, getResources().getDisplayMetrics()));
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
