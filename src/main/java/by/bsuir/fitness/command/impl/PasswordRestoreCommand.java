package by.bsuir.fitness.command.impl;

import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.validation.DataValidator;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

/**
 * The type Password restore command.
 */
public class PasswordRestoreCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(PasswordRestoreCommand.class);
    private ClientService clientService = new ClientServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        String password = request.getParameter(JspConst.PARAM_PASSWORD);
        if (password==null || !DataValidator.isPasswordValid(password)) {
            log.info("invalid password format was received:" + password);
            request.setAttribute(JspConst.INVALID_PASSWORD, true);
            return new CommandResult(Page.PASSWORD_RESTORE_PAGE);
        }
        String confirmedPassword = request.getParameter(JspConst.PARAM_CONFIRMED_PASSWORD);
        if (confirmedPassword==null || !DataValidator.isPasswordValid(confirmedPassword)) {
            log.info("invalid confirmed password format was received:" + confirmedPassword);
            request.setAttribute(JspConst.INVALID_PASSWORD, true);
            return new CommandResult(Page.PASSWORD_RESTORE_PAGE);
        }
        if (!password.equals(confirmedPassword)) {
            log.info("Passwords do not match: " + confirmedPassword + " and " + password);
            request.setAttribute(JspConst.PASSWORDS_NOT_MATCH, true);
            return new CommandResult(Page.PASSWORD_RESTORE_PAGE);
        }
        String email = request.getParameter(JspConst.PARAM_EMAIL);
        if (email==null || !DataValidator.isEmailValid(email)) {
            log.info("invalid email format was received, link was modified:" + email);
            request.setAttribute(JspConst.INCORRECT_DATA, true);
            return new CommandResult(Page.PASSWORD_RESTORE_PAGE);
        }
        String login = request.getParameter(JspConst.PARAM_LOGIN);
        if (login==null || !DataValidator.isLoginValid(login)) {
            log.info("invalid login format was received, link was modified:" + login);
            request.setAttribute(JspConst.INCORRECT_DATA, true);
            return new CommandResult(Page.PASSWORD_RESTORE_PAGE);
        }
        String hash = request.getParameter(JspConst.PARAM_HASH);
        if (hash==null || !DataValidator.isHashValid(hash)) {
            log.info("invalid hash format was received, link was modified:" + hash);
            request.setAttribute(JspConst.INCORRECT_DATA, true);
            return new CommandResult(Page.PASSWORD_RESTORE_PAGE);
        }
        try {
            Optional<Client> clientOptional = clientService.findByLoginHash(login, email, hash);
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                client.setActive(true);
                password = DigestUtils.sha512Hex(password);
                client.setPassword(password);
                clientService.save(client);
                log.info("password of user " + login + " was changed");
                request.setAttribute(JspConst.PASSWORD_CHANGED, true);
                Long clientId = (Long) request.getSession().getAttribute(SessionAttributes.ID);
                if (clientId != null) {
                    page = Page.LOGOUT_COMMAND;
                } else {
                    page = Page.LOGIN_PAGE;
                }
            } else {
                page = Page.PASSWORD_RESTORE_PAGE;
            }
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.PASSWORD_RESTORE_PAGE;
        }
        return new CommandResult(page);
    }
}
