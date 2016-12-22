package totoro.project.interaction.interactiontestproject;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import totoro.project.interaction.interactiontestproject.databinding.SwipeActivityBinding;
import totoro.project.interaction.interactiontestproject.swipe.SwipeGenerator.SwipeType;
import totoro.project.interaction.interactiontestproject.swipe.SwipeManager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.changePosition;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.getMeasuredPositionLegacy;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.getPosition;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.toCenterPosition;

public class SwipeActivity extends AppCompatActivity implements
    View.OnTouchListener, View.OnClickListener {

  private SwipeActivityBinding binding;

  private Pair<Integer, Integer> screenSize;

  private SwipeManager manager;
  private SwipeType type;
  private int buttonSize;
  private int testBasePointX;
  private int testBasePointY;
  private int testBaseScale;

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

        //Now you can get the width and height from content
        System.out.println("View width, height: " + content.getMeasuredWidth() + ", " + content.getMeasuredHeight());
        screenSize = Pair.create(content.getMeasuredWidth(), content.getMeasuredHeight());
        initViews();
      }
    });
  }

  private void initViews() {
    buttonSize = getResources().getDimensionPixelSize(R.dimen.swipe_button_size);
    validateByTestType((MainActivity.TestType) getIntent().getSerializableExtra(KeyMap.INTENT_SWIPE_TEST_TYPE));
    testBasePointY = screenSize.second - buttonSize;
    testBaseScale = getResources().getDimensionPixelSize(R.dimen.base_scale);
    manager = new SwipeManager(
        testBasePointX, testBasePointY, testBaseScale, screenSize.first, screenSize.second,
        buttonSize, type);
    Pair<Integer, Integer> basePosition = manager.getCurrentPosition();
    // Set start position of base button.
    changePosition(binding.baseButton, basePosition.first, basePosition.second);
    // Set start position of target view.
    Pair<Integer, Integer> mirrorPosition = manager.getMirrorCurrentPosition();
    changePosition(binding.targetLayout, mirrorPosition.first, mirrorPosition.second);

    binding.baseButton.setOnTouchListener(this);
    binding.nextButton.setOnClickListener(this);

    binding.baseButton.setVisibility(GONE);
    binding.targetLayout.setVisibility(GONE);
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
        testBasePointX = screenSize.first - swipeButtonMargin - buttonSize;
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
  }

  private void clickBaseButton(Pair<Integer, Integer> nestPosition) {
    Pair<Integer, Integer> measuredPosition = getMeasuredPositionLegacy(
        binding.baseButton, nestPosition);
    System.out.println("Base button click: " + measuredPosition.first + ", " + measuredPosition.second);
    Pair<Integer, Integer> touchPosition = toTouchPosition(measuredPosition, buttonSize);
    changePosition(binding.baseButton, touchPosition.first, touchPosition.second);
    // TODO(totoro): CSV에 클릭 성공여부를 입력해야함.
  }

  private void moveBaseButton(Pair<Integer, Integer> nestPosition) {
    Pair<Integer, Integer> measuredPosition = getMeasuredPositionLegacy(
        binding.baseButton, nestPosition);
    System.out.println("Base button move: " + measuredPosition.first + ", " + measuredPosition.second);
    Pair<Integer, Integer> touchPosition = toTouchPosition(measuredPosition, buttonSize);
    changePosition(binding.baseButton, touchPosition.first, touchPosition.second);
    // TODO(totoro): CSV에 드래그 좌표를 입력해야함.
  }

  private void dropBaseButton(Pair<Integer, Integer> nestPosition) {
    Pair<Integer, Integer> measuredPosition = getMeasuredPositionLegacy(
        binding.baseButton, nestPosition);
    System.out.println("Base button drop: " + measuredPosition.first + ", " + measuredPosition.second);
    if (!checkSuccess(
        toCenterPosition(getPosition(binding.baseButton), buttonSize),
        getPosition(binding.targetLayout),
        buttonSize)) {
      // 버튼 위치를 다시 원상복구 시킴.
      changePosition(
          binding.baseButton, manager.getCurrentPosition().first,
          manager.getCurrentPosition().second);
      return;
    }
    // TODO(totoro): CSV에 성공 기록을 남겨야 함.
    manager.increaseCount();
    Pair<Integer, Integer> newPosition = manager.getCurrentPosition();
    if (newPosition == null) {
      // TODO(totoro): 실험 끝 페이지로 넘어가야함.
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
  }
}
