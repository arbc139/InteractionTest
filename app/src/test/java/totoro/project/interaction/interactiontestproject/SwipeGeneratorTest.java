package totoro.project.interaction.interactiontestproject;

import static org.junit.Assert.assertEquals;

import android.util.Pair;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;

import totoro.project.interaction.interactiontestproject.swipe.SwipeGenerator;
import totoro.project.interaction.interactiontestproject.swipe.SwipeGenerator.SwipeType;

@RunWith(RobolectricTestRunner.class)
public class SwipeGeneratorTest {

  @Test
  public void makePointsHorizontalTest() {
    /**
     * basePoint: (0, 290)
     * baseScale: 20
     * screenWidth: 640
     * screenHeight: 320
     * buttonSize: 10
     */
    SwipeGenerator generator = new SwipeGenerator(Pair.create(0, 290), 20, 640, 320, 10);
    for (Pair<Integer, Integer> point : generator.makePositions(SwipeType.HORIZONTAL)) {
      System.out.print("Pair.create(" + point.first + ", " + point.second + "), ");
    }
    assertEquals(
        generator.makePositions(SwipeType.HORIZONTAL),
        Arrays.asList(
            Pair.create(0, 290), Pair.create(0, 270), Pair.create(0, 250), Pair.create(0, 230),
            Pair.create(0, 210), Pair.create(0, 190), Pair.create(0, 170)));
  }

  @Test
  public void makePointsVerticalTest() {
    /**
     * basePoint: (0, 290)
     * baseScale: 20
     * screenWidth: 640
     * screenHeight: 320
     * buttonSize: 10
     */
    SwipeGenerator generator = new SwipeGenerator(Pair.create(0, 290), 20, 640, 320, 10);
    for (Pair<Integer, Integer> point : generator.makePositions(SwipeType.VERTICAL)) {
      System.out.print("Pair.create(" + point.first + ", " + point.second + "), ");
    }
    assertEquals(
        generator.makePositions(SwipeType.VERTICAL),
        Arrays.asList(
            Pair.create(0, 290), Pair.create(20, 290), Pair.create(40, 290), Pair.create(60, 290),
            Pair.create(80, 290), Pair.create(100, 290), Pair.create(120, 290),
            Pair.create(140, 290), Pair.create(160, 290), Pair.create(180, 290),
            Pair.create(200, 290), Pair.create(220, 290), Pair.create(240, 290),
            Pair.create(260, 290), Pair.create(280, 290), Pair.create(300, 290),
            Pair.create(320, 290), Pair.create(340, 290), Pair.create(360, 290),
            Pair.create(380, 290), Pair.create(400, 290), Pair.create(420, 290),
            Pair.create(440, 290), Pair.create(460, 290), Pair.create(480, 290),
            Pair.create(500, 290), Pair.create(520, 290), Pair.create(540, 290),
            Pair.create(560, 290), Pair.create(580, 290), Pair.create(600, 290),
            Pair.create(620, 290)));
  }
}
