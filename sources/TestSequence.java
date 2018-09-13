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

public class TestSequence {
    public static void main(String [] args) {
        int min=0, max=0, count=0;
        Random r = new Random();
        Sequence<Integer> seq = new SequenceListe<>();
        // Changer les construction pour tester l'autre sequence
        //SequenceChaines seq = new SequenceChainesTableau();
        
        assert(seq.estVide());
        for (int i=0; i<10000; i++) {
            if (r.nextBoolean()) {
                // Superflu (java s'en occupe en cas d'ommission)
                Integer s = new Integer(max);
                max++;
                System.out.println("Insertion de " + s);
                seq.insereQueue(s);
                count++;
                assert(!seq.estVide());
            } else {
                if (count > 0) {
                    Integer s = seq.extraitTete();
                    System.out.println("Extraction de " + s);
                    // Superflu
                    int val = s.intValue();
                    assert(val == min);
                    min++;
                    count--;
                    assert((count == 0) == (seq.estVide()));
                }
            }
        }
    }
}
