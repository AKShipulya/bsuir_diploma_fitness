package by.bsuir.fitness.command.impl;

import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.validation.DataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static by.bsuir.fitness.util.JspConst.*;
import static by.bsuir.fitness.util.page.Page.LOGIN_PAGE;
import static by.bsuir.fitness.util.page.Page.REGISTER_PAGE;

/**
 * The type Activation command.
 */
public class ActivationCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(ActivationCommand.class);
    private ClientService clientService = new ClientServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        String email = request.getParameter(PARAM_KEY_1);
        if (email==null || !DataValidator.isEmailValid(email)){
            log.info("invalid email format was received, link was modified:" + email);
            request.setAttribute(JspConst.INCORRECT_DATA, true);
            return new CommandResult(REGISTER_PAGE);
        }
        String login = request.getParameter(PARAM_KEY_2);
        if (login==null || !DataValidator.isLoginValid(login)){
            log.info("invalid login format was received, link was modified:" + login);
            request.setAttribute(JspConst.INCORRECT_DATA, true);
            return new CommandResult(REGISTER_PAGE);
        }
        String userHash = request.getParameter(PARAM_KEY_3);
        if (userHash==null || !DataValidator.isHashValid(userHash)) {
            log.info("invalid hash format was received, link was modified:" + userHash);
            request.setAttribute(JspConst.INCORRECT_DATA, true);
            return new CommandResult(REGISTER_PAGE);
        }
        try {
            Optional<Client> clientOptional = clientService.findByLoginHash(login, email, userHash);
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                client.setActive(true);
                clientService.save(client);
                request.setAttribute(USER_ACTIVATED, true);
                log.info("client with login = " + login + " activated his profile via link");
                page = LOGIN_PAGE;
            } else {
                page = REGISTER_PAGE;
            }
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = REGISTER_PAGE;
        }
        return new CommandResult(page);
    }
}
