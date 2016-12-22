package totoro.project.interaction.interactiontestproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import totoro.project.interaction.interactiontestproject.csv.CsvManager;
import totoro.project.interaction.interactiontestproject.databinding.MainActivityBinding;

public class MainActivity extends AppCompatActivity implements
    RadioGroup.OnCheckedChangeListener, View.OnClickListener {

  public enum PostureType {
    UNKNOWN, SIT, STAND, LIE_DOWN,
  }

  public enum HandType {
    UNKNOWN, ONE, HORIZONTAL_BOTH, VERTICAL_BOTH,
  }

  public enum TestType {
    UNKNOWN, TEST_A, TEST_B, TEST_C, TEST_D, TEST_E,
  }

  private MainActivityBinding binding;

  private String name = "";
  private String deviceNumber = "";
  private PostureType postureType = PostureType.UNKNOWN;
  private HandType handType = HandType.UNKNOWN;
  private TestType testType = TestType.UNKNOWN;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.main_activity);

    validateSharedPreferences();

    binding.name.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        name = s.toString();
        System.out.println("Name input: " + name);
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });
    binding.deviceNumber.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing.
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        deviceNumber = s.toString();
        System.out.println("DeviceNumber input: " + deviceNumber);
      }

      @Override
      public void afterTextChanged(Editable s) {
        // Do nothing.
      }
    });
    binding.postureType.setOnCheckedChangeListener(this);
    binding.handType.setOnCheckedChangeListener(this);
    binding.testType.setOnCheckedChangeListener(this);
    binding.nextButton.setOnClickListener(this);
  }

  private void validateSharedPreferences() {
    SharedPreferences sharedPreferences =
        getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE);
    name = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_NAME, "");
    binding.name.setText(name);
    deviceNumber = sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_DEVICE_NUMBER, "");
    binding.deviceNumber.setText(deviceNumber);
    postureType = PostureType.valueOf(
        sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_POSTURE_TYPE, PostureType.UNKNOWN.name()));
    checkPostureType(postureType);
    handType = HandType.valueOf(
        sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_HAND_TYPE, HandType.UNKNOWN.name()));
    checkHandType(handType);
    testType = TestType.valueOf(
        sharedPreferences.getString(KeyMap.SHARED_PREFERENCES_TEST_TYPE, TestType.UNKNOWN.name()));
    checkTestType(testType);
  }

  /** RadioGroup methods */
  @Override
  public void onCheckedChanged(RadioGroup group, int checkedId) {
    switch (group.getId()) {
      case R.id.posture_type:
        handlePostureType(checkedId);
        System.out.println("PostureType input: " + postureType.name());
        break;
      case R.id.hand_type:
        handleHandType(checkedId);
        System.out.println("HandType input: " + handType.name());
        break;
      case R.id.test_type:
        handleTestType(checkedId);
        System.out.println("TestType input: " + testType.name());
        break;
      default:
        throw new RuntimeException("Invalid radio group type: " + group.toString());
    }
  }

  private void handlePostureType(int type) {
    switch (type) {
      case R.id.posture_type_sit:
        postureType = PostureType.SIT;
        break;
      case R.id.posture_type_stand:
        postureType = PostureType.STAND;
        break;
      case R.id.posture_type_lie_down:
        postureType = PostureType.LIE_DOWN;
        break;
      default:
        throw new RuntimeException("Invalid posture type: " + type);
    }
  }

  private void checkPostureType(PostureType type) {
    switch (type) {
      case UNKNOWN:
        return;
      case SIT:
        binding.postureType.check(R.id.posture_type_sit);
        break;
      case STAND:
        binding.postureType.check(R.id.posture_type_stand);
        break;
      case LIE_DOWN:
        binding.postureType.check(R.id.posture_type_lie_down);
        break;
      default:
        throw new RuntimeException("Invalid posture type: " + type);
    }
  }

  private void handleHandType(int type) {
    switch (type) {
      case R.id.hand_type_one:
        handType = HandType.ONE;
        break;
      case R.id.hand_type_horizontal_both:
        handType = HandType.HORIZONTAL_BOTH;
        break;
      case R.id.hand_type_vertical_both:
        handType = HandType.VERTICAL_BOTH;
        break;
      default:
        throw new RuntimeException("Invalid hand type: " + type);
    }
  }

  private void checkHandType(HandType type) {
    switch (type) {
      case UNKNOWN:
        return;
      case ONE:
        binding.handType.check(R.id.hand_type_one);
        break;
      case HORIZONTAL_BOTH:
        binding.handType.check(R.id.hand_type_horizontal_both);
        break;
      case VERTICAL_BOTH:
        binding.handType.check(R.id.hand_type_vertical_both);
        break;
      default:
        throw new RuntimeException("Invalid hand type: " + type);
    }
  }

  private void handleTestType(int type) {
    switch (type) {
      case R.id.test_type_a:
        testType = TestType.TEST_A;
        break;
      case R.id.test_type_b:
        testType = TestType.TEST_B;
        break;
      case R.id.test_type_c:
        testType = TestType.TEST_C;
        break;
      case R.id.test_type_d:
        testType = TestType.TEST_D;
        break;
      case R.id.test_type_e:
        testType = TestType.TEST_E;
        break;
      default:
        throw new RuntimeException("Invalid test type: " + type);
    }
  }

  private void checkTestType(TestType type) {
    switch (type) {
      case UNKNOWN:
        return;
      case TEST_A:
        binding.testType.check(R.id.test_type_a);
        break;
      case TEST_B:
        binding.testType.check(R.id.test_type_b);
        break;
      case TEST_C:
        binding.testType.check(R.id.test_type_c);
        break;
      case TEST_D:
        binding.testType.check(R.id.test_type_d);
        break;
      case TEST_E:
        binding.testType.check(R.id.test_type_e);
        break;
      default:
        throw new RuntimeException("Invalid test type: " + type);
    }
  }

  /** OnClickListener methods */
  @Override
  public void onClick(View view) {
    if (view.getId() != R.id.next_button) {
      throw new RuntimeException("Invalid click button: " + view.toString());
    }
    // TODO(totoro): Data들이 모두 설정되었는지 체크해야함.
    if (name.isEmpty() || deviceNumber.isEmpty() || postureType == PostureType.UNKNOWN
        || handType == HandType.UNKNOWN || testType == TestType.UNKNOWN) {
      Toast
          .makeText(
              this, getResources().getString(R.string.incomplete_input_error), Toast.LENGTH_SHORT)
          .show();
      return;
    }
    SharedPreferences.Editor editor =
        getSharedPreferences(KeyMap.SHARED_PREFERENCES_ROOT, MODE_PRIVATE).edit();
    editor.putString(KeyMap.SHARED_PREFERENCES_NAME, name);
    editor.putString(KeyMap.SHARED_PREFERENCES_DEVICE_NUMBER, deviceNumber);
    editor.putString(KeyMap.SHARED_PREFERENCES_POSTURE_TYPE, postureType.name());
    editor.putString(KeyMap.SHARED_PREFERENCES_HAND_TYPE, handType.name());
    editor.putString(KeyMap.SHARED_PREFERENCES_TEST_TYPE, testType.name());
    editor.apply();

    startTest(testType);
  }

  private void startTest(TestType type) {
    switch (type) {
      case TEST_A: {
        Intent intent = new Intent(MainActivity.this, PointActivity.class);
        startActivity(intent);
        break;
      }
      case TEST_B:
        // Fall through.
      case TEST_C:
        // Fall through.
      case TEST_D: {
        Intent intent = new Intent(MainActivity.this, SwipeActivity.class);
        intent.putExtra(KeyMap.INTENT_SWIPE_TEST_TYPE, testType);
        startActivity(intent);
        break;
      }
      case TEST_E: {
        Intent intent = new Intent(MainActivity.this, SlideNotiActivity.class);
        startActivity(intent);
        break;
      }
      default:
        throw new RuntimeException("Invalid test type: " + type);
    }
  }
}
