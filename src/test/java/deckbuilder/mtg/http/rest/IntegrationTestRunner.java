package deckbuilder.mtg.http.rest;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class IntegrationTestRunner extends BlockJUnit4ClassRunner {

	public IntegrationTestRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

}
