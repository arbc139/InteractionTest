package totoro.project.interaction.interactiontestproject.point;

import android.support.annotation.Nullable;
import android.util.Pair;

import java.util.Collections;
import java.util.List;

public class PointManager {

  private int count;
  private Pair<Integer, Integer> basePoint;
  private List<Pair<Integer, Integer>> positions;
  private int maxCount;

  public PointManager(int baseX, int baseY, int baseRadius, double baseDegree, int screenWidth,
                      int screenHeight, int buttonSize, int maxCount) {
    this.maxCount = maxCount;
    basePoint = Pair.create(baseX, baseY);
    // Generates positions.
    PointGenerator generator = new PointGenerator(
        basePoint, baseRadius, baseDegree, screenWidth, screenHeight, buttonSize);
    positions = generator.makePositions();
    Collections.shuffle(positions);
  }

  public void resetCount() {
    count = 0;
    Collections.shuffle(positions);
  }

  public void increaseCount() {
    count++;
  }

  @Nullable
  public Pair<Integer, Integer> getCurrentPoint() {
    if (count >= maxCount) {
      // Done.
      return null;
    }
    if (positions.size() <= count / 2) {
      // Reset count.
      resetCount();
    }
    return positions.get(Math.max(count / 2, 0));
  }
}
