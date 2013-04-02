package deckbuilder.mtg.http.rest;

import java.util.LinkedHashMap;

public abstract class Link extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	protected String getEntityUrl(Class<?> entity, Object id) {
		return EntityUrlFactory.getInstance().createEntityUrl(entity, id);
	}
	
	protected void putEntityUrl(Class<?> entity, Object id) {
		this.put("_url", getEntityUrl(entity, id));
	}
}
