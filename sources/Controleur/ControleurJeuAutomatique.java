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

	public ControleurJeuAutomatique(ControleurMediateur c, Jeu j) {
		con = c;
		jeu = j;
	}

	public void jouer(int dL, int dC) {
		Sequence<Marque> s = null;
		if (cp != null) {
			s = cp.marques;
		}
		if (ControleurMediateur.estDeplacementValide(dL, dC)) {
			cp = jeu.niveau().construireCoup(dL, dC);
			cp.marques = s;
		} else {
			Configuration.logger().severe("Déplacement (" + dL + ", " + dC + ") invalide pour le pousseur");
		}
	}

	public void marquer(int l, int c, int m) {
		if (cp == null) {
			int lP, cP;
			lP = jeu.niveau().lignePousseur();
			cP = jeu.niveau().colonnePousseur();
			cp = new Coup(lP, cP, 0, 0, false);
		}
		if (cp.marques == null)
			cp.marques = Configuration.fabriqueSequence().nouvelle();
		int existant = jeu.niveau().marque(l, c);
		Marque nouvelle = new Marque(l, c, m - existant);
		cp.marques.insereQueue(nouvelle);
	}

	Coup recupereCoup() {
		Coup c = cp;
		cp = null;
		return c;
	}
}
