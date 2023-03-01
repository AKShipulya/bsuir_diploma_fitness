package by.bsuir.fitness.command.impl.client;

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
import java.util.Optional;

import static by.bsuir.fitness.util.JspConst.SUCCESS;

/**
 * The type Delete account command.
 */
public class DeleteAccountCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(DeleteAccountCommand.class);
    private ClientService clientService = new ClientServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page = null;
        Long clientId = (Long) request.getSession().getAttribute(SessionAttributes.ID);
        try {
            Optional<Client> clientOptional = clientService.findById(clientId);
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                client.setActive(false);
                clientService.save(client);
                log.info("client with id = "+ clientId + " successfully deleted his profile");
                request.getSession().setAttribute(SUCCESS, true);
                page = Page.LOGOUT_COMMAND;
            }
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.CLIENT_PROFILE_PAGE;
        }
        return new CommandResult(page, true);
    }
}
