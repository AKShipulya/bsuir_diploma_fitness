package by.bsuir.fitness.dao.impl;

import by.bsuir.fitness.builder.OrderInformationBuilder;
import by.bsuir.fitness.dao.OrderInformationDao;
import by.bsuir.fitness.entity.OrderInformation;
import by.bsuir.fitness.pool.ConnectionPool;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.dao.exception.DaoException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Order information dao.
 */
public class OrderInformationDaoImpl implements OrderInformationDao {
    private static final String SQL_CREATE_TABLE = "INSERT INTO order_information (cost, payment_data, membership_end_date, client_id, card_number) VALUES (?,?,?,?,?)";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM order_information WHERE client_id=?";
    private static final String SQL_UPDATE_TABLE = "UPDATE order_information SET cost=?, payment_data=?, membership_end_date=?, client_id=?, card_number=? WHERE id_order_information=?";
    private static final String SQL_FIND_ALL = "SELECT * FROM order_information";
    private static final String SQL_FIND_ASC_PRICE = "SELECT * FROM order_information ORDER BY cost ASC";
    private static final String SQL_FIND_DESC_PRICE = "SELECT * FROM order_information ORDER BY cost DESC";
    private static final String SQL_FIND_ASC_PAYMENT_DATA = "SELECT * FROM order_information ORDER BY payment_data ASC";
    private static final String SQL_FIND_DESC_PAYMENT_DATA = "SELECT * FROM order_information ORDER BY payment_data DESC";
    private OrderInformationBuilder builder = new OrderInformationBuilder();

    @Override
    public Long save(OrderInformation orderInformation) throws DaoException {
        Connection connection;
        PreparedStatement preparedStatement = null;
        BigDecimal cost = orderInformation.getCost();
        Timestamp paymentData = orderInformation.getPaymentData();
        Date membershipEndDate = orderInformation.getMembershipEndDate();
        Long clientId = orderInformation.getClientId();
        String cardNumber = orderInformation.getCardNumber();
        Long generatedId = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            try {
                connection.setAutoCommit(false);
                if (orderInformation.getId() != null) {
                    preparedStatement = connection.prepareStatement(SQL_UPDATE_TABLE, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setLong(6, orderInformation.getId());
                } else {
                    preparedStatement = connection.prepareStatement(SQL_CREATE_TABLE, Statement.RETURN_GENERATED_KEYS);
                }
                preparedStatement.setBigDecimal(1, cost);
                preparedStatement.setTimestamp(2, paymentData);
                preparedStatement.setDate(3, membershipEndDate);
                preparedStatement.setLong(4, clientId);
                preparedStatement.setString(5, cardNumber);
                preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    generatedId = resultSet.getLong(1);
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException(e);
            } finally {
                close(preparedStatement);
                close(connection);
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return generatedId;
    }

    @Override
    public Optional<OrderInformation> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<OrderInformation> findByClientId(long id) throws DaoException {
        OrderInformation orderInformation = null;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)
        ) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orderInformation = builder.build(resultSet);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return Optional.ofNullable(orderInformation);
    }

    @Override
    public List<OrderInformation> findOrdersByClientId(long id) throws DaoException {
        List<OrderInformation> ordersList = new ArrayList<>();
        OrderInformation orderInformation;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)
        ) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orderInformation = builder.build(resultSet);
                ordersList.add(orderInformation);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return ordersList;
    }

    @Override
    public List<OrderInformation> findAll() throws DaoException {
        List<OrderInformation> orders = new ArrayList<>();
        OrderInformation order;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                order = builder.build(resultSet);
                orders.add(order);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return orders;
    }

    @Override
    public List<OrderInformation> findAscPrice() throws DaoException {
        List<OrderInformation> orders = new ArrayList<>();
        OrderInformation order;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ASC_PRICE)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                order = builder.build(resultSet);
                orders.add(order);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return orders;
    }

    @Override
    public List<OrderInformation> findDescPrice() throws DaoException {
        List<OrderInformation> orders = new ArrayList<>();
        OrderInformation order;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_DESC_PRICE)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                order = builder.build(resultSet);
                orders.add(order);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return orders;
    }

    @Override
    public List<OrderInformation> findAscPaymentData() throws DaoException {
        List<OrderInformation> orders = new ArrayList<>();
        OrderInformation order;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ASC_PAYMENT_DATA)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                order = builder.build(resultSet);
                orders.add(order);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return orders;
    }

    @Override
    public List<OrderInformation> findDescPaymentData() throws DaoException {
        List<OrderInformation> orders = new ArrayList<>();
        OrderInformation order;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_DESC_PAYMENT_DATA)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                order = builder.build(resultSet);
                orders.add(order);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return orders;
    }
}