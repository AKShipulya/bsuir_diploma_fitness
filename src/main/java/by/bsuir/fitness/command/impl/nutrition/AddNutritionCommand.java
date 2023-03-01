package by.bsuir.fitness.command.impl.nutrition;

import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Nutrition;
import by.bsuir.fitness.service.NutritionService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.service.impl.NutritionServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.validation.DataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static by.bsuir.fitness.util.JspConst.NUTRITION_ID;

/**
 * The type Add nutrition command.
 */
public class AddNutritionCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(AddNutritionCommand.class);
    private NutritionService nutritionService = new NutritionServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page = null;
        String nutritionIdString = request.getParameter(NUTRITION_ID);
        if (nutritionIdString==null || !DataValidator.isIdentifiableIdValid(nutritionIdString)) {
            log.info("incorrect nutrition id was received:" + nutritionIdString);
            request.setAttribute(JspConst.INCORRECT_INPUT_NUTRITION_DATA_ERROR, true);
            return new CommandResult(Page.CLIENT_NUTRITION_COMMAND);
        }
        long nutritionId = Long.parseLong(nutritionIdString);
        try {
            Optional<Nutrition> nutritionOptional = nutritionService.findById(nutritionId);
            if (nutritionOptional.isPresent()) {
                nutritionOptional.get().setActive(true);
                nutritionService.save(nutritionOptional.get());
                request.getSession().setAttribute(JspConst.NUTRITION_ADDED, true);
                log.info("nutrition with id = " + nutritionId + " has been added");
                page = Page.CLIENT_NUTRITION_COMMAND;
            }
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.COACH_CLIENTS;
        }
        return new CommandResult(page, true);
    }
}
