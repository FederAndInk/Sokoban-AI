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
	static final int NBMAX = 9;
	int[][] cases;
	int pousseurL, pousseurC;
	int [] nb;
	int [] nbSurBut;
	int nbPas, nbPoussees;

	void nouveauPousseur(int l, int c) {
		pousseurL = l;
		pousseurC = c;
	}
	
	public int lignePousseur() {
		return pousseurL;
	}

	public int colonnePousseur() {
		return pousseurC;
	}
	
	Niveau(int lignes, int colonnes, Sequence<String> s) {
		cases = new int[lignes][colonnes];
		nb = new int[NBMAX];
		nbSurBut = new int[NBMAX];
		for (int i = 0; i < lignes; i++) {
			for (int j = 0; j < colonnes; j++) {
				cases[i][j] = VIDE;
			}
		}
		for (int i = 0; i < NBMAX; i++) {
			nb[i] = 0;
			nbSurBut[i] = 0;
		}
		int i = 0;
		while (!s.estVide()) {
			String l = s.extraitTete();
			for (int j = 0; j < l.length(); j++) {
				char c = l.charAt(j);
				switch (c) {
				case ' ':
					cases[i][j] = VIDE;
					nb[VIDE]++;
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
					nouveauPousseur(i, j);
					break;
				case '$':
					cases[i][j] = CAISSE;
					break;
				case '*':
					cases[i][j] = CAISSE | BUT;
					break;
				case '.':
					cases[i][j] = BUT;
					break;
				default:
					System.err.println("Caractère inconnu : " + c);
				}
				int element = cases[i][j] & ~BUT;
				if (element != 0) {
					nb[element]++;
					if (estBut(cases[i][j])) {
						nb[BUT]++;
						nbSurBut[element]++;
					}
				} else {
					nb[cases[i][j]]++;
				}
			}
			i++;
		}
		if (nb[POUSSEUR] != 1) {
			Configuration.logger().severe("Nombre de pouseurs invalide : " + nb[POUSSEUR]);
		}
	}

	public int lignes() {
		return cases.length;
	}

	public int colonnes() {
		return cases[0].length;
	}
	
	public int get(int l, int c) {
		return cases[l][c];
	}

	public static boolean estMur(int contenu) {
		return (contenu & MUR) != 0;
	}

	public static boolean estBut(int contenu) {
		return (contenu & BUT) != 0;
	}

	public static boolean aPousseur(int contenu) {
		return (contenu & POUSSEUR) != 0;
	}

	public static boolean aCaisse(int contenu) {
		return (contenu & CAISSE) != 0;
	}
	
	public boolean estOccupable(int l, int c) {
		return (cases[l][c] & (CAISSE | MUR)) == 0;
	}

	private void deplace(int element, int srcL, int srcC, int dstL, int dstC) {
		cases[dstL][dstC] |= element;
		cases[srcL][srcC] &= ~element;
		if (estBut(cases[dstL][dstC]))
			nbSurBut[element]++;
		if (estBut(cases[srcL][srcC]))
			nbSurBut[element]--;
	}

	public boolean jouer(int dL, int dC) {
		int destL = pousseurL+dL;
		int destC = pousseurC+dC;
		int dest2L = destL+dL;
		int dest2C = destC+dC;
		if (aCaisse(cases[destL][destC]) && estOccupable(dest2L, dest2C)) {
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
		return false;
	}
	
	public int nbPas() {
		return nbPas;
	}
	
	public int nbPoussees() {
		return nbPoussees;
	}

	public boolean estTermine() {
		return nbSurBut[CAISSE] == nb[BUT];
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
