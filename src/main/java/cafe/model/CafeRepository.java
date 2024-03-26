package cafe.model;

import java.lang.invoke.MethodHandles;
import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cafe.model.entity.Coffee;

@Stateless
public class CafeRepository {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@PersistenceContext
	private EntityManager entityManager;

	public List<Coffee> getAllCoffees() {
		logger.info("Finding all coffees. ");

		return this.entityManager.createNamedQuery("findAllCoffees", Coffee.class)
				.setHint("org.hibernate.cacheable", "true").getResultList();
	}

	public Coffee persistCoffee(Coffee coffee) {
		logger.info("Persisting the new coffee {}.", coffee);
		this.entityManager.persist(coffee);
		return coffee;
	}

	public void removeCoffeeById(Long coffeeId) {
		logger.info("Removing a coffee {}.", coffeeId);
		Coffee coffee = entityManager.find(Coffee.class, coffeeId);
		this.entityManager.remove(coffee);
	}

	public Coffee findCoffeeById(Long coffeeId) {
		logger.info("Finding the coffee with id {}.", coffeeId);
		return this.entityManager.find(Coffee.class, coffeeId);
	}
}
