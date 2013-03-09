package deckbuilder.mtg.service;

import java.util.List;
import java.util.NoSuchElementException;

import deckbuilder.mtg.entities.CardSet;

public interface CardSetService {
	/**
	 * Gets the set associated to the given ID. If the set cannot be found, a {@link NoResultException} is thrown.
	 * @param id The ID of the set
	 * @throws NoResultException when no set is found with the specified ID
	 */
	public CardSet getCardSetById(Long id) throws NoSuchElementException;
	
	public List<CardSet>  getCardSets();
	
	public CardSet createCardSet(CardSet cardSet);
}
