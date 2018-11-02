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

import Modele.Jeu;
import Modele.Niveau;
import Vue.FenetreGraphique;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class ControleurMediateur {
	Jeu jeu;
	FenetreGraphique f;

	public ControleurMediateur(Jeu j, FenetreGraphique fen) {
		jeu = j;
		f = fen;
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
			jeu.jouer(dL, dC);
		}
	}

	public void prochain() {
		jeu.prochainNiveau();
	}
	
	public void annuler() {
		jeu.annuler();
	}
	
	public void refaire() {
		jeu.refaire();
	}

	public void pressionTouche(KeyEvent event) {
		switch (event.getCode()) {
		case LEFT:
			jeu.jouer(0, -1);
			break;
		case RIGHT:
			jeu.jouer(0, 1);
			break;
		case UP:
			jeu.jouer(-1, 0);
			break;
		case DOWN:
			jeu.jouer(1, 0);
			break;
		case U:
			jeu.annuler();
			break;
		case R:
			jeu.refaire();
			break;
		case Q:
		case A:
			System.exit(0);
			break;
		default:
			break;
		}
	}
}
