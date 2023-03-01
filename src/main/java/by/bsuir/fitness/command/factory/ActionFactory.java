package by.bsuir.fitness.command.factory;

import by.bsuir.fitness.command.impl.EmptyCommand;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * The type Action factory.
 */
public class ActionFactory {
    private static Logger log = LogManager.getLogger(ActionFactory.class);

    /**
     * Define command action command.
     *
     * @param request the request
     * @return the action command
     */
    public static ActionCommand defineCommand(HttpServletRequest request) {
        ActionCommand currentCommand = new EmptyCommand();
        String action = request.getParameter("command");
        if (action == null || action.isEmpty()) {
            return currentCommand;
        }
        try {
            currentCommand = CommandEnum.getCurrentCommand(action);
        } catch (IllegalArgumentException e) {
            request.setAttribute("wrongAction", true);
            log.info("Wrong action!");
        }
        return currentCommand;
    }
}
