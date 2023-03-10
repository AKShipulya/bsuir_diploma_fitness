package by.bsuir.fitness.builder;

import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.entity.Nutrition;
import by.bsuir.fitness.util.database.NutritionTableConst;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The type Nutrition builder.
 */
public class NutritionBuilder implements Builder<Nutrition> {
    @Override
    public Nutrition build(ResultSet resultSet) throws ServiceException {
        try {
            Long idNutrition = resultSet.getLong(NutritionTableConst.ID.getFieldName());
            boolean active = resultSet.getBoolean(NutritionTableConst.ACTIVE.getFieldName());
            String name = resultSet.getString(NutritionTableConst.NAME.getFieldName());
            String morningNutrition = resultSet.getString(NutritionTableConst.MORNING_NUTRITION.getFieldName());
            String lunchNutrition = resultSet.getString(NutritionTableConst.LUNCH_NUTRITION.getFieldName());
            String dinnerNutrition = resultSet.getString(NutritionTableConst.DINNER_NUTRITION.getFieldName());
            return new Nutrition(idNutrition, active, name, morningNutrition, lunchNutrition, dinnerNutrition);
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage(),e);
        }
    }
}
