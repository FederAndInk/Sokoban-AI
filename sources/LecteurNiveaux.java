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

    boolean estLigneVide(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ';') {
                return true;
            }
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    boolean lisProchainNiveau() {
        int i = -1;
        String ligne = s.nextLine();
        while (estLigneVide(ligne)) {
            ligne = s.nextLine();
        }
        i = 0;
        while (!estLigneVide(ligne)) {
            for (int j = 0; j < ligne.length(); j++) {
                char c = ligne.charAt(j);
                switch (c) {
                    case ';':
                        j = ligne.length();
                        break;
                    default:
                        System.err.println("Caractère inconnu : " + c);
                }
            }
            ligne = s.nextLine();
        }
        return i>=0;
    }
}
