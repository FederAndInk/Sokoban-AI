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
import Modele.Coup;
import Structures.Iterateur;
import Structures.Sequence;

public class VueNiveauAnimee extends DecorateurDeVueNiveau {
	ImageGraphique[][] pousseurs;
	int direction, etape;
	double vitesseAnimations;
	int lenteurPas, decomptePas;

	Sequence<AnimationCoup> animations;

	public VueNiveauAnimee(VueNiveau v) {
		super(v);
		pousseurs = new ImageGraphique[4][4];
		for (int d = 0; d < pousseurs.length; d++)
			for (int i = 0; i < pousseurs[d].length; i++)
				pousseurs[d][i] = lisImage("Pousseur_" + d + "_" + i);
		etape = 0;
		direction = 2;
		animations = Configuration.instance().fabriqueSequence().nouvelle();
		vitesseAnimations = Double.parseDouble(Configuration.instance().lis("VitesseAnimations"));
		lenteurPas = Integer.parseInt(Configuration.instance().lis("LenteurPas"));
		decomptePas = lenteurPas;
	}

	void traceObjet(int contenu, double l, double c) {
		double x = c * largeurCase();
		double y = l * hauteurCase();
		ImageGraphique image = trouveObjet(contenu);
		fenetre.tracer(image, x, y, largeurCase(), hauteurCase());
	}

	void traceCase(int l, int c) {
		traceSol(l, c);
		traceObjet(l, c);
	}

	public boolean animationsEnCours() {
		return !animations.estVide();
	}

	void afficheAnimations() {
		Iterateur<AnimationCoup> it;
		for (it = animations.iterateur(); it.aProchain();) {
			AnimationCoup a = it.prochain();
			a.effaceZone();
		}
		for (it = animations.iterateur(); it.aProchain();) {
			AnimationCoup a = it.prochain();
			a.afficheObjets();
			if (a.estTerminee())
				it.supprime();
		}
	}

	void metAJourDirection(Coup cp) {
		int dL = cp.dirL;
		int dC = cp.dirC;
		switch (dL + 2 * dC) {
		case -2:
			direction = 1;
			break;
		case -1:
			direction = 0;
			break;
		case 1:
			direction = 2;
			break;
		case 2:
			direction = 3;
			break;
		default:
			Configuration.instance().logger().severe("Bug interne, direction invalide");
		}
	}

	@Override
	void miseAJour() {
		Coup cp = jeu.dernierCoup();
		if (cp != null) {
			metAJourDirection(cp);
			changePousseur(pousseurs[direction][etape]);
			AnimationCoup a;
			a = new AnimationCoup(jeu.niveau(), fenetre, cp, vitesseAnimations);
			animations.insereQueue(a);
		}
		super.miseAJour();
		afficheAnimations();
	}

	@Override
	void tictac() {
		decomptePas--;
		if (decomptePas == 0) {
			etape = (etape + 1) % pousseurs[direction].length;
			changePousseur(pousseurs[direction][etape]);
			traceCase(jeu.niveau().lignePousseur(), jeu.niveau().colonnePousseur());
			decomptePas = lenteurPas;
		}
		Iterateur<AnimationCoup> it;
		for (it = animations.iterateur(); it.aProchain();) {
			it.prochain().miseAJour();
		}
		afficheAnimations();
	}
}