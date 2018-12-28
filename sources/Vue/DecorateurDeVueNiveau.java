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

abstract class DecorateurDeVueNiveau extends VueNiveau {
	VueNiveau vueNiveau;

	DecorateurDeVueNiveau(VueNiveau v) {
		super(v.jeu, v.fenetre);
		vueNiveau = v;
	}

	@Override
	void traceSol(int l, int c) {
		vueNiveau.traceSol(l, c);
	}

	@Override
	void traceObjet(int l, int c) {
		vueNiveau.traceObjet(l, c);
	}

	@Override
	void traceObjet(int contenu, double l, double c) {
		vueNiveau.traceObjet(contenu, l, c);
	}

	@Override
	ImageGraphique trouveObjet(int contenu) {
		return vueNiveau.trouveObjet(contenu);
	}

	@Override
	void miseAJour() {
		vueNiveau.miseAJour();
	}

	@Override
	double tileWidth() {
		return vueNiveau.tileWidth();
	}

	@Override
	double tileHeight() {
		return vueNiveau.tileHeight();
	}

	@Override
	boolean animationsEnCours() {
		return vueNiveau.animationsEnCours();
	}

	@Override
	void tictac() {
		vueNiveau.tictac();
	}

	@Override
	VueNiveau toutNu() {
		return vueNiveau.toutNu();
	}

	@Override
	void changePousseur(ImageGraphique p) {
		vueNiveau.changePousseur(p);
	}
}