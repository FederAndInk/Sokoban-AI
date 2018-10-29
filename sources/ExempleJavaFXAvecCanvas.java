
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

public class ExempleJavaFXAvecCanvas extends Application {
		int x, y;
		Canvas can;
		Image img;

	// Methode permettant de démarrer une application JavaFX
	@Override
	public void start(Stage primaryStage) throws Exception {
		final boolean fullScreen = false;

		primaryStage.setTitle("Sokoban");

		/*
		 * Exemple simple :
		 * - quelques composants graphiques, une image et du texte
		 * - quelques conteneurs pour contenir ces composants, des boites
		 * - une fenêtre dont le contenu est déterminé par un conteneur (c'est forcément un conteneur)
		 * On peut remarquer que l'agencement des composants graphiques est déterminé par les conteneurs,
		 * qui sont toujours associé à un plan d'organisation sous jacent (Layout)
		 */
		Image image = new Image(Global.Configuration.charge("Images/Kenny/PNG/Retina/Player/player_03.png"));
		Canvas can = new Canvas(600, 400);
		Pane vue = new Pane(can);
		Label label = new Label("Redimensionez !");

		// Une boite horizontale avec 3 bouts de texte
		HBox boite2 = new HBox();
		boite2.getChildren().add(new Label("<"));
		boite2.getChildren().add(label);
		boite2.getChildren().add(new Label(">"));
		// Tout est centré verticalement dans la boite horizontale
		boite2.setAlignment(Pos.CENTER);
		// Le label est centrée lui même dans l'espace qu'on lui alloue
		label.setAlignment(Pos.CENTER);
		// S'il y a de la place, on donne tout au label
		HBox.setHgrow(label, Priority.ALWAYS);
		label.setMaxWidth(Double.MAX_VALUE);

		// Une boite verticale pour contenir tout le reste
		VBox boite = new VBox();
		boite.getChildren().add(vue);
		boite.getChildren().add(boite2);
		// Tout est centré horizontalement dans la boite verticale
		boite.setAlignment(Pos.CENTER);
		// S'il y a de la place, on donne tout à la vue
		VBox.setVgrow(vue, Priority.ALWAYS);

		// Contenu de la fenêtre
		Scene s = new Scene(boite);
		primaryStage.setScene(s);

		// Plein écran (éventuellement)
		if (fullScreen) {
			primaryStage.setFullScreen(true);
		}

		/*
		 * On définit l'interaction :
		 * - toute interaction est gérée par l'utilisateur via une fonction de réaction, EventHandler.handle
		 * - tout composant peut réagir à l'interaction
		 * EventHandler est générique, on la spécialise pour le type d'évènement qui nous intéresse
		 */
		label.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				System.out.println("Vous lisez le label d'un oeil circonspet...");
			}
		});
		/* Dans le cas de l'image, on veut compter le nombre de clics. On passe donc
		 * par une classe qui contient un entier pour faire le compte.
		 */
		vue.setOnMouseClicked(new CibleMouvante(this));
		/* Le redimensionnement aussi est géré par un évènement
		 */
		can.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				
			}
		});

		// Petit message dans la console quand la fenetre est fermée
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				System.out.println("Fin du jeu");
			}
		});

		// On affiche la fenêtre
		primaryStage.show();

		x = (int) can.getWidth() / 2;
		y = (int) can.getHeight() / 2;
		// On redimensionne le canvas en même temps que son conteneur
		can.widthProperty().bind(vue.widthProperty());
		can.heightProperty().bind(vue.heightProperty());
	}

	// Efface tout puis trace l'image aux coordonnées données
	void trace(int nX, int nY) {
		GraphicsContext gc = can.getGraphicsContext2D();
		// On remplit en blanc pour voir le Canvas
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, can.getWidth(), can.getHeight());
		gc.drawImage(img, x - 50, y - 50, 100, 100);
	}
}

class CibleMouvante implements EventHandler<MouseEvent> {
	// Attributs initialisés à 0 par java
	int i;
	Random r;
	ExempleJavaFXAvecCanvas app;

	CibleMouvante(ExempleJavaFXAvecCanvas e) {
		r = new Random();
		app = e;
	}

	@Override
	public void handle(MouseEvent e) {
		// Si on clique dans l'image, on la retrace ailleurs
		if ((Math.abs(e.getX() - app.x) < 50) && (Math.abs(e.getY() - app.y) < 50)) {
			i++;
			System.out.print("Ouille, tu m'as clické " + i + " fois");
			app.trace(r.nextInt((int) app.can.getWidth()), r.nextInt((int) app.can.getHeight()));
		} else {
			System.out.print("Loupé");
		}

		System.out.println(", click en (" + e.getX() + ", " + e.getY() + ") !!!");
	}
}
