package by.bsuir.fitness.service.impl;

import by.bsuir.fitness.service.CommentService;
import by.bsuir.fitness.dao.CommentDao;
import by.bsuir.fitness.dao.exception.DaoException;
import by.bsuir.fitness.dao.impl.CommentDaoImpl;
import by.bsuir.fitness.entity.Comment;
import by.bsuir.fitness.service.ServiceException;

import java.util.List;

/**
 * The type Comment service.
 */
public class CommentServiceImpl implements CommentService {
    private CommentDao commentDao = new CommentDaoImpl();

    @Override
    public Long save(Comment comment) throws ServiceException {
        try {
            return commentDao.save(comment);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Comment> findByCoachId(long coachId) throws ServiceException {
        try {
            return commentDao.findByCoachId(coachId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Comment> findAll() throws ServiceException {
        try {
            return commentDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public int delete(long id) throws ServiceException {
        try {
            return commentDao.delete(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
