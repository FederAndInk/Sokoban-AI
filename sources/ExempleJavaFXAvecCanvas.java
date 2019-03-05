
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

import java.io.InputStream;
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
	Random r;

	// Programme principal, ne fait que lancer l'application JavaFX
	public static void main(String[] args) {
		ExempleJavaFXAvecCanvas.launch(args);
	}

	// Methode de démarrage d'une application JavaFX
	@Override
	public void start(Stage primaryStage) {
		final boolean fullScreen = false;
		r = new Random();

		// Titre de la fenêtre
		primaryStage.setTitle("Sokoban");
		// Plein écran (éventuellement)
		if (fullScreen) {
			primaryStage.setFullScreen(true);
		}

		/*
		 * Exemple illustratif :
		 * - quelques composants graphiques, une zone de dessin et du texte
		 * - quelques conteneurs pour contenir ces composants, des boites
		 * - une fenêtre dont le contenu est déterminé par un conteneur (c'est forcément un conteneur)
		 * On peut remarquer que l'agencement des composants graphiques est déterminé par les conteneurs,
		 * qui sont toujours associé à un plan d'organisation sous jacent (Layout)
		 */

		// Zone de dessin
		can = new Canvas(600, 400);
		// Un conteneur simple qui remplit toute la place disponible
		Pane vue = new Pane(can);

		// Une boite horizontale avec 3 bouts de texte
		HBox boiteTexte = new HBox();
		boiteTexte.getChildren().add(new Label("<"));
		Label label = new Label("Cliquez moi !");
		boiteTexte.getChildren().add(label);
		boiteTexte.getChildren().add(new Label(">"));
		// Le texte est centré dans l'espace qu'on lui alloue
		label.setAlignment(Pos.CENTER);
		// S'il y a de la place, on donne tout au label
		HBox.setHgrow(label, Priority.ALWAYS);
		label.setMaxWidth(Double.MAX_VALUE);

		// Une boite verticale pour contenir toute la scène
		VBox boiteScene = new VBox();
		boiteScene.getChildren().add(vue);
		boiteScene.getChildren().add(boiteTexte);
		// S'il y a de la place, on donne tout à la vue
		VBox.setVgrow(vue, Priority.ALWAYS);

		// Contenu de la fenêtre
		Scene s = new Scene(boiteScene);
		primaryStage.setScene(s);
		// On affiche la fenêtre (donne leur taille aux objets graphiques)
		primaryStage.show();

		// On affiche la première image dans la fenêtre
		// (lorsque les objets graphiques ont leurs dimensions)
		// Attention : Images/Pousseur.png doit être dans le CLASSPATH (cf. Correction)
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("Images/Pousseur.png");
		img = new Image(in);
		x = (int) can.getWidth() / 2;
		y = (int) can.getHeight() / 2;
		trace();

		/*
		 * On définit l'interaction :
		 * - toute interaction est gérée par l'utilisateur via une fonction de réaction, EventHandler.handle
		 * - tout composant peut réagir à l'interaction
		 * EventHandler est générique, on la spécialise pour le type d'évènement qui nous intéresse
		 */

		// Clics de souris sur le label
		// Exemple avec un objet d'une classe anonyme pour réagir au clic
		label.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				System.out.println("Vous lisez le label d'un oeil circonspet...");
			}
		});

		// Dans le cas de l'image, on veut compter le nombre de clics
		// Dans cet exemple, on passe par un objet d'une classe nommée CibleMouvante qui
		// contient
		// un entier pour faire le compte
		can.setOnMouseClicked(new CibleMouvante(this));

		// Le redimensionnement aussi est géré par un évènement
		// Ici, on crée un objet d'une classe anonyme qui servira à réagir au
		// redimensionnent selon les deux axes
		ChangeListener<Number> ecouteurRedimensionnement = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				trace();

			}
		};
		can.widthProperty().addListener(ecouteurRedimensionnement);
		can.heightProperty().addListener(ecouteurRedimensionnement);

		// On redimensionne le canvas en même temps que son conteneur
		// Remarque : à faire après le primaryStage.show() sinon le Pane 'vue' a une
		// taille nulle qui est transmise au Canvas
		can.widthProperty().bind(vue.widthProperty());
		can.heightProperty().bind(vue.heightProperty());

		// Petit message dans la console quand la fenetre est fermée
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				System.out.println("Fin du jeu");
			}
		});
	}

	// Efface tout puis trace l'image aux coordonnées enregistrées
	void trace() {
		GraphicsContext gc = can.getGraphicsContext2D();
		// On remplit en blanc pour voir le Canvas
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, can.getWidth(), can.getHeight());
		gc.drawImage(img, x - 50, y - 50, 100, 100);
	}

	// Teste si des coordonnées données sont dans l'image
	boolean toucheBonhomme(double nX, double nY) {
		return (Math.abs(nX - x) < 50) && (Math.abs(nY - y) < 50);
	}

	// Déplace l'image aléatoirement
	void deplacementAleatoire() {
		x = r.nextInt((int) can.getWidth());
		y = r.nextInt((int) can.getHeight());
		trace();
	}
}

class CibleMouvante implements EventHandler<MouseEvent> {
	int i;
	ExempleJavaFXAvecCanvas app;

	CibleMouvante(ExempleJavaFXAvecCanvas e) {
		i = 0;
		app = e;
	}

	@Override
	public void handle(MouseEvent e) {
		// Si on clique dans l'image, on la retrace ailleurs
		if (app.toucheBonhomme(e.getX(), e.getY())) {
			i++;
			System.out.print("Ouille, tu m'as clické " + i + " fois");
			app.deplacementAleatoire();
		} else {
			System.out.print("Loupé");
		}

		System.out.println(", click en (" + e.getX() + ", " + e.getY() + ") !!!");
	}
}
