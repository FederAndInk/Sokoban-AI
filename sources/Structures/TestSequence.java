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

package Structures;

import java.util.Random;

import Global.Configuration;

public class TestSequence {
	static int min, max, count;

	static String operation(Sequence seq, int code) {
		String s = null;
		int val = 0, pos = 0;
		System.out.println(seq);
		System.out.print("Affichage avec itérateur :");
		Iterateur it = seq.iterateur();
		while (it.aProchain()) {
			System.out.print(" " + it.prochain());
		}
		System.out.println();
		switch (code) {
		case 0:
			s = String.format("%08d", min);
			System.out.println("Insertion en Tete de " + s);
			seq.insereTete(s);
			break;
		case 1:
			s = String.format("%08d", max);
			System.out.println("Insertion en Queue de " + s);
			seq.insereQueue(s);
			break;
		case 2:
			if (count > 0) {
				pos = new Random().nextInt(count);
				it = seq.iterateur();
				System.out.println("Extraction de l'élément de position " + pos);
				while (pos > 0) {
					assert (it.aProchain());
					it.prochain();
					pos--;
				}
				assert (it.aProchain());
				s = it.prochain();
				val = Integer.parseInt(s);
			}
		case 3:
			if (count > 0) {
				s = seq.extraitTete();
				System.out.println("Extraction en Tete de " + s);
				val = Integer.parseInt(s);
			}
			break;
		}
		if (code < 2) {
			assert (!seq.estVide());
		} else {
			if (count > 0) {
				assert ((val > min) && (val < max));
				if ((code == 3) || ((code == 2) && (pos == 0)))
					min = val;
				if ((code == 2) && (pos == count - 1))
					max = val;
				if (min == max)
					max++;
				assert ((count == 1) == (seq.estVide()));
			}
		}
		return s;
	}

	public static void main(String[] args) {
		Random r = new Random();
		Sequence s;
		s = Configuration.instance().fabriqueSequence().nouvelle();

		assert (s.estVide());
		min = -1;
		max = 0;
		count = 0;
		for (int i = 0; i < 100; i++) {
			int code = r.nextInt(4);
			operation(s, code);
			if (code < 2) {
				count++;
				if (code < 1)
					min--;
				else
					max++;
			} else {
				if (count > 0) {
					count--;
				}
			}
		}
	}
}
