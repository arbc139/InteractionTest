package totoro.project.interaction.interactiontestproject;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.util.Date;

import totoro.project.interaction.interactiontestproject.common.CommonUtil;
import totoro.project.interaction.interactiontestproject.csv.CsvManager;
import totoro.project.interaction.interactiontestproject.databinding.BeforeTestActivityBinding;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.toMillimeterCsvCoordinate;

public class BeforeTestActivity extends AppCompatActivity
    implements View.OnClickListener, View.OnTouchListener {

  BeforeTestActivityBinding binding;

  private Pair<Integer, Integer> screenSize;

  private CsvManager csvManager = new CsvManager();

  private String name;
  private String deviceNumber;
  private String postureType;
  private String handType;
  private String testType;

  private int screenHideHeight;
  private int totalCount = 0;
  private int count = 0;

  private final String[] csvColumns = new String[] {
      "터치 x 좌표",
      "터치 y 좌표",
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.before_test_activity);

    // Populates screen size.
    final View content = findViewById(android.R.id.content);
    content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        //Remove it here unless you want to get this callback for EVERY
        //layout pass, which can get you into infinite loops if you ever
        //modify the layout from within this method.
        content.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        validateSharedPreferences();

        View mainLayout = findViewById(R.id.main_layout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mainLayout.getLayoutParams();
        params.height = content.getMeasuredHeight() - screenHideHeight;
        mainLayout.setLayoutParams(params);

        //Now you can get the width and height from content
        System.out.println("content width, height: " + content.getMeasuredWidth() + ", " + content.getMeasuredHeight());
        System.out.println("MainLayout width, height: " + mainLayout.getMeasuredWidth() + ", " + mainLayout.getMeasuredHeight());
        screenSize = Pair.create(mainLayout.getMeasuredWidth(), content.getMeasuredHeight() - screenHideHeight);
        initViews();
      }
    });
  }

  private void validateSharedPreferences() {
    SharedPreferences sharedPreferences =
        getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE);
    screenHideHeight = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_SCREEN_HIDE_HEIGHT, 0);
    totalCount = sharedPreferences.getInt(KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_COUNT, 0);
  }

  private void initViews() {
    binding.touchLayout.setOnTouchListener(this);
    binding.nextButton.setOnClickListener(this);

    binding.touchLayout.setVisibility(GONE);
    binding.instructionLayout.setVisibility(VISIBLE);
  }

  private void validateCsvManager(SharedPreferences sharedPreferences) {
    name = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_NAME, "");
    deviceNumber = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_DEVICE_NUMBER, "");
    postureType = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_POSTURE_TYPE, MainActivity.PostureType.UNKNOWN.name());
    handType = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_HAND_TYPE, MainActivity.HandType.UNKNOWN.name());
    testType = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_TEST_TYPE, MainActivity.TestType.UNKNOWN.name());
    csvManager.createCsvWriter(
        getApplicationContext(),
        new String[] { testType },
        String.format("%s_%s_%s_%s_%s.csv",
            name, deviceNumber, handType, postureType, CommonUtil.formattedDate(new Date())),
        csvColumns);
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    if (event.getAction() != MotionEvent.ACTION_UP) {
      return true;
    }
    if (view.getId() != R.id.touch_layout) {
      throw new RuntimeException("Invalid touch view ID: " + view.getId());
    }
    // Csv에 기록.
    Pair<Integer, Integer> position = Pair.create((int) event.getX(), (int) event.getY());
    System.out.println("Touched " + position.first + ", " + position.second);
    writeCsv(count, position.first, position.second);
    count++;
    if (count >= totalCount) {
      setFinishTest();
    }
    return false;
  }

  @Override
  public void onBackPressed() {
    csvManager.safeClear();
    finish();
  }


  @Override
  public void onClick(View view) {
    if (view.getId() != R.id.next_button) {
      throw new RuntimeException("Invalid click view: " + view.toString());
    }
    if (totalCount != 0 && count < totalCount) {
      // Start test.
      startTest();
      return;
    }
    // End test.
    finish();
  }

  private void startTest() {
    binding.instructionLayout.setVisibility(GONE);
    binding.touchLayout.setVisibility(VISIBLE);
    validateCsvManager(getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE));
  }

  private void setFinishTest() {
    binding.touchLayout.setVisibility(GONE);
    binding.instructionLayout.setVisibility(VISIBLE);
    binding.instruction.setText(R.string.end_instruction);
    binding.nextButton.setText(R.string.back_button);

    csvManager.clear();
  }

  /*
    "횟수",
    "터치 x 좌표",
    "터치 y 좌표",
   */
  private void writeCsv(int count, int touchX, int touchY) {
    csvManager.write(new String[] {
        String.valueOf(count),
        String.valueOf(toMillimeterCsvCoordinate(
            touchX, screenSize.first, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeterCsvCoordinate(
            touchY, screenSize.second, getResources().getDisplayMetrics())),
    });
  }
}
