package by.bsuir.fitness.command.impl.client;

import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * The type Reject coach command.
 */
public class RejectCoachCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(RejectCoachCommand.class);
    private ClientService clientService = new ClientServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page = null;
        HttpSession session = request.getSession();
        Long clientId = (Long) session.getAttribute(SessionAttributes.ID);
        try {
            Optional<Client> user = clientService.findById(clientId);
            if (user.isPresent()) {
                user.get().setCoachId(null);
                clientService.save(user.get());
                log.info("client with id = " + clientId + " rejected his coach");
                session.setAttribute(JspConst.COACH_REJECTED, true);
                page = Page.WELCOME_PAGE;
            }
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.ALL_COACHES_COMMAND;
        }
        return new CommandResult(page, true);
    }
}
