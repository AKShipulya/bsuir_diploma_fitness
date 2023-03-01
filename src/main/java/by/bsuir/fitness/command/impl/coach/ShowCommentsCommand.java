package by.bsuir.fitness.command.impl.coach;

import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.CommentService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.service.impl.CommentServiceImpl;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.entity.Comment;
import by.bsuir.fitness.entity.UserRole;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.validation.DataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static by.bsuir.fitness.util.JspConst.*;

/**
 * The type Show comments command.
 */
public class ShowCommentsCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(ShowCommentsCommand.class);
    private CommentService commentService = new CommentServiceImpl();
    private ClientService clientService = new ClientServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        HttpSession session = request.getSession();
        request.setAttribute(MAX_NUMBER_SYMBOLS_ATTRIBUTE,MAX_NUMBER_SYMBOLS_VALUE);
        String role = String.valueOf(session.getAttribute(SessionAttributes.ROLE));
        Long coachId;
        if (role.equals(UserRole.COACH)) {
            coachId = (Long) session.getAttribute(SessionAttributes.ID);
        } else {
            String coachIdString = request.getParameter(COACH_ID);
            if (coachIdString==null || !DataValidator.isIdentifiableIdValid(coachIdString)) {
                log.info("invalid coach id format from user was received:" + coachIdString);
                request.setAttribute(INVALID_COACH_ID, true);
                return new CommandResult(Page.ALL_COACHES_COMMAND);
            }
            coachId = Long.valueOf(coachIdString);
        }
        try {
            List<Comment> comments = commentService.findByCoachId(coachId);
            Map<Comment, Client> commentUserMap = makeCommentMapForCoach(comments);
            request.setAttribute(JspConst.COMMENTS, commentUserMap);
            page = Page.COACH_COMMENTS;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.COACH_COMMENTS;
        }
        return new CommandResult(page);
    }

    private Map<Comment, Client> makeCommentMapForCoach(List<Comment> comments) throws ServiceException {
        Map<Comment, Client> commentUserMap = new HashMap<>();
        for (Comment comment : comments) {
            Optional<Client> optionalUser = clientService.findActiveById(comment.getClientId());
            optionalUser.ifPresent(user -> commentUserMap.put(comment, user));
        }
        return commentUserMap;
    }
}
