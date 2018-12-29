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

public abstract class NiveauConsultable extends HistoriqueAPile<Coup> {
	static final int VIDE = 0;
	static final int MUR = 1;
	static final int POUSSEUR = 2;
	static final int CAISSE = 4;
	static final int BUT = 8;
	static final int NBMAX = 9;
	int l, c;
	int[][] cases;
	String nom;
	int pousseurL, pousseurC;
	int nbButs;
	int nbCaissesSurBut;
	int nbPas, nbPoussees;

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

	public static boolean aMur(int contenu) {
		return (contenu & MUR) != 0;
	}

	public boolean aMur(int l, int c) {
		return aMur(cases[l][c]);
	}

	public static boolean aBut(int contenu) {
		return (contenu & BUT) != 0;
	}

	public boolean aBut(int l, int c) {
		return aBut(cases[l][c]);
	}

	public static boolean aPousseur(int contenu) {
		return (contenu & POUSSEUR) != 0;
	}

	public boolean aPousseur(int l, int c) {
		return aPousseur(cases[l][c]);
	}

	public static boolean aCaisse(int contenu) {
		return (contenu & CAISSE) != 0;
	}

	public boolean aCaisse(int l, int c) {
		return aCaisse(cases[l][c]);
	}

	public boolean estOccupable(int i, int j) {
		return !aMur(i, j) && !aCaisse(i, j);
	}

	public int lignePousseur() {
		return pousseurL;
	}

	public int colonnePousseur() {
		return pousseurC;
	}

	public static int marque(int contenu) {
		return contenu >> 8;
	}

	public int marque(int l, int c) {
		return marque(cases[l][c]);
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
