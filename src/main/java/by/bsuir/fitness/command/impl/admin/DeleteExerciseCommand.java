package by.bsuir.fitness.command.impl.admin;

import by.bsuir.fitness.service.ExerciseService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.service.impl.ExerciseServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.validation.DataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static by.bsuir.fitness.util.JspConst.EXERCISE_ID;

/**
 * The type Delete exercise command.
 */
public class DeleteExerciseCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(DeleteExerciseCommand.class);
    private ExerciseService exerciseService = new ExerciseServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        String exerciseIdString = request.getParameter(EXERCISE_ID);
        if (exerciseIdString==null || !DataValidator.isIdentifiableIdValid(exerciseIdString)) {
            log.info("incorrect exercise id was received:" + exerciseIdString);
            return new CommandResult(Page.ADMIN_EXERCISES_COMMAND);
        }
        long exerciseId = Long.parseLong(exerciseIdString);
        try {
            exerciseService.deleteExercise(exerciseId);
            log.info("exercise with id = " + exerciseId + " was successfully deleted");
            request.getSession().setAttribute(JspConst.EXERCISE_DELETED, true);
            page = Page.ADMIN_EXERCISES_COMMAND;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.ADMIN_EXERCISES;
        }
        return new CommandResult(page, true);
    }
}
