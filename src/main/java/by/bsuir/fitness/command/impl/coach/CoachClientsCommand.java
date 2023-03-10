package by.bsuir.fitness.command.impl.coach;

import by.bsuir.fitness.command.impl.client.RejectCoachCommand;
import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

import static by.bsuir.fitness.util.JspConst.ALL_CLIENTS;

/**
 * The type Coach clients command.
 */
public class CoachClientsCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(RejectCoachCommand.class);
    private ClientService clientService = new ClientServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        HttpSession session = request.getSession();
        Long id = (Long) session.getAttribute(SessionAttributes.ID);
        try {
            List<Client> allClients = clientService.findByCoachId(id);
            session.setAttribute(ALL_CLIENTS, allClients);
            page = Page.COACH_CLIENTS;
        } catch (ServiceException e) {
            log.error("Service exception occurred", e);
            return new CommandResult(Page.COACH_CLIENTS);
        }
        return new CommandResult(page);
    }
}
