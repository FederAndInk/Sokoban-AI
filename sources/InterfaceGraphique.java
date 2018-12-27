
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

import java.util.logging.Logger;

import Global.Configuration;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
	GraphicsContext gc;
	Logger logger;

	private Image lisImage(String nom) {
		String resource = Configuration.instance().lis(nom);
		logger.info("Lecture de " + resource);
		return new Image(Configuration.charge(resource));
	}

	@Override
	public void init() {
		logger = Configuration.instance().logger();
		pousseur = lisImage("Pousseur");
		mur = lisImage("Mur");
		sol = lisImage("Sol");
		caisse = lisImage("Caisse");
		but = lisImage("But");
		caisseSurBut = lisImage("CaisseSurBut");
		n = l.lisProchainNiveau();
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Sokoban");

		can = new Canvas(600, 400);
		Pane vue = new Pane(can);

		VBox boiteTexte = new VBox();
		boiteTexte.setAlignment(Pos.CENTER);

		Label titre = new Label("Sokoban");
		titre.setMaxHeight(Double.MAX_VALUE);
		titre.setAlignment(Pos.TOP_CENTER);
		boiteTexte.getChildren().add(titre);
		VBox.setVgrow(titre, Priority.ALWAYS);

		Button prochain = new Button("Prochain");
		boiteTexte.getChildren().add(prochain);

		Label copyright = new Label("Copyright G. Huard, 2018");
		copyright.setMaxHeight(Double.MAX_VALUE);
		copyright.setAlignment(Pos.BOTTOM_LEFT);
		boiteTexte.getChildren().add(copyright);
		VBox.setVgrow(copyright, Priority.ALWAYS);

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
				if (n == null)
					System.exit(0);
				trace();
			}
		});

		can.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				int l = (int) (e.getY() / tileHeight);
				int c = (int) (e.getX() / tileWidth);
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
				logger.info("Fin du jeu");
			}
		});

		trace();
	}

	void traceSol(int l, int c) {
		double x = c * tileWidth;
		double y = l * tileHeight;
		if (n.aBut(l, c))
			gc.drawImage(but, x, y, tileWidth, tileHeight);
		else
			gc.drawImage(sol, x, y, tileWidth, tileHeight);
	}

	void traceObjet(int l, int c) {
		double x = c * tileWidth;
		double y = l * tileHeight;
		if (n.aMur(l, c))
			gc.drawImage(mur, x, y, tileWidth, tileHeight);
		if (n.aPousseur(l, c))
			gc.drawImage(pousseur, x, y, tileWidth, tileHeight);
		if (n.aCaisse(l, c))
			if (n.aBut(l, c))
				gc.drawImage(caisseSurBut, x, y, tileWidth, tileHeight);
			else
				gc.drawImage(caisse, x, y, tileWidth, tileHeight);
	}

	void trace() {
		if (n == null) {
			logger.info("Dernier niveau lu, fin du jeu !");
			System.exit(0);
		}

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
				traceSol(ligne, colonne);
				traceObjet(ligne, colonne);
			}
	}
}