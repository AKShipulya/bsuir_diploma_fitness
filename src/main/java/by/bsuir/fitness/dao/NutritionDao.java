package by.bsuir.fitness.dao;

import by.bsuir.fitness.entity.Nutrition;
import by.bsuir.fitness.dao.exception.DaoException;

import java.util.Optional;

/**
 * The interface Nutrition dao.
 */
public interface NutritionDao extends BaseDao<Long, Nutrition> {
    /**
     * Find by client id optional.
     *
     * @param clientId the client id
     * @return the optional
     * @throws DaoException the dao exception
     */
    Optional<Nutrition> findByClientId(long clientId) throws DaoException;
}
