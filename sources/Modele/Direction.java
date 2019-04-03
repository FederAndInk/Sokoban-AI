package Modele;

public enum Direction {
  LEFT(-1, 0), //
  RIGHT(1, 0), //
  UP(0, -1), //
  DOWN(0, 1);//

  public int dC;
  public int dL;

  Direction(int dC, int dL) {
    this.dC = dC;
    this.dL = dL;
  }

  public static Direction getDirection(int dC, int dL) {
    if (dC == -1 && dL == 0) {
      return LEFT;
    } else if (dC == 1 && dL == 0) {
      return RIGHT;
    } else if (dC == 0 && dL == -1) {
      return UP;
    } else if (dC == 0 && dL == 1) {
      return DOWN;
    } else {
      return null;
    }
  }

  public static Direction getDirection(PriorityPoint p1, PriorityPoint p2) {
    int dC = p2.c - p1.c;
    int dL = p2.l - p1.l;
    return getDirection(dC, dL);
  }

}