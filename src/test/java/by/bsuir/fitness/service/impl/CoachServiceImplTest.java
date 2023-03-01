package by.bsuir.fitness.service.impl;

import by.bsuir.fitness.entity.Coach;
import by.bsuir.fitness.service.CoachService;
import by.bsuir.fitness.service.ServiceException;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class CoachServiceImplTest {
    private final CoachService coachService = new CoachServiceImpl();

    @Test
    public void CheckCoachByLoginPasswordPositiveTest() throws ServiceException {
        boolean actual = coachService.checkCoachByLoginPassword("coach", "lol").isPresent();
        assertTrue(actual);
    }

    @Test
    public void CheckCoachByLoginPasswordNegativeTest() throws ServiceException {
        boolean actual = coachService.checkCoachByLoginPassword("unknown", "coach").isPresent();
        assertFalse(actual);
    }

    @Test
    public void testFindByClientId() throws ServiceException {
        boolean actual = coachService.findByClientId(6).isPresent();
        assertTrue(actual);
    }

    @Test
    public void testFindById() throws ServiceException {
        boolean actual = coachService.findById(1).isPresent();
        assertTrue(actual);
    }

    @Test
    public void testFindAll() throws ServiceException {
        List<Coach> coaches = coachService.findAll();
        int actual = coaches.size();
        assertEquals(actual, 7);
    }
}