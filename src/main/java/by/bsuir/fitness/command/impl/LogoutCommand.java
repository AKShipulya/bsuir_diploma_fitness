package by.bsuir.fitness.command.impl;

import by.bsuir.fitness.entity.UserRole;
import by.bsuir.fitness.util.CookieConst;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.*;

/**
 * The type Logout command.
 */
public class LogoutCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(LogoutCommand.class);

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute(SessionAttributes.ID);
        String userRole = (String) session.getAttribute(SessionAttributes.ROLE);
        session.removeAttribute(SessionAttributes.USER);
        session.removeAttribute(SessionAttributes.ROLE);
        session.removeAttribute(SessionAttributes.ID);
        if (userRole.equals(UserRole.CLIENT)) {
            session.removeAttribute(SessionAttributes.PROFILE_IMAGE);
            clearCookie(CookieConst.CLIENT_LOGIN, request, response);
            clearCookie(CookieConst.TOKEN, request, response);
        }
        session.invalidate();
        log.info("user with id = " + userId + " and role = " + userRole + " log out");
        return new CommandResult(Page.LOGIN_PAGE);
    }

    private void clearCookie(String cookieName, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                cookie.setPath("/");
                cookie.setMaxAge(0);
                cookie.setValue(null);
                response.addCookie(cookie);
                break;
            }
        }
    }
}
