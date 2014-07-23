package deckbuilder.mtg.service.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Transactional;

import deckbuilder.mtg.entities.DeckCard;
import deckbuilder.mtg.service.DeckCardService;
import deckbuilder.mtg.service.NoResultException;

@Transactional(readOnly=true)
public class DefaultDeckCardService implements DeckCardService {

	@PersistenceContext(unitName="deckbuilder.mtg")
	EntityManager entityManager;
	
	@Override
	@Transactional(readOnly=false)
	public DeckCard createDeckCard(DeckCard deckCard) {
		entityManager.persist(deckCard);
		return deckCard;
	}
	
	@Override
	public DeckCard getDeckCardById(Long deckCardId) {
		final DeckCard deckCard = entityManager.find(DeckCard.class, deckCardId);
		if (deckCard == null) {
			throw new NoResultException();
		}
		return deckCard;
	}
	
	@Override
	public DeckCard getDeckCardByDeckIdAndCardId(Long deckId, Long cardId) {
		final TypedQuery<DeckCard> query = entityManager.createQuery("select dc from DeckCard dc join dc.deck d join dc.card c where d.id = :deckId and c.id = :cardId", DeckCard.class);
		query.setParameter("deckId", cardId);
		query.setParameter("cardId", cardId);
		final DeckCard deckCard = query.getSingleResult();
		if (deckCard == null) {
			throw new NoResultException();
		}
		return deckCard;
	}
}
