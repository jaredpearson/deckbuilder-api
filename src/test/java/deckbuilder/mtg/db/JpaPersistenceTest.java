package deckbuilder.mtg.db;

import java.util.HashMap;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.hsqldb.jdbc.JDBCDataSourceFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.inject.Provider;

import deckbuilder.mtg.db.DatabaseService;

public abstract class JpaPersistenceTest {
	private static final String DATABASE_URL = "jdbc:hsqldb:mem:mymemdb";
	private static boolean schemaInitialized = false;
	private static EntityManagerFactory emf = null;
	protected EntityManager entityManager = null;
	protected Provider<EntityManager> entityManagerProvider;

	@BeforeClass
	public static void beforeSuite() throws Exception {
		if(!schemaInitialized) {
			Properties dbProps = new Properties();
			dbProps.setProperty("url", DATABASE_URL);
			dbProps.setProperty("user", "SA");
			dbProps.setProperty("password", "");
			DataSource dataSource = JDBCDataSourceFactory.createDataSource(dbProps);
			(new DatabaseService(dataSource)).initializeSchema();
			schemaInitialized = true;
		}
		
		emf = Persistence.createEntityManagerFactory("deckbuilder.mtg");
	}

	@AfterClass
	public static void afterSuite() throws Exception {
		if(emf.isOpen()) {
			emf.close();
		}
	}

	@Before
	public void initialize() throws Exception {
		HashMap<String, String> properties = new HashMap<String, String>();
	    properties.put("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
	    properties.put("javax.persistence.jdbc.url", DATABASE_URL);
	    properties.put("javax.persistence.jdbc.user", "SA");
	    properties.put("javax.persistence.jdbc.password", "");
	    properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
	    properties.put("hibernate.show_sql", "true");
		
	    entityManager = emf.createEntityManager(properties);
	    entityManagerProvider = new Provider<EntityManager>() {
			@Override
			public EntityManager get() {
				return entityManager;
			}
		};
		
		//start a transaction
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
	}

	@After
	public void teardown() throws Exception {
		rollbackIfActive();
		if(entityManager != null && entityManager.isOpen()) {
			entityManager.close();
		}
	}
	
	protected void rollbackIfActive() {
		rollbackIfActive(entityManager);
	}

	protected void rollbackIfActive(EntityManager em) {
		if(em != null && em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
	}

}