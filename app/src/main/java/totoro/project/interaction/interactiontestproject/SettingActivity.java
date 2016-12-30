package totoro.project.interaction.interactiontestproject;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.setting_activity);

        validateSharedPreferences();

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
        || aTestCount.isEmpty()) {
      Toast.makeText(
          this, getResources().getString(R.string.incomplete_input_error), Toast.LENGTH_SHORT)
          .show();
      return;
    }
    // Data들이 모두 integer인지 확인.
    if (!checkFloat(screenHideHeight) || !checkInt(aBaseX) || !checkInt(aBaseY)
        || !checkFloat(aButtonSize) || !checkFloat(aButtonDegree) || !checkFloat(aButtonRadius)
        || !checkInt(aTestCount)) {
      Toast.makeText(
          this, getResources().getString(R.string.non_integer_error), Toast.LENGTH_SHORT)
          .show();
      return;
    }
    SharedPreferences.Editor editor =
        getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE).edit();

    Pair<Integer, Integer> originalABasePosition = CommonUtil.toOriginalPosition(
        Pair.create(Integer.valueOf(aBaseX), Integer.valueOf(aBaseY)),
        (int) CommonUtil.toPixel(Float.valueOf(aButtonSize), getResources().getDisplayMetrics()));
    // TODO(totoro): mm -> px로 변환시켜서 넣어야 함.
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_SCREEN_HIDE_HEIGHT,
        CommonUtil.toPixel(Float.valueOf(screenHideHeight), getResources().getDisplayMetrics()));
    editor.putInt(
        KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_X, originalABasePosition.first);
    editor.putInt(KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_Y, originalABasePosition.second);
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_SIZE,
        CommonUtil.toPixel(Float.valueOf(aButtonSize), getResources().getDisplayMetrics()));
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_RADIUS,
        CommonUtil.toPixel(Float.valueOf(aButtonRadius), getResources().getDisplayMetrics()));
    editor.putFloat(
        KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_DEGREE, Float.valueOf(aButtonDegree));
    editor.putInt(KeyMap.SHARED_PREFERENCES_SETTING_A_TEST_COUNT, Integer.valueOf(aTestCount));
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
      screenHideHeight = String.valueOf(
          CommonUtil.toMillimeter(
              sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_SCREEN_HIDE_HEIGHT, 0),
              getResources().getDisplayMetrics()));
      binding.screenHideHeight.setText(screenHideHeight);

      Pair<Integer, Integer> centerBasePosition = CommonUtil.toCenterPosition(Pair.create(
          sharedPreferences.getInt(KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_X, 0),
          sharedPreferences.getInt(KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_Y, 0)),
          (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_SIZE, 0));
      aBaseX = String.valueOf(centerBasePosition.first);
      binding.aBaseX.setText(aBaseX);
      aBaseY = String.valueOf(centerBasePosition.second);
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
    }
}
