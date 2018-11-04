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
import Structures.Iterateur;
import Structures.Sequence;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class FenetreGraphique {
	Jeu jeu;
	Niveau n;
	Image pousseur, mur, sol, caisse, but, caisseSurBut;
	Image[][] pousseurs;
	int direction, etape;
	Scene scene;
	Canvas can;
	Button prochain;
	BoutonAnnuler annuler;
	BoutonRefaire refaire;

	double width;
	double height;
	double tileWidth;
	double tileHeight;
	GraphicsContext gc;
	Sequence<AnimationCoup> animations;

	private Image lisImage(String nom) {
		String resource = Configuration.lis(nom);
		Configuration.logger().info("Lecture de " + resource);
		return new Image(Configuration.charge(resource));
	}

	public void changeEtapePousseur() {
		etape = (etape + 1) % pousseurs[direction].length;
		pousseur = pousseurs[direction][etape];
	}

	public void ajouteAnimation(AnimationCoup a) {
		animations.insereQueue(a);
	}

	public boolean animationsEnCours() {
		return !animations.estVide();
	}

	public void annuleAnimations() {
		animations = Configuration.fabriqueSequence().nouvelle();
	}

	public FenetreGraphique(Jeu j, Stage primaryStage) {
		pousseurs = new Image[4][4];
		for (int d = 0; d < pousseurs.length; d++)
			for (int i = 0; i < pousseurs[d].length; i++)
				pousseurs[d][i] = lisImage("Pousseur_" + d + "_" + i);
		mur = lisImage("Mur");
		sol = lisImage("Sol");
		caisse = lisImage("Caisse");
		but = lisImage("But");
		caisseSurBut = lisImage("CaisseSurBut");

		jeu = j;
		int direction = jeu.direction();
		etape = 0;
		pousseur = pousseurs[direction][etape];
		animations = Configuration.fabriqueSequence().nouvelle();
		primaryStage.setTitle("Sokoban");
		primaryStage.setFullScreen(true);

		can = new Canvas(600, 400);
		Pane vue = new Pane(can);

		VBox boiteTexte = new VBox();
		boiteTexte.setAlignment(Pos.CENTER);

		Label titre = new Label("Sokoban");
		titre.setMaxHeight(Double.MAX_VALUE);
		titre.setAlignment(Pos.TOP_CENTER);
		boiteTexte.getChildren().add(titre);
		VBox.setVgrow(titre, Priority.ALWAYS);

		prochain = new Button("Prochain");
		annuler = new BoutonAnnuler(jeu);
		refaire = new BoutonRefaire(jeu);
		HBox annulerRefaire = new HBox(annuler, refaire);
		annulerRefaire.setAlignment(Pos.CENTER);
		boiteTexte.getChildren().add(prochain);
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

		can.widthProperty().bind(vue.widthProperty());
		can.heightProperty().bind(vue.heightProperty());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				Configuration.logger().info("Fin du jeu");
			}
		});
	}

	public void ecouteurDeRedimensionnement(ChangeListener<Number> l) {
		can.widthProperty().addListener(l);
		can.heightProperty().addListener(l);
	}

	public void ecouteurDeSouris(EventHandler<MouseEvent> h) {
		can.setOnMouseClicked(h);
	}

	public void ecouteurDeClavier(EventHandler<KeyEvent> h) {
		scene.setOnKeyPressed(h);
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

	public void traceSol(int contenu, int l, int c) {
		double x = c * tileWidth;
		double y = l * tileHeight;
		if (Niveau.estBut(contenu))
			gc.drawImage(but, x, y, tileWidth, tileHeight);
		else
			gc.drawImage(sol, x, y, tileWidth, tileHeight);
	}

	public void traceObjet(int contenu, double l, double c) {
		double x = c * tileWidth;
		double y = l * tileHeight;
		if (Niveau.estMur(contenu))
			gc.drawImage(mur, x, y, tileWidth, tileHeight);
		if (Niveau.aPousseur(contenu))
			gc.drawImage(pousseur, x, y, tileWidth, tileHeight);
		if (Niveau.aCaisse(contenu))
			if (Niveau.estBut(contenu))
				gc.drawImage(caisseSurBut, x, y, tileWidth, tileHeight);
			else
				gc.drawImage(caisse, x, y, tileWidth, tileHeight);
	}

	public void miseAJourAnimations() {
		miseAJourPousseur();
		if (animationsEnCours()) {
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
		} else {
			int ligne = n.lignePousseur();
			int colonne = n.colonnePousseur();
			int contenu = n.contenu(ligne, colonne);
			traceSol(contenu, ligne, colonne);
			traceObjet(contenu, ligne, colonne);
		}
	}

	void miseAJourPousseur() {
		direction = jeu.direction();
		pousseur = pousseurs[direction][etape];
	}

	public void retracerNiveau() {
		n = jeu.niveau();
		if (n == null) {
			Configuration.logger().info("Dernier niveau lu, fin du jeu !");
			System.exit(0);
		}

		miseAJourPousseur();

		width = can.getWidth();
		height = can.getHeight();
		tileWidth = width / n.colonnes();
		tileHeight = height / n.lignes();
		tileWidth = Math.min(tileWidth, tileHeight);
		tileHeight = Math.min(tileWidth, tileHeight);
		gc = can.getGraphicsContext2D();

		gc.clearRect(0, 0, width, height);
		for (int ligne = 0; ligne < n.lignes(); ligne++)
			for (int colonne = 0; colonne < n.colonnes(); colonne++) {
				int contenu = n.contenu(ligne, colonne);
				traceSol(contenu, ligne, colonne);
				traceObjet(contenu, ligne, colonne);
			}
	}

	public double tileWidth() {
		return tileWidth;
	}

	public double tileHeight() {
		return tileHeight;
	}
}