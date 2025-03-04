package cafe.model;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cafe.model.entity.Coffee;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class CafeRepository {

    private static final Logger logger = Logger.getLogger(
            MethodHandles.lookup().lookupClass().getName());

    @PersistenceContext
    private EntityManager entityManager;

    public List<Coffee> getAllCoffees() {
        logger.info("Finding all coffees.");

        return this.entityManager.createNamedQuery(
                "findAllCoffees", Coffee.class)
                .setHint("org.hibernate.cacheable", "true")
                .getResultList();
    }

    public Coffee persistCoffee(Coffee coffee) {
        logger.log(Level.INFO, "Persisting new coffee: {0}.", coffee);
        this.entityManager.persist(coffee);
        return coffee;
    }

    public void removeCoffeeById(Long coffeeId) {
        logger.log(Level.INFO, "Removing coffee with ID: {0}.", coffeeId);
        Coffee coffee = entityManager.find(Coffee.class, coffeeId);
        this.entityManager.remove(coffee);
    }

    public Coffee findCoffeeById(Long coffeeId) {
        logger.log(Level.INFO, "Finding coffee with ID: {0}.", coffeeId);
        return this.entityManager.find(Coffee.class, coffeeId);
    }
}
