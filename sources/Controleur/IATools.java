package Controleur;

import java.util.ArrayList;

/**
 * Pair
 */
class Pair<T1, T2> {
  public T1 first;
  public T2 second;

  public Pair(T1 first, T2 second) {
    this.first = first;
    this.second = second;
  }
}

enum Direction {
  LEFT(-1, 0), //
  RIGHT(1, 0), //
  UP(0, -1), //
  DOWN(0, 1);//

  int c;
  int l;

  Direction(int c, int l) {
    this.c = c;
    this.l = l;
  }
}

/**
 * IAAssistance
 */
class PriorityPoint {
  int c;
  int l;
  Integer prio;

  PriorityPoint(int c, int l, int prio) {
    this.c = c;
    this.l = l;
    this.prio = prio;
  }

  PriorityPoint(PriorityPoint pp) {
    this.c = pp.c;
    this.l = pp.l;
    this.prio = pp.prio;
  }

  public PriorityPoint add(Direction d) {
    c += d.c;
    l += d.l;
    return this;
  }

  /**
   * @param l the l to set
   */
  public void setPoint(int c, int l) {
    this.c = c;
    this.l = l;
  }

  public ArrayList<Pair<PriorityPoint, Direction>> getNeighbor() {
    ArrayList<Pair<PriorityPoint, Direction>> list = new ArrayList<>();

    for (Direction d : Direction.values()) {
      // on rajoute 1 au chemin
      list.add(new Pair<>((new PriorityPoint(c, l, prio + 1)).add(d), d));
    }

    return list;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(l) * 13 + Integer.hashCode(c);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PriorityPoint) {
      PriorityPoint pp = (PriorityPoint) obj;
      return c == pp.c && l == pp.l;
    }
    return false;
  }
}