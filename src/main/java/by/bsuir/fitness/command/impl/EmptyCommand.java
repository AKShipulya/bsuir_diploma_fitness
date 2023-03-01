package by.bsuir.fitness.command.impl;

import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static by.bsuir.fitness.util.page.Page.LOGIN_PAGE;

/**
 * The type Empty command.
 */
public class EmptyCommand implements ActionCommand {
    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        return new CommandResult(LOGIN_PAGE);
    }
}
