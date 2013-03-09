package deckbuilder.mtg.service;

import java.util.List;

import deckbuilder.mtg.entities.Deck;

public interface DeckService {
	public List<Deck> getDecksForOwner(Long ownerId);
	public Deck getDeckById(Long id);
	public Deck createDeck(Deck deck);
	public Deck updateDeck(Deck deck);
	public void deleteDeck(Long id);
}