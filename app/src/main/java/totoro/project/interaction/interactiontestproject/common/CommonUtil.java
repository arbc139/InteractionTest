package totoro.project.interaction.interactiontestproject.common;

import android.util.Pair;
import android.view.View;
import android.widget.RelativeLayout;

public class CommonUtil {

  public static Pair<Integer, Integer> toCenterPosition(Pair<Integer, Integer> position,
                                                        int buttonSize) {
    return Pair.create(position.first + buttonSize / 2, position.second + buttonSize / 2);
  }

  @Deprecated
  public static Pair<Integer, Integer> getPosition(View view) {
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
    return Pair.create(params.leftMargin, params.topMargin);
  }

  @Deprecated
  public static Pair<Integer, Integer> getMeasuredPositionLegacy(View view,
                                                                 Pair<Integer, Integer> touchPosition) {
    Pair<Integer, Integer> viewPosition = getPosition(view);
    return Pair.create(
        touchPosition.first + viewPosition.first, touchPosition.second + viewPosition.second);
  }

  public static Pair<Integer, Integer> getMeasuredPosition(View view,
                                                           Pair<Integer, Integer> touchPosition) {
    Pair<Integer, Integer> viewPosition = getRecursivePosition(view);
    return Pair.create(
        touchPosition.first + viewPosition.first, touchPosition.second + viewPosition.second);
  }

  public static void changePosition(View view, int x, int y) {
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
    params.setMargins(x, y, 0, 0);
    view.setLayoutParams(params);
  }

  public static Pair<Integer, Integer> getRecursivePosition(View view) {
    return Pair.create(getRecursiveLeft(view), getRecursiveTop(view));
  }

  private static int getRecursiveLeft(View view) {
    if (view.getParent() == view.getRootView())
      return view.getLeft();
    else
      return view.getLeft() + getRecursiveLeft((View) view.getParent());
  }

  private static int getRecursiveTop(View view) {
    if (view.getParent() == view.getRootView())
      return view.getTop();
    else
      return view.getTop() + getRecursiveTop((View) view.getParent());
  }
}
