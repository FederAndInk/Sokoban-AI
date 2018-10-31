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
package Modele;

import java.io.InputStream;

import Global.Configuration;
import Patterns.Observable;

public class Jeu extends Observable {
	LecteurNiveaux l;
	Niveau n;

	public Jeu() {
		InputStream in;
		// La méthode de chargement suivante ne dépend pas du système de fichier et sera
		// donc utilisable pour un .jar
		// Attention, par contre, le fichier doit se trouver dans le CLASSPATH
		String fichier = Configuration.lis("FichierNiveaux");
		in = Configuration.charge(fichier);
		if (in == null) {
			System.err.println("ERREUR : impossible de trouver le fichier de niveaux nommé " + fichier);
			System.exit(1);
		}

		Configuration.logger().info("Niveaux trouvés");
		l = new LecteurNiveaux(in);
		prochainNiveau();
	}
	
	public Niveau niveau() {
		return n;
	}
	
	public void prochainNiveau() {
		n = l.lisProchainNiveau();
		metAJour();
	}
	
	public void jouer(int l, int c) {
		int dL = l - n.lignePousseur();
		int dC = c - n.colonnePousseur();
		// Seulement une direction, -1 ou +1
		if ((dL*dC == 0) && ((dL+dC)*(dL+dC) == 1)) {
			if (n.jouer(dL, dC)) {
				metAJour();
			}
		}
	}
}
