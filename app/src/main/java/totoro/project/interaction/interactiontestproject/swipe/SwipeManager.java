package totoro.project.interaction.interactiontestproject.swipe;

import android.support.annotation.Nullable;
import android.util.Pair;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import totoro.project.interaction.interactiontestproject.swipe.SwipeGenerator.SwipeType;

public class SwipeManager {

  private int count;
  private Pair<Integer, Integer> basePoint;
  private List<Pair<Integer, Integer>> positions;

  private int screenWidth;
  private int screenHeight;
  private int buttonSize;
  private SwipeType type;

  public SwipeManager(int baseX, int baseY, int interval, int screenWidth, int screenHeight,
                      int buttonSize, SwipeType type) {
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.buttonSize = buttonSize;
    this.type = type;
    basePoint = Pair.create(baseX, baseY);
    // Generates positions.
    SwipeGenerator generator = new SwipeGenerator(
        basePoint, interval, screenWidth, screenHeight, buttonSize);
    positions = generator.makePositions(type);
    Collections.shuffle(positions);
  }

  public void resetCount() {
    count = 0;
    Collections.shuffle(positions);
  }

  public void increaseCount() {
    count++;
  }

  public int getCount() {
    return count;
  }

  @Nullable
  public Pair<Integer, Integer> getMirrorCurrentPosition() {
    if (positions.size() <= count) {
      // Done.
      return null;
    }
    Pair<Integer, Integer> position = positions.get(count);
    switch (type) {
      case HORIZONTAL:
        return Pair.create(screenWidth / 2 - buttonSize / 2, position.second);
      case VERTICAL:
        return Pair.create(position.first, screenHeight / 2 - buttonSize / 2);
      default:
        throw new RuntimeException("Invalid swipe type: " + type);
    }
  }

  @Nullable
  public Pair<Integer, Integer> getCurrentPosition() {
    if (positions.size() <= count) {
      // Done.
      return null;
    }
    return positions.get(count);
  }
}
