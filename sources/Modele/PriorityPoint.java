package Modele;

import java.util.ArrayList;

/**
 * IAAssistance
 */
public class PriorityPoint {
  public int c;
  public int l;
  public Integer prio;

  public PriorityPoint(int c, int l, int prio) {
    this.c = c;
    this.l = l;
    this.prio = prio;
  }

  public PriorityPoint(PriorityPoint pp) {
    this.c = pp.c;
    this.l = pp.l;
    this.prio = pp.prio;
  }

  public PriorityPoint add(Direction d) {
    c += d.dC;
    l += d.dL;
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

  @Override
  public String toString() {
    return "(l, c) = [" + l + ", " + c + "]";
  }
}