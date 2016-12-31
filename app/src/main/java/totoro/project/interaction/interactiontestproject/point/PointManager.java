package totoro.project.interaction.interactiontestproject.point;

import android.support.annotation.Nullable;
import android.util.Pair;

import java.util.Collections;
import java.util.List;

import totoro.project.interaction.interactiontestproject.common.Position;

public class PointManager {

  private int count;
  private Pair<Integer, Integer> basePoint;
  private List<Position> positions;
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

  public int getCount() {
    return count;
  }

  public void increaseCount() {
    count++;
  }

  public String getTargetNumber() {
    Position current = getCurrentPosition();
    if (current == null) {
      return "null";
    }
    return current.id;
  }

  public double getTargetId(int buttonSize, double homeTargetDistance) {
    return Math.log(buttonSize / homeTargetDistance) / Math.log(2);
  }

  @Nullable
  public Position getCurrentPosition() {
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

  @Nullable
  public Pair<Integer, Integer> getCurrentPoint() {
    Position current = getCurrentPosition();
    if (current == null) {
      return null;
    }
    return current.toPair();
  }

  public int getTargetCount() {
    return Math.max(count / 2, 0) + 1;
  }
}
