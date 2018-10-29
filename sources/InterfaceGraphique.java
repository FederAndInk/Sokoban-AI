
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

import java.util.Random;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class InterfaceGraphique extends Application {
	static LecteurNiveaux l = null;
	Image pousseur, mur, sol, caisse, but, caisseSurBut;
	Canvas can;
	Niveau n;

	double width;
	double height;
	double tileWidth;
	double tileHeight;

	private Image lisImage(String nom) {
		String resource = Global.Configuration.lis(nom);
		Global.Configuration.logger().info("Lecture de " + resource);
		return new Image(Global.Configuration.charge(resource));
	}

	@Override
	public void init() {
		pousseur = lisImage("Pousseur");
		mur = lisImage("Mur");
		sol = lisImage("Sol");
		caisse = lisImage("Caisse");
		but = lisImage("But");
		caisseSurBut = lisImage("CaisseSurBut");
		n = l.lisProchainNiveau();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Sokoban");

		can = new Canvas(600, 400);
		Pane vue = new Pane(can);

		VBox boiteTexte = new VBox();
		boiteTexte.getChildren().add(new Label("Sokoban"));
		Button prochain = new Button("Prochain");
		BorderPane conteneurProchain = new BorderPane(prochain);
		boiteTexte.getChildren().add(conteneurProchain);
		boiteTexte.getChildren().add(new Label("Copyright G. Huard, 2018"));
		VBox.setVgrow(conteneurProchain, Priority.ALWAYS);

		HBox boiteScene = new HBox();
		boiteScene.getChildren().add(vue);
		boiteScene.getChildren().add(boiteTexte);
		HBox.setHgrow(vue, Priority.ALWAYS);

		Scene s = new Scene(boiteScene);
		primaryStage.setScene(s);
		primaryStage.show();

		prochain.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				n = l.lisProchainNiveau();
				trace();
			}
		});

		can.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				int l = (int) Math.floor(e.getY()/tileHeight);
				int c = (int) Math.floor(e.getX()/tileWidth);
				System.out.println("Vous avez cliqué en ligne " + l + ", colonne " + c);
			}
		});

		ChangeListener<Number> ecouteurRedimensionnement = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				trace();

			}
		};
		can.widthProperty().addListener(ecouteurRedimensionnement);
		can.heightProperty().addListener(ecouteurRedimensionnement);
		can.widthProperty().bind(vue.widthProperty());
		can.heightProperty().bind(vue.heightProperty());

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				Global.Configuration.logger().info("Fin du jeu");
			}
		});

		trace();
	}

	void trace() {
		if (n == null)
			System.exit(0);

		width = can.getWidth();
		height = can.getHeight();
		tileWidth = width / n.colonnes();
		tileHeight = height / n.lignes();
		tileWidth = Math.min(tileWidth, tileHeight);
		tileHeight = Math.min(tileWidth, tileHeight);
	
		GraphicsContext gc = can.getGraphicsContext2D();
		gc.clearRect(0, 0, width, height);
		for (int ligne = 0; ligne < n.lignes(); ligne++)
			for (int colonne = 0; colonne < n.colonnes(); colonne++) {
				double x = colonne*tileWidth;
				double y = ligne*tileHeight;
				gc.drawImage(sol, x, y, tileWidth, tileHeight);
				if (n.estMur(ligne, colonne)) {
					gc.drawImage(mur, x, y, tileWidth, tileHeight);
				} else {
					if (n.estBut(ligne, colonne)) {
						gc.drawImage(but, x, y, tileWidth, tileHeight);
						if (n.aCaisse(ligne, colonne))
							gc.drawImage(caisseSurBut, x, y, tileWidth, tileHeight);
					} else {
						if (n.aCaisse(ligne, colonne))
							gc.drawImage(caisse, x, y, tileWidth, tileHeight);
					}	
					if (n.aPousseur(ligne, colonne))
						gc.drawImage(pousseur, x, y, tileWidth, tileHeight);
				}
			}
	}
}