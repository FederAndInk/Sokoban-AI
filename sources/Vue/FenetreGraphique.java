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
import Patterns.Observateur;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FenetreGraphique implements Observateur {
	Jeu jeu;
	Scene scene;
	VueNiveau vueNiveau;
	Label nbPas, nbPoussees;
	ToggleButton IA;
	Button prochain;
	BoutonAnnuler annuler;
	BoutonRefaire refaire;

	// On délègue toutes les parties animées à vueNiveau
	public void changeEtapePousseur() {
		vueNiveau.changeEtapePousseur();
	}

	public void ajouteAnimation(AnimationCoup a) {
		vueNiveau.ajouteAnimation(a);
	}

	public boolean animationsEnCours() {
		return vueNiveau.animationsEnCours();
	}

	public void annuleAnimations() {
		vueNiveau.annuleAnimations();
	}
	
	public void afficheAnimations() {
		vueNiveau.afficheAnimations();
	}

	public FenetreGraphique(Jeu j, Stage primaryStage) {
		jeu = j;
		primaryStage.setTitle("Sokoban");
		primaryStage.setFullScreen(true);

		vueNiveau = new VueNiveau(jeu);
		Pane vue = new Pane(vueNiveau);
		vue.setPrefSize(600, 400);

		VBox boiteTexte = new VBox();
		boiteTexte.setAlignment(Pos.CENTER);

		Label titre = new Label("Sokoban");
		titre.setMaxHeight(Double.MAX_VALUE);
		titre.setAlignment(Pos.TOP_CENTER);
		boiteTexte.getChildren().add(titre);
		VBox.setVgrow(titre, Priority.ALWAYS);

		nbPas = new Label("Pas :");
		boiteTexte.getChildren().add(nbPas);
		nbPoussees = new Label("Poussées :");
		boiteTexte.getChildren().add(nbPoussees);
		IA = new ToggleButton("IA");
		boiteTexte.getChildren().add(IA);
		prochain = new Button("Prochain");
		boiteTexte.getChildren().add(prochain);
		annuler = new BoutonAnnuler(jeu);
		refaire = new BoutonRefaire(jeu);
		HBox annulerRefaire = new HBox(annuler, refaire);
		annulerRefaire.setAlignment(Pos.CENTER);
		boiteTexte.getChildren().add(annulerRefaire);

		Label copyright = new Label("Copyright G. Huard, 2018");
		copyright.setMaxHeight(Double.MAX_VALUE);
		copyright.setAlignment(Pos.BOTTOM_LEFT);
		boiteTexte.getChildren().add(copyright);
		VBox.setVgrow(copyright, Priority.ALWAYS);

		HBox boiteScene = new HBox();
		boiteScene.getChildren().add(vue);
		boiteScene.getChildren().add(boiteTexte);
		HBox.setHgrow(vue, Priority.ALWAYS);

		scene = new Scene(boiteScene);
		primaryStage.setScene(scene);
		primaryStage.show();

		vueNiveau.widthProperty().bind(vue.widthProperty());
		vueNiveau.heightProperty().bind(vue.heightProperty());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				Configuration.logger().info("Fin du jeu");
			}
		});
	}

	public void ecouteurDeRedimensionnement(ChangeListener<Number> l) {
		vueNiveau.widthProperty().addListener(l);
		vueNiveau.heightProperty().addListener(l);
	}

	public void ecouteurDeSouris(EventHandler<MouseEvent> h) {
		vueNiveau.setOnMouseClicked(h);
	}

	public void ecouteurDeClavier(EventHandler<KeyEvent> h) {
		scene.setOnKeyPressed(h);
	}

	public void ecouteurIA(EventHandler<ActionEvent> h) {
		IA.setOnAction(h);
	}

	public void ecouteurProchain(EventHandler<ActionEvent> h) {
		prochain.setOnAction(h);
	}

	public void ecouteurAnnuler(EventHandler<ActionEvent> h) {
		annuler.setOnAction(h);
	}

	public void ecouteurRefaire(EventHandler<ActionEvent> h) {
		refaire.setOnAction(h);
	}
	
	public void changeBoutonIA(boolean value) {
		IA.setSelected(value);
	}
	
	@Override
	public void miseAJour() {
		vueNiveau.miseAJour();
		nbPas.setText("Pas :" + jeu.niveau().nbPas());
		nbPoussees.setText("Poussées :" + jeu.niveau().nbPoussees());
	}

	public double tileWidth() {
		return vueNiveau.tileWidth();
	}

	public double tileHeight() {
		return vueNiveau.tileHeight();
	}
}