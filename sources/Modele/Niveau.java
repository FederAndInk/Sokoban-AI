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

import Global.Configuration;
import Structures.Sequence;

public class Niveau {
	static final int VIDE = 0;
	static final int MUR = 1;
	static final int POUSSEUR = 2;
	static final int CAISSE = 4;
	static final int BUT = 8;
	int[][] cases;
	int pousseurL, pousseurC;
	int nbPousseurs;
	int nbButs;
	int nbCaissesSurBut;

	void nouveauPousseur(int l, int c) {
		pousseurL = l;
		pousseurC = c;
		nbPousseurs++;
	}
	
	public int lignePousseur() {
		return pousseurL;
	}

	public int colonnePousseur() {
		return pousseurC;
	}
	
	Niveau(int lignes, int colonnes, Sequence<String> s) {
		cases = new int[lignes][colonnes];
		for (int i = 0; i < lignes; i++) {
			for (int j = 0; j < colonnes; j++) {
				cases[i][j] = VIDE;
			}
		}
		int i = 0;
		while (!s.estVide()) {
			String l = s.extraitTete();
			for (int j = 0; j < l.length(); j++) {
				char c = l.charAt(j);
				switch (c) {
				case ' ':
					cases[i][j] = VIDE;
					break;
				case '#':
					cases[i][j] = MUR;
					break;
				case '@':
					cases[i][j] = POUSSEUR;
					nouveauPousseur(i, j);
					break;
				case '+':
					cases[i][j] = POUSSEUR | BUT;
					nbButs++;
					nouveauPousseur(i, j);
					break;
				case '$':
					cases[i][j] = CAISSE;
					break;
				case '*':
					cases[i][j] = CAISSE | BUT;
					nbButs++;
					nbCaissesSurBut++;
					break;
				case '.':
					cases[i][j] = BUT;
					nbButs++;
					break;
				default:
					System.err.println("Caractère inconnu : " + c);
				}
			}
			i++;
		}
		if (nbPousseurs != 1) {
			Configuration.logger().severe("Nombre de pouseurs invalide : " + nbPousseurs);
		}
	}

	public int lignes() {
		return cases.length;
	}

	public int colonnes() {
		return cases[0].length;
	}

	public boolean estMur(int l, int c) {
		return (cases[l][c] & MUR) != 0;
	}

	public boolean estBut(int l, int c) {
		return (cases[l][c] & BUT) != 0;
	}

	public boolean aPousseur(int l, int c) {
		return (cases[l][c] & POUSSEUR) != 0;
	}

	public boolean aCaisse(int l, int c) {
		return (cases[l][c] & CAISSE) != 0;
	}
	
	public boolean estOccupable(int l, int c) {
		return (cases[l][c] & (CAISSE | MUR)) == 0;
	}

	public boolean jouer(int dL, int dC) {
		int destL = pousseurL+dL;
		int destC = pousseurC+dC;
		int dest2L = destL+dL;
		int dest2C = destC+dC;
		if (aCaisse(destL, destC) && estOccupable(dest2L, dest2C)) {
			cases[destL][destC] &= ~CAISSE;
			if (estBut(destL, destC))
				nbCaissesSurBut--;
			cases[dest2L][dest2C] |= CAISSE;
			if (estBut(dest2L, dest2C))
				nbCaissesSurBut++;
		}
		if (estOccupable(destL, destC)) {
			cases[pousseurL][pousseurC] &= ~POUSSEUR;
			cases[destL][destC] |= POUSSEUR;
			pousseurL = destL;
			pousseurC = destC;
			return true;
		}
		return false;
	}
	
	public boolean estTermine() {
		return nbCaissesSurBut == nbButs;
	}

	@Override
	public String toString() {
		int capacite = lignes() * (colonnes() + 1);
		StringBuilder result = new StringBuilder(capacite);
		for (int i = 0; i < lignes(); i++) {
			int dernier = 0;
			for (int j = 0; j < colonnes(); j++)
				if (cases[i][j] != VIDE)
					dernier = j;
			for (int j = 0; j <= dernier; j++) {
				char c;
				switch (cases[i][j]) {
				case VIDE:
					c = ' ';
					break;
				case MUR:
					c = '#';
					break;
				case POUSSEUR:
					c = '@';
					break;
				case POUSSEUR | BUT:
					c = '+';
					break;
				case CAISSE:
					c = '$';
					break;
				case CAISSE | BUT:
					c = '*';
					break;
				case BUT:
					c = '.';
					break;
				default:
					c = ' ';
					System.err.println("Bug interne, case inconnue en (" + i + ", " + j + ") : " + cases[i][j]);
				}
				result.append(c);
			}
			result.append('\n');
		}
		return result.toString();
	}
}
