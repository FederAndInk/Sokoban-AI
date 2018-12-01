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

public class Jeu extends Observable implements EtatHistorique {
	LecteurNiveaux l;
	Niveau n;
	int direction;

	public Jeu() {
		InputStream in;
		// La méthode de chargement suivante ne dépend pas du système de fichier et sera
		// donc utilisable pour un .jar
		// Attention, par contre, le fichier doit se trouver dans le CLASSPATH
		String fichier = Configuration.instance().lis("FichierNiveaux");
		in = Configuration.charge(fichier);
		if (in == null) {
			System.err.println("ERREUR : impossible de trouver le fichier de niveaux nommé " + fichier);
			System.exit(1);
		}

		Configuration.instance().logger().info("Niveaux trouvés");
		l = new LecteurNiveaux(in);
		prochainNiveau();
		direction = 2;
	}

	public Niveau niveau() {
		return n;
	}

	public int direction() {
		return direction;
	}

	void metAJour(int dL, int dC) {
		switch (dL + 2 * dC) {
		case -2:
			direction = 1;
			break;
		case -1:
			direction = 0;
			break;
		case 1:
			direction = 2;
			break;
		case 2:
			direction = 3;
			break;
		default:
			Configuration.instance().logger().severe("Bug interne, direction invalide");
		}
		super.metAJour();
	}

	public boolean prochainNiveau() {
		Niveau nouveau = l.lisProchainNiveau();
		if (nouveau != null) {
			n = nouveau;
			metAJour(1, 0);
			return true;
		} else {
			return false;
		}
	}

	public Coup jouer(int dL, int dC) {
		Coup cp = n.jouer(dL, dC);
		if (cp != null) {
			metAJour(cp.dirL, cp.dirC);
		}
		return cp;
	}

	@Override
	public boolean peutAnnuler() {
		return niveau().peutAnnuler();
	}
	
	@Override
	public boolean peutRefaire() {
		return niveau().peutRefaire();
	}
	
	public Coup annuler() {
		Coup cp = null;
		if (niveau().peutAnnuler()) {
			cp = niveau().annuler();
			metAJour(cp.dirL, cp.dirC);
		}
		return cp;
	}

	public Coup refaire() {
		Coup cp = null;
		if (niveau().peutRefaire()) {
			cp = niveau().refaire();
			metAJour(cp.dirL, cp.dirC);
		}
		return cp;
	}
}
