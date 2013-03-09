package deckbuilder.mtg.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="Cards")
public class Card {
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private Long id;
	
	private String name;
	
	@ManyToOne
	@JoinColumn(name="cardSet")
	private CardSet set;
	
	private String setIndex;
	
	public Card() {
	}
	
	public Card(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
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
	
	public CardSet getSet() {
		return set;
	}
	
	public void setSet(CardSet set) {
		this.set = set;
	}
	
	public String getSetIndex() {
		return setIndex;
	}
	
	public void setSetIndex(String setIndex) {
		this.setIndex = setIndex;
	}
}