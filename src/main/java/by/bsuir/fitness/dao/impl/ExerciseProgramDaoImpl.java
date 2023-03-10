package by.bsuir.fitness.dao.impl;

import by.bsuir.fitness.builder.ExerciseProgramBuilder;
import by.bsuir.fitness.entity.ExerciseProgram;
import by.bsuir.fitness.pool.ConnectionPool;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.dao.ExerciseProgramDao;
import by.bsuir.fitness.dao.exception.DaoException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Exercise program dao.
 */
public class ExerciseProgramDaoImpl implements ExerciseProgramDao {
    private static final String SQL_CREATE_TABLE = "INSERT INTO exercise_program (program_id, exercise_id, repeat_number, set_number, number_train_day) VALUES (?,?,?,?,?)";
    private static final String SQL_UPDATE_TABLE = "UPDATE exercise_program SET program_id=?, exercise_id=?, repeat_number=?, set_number=?, number_train_day=? WHERE id_exercise_program=?";
    private static final String SQL_FIND_BY_PROGRAM_ID = "SELECT * FROM exercise LEFT JOIN exercise_program ON exercise.id_exercise = exercise_program.exercise_id WHERE exercise_program.program_id=?";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM exercise RIGHT JOIN exercise_program ON exercise.id_exercise = exercise_program.exercise_id WHERE exercise_program.id_exercise_program=?";
    private static final String SQL_FIND_BY_EXERCISE_ID = "SELECT * FROM exercise_program WHERE exercise_id=? AND program_id=?";
    private static final String SQL_DELETE = "DELETE FROM exercise_program WHERE exercise_id=?";
    private ExerciseProgramBuilder builder = new ExerciseProgramBuilder();

    @Override
    public Long save(ExerciseProgram exerciseProgram) throws DaoException {
        Connection connection;
        PreparedStatement preparedStatement = null;
        Long programId = exerciseProgram.getProgramId();
        Long exerciseId = exerciseProgram.getExercise().getId();
        int repeatNumber = exerciseProgram.getRepeatNumber();
        int setNumber = exerciseProgram.getSetNumber();
        int numberTrainDay = exerciseProgram.getNumberTrainDay();
        Long generatedId = null;
        try {
            connection = ConnectionPool.getInstance().takeConnection();
            try {
                connection.setAutoCommit(false);
                if (exerciseProgram.getId() != null) {
                    preparedStatement = connection.prepareStatement(SQL_UPDATE_TABLE, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setLong(6, exerciseProgram.getId());
                } else {
                    preparedStatement = connection.prepareStatement(SQL_CREATE_TABLE, Statement.RETURN_GENERATED_KEYS);
                }
                preparedStatement.setLong(1, programId);
                preparedStatement.setLong(2, exerciseId);
                preparedStatement.setInt(3, repeatNumber);
                preparedStatement.setInt(4, setNumber);
                preparedStatement.setInt(5, numberTrainDay);
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
    public List<ExerciseProgram> findExercisesByProgramId(long programId) throws DaoException {
        List<ExerciseProgram> exercisesList = new ArrayList<>();
        ExerciseProgram exerciseProgram;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_PROGRAM_ID)
        ) {
            preparedStatement.setLong(1, programId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                exerciseProgram = builder.build(resultSet);
                exercisesList.add(exerciseProgram);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return exercisesList;
    }

    @Override
    public Optional<ExerciseProgram> findById(Long id) throws DaoException {
        ExerciseProgram exerciseProgram = null;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_ID)
        ) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                exerciseProgram = builder.build(resultSet);
            }
        } catch (SQLException | ServiceException e) {
            throw new DaoException(e);
        }
        return Optional.ofNullable(exerciseProgram);
    }

    @Override
    public boolean findByExerciseId(long exerciseId, long programId) throws DaoException {
        boolean result = false;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_BY_EXERCISE_ID)
        ) {
            preparedStatement.setLong(1, exerciseId);
            preparedStatement.setLong(2, programId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = true;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return result;
    }

    @Override
    public int deleteExercise(long exerciseId) throws DaoException {
        int result;
        try (
                Connection connection = ConnectionPool.getInstance().takeConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE)
        ) {
            try {
                connection.setAutoCommit(false);
                preparedStatement.setLong(1, exerciseId);
                result = preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException(e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return result;
    }
}