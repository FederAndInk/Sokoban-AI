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
import Structures.Iterateur;
import Structures.Sequence;

public class Niveau extends NiveauConsultable {

	Niveau(int lignes, int colonnes, Sequence<String> s) {
		super(lignes, colonnes, s);
	}

	public void marquer(int l, int c, int m) {
		cases[l][c] = (contenu(l, c) & 0xFF) | (m << 8);
	}

	private void deplace(int element, int srcL, int srcC, int dstL, int dstC) {
		cases[srcL][srcC] &= ~element;
		cases[dstL][dstC] |= element;
		if (estBut(dstL, dstC))
			nbSurBut[element]++;
		if (estBut(srcL, srcC))
			nbSurBut[element]--;
	}

	public void jouer(Coup c) {
		int dstL = c.posL + c.dirL;
		int dstC = c.posC + c.dirC;
		if (c.caisse) {
			deplace(CAISSE, dstL, dstC, dstL + c.dirL, dstC + c.dirC);
			nbPoussees++;
		}
		deplace(POUSSEUR, c.posL, c.posC, dstL, dstC);
		nbPas++;
		pousseurL = dstL;
		pousseurC = dstC;
		if (c.marques != null) {
			Iterateur<Marque> it = c.marques.iterateur();
			while (it.aProchain()) {
				Marque m = it.prochain();
				marquer(m.ligne, m.colonne, m.nouvelle);
			}
		}
	}

	public void dejouer(Coup c) {
		int dstL = c.posL + c.dirL;
		int dstC = c.posC + c.dirC;
		deplace(POUSSEUR, dstL, dstC, c.posL, c.posC);
		nbPas--;
		pousseurL = c.posL;
		pousseurC = c.posC;
		if (c.caisse) {
			deplace(CAISSE, dstL + c.dirL, dstC + c.dirC, dstL, dstC);
			nbPoussees--;
		}
		if (c.marques != null) {
			if (c.inverses == null) {
				c.inverses = Configuration.fabriqueSequence().nouvelle();
				Iterateur<Marque> it = c.marques.iterateur();
				while (it.aProchain()) {
					Marque m = it.prochain();
					c.inverses.insereTete(m);
				}
			}
			Iterateur<Marque> it = c.inverses.iterateur();
			while (it.aProchain()) {
				Marque m = it.prochain();
				marquer(m.ligne, m.colonne, m.ancienne);
			}
		}
	}

	@Override
	public void faire(Coup c) {
		jouer(c);
		super.faire(c);
	}

	@Override
	public Coup annuler() {
		Coup c = super.annuler();
		dejouer(c);
		return c;
	}

	@Override
	public Coup refaire() {
		Coup c = super.refaire();
		jouer(c);
		return c;
	}

	public Coup jouer(int dL, int dC) {/*
		Coup cp = construireCoup(dL, dC);
		if (cp != null)
			faire(cp);
		return cp;
	}

	public Coup construireCoup(int dL, int dC) {*/
		Coup c = null;
		if ((dL * dC == 0) && ((dL + dC) * (dL + dC) <= 1)) {
			int destL = pousseurL + dL;
			int destC = pousseurC + dC;

			if (aCaisse(destL, destC) && estOccupable(destL + dL, destC + dC)) {
				c = new Coup(pousseurL, pousseurC, dL, dC, true);
			}
			if (estOccupable(destL, destC)) {
				c = new Coup(pousseurL, pousseurC, dL, dC, false);
			}
			if (c != null)
				faire(c);
		}
		return c;
	}
}
