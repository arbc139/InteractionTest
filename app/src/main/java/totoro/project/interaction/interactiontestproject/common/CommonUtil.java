package totoro.project.interaction.interactiontestproject.common;

import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import totoro.project.interaction.interactiontestproject.R;

public class CommonUtil {

  public static Pair<Integer, Integer> toCenterPosition(Pair<Integer, Integer> position,
                                                        int buttonSize) {
    return Pair.create(position.first + buttonSize / 2, position.second + buttonSize / 2);
  }

  public static Pair<Float, Float> toCenterPosition(Pair<Float, Float> position,
                                                    float buttonSize) {
    return Pair.create(position.first + buttonSize / 2, position.second + buttonSize / 2);
  }
  
  public static Pair<Integer, Integer> toOriginalPosition(Pair<Integer, Integer> centerPosition,
                                                          int buttonSize) {
    return Pair.create(
        centerPosition.first - buttonSize / 2, centerPosition.second - buttonSize / 2);
  }

  public static Pair<Float, Float> toOriginalPosition(Pair<Float, Float> centerPosition,
                                                      float buttonSize) {
    return Pair.create(
        centerPosition.first - buttonSize / 2, centerPosition.second - buttonSize / 2);
  }

  public static Pair<Float, Float> toOriginalCsvCoorPosition(Pair<Float, Float> centerPosition,
                                                                 float buttonSize) {
    return Pair.create(
        centerPosition.first + buttonSize / 2, centerPosition.second + buttonSize / 2);
  }

  public static Pair<Float, Float> toCenterCsvCoorPosition(Pair<Float, Float> centerPosition,
                                                           float buttonSize) {
    return Pair.create(
        centerPosition.first - buttonSize / 2, centerPosition.second - buttonSize / 2);
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
    if (view.getId() == R.id.main_layout || view.getParent() == view.getRootView())
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

  public static double getDistance(Pair<Integer, Integer> positionA, Pair<Integer, Integer> positionB) {
    return Math.sqrt(
        Math.pow(positionA.first - positionB.first, 2) + Math.pow(positionA.second - positionB.second, 2));
  }


  public static boolean isClickedInCircle(Pair<Integer, Integer> centerPosition,
                                          Pair<Integer, Integer> touchPosition, int buttonSize) {
    System.out.println("isClickedInCircle center: " + centerPosition.first + ", " + centerPosition.second);
    System.out.println("isClickedInCircle touch: " + touchPosition.first + ", " + touchPosition.second);
    System.out.println("isClickedInCircle distance, radius: " + getDistance(centerPosition, touchPosition) + ", " + buttonSize / 2);
    if (getDistance(centerPosition, touchPosition) <= (buttonSize / 2)) {
      return true;
    } else{
      return false;
    }
  }


  public static float toPixel(float millimeter, DisplayMetrics metrics) {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, millimeter, metrics);
  }

  public static float toMillimeter(float pixel, DisplayMetrics metrics) {
    return pixel / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, metrics);
  }

  public static String formattedDate(Date date) {
    SimpleDateFormat formatter = new SimpleDateFormat("(yyyy_MM_dd_hh-mm-ss)", Locale.KOREA);
    return formatter.format(date);
  }

  public static float toMillimeterCsvCoordinate(float pixel, int screenSize,
                                                DisplayMetrics metrics) {
    float csvCoorPixel = screenSize - pixel;
    return toMillimeter(csvCoorPixel, metrics);
  }

  public static float toPixelAppCoordinate(float millimeter, int screenSize,
                                           DisplayMetrics metrics) {
    float pixel = toPixel(millimeter, metrics);
    return screenSize - pixel;
  }
}
