package deckbuilder.mtg.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import deckbuilder.mtg.entities.CardSet;
import deckbuilder.mtg.service.CardSetService;
import deckbuilder.mtg.service.NoResultException;

@Transactional(readOnly=true)
public class DefaultCardSetService implements CardSetService {

	@PersistenceContext(unitName="deckbuilder.mtg")
	EntityManager entityManager;
	
	@Override
	public CardSet getCardSetById(Long id) throws NoSuchElementException {
		CardSet set = entityManager.find(CardSet.class, id);
		if(set == null) {
			throw new NoResultException();
		}
		return set;
	}
	
	@Override
	public List<CardSet> getCardSets() {
		return entityManager.createQuery("select cs from CardSet cs", CardSet.class).getResultList();
	}

	@Override
	@Transactional(readOnly=false)
	public CardSet createCardSet(CardSet cardSet) {
		entityManager.persist(cardSet);
		return cardSet;
	}
}
