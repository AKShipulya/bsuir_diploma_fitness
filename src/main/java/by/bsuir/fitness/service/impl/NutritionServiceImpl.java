package by.bsuir.fitness.service.impl;

import by.bsuir.fitness.dao.NutritionDao;
import by.bsuir.fitness.dao.exception.DaoException;
import by.bsuir.fitness.dao.impl.NutritionDaoImpl;
import by.bsuir.fitness.entity.Nutrition;
import by.bsuir.fitness.service.NutritionService;
import by.bsuir.fitness.service.ServiceException;

import java.util.Optional;

/**
 * The type Nutrition service.
 */
public class NutritionServiceImpl implements NutritionService {
    private NutritionDao nutritionDao = new NutritionDaoImpl();

    @Override
    public Long save(Nutrition nutrition) throws ServiceException {
        try {
            return nutritionDao.save(nutrition);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<Nutrition> findByClientId(long clientId) throws ServiceException {
        try {
            return nutritionDao.findByClientId(clientId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<Nutrition> findById(long id) throws ServiceException {
        try {
            return nutritionDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
