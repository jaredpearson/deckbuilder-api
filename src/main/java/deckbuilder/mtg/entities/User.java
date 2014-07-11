package deckbuilder.mtg.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="Users")
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@GenericGenerator(name="increment", strategy="increment")
	private Long id;
	private String facebookUsername;
	private Long facebookId;
	private boolean administrator = false;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getFacebookUsername() {
		return facebookUsername;
	}
	
	public void setFacebookUsername(String facebookUsername) {
		this.facebookUsername = facebookUsername;
	}
	
	public Long getFacebookId() {
		return facebookId;
	}
	
	public void setFacebookId(Long facebookId) {
		this.facebookId = facebookId;
	}
	
	public boolean isAdministrator() {
		return administrator;
	}
	
	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}
	
	public boolean getAdministrator() {
		return administrator;
	}
}
