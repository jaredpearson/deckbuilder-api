package deckbuilder.mtg.http.rest;

/**
 * When the user attempts to do something they are authorized to do, like modifying a resource that they
 * do not own.
 * 
 * @author jared.pearson
 */
public class NotAuthorizedException extends RuntimeException {
	private static final long serialVersionUID = -5791704200768338600L;
	private final Object entity;
	
	public NotAuthorizedException(String message, Object entity) {
		super(message);
		this.entity = entity;
	}
	
	/**
	 * <strong>IMPORTANT: The message field is shown to the user!</strong>
	 */
	public NotAuthorizedException(String message, Object entity, Throwable cause) {
		super(message, cause);
		this.entity = entity;
	}
	
	public Object getEntity() {
		return entity;
	}
}