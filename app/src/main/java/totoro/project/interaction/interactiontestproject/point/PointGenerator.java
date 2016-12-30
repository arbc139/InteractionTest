package totoro.project.interaction.interactiontestproject.point;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import totoro.project.interaction.interactiontestproject.common.Position;

public class PointGenerator {

  private Position basePoint;
  private int baseRadius;
  private double baseDegree;
  private int screenWidth;
  private int screenHeight;
  private int buttonSize;

  public PointGenerator(Pair<Integer, Integer> basePoint, int baseRadius, double baseDegree,
                        int screenWidth, int screenHeight, int buttonSize) {
    this.basePoint = new Position(basePoint);
    this.baseRadius = baseRadius;
    // 이 값으로 반드시 180이 나누어 떨어져야 함.
    this.baseDegree = baseDegree;
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.buttonSize = buttonSize;
  }

  public List<Position> makePositions() {
    List<Position> result = new ArrayList<>();
    for (int degree = 0; degree <= 180; degree += baseDegree) {
      Pair<Double, Double> delta = Pair.create(
          Math.cos(Math.toRadians(degree)), Math.sin(Math.toRadians(degree)));
      result.addAll(makeLinePositions(delta, degree));
      result.addAll(makeLinePositions(Pair.create(-delta.first, -delta.second), degree));
    }
    return result;
  }

  // 한 라인의 point들을 생성하는 함수.
  private List<Position> makeLinePositions(Pair<Double, Double> delta, int degree) {
    List<Position> result = new ArrayList<>();
    for (int radius = baseRadius; ; radius += baseRadius) {
      Position position = new Position(
          Double.valueOf(basePoint.x + radius * delta.first).intValue(),
          Double.valueOf(basePoint.y + radius * delta.second).intValue());
      position.id = String.format(Locale.KOREA, "%d, %d", radius, degree);
      if (!checkAvailable(position)) {
        break;
      }
      result.add(position);
    }
    return result;
  }

  private boolean checkAvailable(Position position) {
    return position.x > 0 && position.y > 0 && position.x < screenWidth - buttonSize
        && position.y < screenHeight - buttonSize;
  }
}
