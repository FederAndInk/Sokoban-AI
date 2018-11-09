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

import java.util.Random;

import Global.Configuration;

class IAAleatoire extends IA {
	Random r;

	public IAAleatoire() {
		r = new Random();
	}

	@Override
	public void initialise() {
		Configuration.logger().info("Démarrage d'un nouveau niveau de taille " + niveau.lignes() + "x" + niveau.colonnes());
	}

	@Override
	public void joue() {
		boolean mur = true;
		int dL = 0, dC = 0;

		while (mur) {
			int direction = r.nextInt(2) * 2 - 1;
			if (r.nextBoolean()) {
				dL = direction;
			} else {
				dC = direction;
			}
			int l = niveau.lignePousseur()+dL;
			int c = niveau.colonnePousseur()+dC;
			if (niveau.estMur(l, c)) {
				Configuration.logger().info("Tentative de déplacement (" + dL + ", " + dC + ") heurte un mur");
				dL = dC = 0;
			} else
				mur = false;
		}
		controle.jouer(dL, dC);
	}

	@Override
	public void finalise() {
		Configuration.logger().info("Niveau terminé en " + niveau.nbPas() + " pas, et " + niveau.nbPoussees() + " Poussees !");
	}
}
