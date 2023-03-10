package by.bsuir.fitness.command.impl;

import by.bsuir.fitness.service.AdminService;
import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.CoachService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Admin;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.entity.Coach;
import by.bsuir.fitness.entity.UserRole;
import by.bsuir.fitness.service.impl.AdminServiceImpl;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.service.impl.CoachServiceImpl;
import by.bsuir.fitness.util.CookieConst;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.validation.DataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static by.bsuir.fitness.util.JspConst.PARAM_LOGIN;
import static by.bsuir.fitness.util.JspConst.PARAM_PASSWORD;

/**
 * The type Login command.
 */
public class LoginCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(LoginCommand.class);
    private static ClientService clientService = new ClientServiceImpl();
    private static CoachService coachService = new CoachServiceImpl();
    private static AdminService adminService = new AdminServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        Client client;
        Coach coach;
        Admin admin;
        String login = request.getParameter(PARAM_LOGIN);
        if (login==null || !DataValidator.isLoginValid(login)) {
            log.info("invalid login format was received:" + login);
            request.setAttribute(JspConst.INVALID_LOGIN, true);
            return new CommandResult(Page.LOGIN_PAGE);
        }
        String password = request.getParameter(PARAM_PASSWORD);
        if (password==null || !DataValidator.isPasswordValid(password)) {
            log.info("invalid password format was received:" + password);
            request.setAttribute(JspConst.INVALID_PASSWORD, true);
            return new CommandResult(Page.LOGIN_PAGE);
        }
        boolean rememberMe = Boolean.parseBoolean(request.getParameter(JspConst.REMEMBER_ME));
        HttpSession session = request.getSession();
        try {
            if (clientService.checkUserByLoginPassword(login, password).isPresent()) {
                client = clientService.checkUserByLoginPassword(login, password).get();
                session.setAttribute(SessionAttributes.PROFILE_IMAGE, client.getImage());
                session.setAttribute(SessionAttributes.USER, login);
                session.setAttribute(SessionAttributes.ROLE, UserRole.CLIENT);
                session.setAttribute(SessionAttributes.ID, client.getId());
                if (rememberMe) {
                    Cookie cookieLogin = new Cookie(CookieConst.CLIENT_LOGIN, login);
                    cookieLogin.setMaxAge(CookieConst.EXPIRY);
                    cookieLogin.setPath("/");
                    response.addCookie(cookieLogin);
                    Cookie cookieToken = new Cookie(CookieConst.TOKEN, client.getUserHash());
                    cookieToken.setMaxAge(CookieConst.EXPIRY);
                    cookieToken.setPath("/");
                    response.addCookie(cookieToken);
                }
                log.info("client with id = " + client.getId() + " log in. RememberMe = " + rememberMe);
                page = Page.WELCOME_PAGE;
            } else if (coachService.checkCoachByLoginPassword(login, password).isPresent()) {
                coach = coachService.checkCoachByLoginPassword(login, password).get();
                session.setAttribute(SessionAttributes.USER, login);
                session.setAttribute(SessionAttributes.ROLE, UserRole.COACH);
                session.setAttribute(SessionAttributes.ID, coach.getId());
                log.info("coach with id = " + coach.getId() + " log in");
                page = Page.WELCOME_PAGE;
            } else if (adminService.checkAdminByLoginPassword(login, password).isPresent()) {
                admin = adminService.checkAdminByLoginPassword(login, password).get();
                session.setAttribute(SessionAttributes.USER, login);
                session.setAttribute(SessionAttributes.ROLE, UserRole.ADMIN);
                session.setAttribute(SessionAttributes.ID, admin.getId());
                log.info("admin with id = " + admin.getId() + " log in");
                page = Page.WELCOME_PAGE;
            } else {
                request.setAttribute(JspConst.WRONG_DATA, true);
                return new CommandResult(Page.LOGIN_PAGE);
            }
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.LOGIN_PAGE;
        }
        return new CommandResult(page, true);
    }
}
