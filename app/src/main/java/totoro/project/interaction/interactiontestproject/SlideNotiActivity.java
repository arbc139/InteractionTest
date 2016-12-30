package totoro.project.interaction.interactiontestproject;

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

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.concurrent.TimeUnit;

import totoro.project.interaction.interactiontestproject.common.CommonUtil;
import totoro.project.interaction.interactiontestproject.databinding.SlideNotiActivityBinding;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SlideNotiActivity extends AppCompatActivity implements
    View.OnTouchListener, View.OnClickListener {

  private SlideNotiActivityBinding binding;

  private Pair<Integer, Integer> screenSize;

  private boolean notibarClickedState = false;
  private boolean isFinished = false;

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

        //Now you can get the width and height from content
        System.out.println("View width, height: " + content.getMeasuredWidth() + ", " + content.getMeasuredHeight());
        screenSize = Pair.create(content.getMeasuredWidth(), content.getMeasuredHeight());
        initViews();
      }
    });
  }

  public void initViews() {
    binding.wifiButton.setOnTouchListener(this);
    binding.mainLayout.setOnTouchListener(this);
    binding.notiBar.setOnTouchListener(this);
    binding.nextButton.setOnClickListener(this);

    binding.mainLayout.setVisibility(GONE);
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

  private void startTest() {
    binding.instructionLayout.setVisibility(GONE);
    binding.mainLayout.setVisibility(VISIBLE);
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    switch (view.getId()) {
      case R.id.main_layout:
        return handleClickDragNotiBar(event);
      case R.id.noti_bar:
        return false;
      case R.id.wifi_button:
        return handleClickWifiButton(event);
      default:
        throw new RuntimeException("Invalid touch view ID: " + view.getId());
    }
  }

  private boolean handleClickDragNotiBar(MotionEvent event) {
    Pair<Integer, Integer> position = Pair.create((int) event.getX(), (int) event.getY());
    SlidingUpPanelLayout.PanelState panelState = binding.mainLayout.getPanelState();

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        // Fall through.
      case MotionEvent.ACTION_MOVE:
        if (panelState == SlidingUpPanelLayout.PanelState.DRAGGING) {
          // Success case.
          // TODO(totoro): CSV에 성공 기록을 남겨야 함.
          System.out.println("drag notibar success: " + position.first + ", " + position.second);
        } else {
          // TODO(totoro): CSV에 실패 기록을 남겨야 함.
          System.out.println("failed notibar position: " + position.first + ", " + position.second);
          System.out.println("Panel state: " + panelState.name());
        }
    }
    return false;
  }

  private boolean handleClickWifiButton(MotionEvent event) {
    if (event.getAction() != MotionEvent.ACTION_UP) {
      return false;
    }
    // TODO(totoro): CSV에 성공 기록을 남겨야 함.
    Pair<Integer, Integer> position = CommonUtil.getMeasuredPosition(
        binding.wifiButton, Pair.create((int) event.getX(), (int) event.getY()));
    System.out.println("Wifi button position: " + position.first + ", " + position.second);
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

  private void setFinishTest() {
    isFinished = true;
    binding.mainLayout.setVisibility(GONE);
    binding.instructionLayout.setVisibility(VISIBLE);
    binding.instruction.setText(R.string.end_instruction);
    binding.nextButton.setText(R.string.back_button);
  }
}
