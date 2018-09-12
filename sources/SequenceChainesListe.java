class SequenceChainesListe implements SequenceChaines {
	MaillonChaines tete, queue;

	// Les méthodes implémentant l'interface
	// doivent être publiques
	public void insereQueue(String element) {
		MaillonChaines m = new MaillonChaines(element, null);
                if (queue == null)
                    tete = queue = m;
                else {
                    queue.suivant = m;
                    queue = m;
                }
	}
	public String extraitTete() {
		String resultat;
		// Exception si sommet == null (pile vide)
		resultat = tete.element;
		tete = tete.suivant;
                if (tete == null)
                    queue = null;
		return resultat;
	}
	public boolean estVide() {
		return tete == null;
	}
}
