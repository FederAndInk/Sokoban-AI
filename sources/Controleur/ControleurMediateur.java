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
import Modele.Niveau;
import Structures.Iterateur;
import Structures.Sequence;
import Vue.FenetreGraphique;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class ControleurMediateur extends AnimationTimer {
	Jeu jeu;
	FenetreGraphique f;
	Sequence<AnimationCoup> animations;
	boolean avecAnimations;
	double vitesseAnimations;
	boolean enMouvement;
	int lenteurPas, decompte;

	public ControleurMediateur(Jeu j, FenetreGraphique fen) {
		jeu = j;
		f = fen;
		animations = Configuration.fabriqueSequence().nouvelle();
		avecAnimations = Boolean.parseBoolean(Configuration.lis("Animations"));
		vitesseAnimations = Double.parseDouble(Configuration.lis("VitesseAnimations"));
		lenteurPas = Integer.parseInt(Configuration.lis("LenteurPas"));
		decompte = lenteurPas;
		if (avecAnimations)
			start();
	}

	public void redimensionnement() {
		f.miseAJour();
	}

	public void clicSouris(MouseEvent e) {
		int l = (int) (e.getY() / f.tileHeight());
		int c = (int) (e.getX() / f.tileWidth());

		Niveau n = jeu.niveau();
		int dL = l - n.lignePousseur();
		int dC = c - n.colonnePousseur();
		// Seulement une direction, -1 ou +1
		if ((dL * dC == 0) && ((dL + dC) * (dL + dC) == 1)) {
			jouer(dL, dC);
		}
	}

	public void prochain() {
		if (!enMouvement)
			jeu.prochainNiveau();
	}

	public void annuler() {
		if (!enMouvement)
			animeCoup(jeu.annuler(), -1);
	}

	public void refaire() {
		if (!enMouvement)
			animeCoup(jeu.refaire(), 1);
	}

	public void jouer(int l, int c) {
		if (!enMouvement) {
			animeCoup(jeu.jouer(l, c), 1);
		}
	}

	void animeCoup(Coup cp, int sens) {
		if (cp != null) {
			double vitesse;
			if (avecAnimations) {
				vitesse = vitesseAnimations;
			} else {
				vitesse = 1;
			}
			animations.insereQueue(new AnimationCoup(jeu.niveau(), f, cp, sens, vitesse));
			if (avecAnimations) {
				enMouvement = true;
			} else {
				handle(0);
			}
		}
	}

	public void pressionTouche(KeyEvent event) {
		switch (event.getCode()) {
		case LEFT:
			jouer(0, -1);
			break;
		case RIGHT:
			jouer(0, 1);
			break;
		case UP:
			jouer(-1, 0);
			break;
		case DOWN:
			jouer(1, 0);
			break;
		case U:
			annuler();
			break;
		case R:
			refaire();
			break;
		case P:
			if (!enMouvement) {
				if (avecAnimations) {
					avecAnimations = false;
					stop();
				} else {
					avecAnimations = true;
					animations = Configuration.fabriqueSequence().nouvelle();
					start();
				}
			}
			break;
		case Q:
		case A:
			System.exit(0);
			break;
		default:
			break;
		}
	}

	@Override
	public void handle(long now) {
		decompte--;
		if (decompte == 0) {
			f.changeEtape();
			if (!enMouvement)
				jeu.metAJour();
			decompte = lenteurPas;
		}
		if (enMouvement) {
			Iterateur<AnimationCoup> it;
			for (it = animations.iterateur(); it.aProchain();) {
				AnimationCoup a = it.prochain();
				a.effaceZone();
			}
			for (it = animations.iterateur(); it.aProchain();) {
				AnimationCoup a = it.prochain();
				a.progresse();
				a.afficheObjets();
				if (a.estTerminee())
					it.supprime();
			}
			if (animations.estVide()) {
				enMouvement = false;
				if (jeu.niveau().estTermine())
					jeu.prochainNiveau();
			}
		}
	}
}
