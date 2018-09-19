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

import Structures.Iterateur;
import Structures.SequenceTableau;
import Structures.Sequence;
import Structures.SequenceListe;
import java.util.Random;

public class TestSequence {
    static void afficheSequence(Sequence<Integer> s) {
        System.out.print("Sequence : ");
        Iterateur<Integer> it = s.iterateur();
        while (it.aProchain()) {
            Integer i = it.prochain();
            System.out.print(i + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int max = 0, count = 0;
        Random r = new Random();
        Sequence<Integer>[] seq;
        Iterateur<Integer>[] it;
        seq = new Sequence[2];
        it = new Iterateur[2];
        seq[0] = new SequenceListe<>();
        seq[1] = new SequenceTableau<>();
        for (int k = 0; k < seq.length; k++) {
            assert (seq[k].estVide());
        }
        for (int i = 0; i < 100; i++) {
            if (r.nextBoolean()) {
                System.out.println("Insertion de " + max);
                for (int k = 0; k < seq.length; k++) {
                    seq[k].insereQueue(max);
                    assert (!seq[k].estVide());
                }
                max++;
                count++;
            } else {
                if (count > 0) {
                    Integer s = null;
                    if (r.nextBoolean()) {
                        for (int k = 0; k < seq.length; k++) {
                            s = seq[k].extraitTete();
                            assert ((count == 0) == (seq[k].estVide()));
                        }
                        System.out.println("Extraction (tete)");
                    } else {
                        int position = r.nextInt(count);
                        for (int k = 0; k < seq.length; k++) {
                            it[k] = seq[k].iterateur();
                        }
                        for (int j=0; j<=position; j++) {
                            s = null;
                            for (int k = 0; k < seq.length; k++) {
                                assert(it[k].aProchain());
                                if (s == null)
                                    s = it[k].prochain();
                                else {
                                    Integer elt = it[k].prochain();
                                    assert(s == elt);
                                }
                            }
                        }
                        for (int k = 0; k < seq.length; k++)
                            it[k].supprime();
                        System.out.println("Extraction (random) en position " + position);                        
                    }
                    count--;
                    for (int k = 0; k < seq.length; k++)
                        assert ((count == 0) == (seq[k].estVide()));
                }
            }
            for (int k = 0; k < seq.length; k++)
                afficheSequence(seq[k]);
        }
    }
}
