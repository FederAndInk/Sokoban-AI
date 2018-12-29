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

import Structures.Iterateur;
import Structures.Sequence;
import Global.Configuration;
import Patterns.Commande;

public class Coup extends Commande {
	Niveau n;
	public int dirL, dirC, posL, posC;
	public boolean caisse;
	public int sens;
	public Sequence<Marque> marques;
	public Sequence<Marque> inverses;

	public Coup(Niveau niveau, int pL, int pC, int dL, int dC, boolean c) {
		n = niveau;
		posL = pL;
		posC = pC;
		dirL = dL;
		dirC = dC;
		caisse = c;
		marques = null;
		inverses = null;
	}

	@Override
	public void execute() {
		int dstL = posL + dirL;
		int dstC = posC + dirC;
		if (caisse) {
			n.deplace(dstL, dstC, dstL + dirL, dstC + dirC);
			n.comptePoussee();
		}
		n.deplace(posL, posC, dstL, dstC);
		if (marques != null) {
			Iterateur<Marque> it;
			for (it = marques.iterateur(); it.aProchain();) {
				Marque m = it.prochain();
				n.marquer(m.ligne, m.colonne, m.nouvelle);
			}
		}
		n.comptePas();
		sens = 1;
	}

	@Override
	public void desexecute() {
		int dstL = posL + dirL;
		int dstC = posC + dirC;
		n.deplace(dstL, dstC, posL, posC);
		n.decomptePas();
		if (caisse) {
			n.deplace(dstL + dirL, dstC + dirC, dstL, dstC);
			n.decomptePoussee();
		}
		if (marques != null) {
			Iterateur<Marque> it;
			if (inverses == null) {
				inverses = Configuration.instance().fabriqueSequence().nouvelle();
				for (it = marques.iterateur(); it.aProchain();)
					inverses.insereQueue(it.prochain());
			}
			for (it = inverses.iterateur(); it.aProchain();) {
				Marque m = it.prochain();
				n.marquer(m.ligne, m.colonne, m.ancienne);
			}
		}
		sens = -1;
	}
}
