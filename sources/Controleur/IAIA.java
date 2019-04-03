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
import Modele.Direction;
import Modele.NiveauConsultable;
import Modele.Pair;
import Modele.PriorityPoint;

/**
 * LevelState
 */
class LevelState {
	PriorityPoint player;
	PriorityPoint box;
	NiveauConsultable niv;

	LevelState(NiveauConsultable niv) {
		this.player = new PriorityPoint(niv.colonnePousseur(), niv.lignePousseur(), 0);
		this.niv = niv;
		this.box = getBoites();
		Configuration.instance().logger().info("Create levelState with\n - player on: " + player + "\n - box on: " + box);
	}

	private LevelState(PriorityPoint player, PriorityPoint box, NiveauConsultable niv) {
		this.player = player;
		this.box = box;
		this.niv = niv;
		Configuration.instance().logger().info("Create levelState with\n - player on: " + player + "\n - box on: " + box);
	}

	public Integer getNbSteps() {
		return player.prio;
	}

	public boolean isResolved() {
		return niv.aBut(box.l, box.c);
	}

	ArrayList<LevelState> getNeighbor() {
		ArrayList<LevelState> ar = new ArrayList<>();
		for (Pair<PriorityPoint, Direction> p : player.getNeighbor()) {
			// if "accessibles" with pousse boite
			PriorityPoint pP = p.first;
			// if (isInBounds(pP)) {
			if (isFree(pP) || (isPushable(pP, p.second))) {
				PriorityPoint nextBox = box;
				if (isBox(pP)) { // already pushable
					nextBox = new PriorityPoint(box);
					nextBox.add(p.second);
					nextBox.prio++;
				}

				ar.add(new LevelState(p.first, nextBox, niv));
			}
			// }

		}
		return ar;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LevelState) {
			LevelState ls = (LevelState) obj;
			return ls.box.equals(box) && ls.player.equals(player);
		}
		return false;
	}

	@Override
	// Notre fonction Hashcode permettant de hash une paire
	public int hashCode() {
		return player.hashCode() * 13 + box.hashCode();
	}

	private PriorityPoint getBoites() {
		for (int l = 0; l < niv.lignes(); l++) {
			for (int c = 0; c < niv.colonnes(); c++) {
				if (niv.aCaisse(l, c)) {
					return new PriorityPoint(c, l, 0);
				}
			}
		}
		return null;
	}

	private boolean isInBounds(PriorityPoint p) {
		return niv.estDedans(p.l, p.c);
	}

	private boolean isBox(PriorityPoint p) {
		return box.equals(p);
	}

	private boolean isFree(PriorityPoint p) {
		return isInBounds(p) && !niv.aMur(p.l, p.c) && !isBox(p);
	}

	private boolean isPushable(PriorityPoint p, Direction d) {
		PriorityPoint ppPlusLoin = new PriorityPoint(p).add(d);
		return isBox(p) && isFree(ppPlusLoin);
	}

}

public class IAIA extends IA {
	Logger logger;
	ArrayList<Direction> path;
	int currPosPath;
	PriorityPoint lastPos;

	public IAIA() {
		logger = Configuration.instance().logger();
		path = new ArrayList<>();
	}

	private PriorityPoint getPlayerPos() {
		return new PriorityPoint(niveau.colonnePousseur(), niveau.lignePousseur(), 0);
	}

	@Override
	public void initialise() {
		HashMap<LevelState, Pair<Integer, LevelState>> accessibles = new HashMap<>();
		logger.info("Démarrage d'un nouveau niveau de taille " + niveau.lignes() + "x" + niveau.colonnes());
		int lP = niveau.lignePousseur();
		int cP = niveau.colonnePousseur();
		lastPos = getPlayerPos();
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

		// Dijktra commence
		// Le constructeur prend une fonction servant à déterminer le tri des éléments
		// (la comparaison)
		PriorityQueue<LevelState> pq = new PriorityQueue<>((ls1, ls2) -> {
			// minimize the number of player steps
			return ls1.getNbSteps().compareTo(ls2.getNbSteps());
		});

		// L'origine est la position du pousseur :
		LevelState lvlState = new LevelState(niveau);
		pq.add(lvlState);
		accessibles.put(lvlState, new Pair<>(0, lvlState));
		while (!pq.isEmpty() && (lvlState == null || !lvlState.isResolved())) {
			lvlState = pq.remove();

			for (LevelState lsNbg : lvlState.getNeighbor()) {
				// Si on vient de trouver le premier chemin vers la case
				// ou on a trouvé un chemin plus court
				if (!accessibles.containsKey(lsNbg) || lsNbg.getNbSteps() < accessibles.get(lsNbg).first) {
					accessibles.put(lsNbg, new Pair<>(lsNbg.getNbSteps(), lvlState));
					pq.remove(lsNbg);
					pq.add(lsNbg);
					// update prev
				}
			}
		}
		// fin de dijkstra
		// arrivé ici, soit on a notre solution, soit on a tout analysé et il n'y a pas
		// de solution

		if (lvlState.isResolved()) {
			// on affiche la solution trouvée
			path.clear();
			showPath(accessibles, lvlState);
			currPosPath = 0;
		}
	}

	@Override
	public void joue() {
		if (!lastPos.equals(getPlayerPos())) {
			initialise();
		}
		Direction d = path.get(currPosPath++);
		controle.jeu.jouer(d.dL, d.dC);
		lastPos = getPlayerPos();
		controle.jeu.metAJour();
	}

	private void showPath(HashMap<LevelState, Pair<Integer, LevelState>> accessibles, LevelState lvlState) {
		Pair<Integer, LevelState> pair = accessibles.get(lvlState);
		if (pair.first != 0) {
			showPath(accessibles, pair.second);
			Direction d = Direction.getDirection(pair.second.player, lvlState.player);
			path.add(d);
			System.out.println(d + ": " + lvlState.player);
		} else {
			System.out.println("Begin: " + lvlState.player);
		}
	}

	@Override
	public void finalise() {
		logger.info("Niveau terminé en " + niveau.nbPas() + " pas, et " + niveau.nbPoussees() + " Poussees !");
	}
}
