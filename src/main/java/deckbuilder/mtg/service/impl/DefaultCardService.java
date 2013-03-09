package deckbuilder.mtg.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Provider;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.NoResultException;

public class DefaultCardService implements CardService {
	
	@Inject
	Provider<EntityManager> entityManagerProvider;
	
	@Override
	public List<Card> getCards() {
		EntityManager em = entityManagerProvider.get();
		TypedQuery<Card> query = em.createQuery("select c from Cards c", Card.class);
		return query.getResultList();
	}
	
	@Override
	public Card getCardById(Long id) {
		EntityManager em = entityManagerProvider.get();
		Card card = em.find(Card.class, id);
		if(card == null) {
			throw new NoResultException();
		}
		return card;
	}
	
	@Override
	public Card createCard(Card card) {
		EntityManager em = entityManagerProvider.get();
		em.persist(card);
		return card;
	}
}
