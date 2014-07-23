package deckbuilder.mtg.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="Decks")
public class Deck {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@GenericGenerator(name="increment", strategy="increment")
	private Long id;
	private String name;
	
	@OneToOne
	@JoinColumn(name="owner")
	private User owner;
	
	@OneToMany(mappedBy="deck", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private List<DeckCard> cards;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public List<DeckCard> getCards() {
		return cards;
	}
	
	public void setCards(List<DeckCard> cards) {
		this.cards = cards;
	}
}