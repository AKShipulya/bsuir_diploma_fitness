package by.bsuir.fitness.command.impl;

import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.page.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The type Locale command.
 */
public class LocaleCommand implements ActionCommand {
    private static final String COMMAND = "/controller?command=";

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String lang = request.getParameter(JspConst.LANGUAGE);
        request.getSession().setAttribute(JspConst.LOCAL, lang);
        String page = request.getParameter(JspConst.CURRENT_PAGE);
        if (page == null || page.isEmpty()) {
            return new CommandResult(Page.LOGIN_PAGE);
        } else if (page.equals(JspConst.PASSWORD_RESTORE_PAGE)) {
            return new CommandResult(Page.PASSWORD_RESTORE_PAGE);
        } else if (page.equals(JspConst.RESTORE_PAGE)) {
            return new CommandResult(Page.RESTORE_PAGE);
        } else if (page.equals(JspConst.VERIFICATION_PAGE)) {
            return new CommandResult(Page.VERIFY_PAGE);
        } else if (page.equals(JspConst.FINAL_RESTORE_PAGE)) {
            return new CommandResult(Page.FINAL_RESTORE_PAGE);
        }
        return new CommandResult(COMMAND + page);
    }
}
