package by.bsuir.fitness.dao;

import by.bsuir.fitness.entity.Comment;
import by.bsuir.fitness.dao.exception.DaoException;

import java.util.List;

/**
 * The interface Comment dao.
 */
public interface CommentDao extends BaseDao<Long, Comment> {
    /**
     * Find by coach id list.
     *
     * @param coachId the coach id
     * @return the list
     * @throws DaoException the dao exception
     */
    List<Comment> findByCoachId(long coachId) throws DaoException;

    /**
     * Find all list.
     *
     * @return the list
     * @throws DaoException the dao exception
     */
    List<Comment> findAll() throws DaoException;

    /**
     * Delete int.
     *
     * @param id the id
     * @return the int
     * @throws DaoException the dao exception
     */
    int delete(long id) throws DaoException;
}
