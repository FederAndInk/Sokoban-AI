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
import Patterns.Observateur;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class VueNiveau extends Canvas implements Observateur {
	Jeu jeu;
	Image pousseur, mur, sol, caisse, but, caisseSurBut;

	double width;
	double height;
	double tileWidth;
	double tileHeight;
	GraphicsContext gc;

	private Image lisImage(String nom) {
		String resource = Configuration.lis(nom);
		Configuration.logger().info("Lecture de " + resource);
		return new Image(Configuration.charge(resource));
	}

	public VueNiveau(Jeu j) {
		pousseur = lisImage("Pousseur");
		mur = lisImage("Mur");
		sol = lisImage("Sol");
		caisse = lisImage("Caisse");
		but = lisImage("But");
		caisseSurBut = lisImage("CaisseSurBut");

		jeu = j;
	}
	
	void traceSol(int contenu, int l, int c) {
		double x = c * tileWidth;
		double y = l * tileHeight;
		if (Niveau.estBut(contenu))
			gc.drawImage(but, x, y, tileWidth, tileHeight);
		else
			gc.drawImage(sol, x, y, tileWidth, tileHeight);
	}
	
	void traceObjet(int contenu, int l, int c) {
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

	@Override
	public void miseAJour() {
		Niveau n = jeu.niveau();
		if (n == null) {
			Configuration.logger().info("Dernier niveau lu, fin du jeu !");
			System.exit(0);
		}

		width = getWidth();
		height = getHeight();
		tileWidth = width / n.colonnes();
		tileHeight = height / n.lignes();
		tileWidth = Math.min(tileWidth, tileHeight);
		tileHeight = Math.min(tileWidth, tileHeight);
		gc = getGraphicsContext2D();

		gc.clearRect(0, 0, width, height);
		for (int ligne = 0; ligne < n.lignes(); ligne++)
			for (int colonne = 0; colonne < n.colonnes(); colonne++) {
				int contenu = n.get(ligne, colonne);
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