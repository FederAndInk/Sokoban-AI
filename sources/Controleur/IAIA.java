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
	HashMap<PriorityPoint, PriorityPoint> box;
	NiveauConsultable niv;
	boolean minPlayer;

	LevelState(NiveauConsultable niv, boolean minPlayer) {
		this.minPlayer = minPlayer;
		this.player = new PriorityPoint(niv.colonnePousseur(), niv.lignePousseur(), 0);
		this.niv = niv;
		initBoites();
	}

	public LevelState(LevelState ls) {
		player = new PriorityPoint(ls.player);
		box = new HashMap<>();
		niv = ls.niv;
		minPlayer = ls.minPlayer;
		for (PriorityPoint p : ls.box.values()) {
			PriorityPoint newP = new PriorityPoint(p);
			box.put(newP, newP);
		}
	}

	public Integer getNbSteps() {
		if (minPlayer) {
			return player.prio;
		} else {
			int boxPrios = 0;
			for (PriorityPoint b : box.values()) {
				boxPrios += b.prio * niv.lignes() * niv.colonnes();
			}
			return player.prio + boxPrios;
		}
	}

	public boolean isResolved() {
		boolean ret = true;
		for (PriorityPoint p : box.values()) {
			ret = ret && isGoal(p);
		}
		return ret;
	}

	private void move(Direction d) {
		player.add(d);
		player.prio++;
		if (isBox(player)) {
			box.remove(player);
			PriorityPoint newP = new PriorityPoint(player).add(d);
			newP.prio++;
			box.put(newP, newP);
		}
	}

	ArrayList<LevelState> getNeighbor() {
		ArrayList<LevelState> ar = new ArrayList<>();
		for (Pair<PriorityPoint, Direction> p : player.getNeighbor()) {
			PriorityPoint pP = p.first;
			if (isFree(pP) || (isPushable(pP, p.second))) {
				LevelState newLS = new LevelState(this);
				newLS.move(p.second);
				if (newLS.isSolvable()) {
					ar.add(newLS);
				}
			}

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
	public int hashCode() {
		return player.hashCode() * 13 + box.hashCode();
	}

	private HashMap<PriorityPoint, PriorityPoint> initBoites() {
		box = new HashMap<>();
		for (int l = 0; l < niv.lignes(); l++) {
			for (int c = 0; c < niv.colonnes(); c++) {
				if (niv.aCaisse(l, c)) {
					PriorityPoint p = new PriorityPoint(c, l, 0);
					box.put(p, p);
				}
			}
		}
		return null;
	}

	private boolean isInBounds(PriorityPoint p) {
		return niv.estDedans(p.l, p.c);
	}

	private boolean isBox(PriorityPoint p) {
		return box.containsKey(p);
	}

	private boolean isWall(PriorityPoint p) {
		return niv.aMur(p.l, p.c);
	}

	private boolean isGoal(PriorityPoint p) {
		return niv.aBut(p.l, p.c);
	}

	private boolean isFree(PriorityPoint p) {
		return isInBounds(p) && !isWall(p) && !isBox(p);
	}

	private boolean isSolvable() {
		boolean ret = true;
		for (PriorityPoint p : box.values()) {
			ret = ret && (isPushable(p) || isGoal(p));
		}
		return ret;
	}

	private boolean isPushable(PriorityPoint p) {
		if (isBox(p)) {
			PriorityPoint tl = new PriorityPoint(p).add(Direction.LEFT);
			PriorityPoint tr = new PriorityPoint(p).add(Direction.RIGHT);
			PriorityPoint tu = new PriorityPoint(p).add(Direction.UP);
			PriorityPoint td = new PriorityPoint(p).add(Direction.DOWN);
			boolean uord = isWall(tu) || isWall(td);
			return !((isWall(tl) && uord) || (isWall(tr) && uord));
		} else {
			throw new IllegalArgumentException("no box at " + p);
		}
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
	int nbExplored;

	public IAIA() {
		logger = Configuration.instance().logger();
		path = new ArrayList<>();
		nbExplored = 0;
	}

	private PriorityPoint getPlayerPos() {
		return new PriorityPoint(niveau.colonnePousseur(), niveau.lignePousseur(), 0);
	}

	private void find_solution() {
		// Hashmap contenant une configuration visitée associée à sa configuration
		// predec + la longueur pour l'atteindre
		HashMap<LevelState, Pair<Integer, LevelState>> accessibles = new HashMap<>();
		logger.info("Démarrage d'un nouveau niveau de taille " + niveau.lignes() + "x" + niveau.colonnes());
		lastPos = getPlayerPos();
		logger.info("Update AI accessibles");
		accessibles.clear();

		// Dijktra commence
		// Le constructeur prend une fonction servant à déterminer le tri des éléments
		// (la comparaison)
		PriorityQueue<LevelState> pq = new PriorityQueue<>((ls1, ls2) -> {
			// minimize the number of player steps
			return ls1.getNbSteps().compareTo(ls2.getNbSteps());
		});

		// L'origine est la position du pousseur :
		LevelState lvlState = new LevelState(niveau, controle.con.f.isPlayerMin());
		pq.add(lvlState);
		accessibles.put(lvlState, new Pair<>(0, lvlState));
		while (!pq.isEmpty() && (lvlState == null || !lvlState.isResolved())) {
			lvlState = pq.remove();

			for (LevelState lsNbg : lvlState.getNeighbor()) {
				nbExplored++;
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
		niveau.setAiInfo("Nb state explored: " + nbExplored);
		logger.info("Nb state explored: " + nbExplored);
	}

	@Override
	public void initialise() {
		controle.con.f.ecouteurAIMin((e) -> {
			controle.con.f.changeBoutonAIMin();
			find_solution();
		});

		find_solution();
	}

	@Override
	public void joue() {
		if (!lastPos.equals(getPlayerPos())) {
			find_solution();
		}
		if (currPosPath < path.size()) {
			Direction d = path.get(currPosPath++);
			controle.jeu.jouer(d.dL, d.dC);
			lastPos = getPlayerPos();
		} else {
			niveau.setAiInfo("No solution");
		}
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
		controle.con.basculeIA(false);
	}
}
