package totoro.project.interaction.interactiontestproject.common;

import android.util.Pair;

import java.util.Locale;

public class Position {

  public String id;
  public int x;
  public int y;

  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Position(Pair<Integer, Integer> position) {
    x = position.first;
    y = position.second;
  }

  public Pair<Integer, Integer> toPair() {
    return Pair.create(x, y);
  }

  @Override
  public String toString() {
    return String.format(Locale.KOREA, "ID: (%s), x: %d, y: %d", id, x, y);
  }

  @Override
  public boolean equals(Object target) {
    if (!(target instanceof Position)) {
      throw new RuntimeException();
    }
    Position targetPos = (Position) target;
    return id.equals(targetPos.id) && x == targetPos.x && y == targetPos.y;
  }
}
