
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

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ExempleJavaFXAvecImages extends Application {
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
		ImageView vue = new ImageView();
		vue.setImage(image);
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
		// S'il y a de la place, on donne tout à la boite horizontale
		VBox.setVgrow(boite2, Priority.ALWAYS);

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
		vue.setOnMouseClicked(new CibleVivante());

		// Petit message dans la console quand la fenetre est fermée
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent we) {
				System.out.println("Fin du jeu");
			}
		});

		// On affiche la fenêtre
		primaryStage.show();
	}
}

class CibleVivante implements EventHandler<MouseEvent> {
	// Attributs initialisés à 0 par java
	int i;

	@Override
	public void handle(MouseEvent e) {
		i++;
		System.out.println("Ouille, tu m'as clické " + i + " fois en (" + e.getX() + ", " + e.getY() + ") !!!");
	}
}
