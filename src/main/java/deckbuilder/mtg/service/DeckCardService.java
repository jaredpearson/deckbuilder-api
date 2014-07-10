package deckbuilder.mtg.service;

import javax.annotation.Nonnull;

import deckbuilder.mtg.entities.DeckCard;

public interface DeckCardService {
	public DeckCard createDeckCard(DeckCard deckCard);

	/**
	 * Gets the deck card associated to the given ID. If the deck card cannot be found, a {@link NoResultException} is thrown.
	 * @param id The ID of the set
	 * @throws NoResultException when no deck card is found with the specified ID
	 */
	public @Nonnull DeckCard getDeckCardById(@Nonnull Long deckCardId);
	
	/**
	 * Gets the deck card associated to the given ID. If no deck card is found, a {@link NoResultException} is thrown.
	 * @param deckId the ID of the deck
	 * @param cardId the ID of the card
	 */
	public @Nonnull DeckCard getDeckCardByDeckIdAndCardId(@Nonnull Long deckId, Long cardId);
}
