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
package Vue;

import Global.Configuration;
import Modele.Jeu;

abstract class VueNiveau {
	Jeu jeu;
	FenetreGraphique fenetre;

	protected ImageGraphique lisImage(String nom) {
		String resource = Configuration.instance().lis(nom);
		Configuration.instance().logger().info("Lecture de " + resource);
		return fenetre.charger(Configuration.charge(resource));
	}

	VueNiveau(Jeu j, FenetreGraphique f) {
		jeu = j;
		fenetre = f;
	}

	abstract void traceSol(int l, int c);

	abstract void traceObjet(int l, int c);

	void traceObjet(int contenu, double l, double c) {
		// Version continue
		// Rien à faire par défaut, le niveau n'est pas forcément animé
	}

	abstract ImageGraphique trouveObjet(int contenu);

	abstract void trace();

	abstract double largeurCase();

	abstract double hauteurCase();

	boolean animationsEnCours() {
		return false;
	}

	void tictac() {
		// Rien à faire par défaut, le niveau n'est pas forcément animé
	}

	VueNiveau toutNu() {
		return this;
	}

	abstract void changePousseur(ImageGraphique p);
}