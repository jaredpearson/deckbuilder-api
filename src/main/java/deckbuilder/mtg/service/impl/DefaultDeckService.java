package deckbuilder.mtg.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Provider;

import deckbuilder.mtg.entities.Deck;
import deckbuilder.mtg.service.DeckService;
import deckbuilder.mtg.service.NoResultException;

public class DefaultDeckService implements DeckService {
	
	@Inject 
	Provider<EntityManager> entityManagerProvider;
	
	@Override
	public List<Deck> getDecksForOwner(Long userId) {
		EntityManager entityManager = entityManagerProvider.get();
		TypedQuery<Deck> query = entityManager.createQuery("select d from Deck d where d.owner.id = :owner", Deck.class).setParameter("owner", userId);
		return query.getResultList();
	}
	
	@Override
	public Deck getDeckById(Long id) {
		EntityManager entityManager = entityManagerProvider.get();
		TypedQuery<Deck> query = entityManager.createQuery("select d from Deck d where d.id = :id", Deck.class).setParameter("id", id);
		try {
			return query.getSingleResult();
		} catch(javax.persistence.NoResultException exc) {
			throw new NoResultException(exc);
		}
	}
	
	@Override
	public Deck createDeck(Deck deck) {
		EntityManager entityManager = entityManagerProvider.get();
		entityManager.persist(deck);
		return deck;
	}
	
	@Override
	public Deck updateDeck(Deck deck) {
		EntityManager em = entityManagerProvider.get();
		em.merge(deck);
		return deck;
	}
	
	@Override
	public void deleteDeck(Long id) {
		EntityManager em = entityManagerProvider.get();
		Deck deck = em.find(Deck.class, id);
		if(deck != null) {
			em.remove(deck);
		}
	}
}