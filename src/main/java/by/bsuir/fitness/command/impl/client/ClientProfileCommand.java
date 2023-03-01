package by.bsuir.fitness.command.impl.client;

import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.CoachService;
import by.bsuir.fitness.service.OrderInformationService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.entity.Coach;
import by.bsuir.fitness.entity.OrderInformation;
import by.bsuir.fitness.entity.UserRole;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.service.impl.CoachServiceImpl;
import by.bsuir.fitness.service.impl.OrderInformationServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.MembershipValidChecker;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.validation.DataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

import static by.bsuir.fitness.util.JspConst.ADMIN_CLIENT_ID;

/**
 * The type Client profile command.
 */
public class ClientProfileCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(ClientProfileCommand.class);
    private OrderInformationService orderInformationService = new OrderInformationServiceImpl();
    private ClientService clientService = new ClientServiceImpl();
    private CoachService coachService = new CoachServiceImpl();
    private MembershipValidChecker membershipValidChecker = new MembershipValidChecker();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        HttpSession session = request.getSession();
        String role = String.valueOf(session.getAttribute(SessionAttributes.ROLE));
        Long clientId;
        if (role.equals(UserRole.CLIENT)) {
            clientId = (Long) session.getAttribute(SessionAttributes.ID);
        } else {
            clientId = getClientIdForAppropriateAdmin(session, request);
            if (clientId == -1L) {
                return new CommandResult(Page.ADMIN_CLIENTS);
            }
        }
        try {
            List<OrderInformation> ordersList = orderInformationService.findOrdersByClientId(clientId);
            request.setAttribute(JspConst.ORDERS, ordersList);
            Optional<Client> userOptional = clientService.findById(clientId);
            if (userOptional.isPresent()) {
                Client client = userOptional.get();
                request.setAttribute(JspConst.CLIENT, client);
                Optional<OrderInformation> orderInformation = orderInformationService.findByClientId(clientId);
                if (orderInformation.isPresent()) {
                    request.setAttribute(JspConst.MEMBERSHIP_VALID, membershipValidChecker.isCurrentMembershipValid(client.getId()));
                }
                Long coachId = client.getCoachId();
                Optional<Coach> coach = coachService.findById(coachId);
                coach.ifPresent(aCoach -> {
                    request.setAttribute(JspConst.COACH_NAME, aCoach.getName());
                    request.setAttribute(JspConst.COACH_SURNAME, aCoach.getSurname());
                });
            }
            page = Page.CLIENT_PROFILE_PAGE;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.CLIENT_PROFILE_PAGE;
        }
        return new CommandResult(page);
    }

    private Long getClientIdForAppropriateAdmin(HttpSession session, HttpServletRequest request) {
        String clientIdString = request.getParameter(ADMIN_CLIENT_ID);
        Long clientId;
        if (clientIdString == null) {
            clientId = (Long) session.getAttribute(ADMIN_CLIENT_ID);
        } else {
            clientIdString = request.getParameter(ADMIN_CLIENT_ID);
            if (!DataValidator.isIdentifiableIdValid(clientIdString)) {
                log.info("invalid client id format from admin was received:" + clientIdString);
                request.setAttribute(JspConst.INVALID_EXERCISE_ID_FORMAT, true);
                return -1L;
            }
            clientId= Long.valueOf(clientIdString);
            session.setAttribute(ADMIN_CLIENT_ID,clientId);
        }
        return clientId;
    }
}
