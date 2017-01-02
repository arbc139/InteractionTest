package totoro.project.interaction.interactiontestproject;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import totoro.project.interaction.interactiontestproject.common.CommonUtil;
import totoro.project.interaction.interactiontestproject.csv.CsvManager;
import totoro.project.interaction.interactiontestproject.databinding.SlideNotiActivityBinding;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.toMillimeterCsvCoordinate;

public class SlideNotiActivity extends AppCompatActivity implements
    View.OnTouchListener, View.OnClickListener {

  private SlideNotiActivityBinding binding;

  private Pair<Integer, Integer> screenSize;

  private boolean isFinished = false;
  private boolean isDragged = false;

  private CsvManager dragCsvManager = new CsvManager();
  private CsvManager touchUpCsvManager = new CsvManager();

  private String name;
  private String deviceNumber;
  private String postureType;
  private String handType;
  private String testType;

  private int screenHideHeight = 0;

  private int dragCounter = 0;
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
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.slide_noti_activity);
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
  }

  public void initViews() {
    binding.flightButton.setOnTouchListener(this);
    binding.wifiButton.setOnTouchListener(this);
    binding.bluetoothButton.setOnTouchListener(this);
    binding.rotationButton.setOnTouchListener(this);
    binding.panelLayout.setOnTouchListener(this);
    binding.notiBar.setOnTouchListener(this);
    binding.nextButton.setOnClickListener(this);

    binding.panelLayout.setVisibility(GONE);
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

  @Override
  public void onClick(View view) {
    if (view.getId() != R.id.next_button) {
      throw new RuntimeException("Invalid click view: " + view.toString());
    }
    if (!isFinished) {
      // Start test.
      startTest();
      return;
    }
    // End test.
    finish();
  }

  @Override
  public void onBackPressed() {
    dragCsvManager.safeClear();
    touchUpCsvManager.safeClear();
    finish();
  }

  private void startTest() {
    binding.instructionLayout.setVisibility(GONE);
    binding.panelLayout.setVisibility(VISIBLE);
    validateCsvManager(getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE));
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    switch (view.getId()) {
      case R.id.panel_layout:
        return handleClickDragNotiBar(event);
      case R.id.noti_bar:
        return false;
      case R.id.wifi_button:
        return handleClickWifiButton(event);
      case R.id.flight_button:
      case R.id.bluetooth_button:
      case R.id.rotation_button:
        return handleClickOtherButton(event);
      default:
        throw new RuntimeException("Invalid touch view ID: " + view.getId());
    }
  }

  private boolean handleClickDragNotiBar(MotionEvent event) {
    Pair<Integer, Integer> position = Pair.create((int) event.getX(), (int) event.getY());
    SlidingUpPanelLayout.PanelState panelState = binding.panelLayout.getPanelState();

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        if (isDragged) {
          // CSV에 실패 기록을 남김.
          writeTouchUpCsv(position.first, position.second, false);
        }
        isDragged = false;
        break;
      case MotionEvent.ACTION_MOVE:
        if (panelState == SlidingUpPanelLayout.PanelState.DRAGGING) {
          isDragged = true;
          // Success case.
          // CSV에 성공 기록을 남김.
          writeDragCsv(dragCounter, touchCounter, position.first, position.second);
          System.out.println("drag notibar success: " + position.first + ", " + position.second);
        } else {
          System.out.println("failed notibar position: " + position.first + ", " + position.second);
          System.out.println("Panel state: " + panelState.name());
        }
        touchCounter++;
        break;
      case MotionEvent.ACTION_UP:
        touchCounter = 0;
        dragCounter++;
        isDragged = false;
    }
    return false;
  }

  private boolean handleClickWifiButton(MotionEvent event) {
    if (event.getAction() != MotionEvent.ACTION_UP) {
      return false;
    }
    Pair<Integer, Integer> position = CommonUtil.getMeasuredPosition(
        binding.wifiButton, Pair.create((int) event.getX(), (int) event.getY()));
    System.out.println("Wifi button position: " + position.first + ", " + position.second);
    // CSV에 성공 기록을 남김.
    writeTouchUpCsv(position.first, position.second, true);
    binding.wifiButtonLayout.setBackground(getResources().getDrawable(R.drawable.circle_accent));
    binding.wifiButton.setColorFilter(ContextCompat.getColor(this, R.color.background_primary));
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
      @Override
      public void run() {
        setFinishTest();
      }
    }, TimeUnit.SECONDS.toMillis(1));
    // Only trigger once.
    return false;
  }

  private boolean handleClickOtherButton(MotionEvent event) {
    if (event.getAction() != MotionEvent.ACTION_UP) {
      return false;
    }
    Pair<Integer, Integer> position = CommonUtil.getMeasuredPosition(
        binding.wifiButton, Pair.create((int) event.getX(), (int) event.getY()));
    System.out.println("Wifi button position: " + position.first + ", " + position.second);
    // CSV에 실패 기록을 남김.
    writeTouchUpCsv(position.first, position.second, false);
    return false;
  }

  private void setFinishTest() {
    isFinished = true;
    binding.panelLayout.setVisibility(GONE);
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
  */
  private void writeTouchUpCsv(int touchX, int touchY, boolean success) {
    touchUpCsvManager.write(new String[] {
        String.valueOf(toMillimeterCsvCoordinate(
            touchX, screenSize.first, getResources().getDisplayMetrics())),
        String.valueOf(toMillimeterCsvCoordinate(
            touchY, screenSize.second, getResources().getDisplayMetrics())),
        success ? "SUCCESS" : "FAILED",
    });
  }
}
