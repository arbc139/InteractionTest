package totoro.project.interaction.interactiontestproject;

import static org.junit.Assert.assertEquals;

import android.util.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

import totoro.project.interaction.interactiontestproject.point.PointGenerator;

@RunWith(RobolectricTestRunner.class)
public class PointGeneratorTest {

  @Test
  public void makePointsTest() {
    /**
     * basePoint: (120, 60)
     * baseRadius: 100
     * baseDegree: 30
     * screenWidth: 640
     * screenHeight: 320
     * buttonSize: 30
     */
    PointGenerator generator = new PointGenerator(Pair.create(120, 60), 100, 30, 640, 320, 30);
    for (Pair<Integer, Integer> point : generator.makePositions()) {
      System.out.println("Pair.create(" + point.first + ", " + point.second + ")");
    }
    assertEquals(
        generator.makePositions(),
        Arrays.asList(
            Pair.create(220, 60),
            Pair.create(320, 60),
            Pair.create(420, 60),
            Pair.create(520, 60),
            Pair.create(20, 60),
            Pair.create(206, 110),
            Pair.create(293, 160),
            Pair.create(379, 209),
            Pair.create(466, 260),
            Pair.create(33, 10),
            Pair.create(170, 146),
            Pair.create(220, 233),
            Pair.create(120, 160),
            Pair.create(120, 260),
            Pair.create(70, 146),
            Pair.create(20, 233),
            Pair.create(33, 110),
            Pair.create(206, 10),
            Pair.create(20, 60),
            Pair.create(220, 59),
            Pair.create(320, 59),
            Pair.create(420, 59),
            Pair.create(520, 59)));
  }
}
