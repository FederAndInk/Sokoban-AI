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
import Modele.Jeu;
import Modele.Niveau;
import Vue.FenetreGraphique;

public class ControleurMediateur {
	Jeu jeu;
	FenetreGraphique f;
	boolean avecAnimations;
	ControleurJeuAutomatique ctrlAuto;
	boolean jeuAutomatique;
	int lenteurJeuAutomatique, decompteJA;
	IA joueurAutomatique;

	public ControleurMediateur(Jeu j, FenetreGraphique fen) {
		jeu = j;
		f = fen;
		avecAnimations = Boolean.parseBoolean(Configuration.instance().lis("Animations"));
		fen.basculeAnimations(avecAnimations);
		ctrlAuto = new ControleurJeuAutomatique(this, jeu);
		jeuAutomatique = false;
		f.changeBoutonIA(jeuAutomatique);
		lenteurJeuAutomatique = Integer.parseInt(Configuration.instance().lis("LenteurJeuAutomatique"));
		decompteJA = lenteurJeuAutomatique;
		joueurAutomatique = IA.nouvelle(ctrlAuto);
	}

	public void redimensionnement() {
		f.miseAJour();
	}

	public void clicSouris(double x, double y) {
		int l = (int) (y / f.tileHeight());
		int c = (int) (x / f.tileWidth());

		Niveau n = jeu.niveau();
		int dL = l - n.lignePousseur();
		int dC = c - n.colonnePousseur();
		jouer(dL, dC);
	}

	private boolean enMouvement() {
		return f.animationsEnCours();
	}

	public void prochain() {
		if (!enMouvement()) {
			if (!jeu.prochainNiveau())
				System.exit(0);
			else
				activeNiveauIA();
		}
	}

	public void annuler() {
		if (!enMouvement())
			jeu.annuler();
	}

	public void refaire() {
		if (!enMouvement())
			jeu.refaire();
	}

	public void jouer(int l, int c) {
		if (!enMouvement())
			jeu.jouer(l, c);
	}

	public void basculeAnimations(boolean valeur) {
		if (!enMouvement()) {
			avecAnimations = valeur;
			f.basculeAnimations(valeur);
		}
	}

	public void basculeIA(boolean value) {
		jeuAutomatique = value;
		activeNiveauIA();
		f.changeBoutonIA(value);
	}

	void activeNiveauIA() {
		if (jeuAutomatique)
			joueurAutomatique.nouveauNiveau(jeu.niveau());
	}

	public boolean IAEnCours() {
		return jeuAutomatique;
	}

	public void pressionTouche(char touche) {
		switch (touche) {
		case 'l':
			jouer(0, -1);
			break;
		case 'r':
			jouer(0, 1);
			break;
		case 'u':
			jouer(-1, 0);
			break;
		case 'd':
			jouer(1, 0);
			break;
		case 'I':
			basculeIA(!jeuAutomatique);
			break;
		case 'U':
			annuler();
			break;
		case 'R':
			refaire();
			break;
		case 'P':
			basculeAnimations(!avecAnimations);
			break;
		case 'Q':
			System.exit(0);
			break;
		default:
			break;
		}
	}

	public void tictac() {
		if (!enMouvement()) {
			decompteJA--;
			if (jeuAutomatique && (decompteJA <= 0)) {
				joueurAutomatique.elaboreCoup();
				decompteJA = lenteurJeuAutomatique;
			}
		}
		f.tictac();
		if (jeu.niveau().estTermine())
			prochain();
	}
}