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
import Modele.Niveau;

public class VueNiveau {
	Jeu jeu;
	FenetreGraphique fenetre;
	ImageGraphique pousseur, mur, sol, caisse, but, caisseSurBut;

	Niveau n;
	double largeur;
	double hauteur;
	double largeurCase;
	double hauteurCase;

	private ImageGraphique lisImage(String nom) {
		String resource = Configuration.instance().lis(nom);
		Configuration.instance().logger().info("Lecture de " + resource);
		return fenetre.charger(Configuration.charge(resource));
	}

	public VueNiveau(Jeu j, FenetreGraphique f) {
		jeu = j;
		fenetre = f;

		pousseur = lisImage("Pousseur");
		mur = lisImage("Mur");
		sol = lisImage("Sol");
		caisse = lisImage("Caisse");
		but = lisImage("But");
		caisseSurBut = lisImage("CaisseSurBut");
	}

	void traceSol(int l, int c) {
		double x = c * largeurCase;
		double y = l * hauteurCase;
		if (n.aBut(l, c))
			fenetre.tracer(but, x, y, largeurCase, hauteurCase);
		else
			fenetre.tracer(sol, x, y, largeurCase, hauteurCase);
	}

	void traceObjet(int l, int c) {
		double x = c * largeurCase;
		double y = l * hauteurCase;
		if (n.aMur(l, c))
			fenetre.tracer(mur, x, y, largeurCase, hauteurCase);
		if (n.aPousseur(l, c))
			fenetre.tracer(pousseur, x, y, largeurCase, hauteurCase);
		if (n.aCaisse(l, c))
			if (n.aBut(l, c))
				fenetre.tracer(caisseSurBut, x, y, largeurCase, hauteurCase);
			else
				fenetre.tracer(caisse, x, y, largeurCase, hauteurCase);
	}

	public void trace() {
		n = jeu.niveau();
		if (n == null) {
			Configuration.instance().logger().info("Dernier niveau lu, fin du jeu !");
			System.exit(0);
		}

		largeur = fenetre.largeur();
		hauteur = fenetre.hauteur();
		largeurCase = largeur / n.colonnes();
		hauteurCase = hauteur / n.lignes();
		largeurCase = Math.min(largeurCase, hauteurCase);
		hauteurCase = Math.min(largeurCase, hauteurCase);

		fenetre.effacer();
		for (int ligne = 0; ligne < n.lignes(); ligne++)
			for (int colonne = 0; colonne < n.colonnes(); colonne++) {
				traceSol(ligne, colonne);
				traceObjet(ligne, colonne);
			}
	}

	public double largeurCase() {
		return largeurCase;
	}

	public double hauteurCase() {
		return hauteurCase;
	}
}