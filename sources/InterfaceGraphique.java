
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
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class InterfaceGraphique extends Application {
	static Jeu jeu = null;

	@Override
	public void start(Stage primaryStage) {
		FenetreGraphique f = new FenetreGraphique(jeu, primaryStage);
		ControleurMediateur c = new ControleurMediateur(jeu, f);
		f.ecouteurProchain(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				c.prochain();
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
				c.clicSouris(e);
			}
		});
		f.ecouteurDeClavier(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				c.pressionTouche(event);
			}
		});
		f.ecouteurDeRedimensionnement(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				c.redimensionnement();

			}
		});
		f.retraceNiveau();
		jeu.metAJour();
	}
}