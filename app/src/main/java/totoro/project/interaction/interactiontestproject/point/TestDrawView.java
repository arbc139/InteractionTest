package totoro.project.interaction.interactiontestproject.point;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

import totoro.project.interaction.interactiontestproject.R;

public class TestDrawView extends View {

  List<Pair<Integer, Integer>> points;
  Paint paint;

  public TestDrawView(Context context, AttributeSet attrs) {
    super(context, attrs);
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    System.out.println("DY - TestDrawView, width height: " + size.x + ", " + size.y);
    PointGenerator generator = new PointGenerator(Pair.create(300, 400), 20, 30, size.x, size.y, 40);
    points = generator.makePositions();
    paint = new Paint();
    paint.setColor(getResources().getColor(R.color.accent));
  }

  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    for (Pair<Integer, Integer> point : points) {
      canvas.drawCircle(point.first, point.second, 5, paint);
    }
  }
}
