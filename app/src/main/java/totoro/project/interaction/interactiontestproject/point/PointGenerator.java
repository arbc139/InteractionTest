package totoro.project.interaction.interactiontestproject.point;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PointGenerator {

  private Pair<Integer, Integer> basePoint;
  private int baseRadius;
  private double baseDegree;
  private int screenWidth;
  private int screenHeight;
  private int buttonSize;

  public PointGenerator(Pair<Integer, Integer> basePoint, int baseRadius, double baseDegree,
                        int screenWidth, int screenHeight, int buttonSize) {
    this.basePoint = basePoint;
    this.baseRadius = baseRadius;
    // 이 값으로 반드시 180이 나누어 떨어져야 함.
    this.baseDegree = baseDegree;
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.buttonSize = buttonSize;
  }

  public List<Pair<Integer, Integer>> makePositions() {
    List<Pair<Integer, Integer>> result = new ArrayList<>();
    for (int degree = 0; degree <= 180; degree += baseDegree) {
      Pair<Double, Double> delta = Pair.create(
          Math.cos(Math.toRadians(degree)), Math.sin(Math.toRadians(degree)));
      result.addAll(makeLinePositions(delta));
      result.addAll(makeLinePositions(Pair.create(-delta.first, -delta.second)));
    }
    return result;
  }

  // 한 라인의 point들을 생성하는 함수.
  private List<Pair<Integer, Integer>> makeLinePositions(Pair<Double, Double> delta) {
    List<Pair<Integer, Integer>> result = new ArrayList<>();
    for (int radius = baseRadius; ; radius += baseRadius) {
      Pair<Integer, Integer> position = Pair.create(
          Double.valueOf(basePoint.first + radius * delta.first).intValue(),
          Double.valueOf(basePoint.second + radius * delta.second).intValue());
      if (!checkAvailable(position)) {
        break;
      }
      result.add(position);
    }
    return result;
  }

  private boolean checkAvailable(Pair<Integer, Integer> position) {
    return position.first > 0 && position.second > 0 && position.first < screenWidth - buttonSize
        && position.second < screenHeight - buttonSize;
  }
}
