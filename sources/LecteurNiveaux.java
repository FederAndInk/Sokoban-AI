
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
import java.util.Scanner;

public class LecteurNiveaux {
	Scanner s;

	LecteurNiveaux(InputStream in) {
		s = new Scanner(in);
	}

	String lireLigne() {
		String ligne = null;
		try {
			ligne = s.nextLine();
			// Nettoyage des séparateurs de fin et commentaires
			int dernier = -1;
			for (int i = 0; i < ligne.length(); i++) {
				char c = ligne.charAt(i);
				if (!Character.isWhitespace(c) && (c != ';')) {
					dernier = i;
				}
				if (c == ';') {
					i = ligne.length();
				}
			}
			return ligne.substring(0, dernier + 1);
		} catch (Exception e) {
		}
		return ligne;
	}

	Niveau lisProchainNiveau() {
		String ligne = lireLigne();
		while (ligne.length() == 0) {
			ligne = lireLigne();
			if (ligne == null) {
				return null;
			}
		}
		int i = 0;
		int jMax = 0;
		Sequence<String> seq = new SequenceListe<>();
		while (ligne.length() > 0) {
			if (ligne.length() > jMax) {
				jMax = ligne.length();
			}
			seq.insereQueue(ligne);
			ligne = lireLigne();
			i++;
		}
		return new Niveau(i, jMax, seq);
	}
}
