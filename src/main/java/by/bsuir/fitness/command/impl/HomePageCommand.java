package by.bsuir.fitness.command.impl;

import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.util.page.Page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The type Home page command.
 */
public class HomePageCommand implements ActionCommand {
    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        return new CommandResult(Page.HOME_PAGE);
    }
}
