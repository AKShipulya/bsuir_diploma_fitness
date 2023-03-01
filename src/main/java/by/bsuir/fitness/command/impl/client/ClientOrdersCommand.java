package by.bsuir.fitness.command.impl.client;

import by.bsuir.fitness.entity.OrderInformation;
import by.bsuir.fitness.entity.UserRole;
import by.bsuir.fitness.service.OrderInformationService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.validation.DataValidator;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.service.impl.OrderInformationServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * The type Client orders command.
 */
public class ClientOrdersCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(ClientOrdersCommand.class);
    private OrderInformationService orderInformationService = new OrderInformationServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        HttpSession session = request.getSession();
        String role = String.valueOf(session.getAttribute(SessionAttributes.ROLE));
        Long clientId;
        if (role.equals(UserRole.COACH)) {
            clientId = getClientIdForAppropriateCoach(session, request);
            if (clientId == -1L) {
                return new CommandResult(Page.CLIENT_ORDERS);
            }
        } else {
            clientId = (Long) session.getAttribute(SessionAttributes.ID);
        }
        try {
            List<OrderInformation> ordersList = orderInformationService.findOrdersByClientId(clientId);
            session.setAttribute(JspConst.ORDERS, ordersList);
            page = Page.CLIENT_ORDERS;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.WELCOME_PAGE;
        }
        return new CommandResult(page);
    }

    private Long getClientIdForAppropriateCoach(HttpSession session, HttpServletRequest request) {
        String clientIdString = request.getParameter(JspConst.COACH_CLIENT_ID);
        Long clientId;
        if (clientIdString == null) {
            clientId = (Long) session.getAttribute(JspConst.COACH_CLIENT_ID);
        } else {
            clientIdString = request.getParameter(JspConst.COACH_CLIENT_ID);
            if (!DataValidator.isIdentifiableIdValid(clientIdString)) {
                log.info("invalid client id format from coach was received:" + clientIdString);
                request.setAttribute(JspConst.INVALID_EXERCISE_ID_FORMAT, true);
                return -1L;
            }
            clientId = Long.valueOf(clientIdString);
            session.setAttribute(JspConst.COACH_CLIENT_ID,clientId);
        }
        return clientId;
    }
}
