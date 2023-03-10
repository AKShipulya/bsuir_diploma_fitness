package by.bsuir.fitness.command.impl;

import by.bsuir.fitness.mail.SendingEmail;
import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.util.JspConst;
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
import java.security.SecureRandom;
import java.util.Random;

/**
 * The type Restore command.
 */
public class RestoreCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(RestoreCommand.class);
    private ClientService clientService = new ClientServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        String email = request.getParameter(JspConst.PARAM_EMAIL);
        if (email==null || !DataValidator.isEmailValid(email)){
            log.info("invalid email format was received:" + email);
            request.setAttribute(JspConst.INVALID_EMAIL, true);
            return new CommandResult(Page.RESTORE_PAGE);
        }
        String login = request.getParameter(JspConst.PARAM_LOGIN);
        if (login==null || !DataValidator.isLoginValid(login)) {
            log.info("invalid login format was received:" + login);
            request.setAttribute(JspConst.INVALID_LOGIN, true);
            return new CommandResult(Page.RESTORE_PAGE);
        }
        Random random = new SecureRandom();
        String userHash = DigestUtils.sha512Hex("" + random.nextInt(999999));
        try {
            if (clientService.restoreUser(login, email, userHash)) {
                SendingEmail.restorePassword(login, email, userHash);
                log.info("client with login = " + login + "trying to restore his password");
                page = Page.FINAL_RESTORE_PAGE;
            } else {
                log.info("there is no client with such login " + login + " or email " + email);
                request.setAttribute(JspConst.WRONG_DATA, true);
                page = Page.RESTORE_PAGE;
            }
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.RESTORE_PAGE;
        }
        return new CommandResult(page);
    }
}
