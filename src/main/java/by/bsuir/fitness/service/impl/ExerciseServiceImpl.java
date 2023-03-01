package by.bsuir.fitness.service.impl;

import by.bsuir.fitness.dao.ExerciseDao;
import by.bsuir.fitness.dao.exception.DaoException;
import by.bsuir.fitness.dao.impl.ExerciseDaoImpl;
import by.bsuir.fitness.entity.Exercise;
import by.bsuir.fitness.service.ExerciseService;
import by.bsuir.fitness.service.ServiceException;

import java.util.List;
import java.util.Optional;

/**
 * The type Exercise service.
 */
public class ExerciseServiceImpl implements ExerciseService {
    private ExerciseDao exerciseDao = new ExerciseDaoImpl();

    @Override
    public List<Exercise> findAll(int start, int total) throws ServiceException {
        try {
            return exerciseDao.findAllLimited(start, total);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<Exercise> findById(long id) throws ServiceException {
        try {
            return exerciseDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Long save(Exercise exercise) throws ServiceException {
        try {
            return exerciseDao.save(exercise);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public int deleteExercise(long exerciseId) throws ServiceException {
        try {
            return exerciseDao.deleteExercise(exerciseId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public int getNumberOfPages(int totalPerPage) throws ServiceException {
        try {
            int rows = exerciseDao.getNumberOfRows();
            int numberOfPages = rows / totalPerPage;
            if (rows % totalPerPage > 0) {
                numberOfPages++;
            }
            return numberOfPages;
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}