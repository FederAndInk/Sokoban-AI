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

import Modele.Coup;
import Modele.Niveau;

public class AnimationCoup extends Animation {
	double vitesse;
	Niveau niv;
	VueNiveauAnimee vue;
	Coup coup;
	double progres;
	int nbTuiles, pousseur, caisse, offset;

	public AnimationCoup(Niveau n, VueNiveauAnimee vn, Coup cp, double v) {
		super(1);
		niv = n;
		vue = vn;
		coup = cp;
		vitesse = v;
		offset = (coup.sens + 1) / 2;
		progres = 1 - offset;
		nbTuiles = 2;
		int l = coup.posL + offset * coup.dirL;
		int c = coup.posC + offset * coup.dirC;
		pousseur = niv.contenu(l, c);
		if (coup.caisse) {
			nbTuiles++;
			l += coup.dirL;
			c += coup.dirC;
			caisse = niv.contenu(l, c);
		}
	}

	void effaceZone() {
		int l = coup.posL;
		int c = coup.posC;
		for (int i = 0; i < nbTuiles; i++) {
			vue.traceSol(l, c);
			l += coup.dirL;
			c += coup.dirC;
		}
	}

	void afficheObjets() {
		double l = coup.posL + coup.dirL * progres;
		double c = coup.posC + coup.dirC * progres;
		vue.traceObjet(pousseur, l, c);
		if (coup.caisse) {
			l += coup.dirL;
			c += coup.dirC;
			vue.traceObjet(caisse, l, c);
		}
	}

	@Override
	public void miseAJour() {
		if (!estTerminee()) {
			progres += vitesse * coup.sens;
			if (progres < 0)
				progres = 0;
			if (progres > 1)
				progres = 1;
			effaceZone();
			afficheObjets();
			if (estTerminee())
				vue.termineCoup();
		}
	}

	@Override
	public boolean estTerminee() {
		return progres == offset;
	}
}
