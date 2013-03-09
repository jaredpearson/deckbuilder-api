package deckbuilder.mtg;

import java.util.List;

public class Configuration {
	private List<String> administratorNames;

	public Configuration() {
	}

	public void setAdministratorNames(List<String> administratorNames) {
		this.administratorNames = administratorNames;
	}
	
	public List<String> getAdministratorNames() {
		return administratorNames;
	}
}
