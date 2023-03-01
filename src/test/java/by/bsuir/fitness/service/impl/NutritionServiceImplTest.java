package by.bsuir.fitness.service.impl;

import by.bsuir.fitness.service.NutritionService;
import by.bsuir.fitness.service.ServiceException;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class NutritionServiceImplTest {
    private final NutritionService nutritionService = new NutritionServiceImpl();

    @Test
    public void testFindByClientId() throws ServiceException {
        boolean actual = nutritionService.findByClientId(6).isPresent();
        assertTrue(actual);
    }

    @Test
    public void testFindById() throws ServiceException {
        boolean actual = nutritionService.findById(14).isPresent();
        assertTrue(actual);
    }
}