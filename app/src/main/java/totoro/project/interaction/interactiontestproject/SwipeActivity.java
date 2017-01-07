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
import java.util.Locale;

import totoro.project.interaction.interactiontestproject.common.CommonUtil;
import totoro.project.interaction.interactiontestproject.csv.CsvManager;
import totoro.project.interaction.interactiontestproject.databinding.SwipeActivityBinding;
import totoro.project.interaction.interactiontestproject.swipe.SwipeGenerator.SwipeType;
import totoro.project.interaction.interactiontestproject.swipe.SwipeManager;
import totoro.project.interaction.interactiontestproject.timer.Timer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.changePosition;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.getMeasuredPositionLegacy;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.getPosition;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.isClickedInCircle;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.toCenterPosition;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.toMillimeterCsvCoordinate;

public class SwipeActivity extends AppCompatActivity implements
    View.OnTouchListener, View.OnClickListener {

  private SwipeActivityBinding binding;

  private Pair<Integer, Integer> screenSize;

  private SwipeManager manager;
  private SwipeType type;
  private CsvManager dragCsvManager = new CsvManager();
  private CsvManager touchUpCsvManager = new CsvManager();
  private Timer timer = new Timer();

  private String name;
  private String deviceNumber;
  private String postureType;
  private String handType;
  private String testType;

  private int screenHideHeight = 0;
  private int testButtonSize;
  private int testBasePointX;
  private int testBasePointY;
  private int testBaseInterval;

  private int touchCounter = 0;

  private final String[] dragCsvColumns = new String[] {
      "드래그 번호",
      "터치 번호",
      "드래그 x 좌표",
      "드래그 y 좌표",
  };

  private final String[] touchUpCsvColumns = new String[] {
      "타겟 x 좌표",
      "타겟 y 좌표",
      "성공 여부",
      "시간",
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.swipe_activity);
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
    testButtonSize = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_B_BUTTON_SIZE, 50);
    testBaseInterval = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_B_BUTTON_INTERVAL, 50);
  }

  private void initViews() {
    validateByTestType((MainActivity.TestType) getIntent().getSerializableExtra(KeyMap.INTENT_SWIPE_TEST_TYPE));
    testBasePointY = screenSize.second - testButtonSize;
    manager = new SwipeManager(
        testBasePointX, testBasePointY, testBaseInterval, screenSize.first, screenSize.second,
        testButtonSize, type);
    Pair<Integer, Integer> basePosition = manager.getCurrentPosition();
    // Set start position of base button.
    changePosition(binding.baseButton, basePosition.first, basePosition.second);
    // Set start position of target view.
    Pair<Integer, Integer> mirrorPosition = manager.getMirrorCurrentPosition();
    changePosition(binding.targetLayout, mirrorPosition.first, mirrorPosition.second);

    binding.baseButton.setOnTouchListener(this);
    binding.nextButton.setOnClickListener(this);

    RelativeLayout.LayoutParams baseParams = (RelativeLayout.LayoutParams) binding.baseButton.getLayoutParams();
    baseParams.width = testButtonSize;
    baseParams.height = testButtonSize;
    binding.baseButton.setLayoutParams(baseParams);

    RelativeLayout.LayoutParams targetParams = (RelativeLayout.LayoutParams) binding.targetLayout.getLayoutParams();
    targetParams.width = testButtonSize;
    targetParams.height = testButtonSize;
    binding.targetLayout.setLayoutParams(targetParams);

    binding.baseButton.setVisibility(GONE);
    binding.targetLayout.setVisibility(GONE);
  }

  private void validateCsvManager(SharedPreferences sharedPreferences) {
    name = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_NAME, "");
    deviceNumber = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_DEVICE_NUMBER, "");
    postureType = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_POSTURE_TYPE, MainActivity.PostureType.UNKNOWN.name());
    handType = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_HAND_TYPE, MainActivity.HandType.UNKNOWN.name());
    testType = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_TEST_TYPE, MainActivity.TestType.UNKNOWN.name());
    dragCsvManager.createCsvWriter(
        getApplicationContext(),
        new String[] { testType },
        String.format("%s_%s_%s_%s_%s_drag.csv",
            name, deviceNumber, handType, postureType, CommonUtil.formattedDate(new Date())),
        dragCsvColumns);
    touchUpCsvManager.createCsvWriter(
        getApplicationContext(),
        new String[] { testType },
        String.format("%s_%s_%s_%s_%s_touch_up.csv",
            name, deviceNumber, handType, postureType, CommonUtil.formattedDate(new Date())),
        touchUpCsvColumns);
  }

  private void validateByTestType(MainActivity.TestType testType) {
    int swipeButtonMargin = getResources().getDimensionPixelSize(R.dimen.common_margin_short);

    switch (testType) {
      case TEST_B:
        type = SwipeType.HORIZONTAL;
        testBasePointX = swipeButtonMargin;
        binding.instruction.setText(R.string.test_b_instruction);
        break;
      case TEST_C:
        type = SwipeType.HORIZONTAL;
        testBasePointX = screenSize.first - swipeButtonMargin - testButtonSize;
        binding.instruction.setText(R.string.test_c_instruction);
        break;
      case TEST_D:
        type = SwipeType.VERTICAL;
        testBasePointX = swipeButtonMargin;
        binding.instruction.setText(R.string.test_d_instruction);
        break;
      default:
        throw new RuntimeException("Invalid test type: " + testType);
    }
  }

  @Override
  public void onBackPressed() {
    dragCsvManager.safeClear();
    touchUpCsvManager.safeClear();
    finish();
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    if (view.getId() != R.id.base_button) {
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
        dropBaseButton(nestPosition);
        return true;
      default:
        return true;
    }
  }

  @Override
  public void onClick(View view) {
    if (view.getId() != R.id.next_button) {
      throw new RuntimeException("Invalid click view: " + view.toString());
    }
    if (manager.getCurrentPosition() != null) {
      // Start test.
      startTest();
      return;
    }
    // End test.
    finish();
  }

  private void startTest() {
    binding.instructionLayout.setVisibility(GONE);
    binding.baseButton.setVisibility(VISIBLE);
    binding.targetLayout.setVisibility(VISIBLE);
    validateCsvManager(getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE));
    timer.start();
  }

  private void clickBaseButton(Pair<Integer, Integer> nestPosition) {
    Pair<Integer, Integer> measuredPosition = getMeasuredPositionLegacy(
        binding.baseButton, nestPosition);
    System.out.println("Base button click: " + measuredPosition.first + ", " + measuredPosition.second);
    if (!isClickedInCircle(toCenterPosition(manager.getCurrentPosition(), testButtonSize), measuredPosition, testButtonSize)) {
      System.out.println("Out of circle: " + measuredPosition.first + ", " + measuredPosition.second);
      return;
    }
    Pair<Integer, Integer> touchPosition = toTouchPosition(measuredPosition, testButtonSize);
    changePosition(binding.baseButton, touchPosition.first, touchPosition.second);
  }

  private void moveBaseButton(Pair<Integer, Integer> nestPosition) {
    Pair<Integer, Integer> measuredPosition = getMeasuredPositionLegacy(
        binding.baseButton, nestPosition);
    System.out.println("Base button move: " + measuredPosition.first + ", " + measuredPosition.second);
    Pair<Integer, Integer> touchPosition = toTouchPosition(measuredPosition, testButtonSize);
    changePosition(binding.baseButton, touchPosition.first, touchPosition.second);
    // CSV에 드래그 좌표를 입력.
    writeDragCsv(manager.getCount(), touchCounter, touchPosition.first, touchPosition.second);
    touchCounter++;
  }

  private void dropBaseButton(Pair<Integer, Integer> nestPosition) {
    Pair<Integer, Integer> measuredPosition = getMeasuredPositionLegacy(
        binding.baseButton, nestPosition);
    System.out.println("Base button drop: " + measuredPosition.first + ", " + measuredPosition.second);
    touchCounter = 0;
    if (!checkSuccess(
        toCenterPosition(getPosition(binding.baseButton), testButtonSize),
        getPosition(binding.targetLayout),
        testButtonSize)) {
      // 버튼 위치를 다시 원상복구 시킴.
      changePosition(
          binding.baseButton, manager.getCurrentPosition().first,
          manager.getCurrentPosition().second);
      // CSV에 실패 기록을 남김.
      writeTouchUpCsv(
          manager.getMirrorCurrentPosition().first, manager.getMirrorCurrentPosition().second,
          false, timer.elapse(false));
      return;
    }
    // CSV에 성공 기록을 남김.
    writeTouchUpCsv(
        manager.getMirrorCurrentPosition().first, manager.getMirrorCurrentPosition().second,
        true, timer.elapse(true));
    manager.increaseCount();
    Pair<Integer, Integer> newPosition = manager.getCurrentPosition();
    if (newPosition == null) {
      // 실험 끝 페이지로 넘어가가.
      setFinishTest();
      return;
    }
    changePosition(
        binding.baseButton, manager.getCurrentPosition().first,
        manager.getCurrentPosition().second);
    changePosition(
        binding.targetLayout, manager.getMirrorCurrentPosition().first,
        manager.getMirrorCurrentPosition().second);
  }

  private Pair<Integer, Integer> toTouchPosition(Pair<Integer, Integer> position, int buttonSize) {
    return Pair.create(position.first - buttonSize / 2, position.second - buttonSize / 2);
  }

  private boolean checkSuccess(Pair<Integer, Integer> centerPosition,
                               Pair<Integer, Integer> targetPosition, int targetSize) {
    return centerPosition.first >= targetPosition.first
        && centerPosition.first <= targetPosition.first + targetSize
        && centerPosition.second >= targetPosition.second
        && centerPosition.second <= targetPosition.second + targetSize;
  }

  private void setFinishTest() {
    binding.baseButton.setVisibility(GONE);
    binding.targetLayout.setVisibility(GONE);
    binding.instructionLayout.setVisibility(VISIBLE);
    binding.instruction.setText(R.string.end_instruction);
    binding.nextButton.setText(R.string.back_button);

    dragCsvManager.clear();
    touchUpCsvManager.clear();
  }

  /*
    "드래그 번호",
    "터치 번호",
    "드래그 x 좌표",
    "드래그 y 좌표",
  */
  private void writeDragCsv(int dragCount, int touchCount, int dragX, int dragY) {
    dragCsvManager.write(new String[] {
        String.valueOf(dragCount),
        String.valueOf(touchCount),
        String.valueOf(toMillimeterCsvCoordinate(
            dragX, screenSize.first, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeterCsvCoordinate(
            dragY, screenSize.second, getResources().getDisplayMetrics())),
    });
  }

  /*
    "타겟 x 좌표",
    "타겟 y 좌표",
    "성공 여부",
    "시간",
  */
  private void writeTouchUpCsv(int targetX, int targetY, boolean success, long elapsedTimeMillis) {
    Pair<Integer, Integer> targetCenter = CommonUtil.toCenterPosition(
        Pair.create(targetX, targetY), testButtonSize);
    touchUpCsvManager.write(new String[] {
        String.valueOf(toMillimeterCsvCoordinate(
            targetCenter.first, screenSize.first, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeterCsvCoordinate(
            targetCenter.second, screenSize.second, getResources().getDisplayMetrics())),
        success ? "SUCCESS" : "FAILED",
        String.format(Locale.KOREA, "%dms", elapsedTimeMillis),
    });
  }
}
