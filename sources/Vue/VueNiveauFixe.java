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

class VueNiveauFixe extends VueNiveau {
	ImageGraphique pousseur, mur;
	ImageGraphique[] sol, caisse, but, caisseSurBut;

	Niveau n;
	double largeur;
	double hauteur;
	double largeurCase;
	double hauteurCase;

	VueNiveauFixe(Jeu j, FenetreGraphique f) {
		super(j, f);

		changePousseur(lisImage("Pousseur"));
		mur = lisImage("Mur");
		sol = lisImages("Sol");
		caisse = lisImages("Caisse");
		but = lisImages("But");
		caisseSurBut = lisImages("CaisseSurBut");
	}

	private ImageGraphique[] lisImages(String nom) {
		ImageGraphique[] resultat = new ImageGraphique[3];
		for (int i = 0; i < resultat.length; i++)
			resultat[i] = lisImage(nom + "_" + i);
		return resultat;
	}

	@Override
	void traceSol(int l, int c) {
		double x = c * largeurCase;
		double y = l * hauteurCase;
		int marque = n.marque(l, c);
		if (n.aBut(l, c))
			fenetre.tracer(but[marque], x, y, largeurCase, hauteurCase);
		else
			fenetre.tracer(sol[marque], x, y, largeurCase, hauteurCase);
	}

	@Override
	void traceObjet(int l, int c) {
		double x = c * largeurCase;
		double y = l * hauteurCase;
		ImageGraphique image = trouveObjet(n.contenu(l, c));
		if (image != null)
			fenetre.tracer(image, x, y, largeurCase, hauteurCase);
	}

	@Override
	ImageGraphique trouveObjet(int contenu) {
		int marque = Niveau.marque(contenu);
		if (Niveau.aMur(contenu))
			return mur;
		if (Niveau.aPousseur(contenu))
			return pousseur;
		if (Niveau.aCaisse(contenu))
			if (Niveau.aBut(contenu))
				return caisseSurBut[marque];
			else
				return caisse[marque];
		return null;
	}

	@Override
	void miseAJour() {
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

	@Override
	double largeurCase() {
		return largeurCase;
	}

	@Override
	double hauteurCase() {
		return hauteurCase;
	}

	@Override
	void changePousseur(ImageGraphique p) {
		pousseur = p;
	}
}