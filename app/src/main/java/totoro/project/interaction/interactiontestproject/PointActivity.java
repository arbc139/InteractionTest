package totoro.project.interaction.interactiontestproject;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import totoro.project.interaction.interactiontestproject.databinding.PointActivityBinding;
import totoro.project.interaction.interactiontestproject.point.PointManager;
import totoro.project.interaction.interactiontestproject.timer.Timer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.changePosition;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.getMeasuredPosition;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.toMillimeter;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.toMillimeterCsvCoordinate;

public class PointActivity extends AppCompatActivity implements
    View.OnTouchListener, View.OnClickListener {

  private PointActivityBinding binding;

  private Pair<Integer, Integer> screenSize;

  private PointManager pointManager;
  private CsvManager csvManager = new CsvManager();
  private Timer timer = new Timer();

  private String name;
  private String deviceNumber;
  private String postureType;
  private String handType;
  private String testType;

  private int screenHideHeight = 0;
  private int testBasePointX = 300;
  private int testBasePointY = 400;
  private int testButtonSize = 50;
  private double testButtonDegree = 22.5;
  private int testButtonRadius = 50;
  private int testMaxCount = 50;

  private final String[] csvColumns = new String[] {
      "횟수 번호",
      "피험자 번호",
      "기기 번호",
      "자세",
      "손자세",
      "타겟 번호",
      "타겟 x 좌표",
      "타겟 y 좌표",
      "홈버튼 중심에서 실제 타겟 중심까지 거리 (A)",
      "타겟 지름 (R)",
      "타겟 기준 거리",
      "타겟 기준 각도",
      "타겟 ID",
      "과업 성공 여부",
      "실제 터치 x 좌표",
      "실제 터치 y 좌표",
      "델타 x",
      "델타 y",
      "타겟 중심에서 실제 터치 중심까지 거리 (B)",
      "홈버튼 중심에서 실제 터치 중심까지 거리 (C)",
      "dx",
      "터치 성공까지 걸린 시간",
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.point_activity);

    validateSharedPreferences();

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
        testBasePointX = screenSize.first - testBasePointX;
        testBasePointY = screenSize.second - testBasePointY;
        initViews();
      }
    });
  }

  private void validateSharedPreferences() {
    SharedPreferences sharedPreferences =
        getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE);
    screenHideHeight = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_SCREEN_HIDE_HEIGHT, 0);
    testBasePointX = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_X, 300);
    testBasePointY = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BASE_Y, 400);
    testButtonSize = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_SIZE, 50);
    testButtonDegree = sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_DEGREE, 22.5f);
    testButtonRadius = (int) sharedPreferences.getFloat(KeyMap.SHARED_PREFERENCES_SETTING_A_BUTTON_RADIUS, 50);
    testMaxCount = sharedPreferences.getInt(KeyMap.SHARED_PREFERENCES_SETTING_A_TEST_COUNT, 50);
  }

  private void initViews() {
    System.out.println("testBasePointX: " + testBasePointX);
    System.out.println("testBasePointY: " + testBasePointX);
    System.out.println("testButtonRadius: " + testButtonRadius);
    System.out.println("testButtonDegree: " + testButtonDegree);
    System.out.println("screenSize: " + screenSize.first + ", " + screenSize.second);
    System.out.println("testButtonSize: " + testButtonSize);
    System.out.println("testMaxCount: " + testMaxCount);
    pointManager = new PointManager(
        testBasePointX, testBasePointY, testButtonRadius, testButtonDegree, screenSize.first,
        screenSize.second, testButtonSize, testMaxCount);
    // Set start position of base button.
    changePosition(
        binding.baseButtonLayout, testBasePointX, testBasePointY);
    // Set start position of target button.
    Pair<Integer, Integer> firstTargetPosition = pointManager.getCurrentPoint();
    changePosition(
        binding.targetButtonLayout, firstTargetPosition.first, firstTargetPosition.second);

    binding.baseButtonLayout.setOnTouchListener(this);
    binding.targetButtonLayout.setOnTouchListener(this);
    binding.nextButton.setOnClickListener(this);

    RelativeLayout.LayoutParams baseParams = (RelativeLayout.LayoutParams) binding.baseButtonLayout.getLayoutParams();
    baseParams.width = testButtonSize;
    baseParams.height = testButtonSize;
    binding.baseButtonLayout.setLayoutParams(baseParams);

    RelativeLayout.LayoutParams targetParams = (RelativeLayout.LayoutParams) binding.targetButtonLayout.getLayoutParams();
    targetParams.width = testButtonSize;
    targetParams.height = testButtonSize;
    binding.targetButtonLayout.setLayoutParams(targetParams);

    binding.baseButtonLayout.setVisibility(GONE);
    binding.targetButtonLayout.setVisibility(GONE);
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
  public boolean onTouchEvent(MotionEvent event) {
    int x = (int) event.getX();
    int y = (int) event.getY();
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        // CSV에 오류를 입력.
        System.out.println("Touched " + x + ", " + y);
        Pair<Integer, Integer> buttonPosition = getCurrentShowButtonPosition();
        writeCsv(
            pointManager.getCount(), pointManager.getTargetNumber(), buttonPosition.first,
            buttonPosition.second, false, x, y, timer.elapse(false));
      case MotionEvent.ACTION_MOVE:
      case MotionEvent.ACTION_UP:
    }
    return false;
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    if (event.getAction() != MotionEvent.ACTION_DOWN) {
      return false;
    }
    Pair<Integer, Integer> nestPosition = Pair.create((int) event.getX(), (int) event.getY());
    switch (view.getId()) {
      case R.id.base_button_layout:
        handleClickBaseButton(nestPosition);
        return true;
      case R.id.target_button_layout:
        handleClickTargetButton(nestPosition);
        return true;
      default:
        throw new RuntimeException("Invalid touch view ID: " + view.getId());
    }
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
    if (pointManager.getCurrentPoint() != null) {
      // Start test.
      startTest();
      return;
    }
    // End test.
    finish();
  }

  private void startTest() {
    binding.instructionLayout.setVisibility(GONE);
    binding.baseButtonLayout.setVisibility(VISIBLE);
    binding.targetButtonLayout.setVisibility(GONE);
    validateCsvManager(getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE));
    timer.start();
  }

  private void handleClickBaseButton(Pair<Integer, Integer> nestPosition) {
    Pair<Integer, Integer> measuredPosition = getMeasuredPosition(
        binding.baseButtonLayout, nestPosition);
    System.out.println("Base button click: " + measuredPosition.first + ", " + measuredPosition.second);
    // CSV에 성공여부를 입력.
    Pair<Integer, Integer> buttonPosition = getCurrentShowButtonPosition();
    writeCsv(
        pointManager.getCount(), pointManager.getTargetNumber(), buttonPosition.first,
        buttonPosition.second, true, measuredPosition.first,
        measuredPosition.second, timer.elapse(true));

    pointManager.increaseCount();
    Pair<Integer, Integer> point = pointManager.getCurrentPoint();
    if (point == null) {
      // 실험 끝 페이지로 넘어가기.
      setFinishTest();
      return;
    }
    binding.baseButtonLayout.setVisibility(GONE);
    binding.targetButtonLayout.setVisibility(VISIBLE);
  }

  private void handleClickTargetButton(Pair<Integer, Integer> nestPosition) {
    Pair<Integer, Integer> measuredPosition = getMeasuredPosition(
        binding.targetButtonLayout, nestPosition);
    System.out.println("Target button click: " + measuredPosition.first + ", " + measuredPosition.second);
    // CSV에 성공여부를 입력.
    Pair<Integer, Integer> buttonPosition = getCurrentShowButtonPosition();
    writeCsv(
        pointManager.getCount(), pointManager.getTargetNumber(), buttonPosition.first,
        buttonPosition.second, true, measuredPosition.first,
        measuredPosition.second, timer.elapse(true));

    pointManager.increaseCount();
    Pair<Integer, Integer> point = pointManager.getCurrentPoint();
    if (point == null) {
      // 실험 끝 페이지로 넘어가기.
      setFinishTest();
      return;
    }
    changePosition(binding.targetButtonLayout, point.first, point.second);
    binding.baseButtonLayout.setVisibility(VISIBLE);
    binding.targetButtonLayout.setVisibility(GONE);
  }

  private void setFinishTest() {
    binding.baseButtonLayout.setVisibility(GONE);
    binding.targetButtonLayout.setVisibility(GONE);
    binding.instructionLayout.setVisibility(VISIBLE);
    binding.instruction.setText(R.string.end_instruction);
    binding.nextButton.setText(R.string.back_button);

    csvManager.clear();
  }

  private Pair<Integer, Integer> getCurrentShowButtonPosition() {
    // 타겟버튼
    if (pointManager.getCount() % 2 == 1) {
      return pointManager.getCurrentPoint();
    }
    return Pair.create(testBasePointX, testBasePointY);
  }

  /*
    "횟수 번호",
    "피험자 번호",
    "기기 번호",
    "자세",
    "손자세",
    "타겟 번호",
    "타겟 x 좌표",
    "타겟 y 좌표",
    "홈버튼 중심에서 실제 타겟 중심까지 거리 (A)",
    "타겟 지름 (R)",
    "타겟 기준 거리",
    "타겟 기준 각도",
    "타겟 ID",
    "과업 성공 여부",
    "실제 터치 x 좌표",
    "실제 터치 y 좌표",
    "델타 x",
    "델타 y",
    "타겟 중심에서 실제 터치 중심까지 거리 (B)",
    "홈버튼 중심에서 실제 터치 중심까지 거리 (C)",
    "dx",
    "터치 성공까지 걸린 시간",
   */
  private void writeCsv(int count,
                        /* String name, String deviceNumber, String postureType, String handType, */
                        String targetNumber, int targetX, int targetY, /* int homeTargetDistance, */
                        /* int targetDiameter, int buttonRadius, int buttonDegree, */
                        /* double targetId, */ boolean success, int touchX, int touchY,
                        /* int deltaX, int deltaY, int targetTouchDistance, int homeTouchDistance,*/
                        /* int dx,*/
                        long elapsedTimeMillis) {
    Pair<Integer, Integer> targetCenter = CommonUtil.toCenterPosition(
        Pair.create(targetX, targetY), testButtonSize);
    Pair<Integer, Integer> homeCenter = CommonUtil.toCenterPosition(
        Pair.create(testBasePointX, testBasePointY), testButtonSize);
    Pair<Integer, Integer> touch = Pair.create(touchX, touchY);

    double homeTargetDistance = CommonUtil.getDistance(homeCenter, targetCenter); // A
    double targetTouchDistance = CommonUtil.getDistance(targetCenter, touch); // B
    double homeTouchDistance = CommonUtil.getDistance(homeCenter, touch); // C
    int deltaX = touchX - targetCenter.first;
    int deltaY = touchY - targetCenter.second;
    double targetId = pointManager.getTargetId(testButtonSize, homeTargetDistance);
    double dx = (Math.pow(homeTouchDistance, 2) - Math.pow(targetTouchDistance, 2) - Math.pow(homeTargetDistance, 2)) / (2 * homeTargetDistance);

    csvManager.write(new String[] {
        String.valueOf(count),
        name,
        deviceNumber,
        postureType,
        handType,
        targetNumber,
        String.valueOf(toMillimeterCsvCoordinate(
            targetCenter.first, screenSize.first, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeterCsvCoordinate(
            targetCenter.second, screenSize.second, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeter(
            Double.valueOf(homeTargetDistance).floatValue(), getResources().getDisplayMetrics())),
        String.valueOf(toMillimeter(testButtonSize, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeter(testButtonRadius, getResources().getDisplayMetrics())),
        String.valueOf(testButtonDegree),
        String.valueOf(targetId),
        success ? "SUCCESS" : "FAILED",
        String.valueOf(toMillimeterCsvCoordinate(
            touchX, screenSize.first, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeterCsvCoordinate(
            touchY, screenSize.second, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeter(deltaX, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeter(deltaY, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeter(
            Double.valueOf(targetTouchDistance).floatValue(), getResources().getDisplayMetrics())),
        String.valueOf(toMillimeter(
            Double.valueOf(homeTouchDistance).floatValue(), getResources().getDisplayMetrics())),
        String.valueOf(dx),
        String.format(Locale.KOREA, "%dms", elapsedTimeMillis),
    });
  }
}
