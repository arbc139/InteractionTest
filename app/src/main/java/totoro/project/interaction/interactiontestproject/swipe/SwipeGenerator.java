package totoro.project.interaction.interactiontestproject.swipe;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class SwipeGenerator {

  public enum SwipeType {
    HORIZONTAL,
    VERTICAL,
  }

  private Pair<Integer, Integer> basePoint;
  private int baseInterval;
  private int screeenWidth;
  private int screenHeight;
  private int buttonSize;

  public SwipeGenerator(Pair<Integer, Integer> basePoint, int baseInterval, int screenWidth,
                        int screenHeight, int buttonSize) {
    this.basePoint = basePoint;
    this.baseInterval = baseInterval;
    this.screeenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.buttonSize = buttonSize;
  }

  public List<Pair<Integer, Integer>> makePositions(SwipeType type) {
    List<Pair<Integer, Integer>> result = new ArrayList<>();
    for (int interval = 0; ; interval += baseInterval) {
      Pair<Integer, Integer> position = makePosition(type, interval);
      if(!checkAvailable(type, position)) {
        break;
      }
      result.add(position);
    }
    return result;
  }

  private Pair<Integer, Integer> makePosition(SwipeType type, int interval) {
    switch (type) {
      case HORIZONTAL:
        return Pair.create(basePoint.first, basePoint.second - interval);
      case VERTICAL:
        return Pair.create(basePoint.first + interval, basePoint.second);
      default:
        throw new RuntimeException("Invalid swipe type: " + type);
    }
  }

  private boolean checkAvailable(SwipeType type, Pair<Integer, Integer> position) {
    switch (type) {
      case HORIZONTAL:
        return position.second > (screenHeight / 2 + buttonSize / 2);
      case VERTICAL:
        return position.first < (screeenWidth - buttonSize);
      default:
        throw new RuntimeException("Invalid swipe type: " + type);
    }
  }
}
