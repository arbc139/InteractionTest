package totoro.project.interaction.interactiontestproject.timer;

import java.util.Date;

public class Timer {

  private long startTimeMillis;
  private long elapsedTimeMillis;
  private long endTimeMillis;

  public void clear() {
    startTimeMillis = 0;
    elapsedTimeMillis = 0;
    endTimeMillis = 0;
  }

  public void start() {
    startTimeMillis = new Date().getTime();
    elapsedTimeMillis = startTimeMillis;
  }

  public long elapse(boolean success) {
    long currentTimeMillis = new Date().getTime();
    long interval = currentTimeMillis - elapsedTimeMillis;
    if (success) {
      elapsedTimeMillis = currentTimeMillis;
    }
    return interval;
  }
}
