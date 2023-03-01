package by.bsuir.fitness.service.impl;

import by.bsuir.fitness.dao.OrderInformationDao;
import by.bsuir.fitness.dao.exception.DaoException;
import by.bsuir.fitness.dao.impl.OrderInformationDaoImpl;
import by.bsuir.fitness.entity.OrderInformation;
import by.bsuir.fitness.service.OrderInformationService;
import by.bsuir.fitness.service.ServiceException;

import java.util.List;
import java.util.Optional;

/**
 * The type Order information service.
 */
public class OrderInformationServiceImpl implements OrderInformationService {
    private OrderInformationDao orderInformationDao = new OrderInformationDaoImpl();

    @Override
    public Long save(OrderInformation orderInformation) throws ServiceException {
        try {
            return orderInformationDao.save(orderInformation);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<OrderInformation> findByClientId(Long id) throws ServiceException {
        try {
            return orderInformationDao.findByClientId(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<OrderInformation> findOrdersByClientId(Long id) throws ServiceException {
        try {
            return orderInformationDao.findOrdersByClientId(id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<OrderInformation> findAll() throws ServiceException {
        try {
            return orderInformationDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<OrderInformation> findAscPrice() throws ServiceException {
        try {
            return orderInformationDao.findAscPrice();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<OrderInformation> findDescPrice() throws ServiceException {
        try {
            return orderInformationDao.findDescPrice();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<OrderInformation> findAscPaymentData() throws ServiceException {
        try {
            return orderInformationDao.findAscPaymentData();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<OrderInformation> findDescPaymentData() throws ServiceException {
        try {
            return orderInformationDao.findDescPaymentData();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
