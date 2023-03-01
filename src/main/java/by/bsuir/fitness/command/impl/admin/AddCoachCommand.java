package by.bsuir.fitness.command.impl.admin;

import by.bsuir.fitness.entity.Coach;
import by.bsuir.fitness.service.CoachService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.validation.DataValidator;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.service.impl.CoachServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The type Add coach command.
 */
public class AddCoachCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(AddCoachCommand.class);
    private CoachService coachService = new CoachServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        String name = request.getParameter(JspConst.PARAM_NAME);
        if (name==null || !DataValidator.isNameValid(name)) {
            log.info("invalid name format was received:" + name);
            request.setAttribute(JspConst.INVALID_NAME, true);
            return new CommandResult(Page.ADMIN_COACHES_COMMAND);
        }
        String surname = request.getParameter(JspConst.PARAM_SURNAME);
        if (surname==null || !DataValidator.isSurnameValid(surname)) {
            log.info("invalid name format was received:" + surname);
            request.setAttribute(JspConst.INVALID_SURNAME, true);
            return new CommandResult(Page.ADMIN_COACHES_COMMAND);
        }
        String patronymic = request.getParameter(JspConst.PARAM_PATRONYMIC);
        if (patronymic==null || !DataValidator.isPatronymicValid(patronymic)) {
            log.info("invalid patronymic format was received:" + patronymic);
            request.setAttribute(JspConst.INVALID_PATRONYMIC, true);
            return new CommandResult(Page.ADMIN_COACHES_COMMAND);
        }
        String login = request.getParameter(JspConst.PARAM_LOGIN);
        if (login==null || !DataValidator.isLoginValid(login)) {
            log.info("invalid login format was received:" + login);
            request.setAttribute(JspConst.INVALID_LOGIN, true);
            return new CommandResult(Page.ADMIN_COACHES_COMMAND);
        }
        String password = request.getParameter(JspConst.PARAM_PASSWORD);
        if (password==null || !DataValidator.isPasswordValid(password)) {
            log.info("invalid password format was received:" + password);
            request.setAttribute(JspConst.INVALID_PASSWORD, true);
            return new CommandResult(Page.ADMIN_COACHES_COMMAND);
        }
        Coach coach = makeCoach(request);
        try {
            long coachId = coachService.save(coach);
            log.info("Coach with id = " + coachId + " successfully saved");
            request.getSession().setAttribute(JspConst.SUCCESS, true);
            page = Page.ADMIN_COACHES_COMMAND;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.ADMIN_COACHES_COMMAND;
        }
        return new CommandResult(page, true);
    }

    private Coach makeCoach(HttpServletRequest request) {
        String name = request.getParameter(JspConst.PARAM_NAME);
        String surname = request.getParameter(JspConst.PARAM_SURNAME);
        String patronymic = request.getParameter(JspConst.PARAM_PATRONYMIC);
        String login = request.getParameter(JspConst.PARAM_LOGIN);
        String password = request.getParameter(JspConst.PARAM_PASSWORD);
        password = DigestUtils.sha512Hex(password);
        return new Coach(null, name, surname, patronymic, login, password);
    }
}
