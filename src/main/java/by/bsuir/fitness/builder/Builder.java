package by.bsuir.fitness.builder;

import by.bsuir.fitness.entity.Entity;
import by.bsuir.fitness.service.ServiceException;

import java.sql.ResultSet;

/**
 * The interface Builder.
 *
 * @param <T> the type parameter
 */
public interface Builder <T extends Entity> {
    /**
     * Build t.
     *
     * @param resultSet the result set
     * @return the t
     * @throws ServiceException the service exception
     */
    T build(ResultSet resultSet) throws ServiceException;
}
