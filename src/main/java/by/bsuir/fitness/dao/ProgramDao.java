package by.bsuir.fitness.dao;

import by.bsuir.fitness.entity.Program;
import by.bsuir.fitness.dao.exception.DaoException;

import java.util.Optional;

/**
 * The interface Program dao.
 */
public interface ProgramDao extends BaseDao<Long, Program> {
    /**
     * Find program by id optional.
     *
     * @param programId the program id
     * @return the optional
     * @throws DaoException the dao exception
     */
    Optional<Program> findProgramById(long programId) throws DaoException;
}
