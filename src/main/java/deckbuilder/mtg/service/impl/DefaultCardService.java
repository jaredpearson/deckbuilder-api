package deckbuilder.mtg.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Transactional;

import deckbuilder.mtg.entities.Card;
import deckbuilder.mtg.service.CardService;
import deckbuilder.mtg.service.NoResultException;

@Transactional(readOnly=true)
public class DefaultCardService implements CardService {

	@PersistenceContext(unitName="deckbuilder.mtg")
	EntityManager entityManager;
	
	@Override
	public List<Card> getCards() {
		TypedQuery<Card> query = entityManager.createQuery("select c from Cards c", Card.class);
		return query.getResultList();
	}
	
	@Override
	public Card getCardById(Long id) {
		Card card = entityManager.find(Card.class, id);
		if(card == null) {
			throw new NoResultException();
		}
		return card;
	}
	
	@Override
	@Transactional(readOnly=false)
	public Card createCard(Card card) {
		entityManager.persist(card);
		return card;
	}
	
	@Override
	public List<Card> getCardsById(List<Long> cardIds) {
		TypedQuery<Card> q = entityManager.createQuery("select c from Card c where id in (:ids)", Card.class);
		q.setParameter("ids", cardIds);
		return q.getResultList();
	}
}
