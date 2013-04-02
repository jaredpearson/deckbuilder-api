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
	
	private String setIndex;
	private String name;
	private String powerToughness;
	private String castingCost;
	private String text;
	private String typeLine;
	private String rarity;
	private String artist;
	
	@ManyToOne
	@JoinColumn(name="cardSet")
	private CardSet set;
	
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

	public String getPowerToughness() {
		return powerToughness;
	}

	public void setPowerToughness(String powerToughness) {
		this.powerToughness = powerToughness;
	}

	public String getCastingCost() {
		return castingCost;
	}

	public void setCastingCost(String castingCost) {
		this.castingCost = castingCost;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTypeLine() {
		return typeLine;
	}

	public void setTypeLine(String typeLine) {
		this.typeLine = typeLine;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	public String getAuthor() {
		return artist;
	}

	public void setAuthor(String author) {
		this.artist = author;
	}
}