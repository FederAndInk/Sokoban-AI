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

	/**
	 * @param l the l to set
	 */
	public void setPoint(int c, int l) {
		this.c = c;
		this.l = l;
	}

	public ArrayList<PriorityPoint> getNeighbor() {
		ArrayList<PriorityPoint> list = new ArrayList<>();

		list.add(new PriorityPoint(c + 1, l, c + 1));
		list.add(new PriorityPoint(c, l + 1, c + 1));
		list.add(new PriorityPoint(c - 1, l, c + 1));
		list.add(new PriorityPoint(c, l - 1, c + 1));

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
	HashMap<PriorityPoint, Integer> accessibles;
	PriorityPoint curr;

	public IAAssistance() {
		logger = Configuration.instance().logger();
		accessibles = new HashMap<>();
	}

	@Override
	public void initialise() {
		logger.info("Démarrage d'un nouveau niveau de taille " + niveau.lignes() + "x" + niveau.colonnes());
	}

	@Override
	public void joue() {
		int lP = niveau.lignePousseur();
		int cP = niveau.colonnePousseur();
		if (curr == null || curr.c != cP || curr.l != lP) {
			curr = new PriorityPoint(cP, lP, 0);
			logger.info("Update AI accessibles");
			accessibles.clear();
			for (int l = 0; l < niveau.lignes(); l++) {
				for (int c = 0; c < niveau.colonnes(); c++) {
					int marque = niveau.marque(l, c);
					if (marque != 0)
						controle.marquer(l, c, 0);
				}
			}

			PriorityQueue<PriorityPoint> pq = new PriorityQueue<>((p1, p2) -> {
				return p1.prio.compareTo(p2.prio);
			});

			pq.add(new PriorityPoint(cP, lP, 0));

			while (!pq.isEmpty()) {
				PriorityPoint pp = pq.remove();
				controle.marquer(pp.l, pp.c, 1);
				for (PriorityPoint ngb : pp.getNeighbor()) {
					if (niveau.estOccupable(ngb.l, ngb.c)) {
						if (!accessibles.containsKey(ngb) || ngb.prio < accessibles.get(ngb)) {
							accessibles.put(ngb, ngb.prio);
							pq.remove(ngb);
							pq.add(ngb);
						}
					}
				}
			}
			controle.marquer(lP, cP, 2);

			controle.jeu.metAJour();
		}
	}

	@Override
	public void finalise() {
		logger.info("Niveau terminé en " + niveau.nbPas() + " pas, et " + niveau.nbPoussees() + " Poussees !");
	}
}