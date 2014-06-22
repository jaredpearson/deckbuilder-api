package deckbuilder.mtg.service;

import java.util.List;

import deckbuilder.mtg.entities.Card;

public interface CardService {
	public List<Card> getCards();

	/**
	 * Gets the card associated to the given ID. If the card cannot be found, a {@link NoResultException} is thrown.
	 * @param id The ID of the card
	 * @throws NoResultException when no card is found with the specified ID
	 */
	public Card getCardById(Long id) throws NoResultException;

	/**
	 * Creates a new card from the card data specified.
	 * @param card The card data
	 * @return Returns a fully instantiated card
	 */
	public Card createCard(Card card);
	
	/**
	 * Attempts to find all of the cards from the specified card IDs. If the ID cannot be found, then
	 * it is omitted and the other entities are returned.
	 * @param cardIds The IDs of the cards to be found
	 * @return Returns the entities that could be found
	 */
	public List<Card> getCardsById(List<Long> cardIds);
}
