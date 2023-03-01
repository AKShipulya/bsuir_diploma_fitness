package by.bsuir.fitness.service.impl;

import by.bsuir.fitness.dao.ProgramDao;
import by.bsuir.fitness.dao.exception.DaoException;
import by.bsuir.fitness.dao.impl.ProgramDaoImpl;
import by.bsuir.fitness.entity.Program;
import by.bsuir.fitness.service.ProgramService;
import by.bsuir.fitness.service.ServiceException;

import java.util.Optional;

/**
 * The type Program service.
 */
public class ProgramServiceImpl implements ProgramService {
    private ProgramDao programDao = new ProgramDaoImpl();

    @Override
    public Long save(Program program) throws ServiceException {
        try {
            return programDao.save(program);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<Program> findProgramById(Long programId) throws ServiceException {
        try {
            return programDao.findProgramById(programId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
