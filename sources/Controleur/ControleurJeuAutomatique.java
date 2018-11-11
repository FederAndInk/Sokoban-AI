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

import Global.Configuration;
import Modele.Coup;
import Modele.Jeu;
import Modele.Marque;
import Structures.Sequence;

public class ControleurJeuAutomatique {
	ControleurMediateur con;
	Jeu jeu;
	Coup cp;
	Sequence<Marque> s;

	public ControleurJeuAutomatique(ControleurMediateur c, Jeu j) {
		con = c;
		jeu = j;
		s = Configuration.fabriqueSequence().nouvelle();
	}
	
	void creerCoupSiBesoin() {
		if (cp == null) {
			int lP, cP;
			lP = jeu.niveau().lignePousseur();
			cP = jeu.niveau().colonnePousseur();
			cp = new Coup(lP, cP, 0, 0, false);
		}
	}

	public void jouer(int dL, int dC) {
		cp = jeu.jouer(dL, dC);
		if (cp == null) {
			Configuration.logger().info("Déplacement (" + dL + ", " + dC + ") impossible pour le pousseur");
		}
	}

	public void marquer(int l, int c, int m) {
		int existant = jeu.niveau().marque(l, c);
		jeu.niveau().marquer(l, c, m);
		Marque nouvelle = new Marque(l, c, existant, m);
		s.insereQueue(nouvelle);
	}

	Coup recupereCoup() {
		if (!s.estVide()) {
			creerCoupSiBesoin();
			cp.marques = s;
			s = Configuration.fabriqueSequence().nouvelle();
		}
		Coup c = cp;
		cp = null;
		return c;
	}
}
