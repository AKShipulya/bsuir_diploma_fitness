package by.bsuir.fitness.service.impl;

import by.bsuir.fitness.service.ProgramService;
import by.bsuir.fitness.service.ServiceException;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ProgramServiceImplTest {
    private final ProgramService programService = new ProgramServiceImpl();

    @Test
    public void testFindProgramById() throws ServiceException {
        boolean actual = programService.findProgramById(14L).isPresent();
        assertTrue(actual);
    }
}