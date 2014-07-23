package deckbuilder.mtg.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Transactional;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.service.DeckService;
import deckbuilder.mtg.service.NoResultException;

@Transactional(readOnly=true)
public class DefaultDeckService implements DeckService {
	
	@PersistenceContext(unitName="deckbuilder.mtg")
	EntityManager entityManager;
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Override
	public List<Deck> getDecksForOwner(Long userId) {
		final TypedQuery<Deck> query = entityManager.createQuery("select d from Deck d where d.owner.id = :owner", Deck.class).setParameter("owner", userId);
		return query.getResultList();
	}
	
	@Override
	public Deck getDeckById(Long id) {
		final TypedQuery<Deck> query = entityManager.createQuery("select d from Deck d where d.id = :id", Deck.class).setParameter("id", id);
		try {
			return query.getSingleResult();
		} catch(javax.persistence.NoResultException exc) {
			throw new NoResultException(exc);
		}
	}
	
	@Override
	@Transactional(readOnly=false)
	public Deck createDeck(Deck deck) {
		entityManager.persist(deck);
		return deck;
	}
	
	@Override
	@Transactional(readOnly=false)
	public Deck updateDeck(Deck deck) {
		entityManager.merge(deck);
		return deck;
	}
	
	@Override
	@Transactional(readOnly=false)
	public void deleteDeck(Long id) {
		Deck deck = entityManager.find(Deck.class, id);
		if(deck != null) {
			entityManager.remove(deck);
		}
	}
}