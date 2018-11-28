
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

import Controleur.ControleurMediateur;
import Modele.Jeu;
import Vue.FenetreGraphique;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class InterfaceGraphique extends Application {
	Jeu jeu;

	@Override
	public void start(Stage primaryStage) {
		jeu = new Jeu();
		FenetreGraphique f = new FenetreGraphique(jeu, primaryStage);
		ControleurMediateur c = new ControleurMediateur(jeu, f);
		f.ecouteurIA(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ToggleButton b = (ToggleButton) event.getSource();
				c.basculeIA(b.isSelected());
			}
		});
		f.ecouteurProchain(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				c.prochain();
			}
		});
		f.ecouteurAnimation(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ToggleButton b = (ToggleButton) event.getSource();
				c.basculeAnimations(b.isSelected());
			}
		});
		f.ecouteurAnnuler(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				c.annuler();
			}
		});
		f.ecouteurRefaire(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				c.refaire();
			}
		});
		f.ecouteurDeSouris(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				c.clicSouris(e.getX(), e.getY());
			}
		});
		f.ecouteurDeClavier(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				char touche = 0;
				switch (event.getCode()) {
				case LEFT:
					touche = 'l';
					break;
				case RIGHT:
					touche = 'r';
					break;
				case UP:
					touche = 'u';
					break;
				case DOWN:
					touche = 'd';
					break;
				case U:
					touche = 'U';
					break;
				case R:
					touche = 'R';
					break;
				case P:
					touche = 'P';
					break;
				case I:
					touche = 'I';
					break;
				case Q:
				case A:
					touche = 'Q';
					break;
				default:
					break;
				}
				c.pressionTouche(touche);
			}
		});
		f.ecouteurDeRedimensionnement(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				c.redimensionnement();
			}
		});
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				c.tictac();
			}
		}.start();
	}
}