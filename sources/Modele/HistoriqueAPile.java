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

import Global.Configuration;
import Structures.FabriqueSequence;
import Structures.Sequence;

public class HistoriqueAPile<E> implements EtatHistorique {
	Sequence<E> passe, futur;
	
	HistoriqueAPile() {
		FabriqueSequence fab = Configuration.instance().fabriqueSequence();
		passe = fab.nouvelle();
		futur = fab.nouvelle();
	}
	
	public boolean peutAnnuler() {
		return !passe.estVide();
	}
	
	public boolean peutRefaire() {
		return !futur.estVide();
	}
	
	private E transfert(Sequence<E> source, Sequence<E> destination) {
		E resultat = source.extraitTete();
		destination.insereTete(resultat);
		return resultat;
	}
	
	public E annuler() {
		return transfert(passe, futur);
	}
	
	public E refaire() {
		return transfert(futur, passe);
	}
	
	public void faire(E nouveau) {
		passe.insereTete(nouveau);
		while (!futur.estVide()) {
			futur.extraitTete();
		}
	}
}
