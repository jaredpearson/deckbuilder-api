package deckbuilder.mtg.service.impl;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.inject.Provider;

import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.service.DeckCardService;
import deckbuilder.mtg.service.NoResultException;

public class DefaultDeckCardService implements DeckCardService {
	@Inject
	Provider<EntityManager> entityManagerProvider;
	
	@Override
	public DeckCard createDeckCard(DeckCard deckCard) {
		entityManagerProvider.get().persist(deckCard);
		return deckCard;
	}
	
	@Override
	public DeckCard getDeckCardById(Long deckCardId) {
		final DeckCard deckCard = entityManagerProvider.get().find(DeckCard.class, deckCardId);
		if (deckCard == null) {
			throw new NoResultException();
		}
		return deckCard;
	}
}
