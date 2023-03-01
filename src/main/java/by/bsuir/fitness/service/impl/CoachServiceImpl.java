package by.bsuir.fitness.service.impl;

import by.bsuir.fitness.dao.CoachDao;
import by.bsuir.fitness.dao.exception.DaoException;
import by.bsuir.fitness.dao.impl.CoachDaoImpl;
import by.bsuir.fitness.entity.Coach;
import by.bsuir.fitness.service.CoachService;
import by.bsuir.fitness.service.ServiceException;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.Optional;

/**
 * The type Coach service.
 */
public class CoachServiceImpl implements CoachService {
    private CoachDao coachDao = new CoachDaoImpl();

    @Override
    public Optional<Coach> checkCoachByLoginPassword(String login, String password) throws ServiceException {
        try {
            return coachDao.checkCoachByLoginPassword(login, DigestUtils.sha512Hex(password));
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<Coach> findByClientId(long clientId) throws ServiceException {
        try {
            return coachDao.findByClientId(clientId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<Coach> findById(long id) throws ServiceException {
        try {
            return coachDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Coach> findAll() throws ServiceException {
        try {
            return coachDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Long save(Coach coach) throws ServiceException {
        try {
            return coachDao.save(coach);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
