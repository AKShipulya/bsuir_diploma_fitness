package by.bsuir.fitness.dao.impl;

import by.bsuir.fitness.dao.ClientDao;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.pool.ConnectionPool;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.builder.ClientBuilder;
import by.bsuir.fitness.dao.exception.DaoException;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Client dao.
 */
public class ClientDaoImpl implements ClientDao {
    private static final String SQL_CHECK_USER_BY_LOGIN_PASSWORD = "SELECT * FROM client WHERE login=? AND password=? AND active='1'";
    private static final String SQL_IS_LOGIN_UNIQUE = "SELECT login FROM client WHERE login=?";
    private static final String SQL_RESTORE_USER = "SELECT hash FROM client WHERE email=? AND login=? AND active='1'";
    private static final String SQL_DEACTIVATE_AND_HASH = "UPDATE client SET active='0', hash=? WHERE email=? AND login=?";
    private static final String SQL_UPDATE_USER = "UPDATE client SET coach_id=?, name=?, surname=?, login=?, password=?, email=?, hash=?, membership_purchased_number=?, personal_discount=?, program_id=?, image=?, active=? WHERE id_client=?";
    private static final String SQL_FIND_ACTIVE_BY_ID = "SELECT * FROM client WHERE id_client=? AND active='1'";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM client WHERE id_client=?";
    private static final String SQL_FIND_BY_LOGIN_HASH = "SELECT * FROM client WHERE login=? AND email=? AND hash=?";
    private static final String SQL_CREATE_USER = "INSERT INTO client (coach_id, name, surname, login, password, email, hash, membership_purchased_number, personal_discount, program_id, image) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_FIND_BY_COACH_ID = "SELECT * FROM client WHERE coach_id=? AND active='1'";
    private static final String SQL_FIND_USER_BY_COOKIE = "SELECT * FROM client WHERE login=? AND hash=?";
    private static final String SQL_FIND_ALL = "SELECT * FROM client";
    private static final String SQL_FIND_BY_FILTER = "SELECT * FROM client WHERE name=IFNULL(?, name) AND surname=IFNULL(?, surname) AND login=IFNULL(?, login) AND membership_purchased_number=IFNULL(?, membership_purchased_number) AND personal_discount=IFNULL(?, personal_discount)";
    private ClientBuilder builder = new ClientBuilder();

    @Override
    public Optional<Client> checkUserByLoginPassword(String login, String newPassword) throws DaoException {
        boolean result = false;
        Client client = null;
        try (Connection connection = ConnectionPool.getInstance().takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_CHECK_USER_BY_LOGIN_PASSWORD)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, newPassword);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                client = builder.build(resultSet);
                result = true;
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return result ? Optional.of(client) : Optional.empty();
    }

    public boolean isLoginUnique(String patternLogin) throws DaoException {
        boolean result = true;
        try (Connection connection = ConnectionPool.getInstance().takeConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_IS_LOGIN_UNIQUE)) {
            preparedStatement.setString(1, patternLogin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = false;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return result;
    }

    @Override
    public boolean registerUser(Client client) throws DaoException {
        if (!isLoginUnique(client.getLogin())) {
            return false;
        }
        Long generatedId = save(client);
        return generatedId != null;
    }

    @Override
    public boolean restoreUser(String login, String userEmail, String userHash) throws DaoException {
        int result = 0;
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            preparedStatement = connection.prepareStatement(SQL_RESTORE_USER);
            try {
                connection.setAutoCommit(false);
                preparedStatement.setString(1, userEmail);
                preparedStatement.setString(2, login);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement(SQL_DEACTIVATE_AND_HASH);
                    preparedStatement.setString(1, userHash);
                    preparedStatement.setString(2, userEmail);
                    preparedStatement.setString(3, login);
                    result = preparedStatement.executeUpdate();
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
        return result != 0;
    }

    @Override
    public Optional<Client> findById(Long id) throws DaoException {
        Client client = null;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)
        ) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                client = builder.build(resultSet);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return Optional.ofNullable(client);
    }

    @Override
    public Optional<Client> findActiveById(long id) throws DaoException {
        Client client = null;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ACTIVE_BY_ID)
        ) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                client = builder.build(resultSet);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return Optional.ofNullable(client);
    }

    @Override
    public List<Client> findByFilter(Client clientForData) throws DaoException {
        List<Client> clientsList = new ArrayList<>();
        Client client;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_FILTER)
        ) {
            preparedStatement.setString(1, clientForData.getName());
            preparedStatement.setString(2, clientForData.getSurname());
            preparedStatement.setString(3, clientForData.getLogin());
            preparedStatement.setObject(4, clientForData.getMembershipNumber());
            preparedStatement.setObject(5, clientForData.getPersonalDiscount());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                client = builder.build(resultSet);
                clientsList.add(client);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return clientsList;

    }

    @Override
    public Optional<Client> findByLoginHash(String login, String email, String hash) throws DaoException {
        Client client = null;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_LOGIN_HASH)
        ) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, hash);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                client = builder.build(resultSet);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return Optional.ofNullable(client);
    }

    @Override
    public Long save(Client client) throws DaoException {
        Connection connection;
        PreparedStatement preparedStatement = null;
        Long coachId = client.getCoachId();
        String name = client.getName();
        String surname = client.getSurname();
        String login = client.getLogin();
        String password = client.getPassword();
        String email = client.getEmail();
        boolean active = client.isActive();
        String userHash = client.getUserHash();
        Integer membershipNumber = client.getMembershipNumber();
        Float personalDiscount = client.getPersonalDiscount();
        Long programId = client.getProgramId();
        InputStream is = client.getIs();
        Long generatedId = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            try {
                connection.setAutoCommit(false);
                if (client.getId() != null) {
                    preparedStatement = connection.prepareStatement(SQL_UPDATE_USER, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setBoolean(12, active);
                    preparedStatement.setLong(13, client.getId());
                } else {
                    preparedStatement = connection.prepareStatement(SQL_CREATE_USER, Statement.RETURN_GENERATED_KEYS);
                }
                preparedStatement.setObject(1, coachId);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, surname);
                preparedStatement.setString(4, login);
                preparedStatement.setString(5, password);
                preparedStatement.setString(6, email);
                preparedStatement.setString(7, userHash);
                preparedStatement.setInt(8, membershipNumber);
                preparedStatement.setFloat(9, personalDiscount);
                preparedStatement.setLong(10, programId);
                preparedStatement.setBlob(11, is);
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
            throw new DaoException();
        }
        return generatedId;
    }

    @Override
    public List<Client> findByCoachId(long coachId) throws DaoException {
        List<Client> clients = new ArrayList<>();
        Client client;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_COACH_ID)
        ) {
            preparedStatement.setLong(1, coachId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                client = builder.build(resultSet);
                clients.add(client);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return clients;
    }

    @Override
    public Optional<Client> getUserByCookieData(String login, String hash) throws DaoException {
        Client client = null;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_USER_BY_COOKIE)
        ) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, hash);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                client = builder.build(resultSet);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return Optional.ofNullable(client);
    }

    @Override
    public List<Client> findAll() throws DaoException {
        List<Client> clientsList = new ArrayList<>();
        Client client;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                Statement statement = connection.createStatement()
        ) {
            ResultSet resultSet = statement.executeQuery(SQL_FIND_ALL);
            while (resultSet.next()) {
                client = builder.build(resultSet);
                clientsList.add(client);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return clientsList;
    }
}