package deckbuilder.mtg.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.inject.Provider;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.service.CardSetService;
import deckbuilder.mtg.service.NoResultException;

public class DefaultCardSetService implements CardSetService {
	
	@Inject 
	Provider<EntityManager> entityManagerProvider;

	@Override
	public CardSet getCardSetById(Long id) throws NoSuchElementException {
		CardSet set = entityManagerProvider.get().find(CardSet.class, id);
		if(set == null) {
			throw new NoResultException();
		}
		return set;
	}
	
	@Override
	public List<CardSet> getCardSets() {
		return entityManagerProvider.get().createQuery("select cs from CardSet cs", CardSet.class).getResultList();
	}

	@Override
	public CardSet createCardSet(CardSet cardSet) {
		entityManagerProvider.get().persist(cardSet);
		return cardSet;
	}
}
