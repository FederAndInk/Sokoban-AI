/*
 * Sokoban - Encore une nouvelle version (à but pédagogique) du célèbre jeu
 * Copyright (C) 2018 Guillaume Huard
 * 
 * Ce programme est libre, vous pouvez le redistribuer et/ou le
 * modifier selon les termes de la Licence Publique Générale GNU publiée par la
 * Free Software Foundation (version 2 ou bien toute autre version ultérieure
 * choisie par vous).
 * 
 * Ce programme est distribué car potentiellement utile, mais SANS
 * AUCUNE GARANTIE, ni explicite ni implicite, y compris les garanties de
 * commercialisation ou d'adaptation dans un but spécifique. Reportez-vous à la
 * Licence Publique Générale GNU pour plus de détails.
 * 
 * Vous devez avoir reçu une copie de la Licence Publique Générale
 * GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,
 * États-Unis.
 * 
 * Contact:
 *          Guillaume.Huard@imag.fr
 *          Laboratoire LIG
 *          700 avenue centrale
 *          Domaine universitaire
 *          38401 Saint Martin d'Hères
 */
package Controleur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.logging.Logger;

import Global.Configuration;

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

public class IAAssistance extends IA {
	Logger logger;
	HashMap<PriorityPoint, Pair<Integer, PriorityPoint>> accessibles;
	PriorityPoint curr;

	public IAAssistance() {
		logger = Configuration.instance().logger();
		accessibles = new HashMap<>();
	}

	@Override
	public void initialise() {
		logger.info("Démarrage d'un nouveau niveau de taille " + niveau.lignes() + "x" + niveau.colonnes());
	}

	public boolean estPoussable(PriorityPoint pp, Direction d) {
		PriorityPoint ppPlusLoin = new PriorityPoint(pp).add(d);
		return niveau.aCaisse(pp.l, pp.c) && niveau.estOccupable(ppPlusLoin.l, ppPlusLoin.c);
	}

	@Override
	public void joue() {
		int lP = niveau.lignePousseur();
		int cP = niveau.colonnePousseur();
		if (curr == null || curr.c != cP || curr.l != lP) {
			curr = new PriorityPoint(cP, lP, 0);
			logger.info("Update AI accessibles");
			accessibles.clear();

			// on clear les marques posées
			for (int l = 0; l < niveau.lignes(); l++) {
				for (int c = 0; c < niveau.colonnes(); c++) {
					int marque = niveau.marque(l, c);
					if (marque != 0) {
						controle.marquer(l, c, 0);
					}
				}
			}

			// Dijktra begins
			// Le constructeur prend une fonction servant à déterminer le tri des éléments
			// (la comparaison)
			PriorityQueue<PriorityPoint> pq = new PriorityQueue<>((p1, p2) -> {
				return p1.prio.compareTo(p2.prio);
			});

			// L'origine est la position du pousseur :
			pq.add(new PriorityPoint(cP, lP, 0));

			while (!pq.isEmpty()) {
				PriorityPoint pp = pq.remove();
				// Check juste si la case n'est pas marron
				if (niveau.marque(pp.l, pp.c) != 2) {
					// On met une marque verte
					controle.marquer(pp.l, pp.c, 1);
				}
				for (Pair<PriorityPoint, Direction> pNgbDir : pp.getNeighbor()) {
					PriorityPoint ngb = pNgbDir.first;
					if (niveau.aCaisse(ngb.l, ngb.c) && estPoussable(ngb, pNgbDir.second)) {
						// ON marque la case suivant la boite grâce à la direction
						PriorityPoint ngbD = new PriorityPoint(ngb).add(pNgbDir.second);
						controle.marquer(ngbD.l, ngbD.c, 2);
					}
					// si la case est soit un goal, soit un vide
					if (niveau.estOccupable(ngb.l, ngb.c)) {
						// Si on vient de trouver le premier chemin vers la case
						// ou
						if (!accessibles.containsKey(ngb) || ngb.prio < accessibles.get(ngb).first) {
							accessibles.put(ngb, new Pair<>(ngb.prio, pp));
							pq.remove(ngb);
							pq.add(ngb);
							// update prev
						}
					}
				}
			}
			// dijkstra ends

			controle.jeu.metAJour();
		}
	}

	@Override
	public void finalise() {
		logger.info("Niveau terminé en " + niveau.nbPas() + " pas, et " + niveau.nbPoussees() + " Poussees !");
	}
}
