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

class SequenceTableau<E> implements Sequence<E> {
	Object[] elements;
	int taille, debut;

	public SequenceTableau() {
		// Taille par défaut
		elements = new Object[1];
		debut = 0;
		taille = 0;
	}

	private void redimensionne(int nouvelleCapacite) {
		Object[] nouveau;

		if (nouvelleCapacite > elements.length) {
			nouveau = new Object[nouvelleCapacite];
			int aCopier = taille;
			for (int i = 0; i < aCopier; i++)
				nouveau[i] = extraitTete();
			debut = 0;
			taille = aCopier;
			elements = nouveau;
		}
	}

	@Override
	public void insereQueue(E element) {
		if (taille == elements.length)
			redimensionne(taille * 2);
		elements[(debut + taille) % elements.length] = element;
		taille++;
	}

	@Override
	public E extraitTete() {
		// Resultat invalide si la sequence est vide
		@SuppressWarnings("unchecked")
		E resultat = (E) elements[debut];
		debut = (debut + 1) % elements.length;
		taille--;
		return resultat;
	}

	@Override
	public boolean estVide() {
		return taille == 0;
	}

	@Override
	public Iterateur<E> iterateur() {
		return new IterateurSequenceTableau<>(this);
	}
}
