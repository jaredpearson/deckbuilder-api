package deckbuilder.mtg.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="DeckCards")
public class DeckCard {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@GenericGenerator(name="increment", strategy="increment")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="deck")
	private Deck deck;
	
	@OneToOne
	@JoinColumn(name="card")
	private Card card;
	
	private Integer quantity = 1;
	
	public DeckCard() {
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Deck getDeck() {
		return deck;
	}
	
	public void setDeck(Deck deck) {
		this.deck = deck;
	}
	
	public Card getCard() {
		return card;
	}
	
	public void setCard(Card card) {
		this.card = card;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}