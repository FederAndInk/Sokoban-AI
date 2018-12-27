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

package Modele;

public class Niveau {
	static final int VIDE = 0;
	static final int MUR = 1;
	static final int POUSSEUR = 2;
	static final int CAISSE = 4;
	static final int BUT = 8;
	static final int NBMAX = 9;
	static final int INNAMOVIBLE = VIDE | MUR | BUT;
	int l, c;
	int[][] cases;
	String nom;
	int pousseurL, pousseurC;
	int nbButs;
	int nbCaissesSurBut;
	int nbPas, nbPoussees;

	Niveau() {
		cases = new int[1][1];
		l = c = 1;
		cases[1][1] = VIDE;
		nbButs = 0;
		nbCaissesSurBut = 0;
		pousseurL = pousseurC = -1;
	}

	int ajuste(int cap, int objectif) {
		while (cap <= objectif) {
			cap = cap * 2;
		}
		return cap;
	}

	void redimensionne(int nouvL, int nouvC) {
		int capL = ajuste(cases.length, nouvL);
		int capC = ajuste(cases[0].length, nouvC);
		if ((capL > cases.length) || (capC > cases[0].length)) {
			int[][] nouvelles = new int[capL][capC];
			for (int i = 0; i < capL; i++)
				for (int j = 0; j < capC; j++)
					if ((i < cases.length) && (j < cases[0].length))
						nouvelles[i][j] = cases[i][j];
					else
						nouvelles[i][j] = VIDE;
			cases = nouvelles;
		}
		if (nouvL >= l)
			l = nouvL + 1;
		if (nouvC >= c)
			c = nouvC + 1;
	}

	void fixeNom(String s) {
		nom = s;
	}

	void supprime(int contenu, int i, int j) {
		redimensionne(i, j);
		if (aBut(i, j)) {
			if (aCaisse(i, j) && (aCaisse(contenu) || aBut(contenu)))
				nbCaissesSurBut--;
			if (aBut(contenu))
				nbButs--;
		}
		if (aPousseur(i, j) && aPousseur(contenu))
			pousseurL = pousseurC = -1;
		cases[i][j] &= ~contenu;
	}

	void ajoute(int contenu, int i, int j) {
		redimensionne(i, j);
		int resultat = cases[i][j] | contenu;
		if (aBut(resultat)) {
			if (aCaisse(resultat) && (!aCaisse(i, j) || !aBut(i, j)))
				nbCaissesSurBut++;
			if (!aBut(i, j))
				nbButs++;
		}
		if (aPousseur(resultat) && !aPousseur(i, j)) {
			if (pousseurL == -1)
				throw new IllegalStateException("Plusieurs pousseurs sur le terrain !");
			pousseurL = i;
			pousseurC = j;
		}
		cases[i][j] = resultat;
	}

	void videCase(int i, int j) {
		supprime(cases[i][j], i, j);
	}

	void ajouteMur(int i, int j) {
		ajoute(MUR, i, j);
	}

	void ajoutePousseur(int i, int j) {
		ajoute(POUSSEUR, i, j);
	}

	void ajouteCaisse(int i, int j) {
		ajoute(CAISSE, i, j);
	}

	void ajouteBut(int i, int j) {
		ajoute(BUT, i, j);
	}

	public int lignes() {
		return l;
	}

	public int colonnes() {
		return c;
	}

	String nom() {
		return nom;
	}

	public boolean estVide(int l, int c) {
		return cases[l][c] == VIDE;
	}

	static boolean aMur(int contenu) {
		return (contenu & MUR) != 0;
	}

	public boolean aMur(int l, int c) {
		return aMur(cases[l][c]);
	}

	static boolean aBut(int contenu) {
		return (contenu & BUT) != 0;
	}

	public boolean aBut(int l, int c) {
		return aBut(cases[l][c]);
	}

	static boolean aPousseur(int contenu) {
		return (contenu & POUSSEUR) != 0;
	}

	public boolean aPousseur(int l, int c) {
		return aPousseur(cases[l][c]);
	}

	static boolean aCaisse(int contenu) {
		return (contenu & CAISSE) != 0;
	}

	public boolean aCaisse(int l, int c) {
		return aCaisse(cases[l][c]);
	}

	public int lignePousseur() {
		return pousseurL;
	}

	public int colonnePousseur() {
		return pousseurC;
	}

	private boolean estOccupable(int i, int j) {
		return !aMur(i, j) && !aCaisse(i, j);
	}

	private void deplace(int element, int srcL, int srcC, int dstL, int dstC) {
		supprime(element, srcL, srcC);
		ajoute(element, dstL, dstC);
	}

	public boolean jouer(int dL, int dC) {
		// Seulement une direction, -1 ou +1
		if ((dL * dC == 0) && ((dL + dC) * (dL + dC) <= 1)) {
			int destL = pousseurL + dL;
			int destC = pousseurC + dC;
			int dest2L = destL + dL;
			int dest2C = destC + dC;
			if (aCaisse(destL, destC) && estOccupable(dest2L, dest2C)) {
				deplace(CAISSE, destL, destC, dest2L, dest2C);
				nbPoussees++;
			}
			if (estOccupable(destL, destC)) {
				deplace(POUSSEUR, pousseurL, pousseurC, destL, destC);
				pousseurL = destL;
				pousseurC = destC;
				nbPas++;
				return true;
			}
		}
		return false;
	}

	public int nbPas() {
		return nbPas;
	}

	public int nbPoussees() {
		return nbPoussees;
	}

	public boolean estTermine() {
		return nbCaissesSurBut == nbButs;
	}
}
