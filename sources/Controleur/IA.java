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
import Modele.NiveauConsultable;

abstract class IA {
	ControleurJeuAutomatique controle;
	NiveauConsultable niveau;

	static IA nouvelle(ControleurJeuAutomatique ctrl) {
		IA instance = null;
		String name = Configuration.instance().lis("IA");
		try {
			instance = (IA) ClassLoader.getSystemClassLoader().loadClass(name).newInstance();
			instance.controle = ctrl;
		} catch (Exception e) {
			Configuration.instance().logger().severe("Impossible de trouver l'IA : " + name);
		}
		return instance;
	}

	final void nouveauNiveau(NiveauConsultable niv) {
		if (niveau != niv) {
			if (niveau != null)
				finalise();
			niveau = niv;
			initialise();
		}
	}

	final void elaboreCoup() {
		joue();
		controle.finaliseCoup();
	}

	void initialise() {
	}

	void joue() {
	}

	void finalise() {
	}
}
