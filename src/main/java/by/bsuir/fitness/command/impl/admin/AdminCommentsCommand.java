package by.bsuir.fitness.command.impl.admin;

import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.CoachService;
import by.bsuir.fitness.service.CommentService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.service.impl.CommentServiceImpl;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.entity.Coach;
import by.bsuir.fitness.entity.Comment;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.service.impl.CoachServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.page.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The type Admin comments command.
 */
public class AdminCommentsCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(AdminCommentsCommand.class);
    private CommentService commentService = new CommentServiceImpl();
    private CoachService coachService = new CoachServiceImpl();
    private ClientService clientService = new ClientServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        try {
            List<Comment> comments = commentService.findAll();
            Map<Comment, Map<Client, Coach>> commentUserCoachMap = makeCommentMapForAdmin(comments);
            request.setAttribute(JspConst.COMMENTS, commentUserCoachMap);
            page = Page.ADMIN_COMMENTS;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.ADMIN_COMMENTS;
        }
        return new CommandResult(page);
    }

    private  Map<Comment, Map<Client, Coach>> makeCommentMapForAdmin(List<Comment> comments) throws ServiceException {
        Map<Comment, Map<Client, Coach>> commentUserCoachMap = new HashMap<>();
        for (Comment comment : comments) {
            Optional<Client> optionalClient = clientService.findById(comment.getClientId());
            Optional<Coach> optionalCoach = coachService.findById(comment.getCoachId());
            if (optionalCoach.isPresent() && optionalClient.isPresent()) {
                Map<Client, Coach> clientCoachMap= new HashMap<>();
                clientCoachMap.put(optionalClient.get(), optionalCoach.get());
                commentUserCoachMap.put(comment, clientCoachMap);
            }
        }
        return commentUserCoachMap;
    }
}
