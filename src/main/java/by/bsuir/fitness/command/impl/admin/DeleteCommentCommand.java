package by.bsuir.fitness.command.impl.admin;

import by.bsuir.fitness.service.CommentService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.service.impl.CommentServiceImpl;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.validation.DataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The type Delete comment command.
 */
public class DeleteCommentCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(DeleteCommentCommand.class);
    private CommentService commentService = new CommentServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        String commentIdString = request.getParameter(JspConst.COMMENT_ID);
        if (commentIdString==null || !DataValidator.isIdentifiableIdValid(commentIdString)) {
            log.info("incorrect comment id was received:" + commentIdString);
            return new CommandResult(Page.ADMIN_COMMENTS_COMMAND);
        }
        long commentId = Long.parseLong(commentIdString);
        try {
            commentService.delete(commentId);
            log.info("comment with id = " + commentId + " was successfully deleted");
            request.getSession().setAttribute(JspConst.SUCCESS, true);
            page = Page.ADMIN_COMMENTS_COMMAND;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.ADMIN_COMMENTS_COMMAND;
        }
        return new CommandResult(page, true);
    }
}
