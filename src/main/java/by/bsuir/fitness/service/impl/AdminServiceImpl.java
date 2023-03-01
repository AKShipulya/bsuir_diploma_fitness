package by.bsuir.fitness.service.impl;

import by.bsuir.fitness.dao.AdminDao;
import by.bsuir.fitness.dao.exception.DaoException;
import by.bsuir.fitness.dao.impl.AdminDaoImpl;
import by.bsuir.fitness.entity.Admin;
import by.bsuir.fitness.service.AdminService;
import by.bsuir.fitness.service.ServiceException;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Optional;

/**
 * The type Admin service.
 */
public class AdminServiceImpl implements AdminService {
    private AdminDao adminDao = new AdminDaoImpl();

    @Override
    public Optional<Admin> checkAdminByLoginPassword(String login, String password) throws ServiceException {
        try {
            return adminDao.checkAdminByLoginPassword(login, DigestUtils.sha512Hex(password));
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
