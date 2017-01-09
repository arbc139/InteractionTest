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
import android.widget.RelativeLayout;

import java.util.Date;

import totoro.project.interaction.interactiontestproject.common.CommonUtil;
import totoro.project.interaction.interactiontestproject.csv.CsvManager;
import totoro.project.interaction.interactiontestproject.databinding.BeforeTest2ActivityBinding;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.changePosition;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.getMeasuredPosition;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.getMeasuredPositionLegacy;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.toMillimeterCsvCoordinate;

public class BeforeTest2Activity extends AppCompatActivity
    implements View.OnClickListener, View.OnTouchListener{

  private BeforeTest2ActivityBinding binding;

  private Pair<Integer, Integer> screenSize;

  private CsvManager csvManager = new CsvManager();

  private String name;
  private String deviceNumber;
  private String postureType;
  private String handType;
  private String testType;

  private int screenHideHeight = 0;
  private int testTargetX = 0;
  private int testTargetY = 0;
  private int testButtonSize = 0;

  private boolean isTestFinished = false;

  private final String[] csvColumns = new String[] {
      "드래그 x 좌표",
      "드래그 y 좌표",
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.before_test_2_activity);
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
        // Workaround.
        testTargetX = screenSize.first - testTargetX;
        testTargetY = screenSize.second - testTargetY;
        initViews();
      }
    });
  }

  private void validateSharedPreferences() {
    SharedPreferences sharedPreferences =
        getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE);
    screenHideHeight = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_SCREEN_HIDE_HEIGHT, 0);
    testTargetX = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_2_TARGET_X, 300);
    testTargetY = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_2_TARGET_Y, 400);
    testButtonSize = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_BEFORE_TEST_2_BUTTON_SIZE, 50);

  }

  private void initViews() {
    binding.targetButton.setOnTouchListener(this);
    binding.nextButton.setOnClickListener(this);

    changePosition(binding.targetButton, testTargetX, testTargetY);

    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.targetButton.getLayoutParams();
    params.width = testButtonSize;
    params.height = testButtonSize;
    binding.targetButton.setLayoutParams(params);

    binding.targetButton.setVisibility(GONE);
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
  public void onBackPressed() {
    csvManager.safeClear();
    finish();
  }


  @Override
  public boolean onTouch(View view, MotionEvent event) {
    if (view.getId() != R.id.target_button) {
      throw new RuntimeException("Invalid touch view ID: " + view.getId());
    }
    Pair<Integer, Integer> nestPosition = Pair.create((int) event.getX(), (int) event.getY());
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        clickBaseButton(nestPosition);
        return true;
      case MotionEvent.ACTION_MOVE:
        moveBaseButton(nestPosition);
        return true;
      case MotionEvent.ACTION_UP:
        setFinishTest();
        return true;
      default:
        return true;
    }
  }

  private void clickBaseButton(Pair<Integer, Integer> nestPosition) {
    Pair<Integer, Integer> measuredPosition = getMeasuredPositionLegacy(
        binding.targetButton, nestPosition);
    System.out.println("Base button click: " + measuredPosition.first + ", " + measuredPosition.second);
    Pair<Integer, Integer> touchPosition = toTouchPosition(measuredPosition, testButtonSize);
    changePosition(binding.targetButton, touchPosition.first, touchPosition.second);
  }

  private void moveBaseButton(Pair<Integer, Integer> nestPosition) {
    Pair<Integer, Integer> measuredPosition = getMeasuredPositionLegacy(
        binding.targetButton, nestPosition);
    System.out.println("Base button move: " + measuredPosition.first + ", " + measuredPosition.second);
    Pair<Integer, Integer> touchPosition = toTouchPosition(measuredPosition, testButtonSize);
    changePosition(binding.targetButton, touchPosition.first, touchPosition.second);
    // CSV에 드래그 좌표를 입력.
    writeCsv(measuredPosition.first, measuredPosition.second);
  }

  private Pair<Integer, Integer> toTouchPosition(Pair<Integer, Integer> position, int buttonSize) {
    return Pair.create(position.first - buttonSize / 2, position.second - buttonSize / 2);
  }

  @Override
  public void onClick(View view) {
    if (view.getId() != R.id.next_button) {
      throw new RuntimeException("Invalid click view: " + view.toString());
    }
    if (!isTestFinished) {
      // Start test.
      startTest();
      return;
    }
    // End test.
    finish();
  }

  private void startTest() {
    binding.instructionLayout.setVisibility(GONE);
    binding.targetButton.setVisibility(VISIBLE);
    validateCsvManager(getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE));
  }

  private void setFinishTest() {
    isTestFinished = true;
    binding.targetButton.setVisibility(GONE);
    binding.instructionLayout.setVisibility(VISIBLE);
    binding.instruction.setText(R.string.end_instruction);
    binding.nextButton.setText(R.string.back_button);

    csvManager.clear();
  }

  /*
    "드래그 x 좌표",
    "드래그 y 좌표",
  */
  private void writeCsv(int dragX, int dragY) {
    csvManager.write(new String[] {
        String.valueOf(toMillimeterCsvCoordinate(
            dragX, screenSize.first, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeterCsvCoordinate(
            dragY, screenSize.second, getResources().getDisplayMetrics())),
    });
  }
}
