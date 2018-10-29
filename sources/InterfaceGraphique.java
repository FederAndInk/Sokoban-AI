
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class InterfaceGraphique extends Application {
	static LecteurNiveaux l = null;
	Image pousseur, mur, sol, caisse, but, caisseSurBut;
	Canvas can;
	Niveau n;
	
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
		boiteTexte.getChildren().add(prochain);
		boiteTexte.getChildren().add(new Label("Copyright G. Huard, 2018"));
		// Le label est centré dans l'espace qu'on lui alloue
		//label.setAlignment(Pos.CENTER);
		// S'il y a de la place, on donne tout au label
		//HBox.setHgrow(label, Priority.ALWAYS);
		//label.setMaxWidth(Double.MAX_VALUE);

		HBox boiteScene = new HBox();
		boiteScene.getChildren().add(vue);
		boiteScene.getChildren().add(boiteTexte);
		VBox.setVgrow(vue, Priority.ALWAYS);

		Scene s = new Scene(boiteScene);
		primaryStage.setScene(s);
		primaryStage.show();

		/*
		label.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				System.out.println("Vous lisez le label d'un oeil circonspet...");
			}
		});
		 */

		ChangeListener<Number> ecouteurRedimensionnement = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				trace();

			}
		};
		can.widthProperty().addListener(ecouteurRedimensionnement);
		can.heightProperty().addListener(ecouteurRedimensionnement);
		// On redimensionne le canvas en même temps que son conteneur
		// Remarque : à faire après le primaryStage.show() sinon le Pane 'vue' a une taille nulle qui est transmise au Canvas
		can.widthProperty().bind(vue.widthProperty());
		can.heightProperty().bind(vue.heightProperty());

		// Petit message dans la console quand la fenetre est fermée
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				System.out.println("Fin du jeu");
			}
		});

		trace();
	}

	// Efface tout puis trace l'image aux coordonnées enregistrées
	void trace() {
		GraphicsContext gc = can.getGraphicsContext2D();
		// On remplit en blanc pour voir le Canvas
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, can.getWidth(), can.getHeight());
		gc.drawImage(pousseur, 250, 150, 100, 100);
	}
}