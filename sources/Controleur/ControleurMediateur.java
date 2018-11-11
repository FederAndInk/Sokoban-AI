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
import Patterns.Observable;
import Vue.AnimationCoup;
import Vue.FenetreGraphique;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class ControleurMediateur {
	Jeu jeu;
	FenetreGraphique f;
	boolean avecAnimations;
	double vitesseAnimations;
	boolean enMouvement;
	int lenteurPas, decomptePas;
	AnimationTimer metronome;
	Observable animations;
	ControleurJeuAutomatique ctrlAuto;
	boolean jeuAutomatique;
	int lenteurJeuAutomatique, decompteJA;
	IA joueurAutomatique;
	boolean JAInitialise;

	public ControleurMediateur(Jeu j, FenetreGraphique fen) {
		jeu = j;
		f = fen;
		avecAnimations = Boolean.parseBoolean(Configuration.lis("Animations"));
		vitesseAnimations = Double.parseDouble(Configuration.lis("VitesseAnimations"));
		lenteurPas = Integer.parseInt(Configuration.lis("LenteurPas"));
		decomptePas = lenteurPas;
		metronome = new AnimationTimer() {
			@Override
			public void handle(long now) {
				tictac();
			}

		};
		animations = new Observable();
		metronome.start();
		ctrlAuto = new ControleurJeuAutomatique(this, jeu);
		jeuAutomatique = false;
		f.changeBoutonIA(jeuAutomatique);
		initialiseIA();
	}

	public void redimensionnement() {
		f.miseAJour();
	}

	public static boolean estDeplacementValide(int dL, int dC) {
		return (dL * dC == 0) && ((dL + dC) * (dL + dC) == 1);
	}

	public void clicSouris(MouseEvent e) {
		int l = (int) (e.getY() / f.tileHeight());
		int c = (int) (e.getX() / f.tileWidth());

		Niveau n = jeu.niveau();
		int dL = l - n.lignePousseur();
		int dC = c - n.colonnePousseur();
		jeu.jouer(dL, dC);
	}

	public void prochain() {
		if (!enMouvement) {
			jeu.prochainNiveau();
			initialiseIA();
		}
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

	void basculeAnimations() {
		if (!enMouvement) {
			if (avecAnimations) {
				avecAnimations = false;
			} else {
				avecAnimations = true;
				if (f.animationsEnCours())
					f.annuleAnimations();
			}
		}
	}

	void jeuIA() {
		Coup cp = ctrlAuto.recupereCoup();
		if (cp != null) {
			// coup déjà appliqué par l'IA, il ne reste qu'à l'animer
			//jeu.jouer(cp);
			animeCoup(cp, 1);
		}
	}

	public void basculeIA(boolean value) {
		jeuAutomatique = value;
		initialiseIA();
		f.changeBoutonIA(value);
	}

	void initialiseIA() {
		if (jeuAutomatique) {
			if (joueurAutomatique == null) {
				lenteurJeuAutomatique = Integer.parseInt(Configuration.lis("LenteurJeuAutomatique"));
				decompteJA = lenteurJeuAutomatique;
				joueurAutomatique = IA.nouvelle(ctrlAuto);
			}
			joueurAutomatique.nouveauNiveau(jeu.niveau());
			jeuIA();
		}
	}

	public boolean IAEnCours() {
		return jeuAutomatique;
	}

	void animeCoup(Coup cp, int sens) {
		if (cp != null) {
			double vitesse;
			if (avecAnimations) {
				vitesse = vitesseAnimations;
			} else {
				vitesse = 1;
			}
			AnimationCoup a = new AnimationCoup(jeu.niveau(), f, cp, sens, vitesse);
			animations.ajouteObservateur(a);
			f.ajouteAnimation(a);
			enMouvement = true;
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
		case I:
			basculeIA(!jeuAutomatique);
			break;
		case U:
			annuler();
			break;
		case R:
			refaire();
			break;
		case P:
			basculeAnimations();
			break;
		case Q:
		case A:
			System.exit(0);
			break;
		default:
			break;
		}
	}

	void tictac() {
		if (!enMouvement) {
			decompteJA--;
			if (jeuAutomatique && (decompteJA <= 0)) {
				joueurAutomatique.joue();
				jeuIA();
				decompteJA = lenteurJeuAutomatique;
			}
		}
		if (avecAnimations) {
			decomptePas--;
			if (decomptePas <= 0) {
				f.changeEtapePousseur();
				if (!enMouvement)
					f.afficheAnimations();
				decomptePas = lenteurPas;
			}
		}
		if (enMouvement) {
			// Progression des animations
			animations.metAJour();
			// Dessin des animations
			f.afficheAnimations();
			if (!f.animationsEnCours()) {
				enMouvement = false;
				if (jeu.niveau().estTermine())
					prochain();
			}
		}
	}
}