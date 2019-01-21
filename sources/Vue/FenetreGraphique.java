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

import java.io.InputStream;

import Global.Configuration;
import Modele.Jeu;
import Patterns.Observateur;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
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
	Canvas canvas;
	GraphicsContext gc;
	Label nbPas, nbPoussees;
	ToggleButton IA;
	Button prochain;
	ToggleButton animation;
	BoutonAnnuler annuler;
	BoutonRefaire refaire;

	public boolean animationsEnCours() {
		return vueNiveau.animationsEnCours();
	}

	public void basculeAnimations(boolean valeur) {
		animation.setSelected(valeur);
		if (valeur)
			vueNiveau = new VueNiveauAnimee(vueNiveau.toutNu());
		else
			vueNiveau = vueNiveau.toutNu();
	}

	public void tictac() {
		vueNiveau.tictac();
	}

	public FenetreGraphique(Jeu j, Stage primaryStage) {
		jeu = j;
		primaryStage.setTitle("Sokoban");
		primaryStage.setFullScreen(true);

		vueNiveau = new VueNiveauFixe(jeu, this);
		canvas = new Canvas();
		Pane vue = new Pane(canvas);
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
		IA.setFocusTraversable(false);
		boiteTexte.getChildren().add(IA);
		animation = new ToggleButton("Animation");
		animation.setFocusTraversable(false);
		boiteTexte.getChildren().add(animation);
		prochain = new Button("Prochain");
		prochain.setFocusTraversable(false);
		boiteTexte.getChildren().add(prochain);
		annuler = new BoutonAnnuler(jeu, jeu);
		annuler.setFocusTraversable(false);
		refaire = new BoutonRefaire(jeu, jeu);
		refaire.setFocusTraversable(false);
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

		canvas.widthProperty().bind(vue.widthProperty());
		canvas.heightProperty().bind(vue.heightProperty());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				Configuration.instance().logger().info("Fin du jeu");
			}
		});

		jeu.ajouteObservateur(this);
		miseAJour();
	}

	public void ecouteurDeRedimensionnement(ChangeListener<Number> l) {
		canvas.widthProperty().addListener(l);
		canvas.heightProperty().addListener(l);
	}

	public void ecouteurDeSouris(EventHandler<MouseEvent> h) {
		canvas.setOnMouseClicked(h);
	}

	public void ecouteurDeClavier(EventHandler<KeyEvent> h) {
		scene.setOnKeyPressed(h);
	}

	public void ecouteurIA(EventHandler<ActionEvent> h) {
		IA.setOnAction(h);
	}

	public void ecouteurAnimation(EventHandler<ActionEvent> h) {
		animation.setOnAction(h);
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
		gc = canvas.getGraphicsContext2D();
		vueNiveau.trace();
		nbPas.setText("Pas :" + jeu.niveau().nbPas());
		nbPoussees.setText("Poussées :" + jeu.niveau().nbPoussees());
	}

	double largeur() {
		return canvas.getWidth();
	}

	double hauteur() {
		return canvas.getHeight();
	}

	ImageGraphique charger(InputStream in) {
		ImageGraphique resultat = new ImageGraphique();
		resultat.setImage(new Image(in));
		return resultat;
	}

	void tracer(ImageGraphique r, double x, double y, double l, double h) {
		gc.drawImage(r.getImage(), x, y, l, h);
	}

	void effacer() {
		gc.clearRect(0, 0, largeur(), hauteur());
	}

	public double largeurCase() {
		return vueNiveau.largeurCase();
	}

	public double hauteurCase() {
		return vueNiveau.hauteurCase();
	}
}