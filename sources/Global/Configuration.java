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
package Global;

import Structures.FabriqueSequence;
import Structures.FabriqueSequenceListe;
import Structures.FabriqueSequenceTableau;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {
    static Properties prop;
    static FabriqueSequence fab;
    static Logger logger;

    static void chargerProprietes(Properties p, InputStream in, String nom) {
        try {
            p.load(in);
            System.err.println("Fichier de configuration " + nom + " lu.");
        } catch (IOException e) {
            System.err.println("Impossible de charger " + nom);
            System.err.println(e);
            System.exit(1);
        }
    }

    static Properties proprietes() {
        Properties p;
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("defaut.cfg");
        Properties defaut = new Properties();
        chargerProprietes(defaut, in, "defaut.cfg");
        String nom = System.getProperty("user.home") + "/.armoroides";
        try {
            in = new FileInputStream(nom);
            p = new Properties(defaut);
            chargerProprietes(p, in, nom);
        } catch (FileNotFoundException e) {
            p = defaut;
        }
        return p;
    }

    public static String lis(String nom) {
        if (prop == null) {
            prop = proprietes();
        }
        String value = prop.getProperty(nom);
        if (value != null) {
            return value;
        } else {
            throw new NoSuchElementException("Propriété " + nom + " manquante");
        }
    }

    public static FabriqueSequence fabriqueSequence() {
        if (fab == null) {
            String type = lis("Sequence");
            switch (type) {
                case "Liste":
                    fab = new FabriqueSequenceListe();
                    break;
                case "Tableau":
                    fab = new FabriqueSequenceTableau();
                    break;
                default:
                    throw new NoSuchElementException("Sequences de type " + type + " non supportées");
            }
        }
        return fab;
    }

    public static Logger logger() {
        if (logger == null) {
            logger = Logger.getLogger("Sokoban.Logger");
            logger.setLevel(Level.parse(lis("LogLevel")));
        }
        return logger;
    }
}
