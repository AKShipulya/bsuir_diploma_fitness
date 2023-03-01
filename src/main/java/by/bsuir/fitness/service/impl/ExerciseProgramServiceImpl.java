package by.bsuir.fitness.service.impl;

import by.bsuir.fitness.dao.ExerciseProgramDao;
import by.bsuir.fitness.dao.exception.DaoException;
import by.bsuir.fitness.dao.impl.ExerciseProgramDaoImpl;
import by.bsuir.fitness.entity.ExerciseProgram;
import by.bsuir.fitness.service.ExerciseProgramService;
import by.bsuir.fitness.service.ServiceException;

import java.util.List;
import java.util.Optional;

/**
 * The type Exercise program service.
 */
public class ExerciseProgramServiceImpl implements ExerciseProgramService {
    private ExerciseProgramDao exerciseProgramDao = new ExerciseProgramDaoImpl();

    @Override
    public Long save(ExerciseProgram exerciseProgram) throws ServiceException {
        try {
            return exerciseProgramDao.save(exerciseProgram);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<ExerciseProgram> findExercisesByProgramId(Long programId) throws ServiceException {
        try {
            return exerciseProgramDao.findExercisesByProgramId(programId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<ExerciseProgram> findById(long id) throws ServiceException {
        try {
            return exerciseProgramDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean findByExerciseId(long exerciseId, long programId) throws ServiceException {
        try {
            return exerciseProgramDao.findByExerciseId(exerciseId, programId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public int deleteExercise(long exerciseId) throws ServiceException {
        try {
            return exerciseProgramDao.deleteExercise(exerciseId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
