package by.bsuir.fitness.command.impl.admin;

import by.bsuir.fitness.service.ExerciseService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Exercise;
import by.bsuir.fitness.service.impl.ExerciseServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.validation.DataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * The type Admin exercises command.
 */
public class AdminExercisesCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(AdminExercisesCommand.class);
    private ExerciseService exerciseService = new ExerciseServiceImpl();
    private static final int TOTAL_PER_PAGE = 3;

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        String pageNumberString = request.getParameter(JspConst.PAGE_NUMBER);
        if (pageNumberString==null || !DataValidator.isIdentifiableIdValid(pageNumberString)) {
            log.info("invalid page number format was received:" + pageNumberString);
            pageNumberString = "1";
        }
        int pageNumber = Integer.parseInt(pageNumberString);
        int start = pageNumber * TOTAL_PER_PAGE - TOTAL_PER_PAGE;
        try {
            int numberOfPages = exerciseService.getNumberOfPages(TOTAL_PER_PAGE);
            request.setAttribute(JspConst.PAGE_NUMBER, pageNumber);
            List<Exercise> exercises = exerciseService.findAll(start, TOTAL_PER_PAGE);
            request.setAttribute(JspConst.NUMBER_OF_PAGES, numberOfPages);
            request.setAttribute(JspConst.All_EXERCISES, exercises);
            page = Page.ADMIN_EXERCISES;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.ADMIN_EXERCISES;
        }
        return new CommandResult(page);
    }
}
