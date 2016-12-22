package totoro.project.interaction.interactiontestproject;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import totoro.project.interaction.interactiontestproject.databinding.PointActivityBinding;
import totoro.project.interaction.interactiontestproject.point.PointManager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.changePosition;
import static totoro.project.interaction.interactiontestproject.common.CommonUtil.getMeasuredPosition;

public class PointActivity extends AppCompatActivity implements
    View.OnTouchListener, View.OnClickListener {

  private PointActivityBinding binding;

  private Pair<Integer, Integer> screenSize;

  private PointManager manager;
  private int testBasePointX = 300;
  private int testBasePointY = 400;
  private double testBaseDegree = 22.5;
  private int testMaxCount = 50;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.point_activity);
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
    manager = new PointManager(
        testBasePointX, testBasePointY, getResources().getDimensionPixelSize(R.dimen.base_radius),
        testBaseDegree, screenSize.first, screenSize.second,
        getResources().getDimensionPixelSize(R.dimen.point_button_size), testMaxCount);
    // Set start position of base button.
    changePosition(
        binding.baseButtonLayout, testBasePointX, testBasePointY);
    // Set start position of target button.
    Pair<Integer, Integer> firstTargetPosition = manager.getCurrentPoint();
    changePosition(
        binding.targetButtonLayout, firstTargetPosition.first, firstTargetPosition.second);

    binding.baseButton.setOnTouchListener(this);
    binding.targetButton.setOnTouchListener(this);
    binding.nextButton.setOnClickListener(this);

    binding.baseButtonLayout.setVisibility(GONE);
    binding.targetButtonLayout.setVisibility(GONE);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int x = (int) event.getX();
    int y = (int) event.getY();
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        // TODO(totoro): CSV에 오류를 입력해야함.
        System.out.println("Touched " + x + ", " + y);
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
      case R.id.base_button:
        handleClickBaseButton(nestPosition);
        return true;
      case R.id.target_button:
        handleClickTargetButton(nestPosition);
        return true;
      default:
        throw new RuntimeException("Invalid touch view ID: " + view.getId());
    }
  }

  @Override
  public void onClick(View view) {
    if (view.getId() != R.id.next_button) {
      throw new RuntimeException("Invalid click view: " + view.toString());
    }
    if (manager.getCurrentPoint() != null) {
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
  }

  private void handleClickBaseButton(Pair<Integer, Integer> nestPosition) {
    Pair<Integer, Integer> measuredPosition = getMeasuredPosition(
        binding.baseButtonLayout, nestPosition);
    System.out.println("Base button click: " + measuredPosition.first + ", " + measuredPosition.second);
    // TODO(totoro): CSV에 성공여부를 입력해야함.
    manager.increaseCount();
    Pair<Integer, Integer> point = manager.getCurrentPoint();
    if (point == null) {
      // TODO(totoro): 실험 끝 페이지로 넘어가야함.
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
    // TODO(totoro): CSV에 성공여부를 입력해야함.
    manager.increaseCount();
    Pair<Integer, Integer> point = manager.getCurrentPoint();
    if (point == null) {
      // TODO(totoro): 실험 끝 페이지로 넘어가야함.
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
  }
}
