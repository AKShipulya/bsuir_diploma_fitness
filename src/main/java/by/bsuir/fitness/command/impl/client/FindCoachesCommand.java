package by.bsuir.fitness.command.impl.client;

import by.bsuir.fitness.service.CoachService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Coach;
import by.bsuir.fitness.service.impl.CoachServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.MembershipValidChecker;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

import static by.bsuir.fitness.util.JspConst.MAX_NUMBER_SYMBOLS_ATTRIBUTE;
import static by.bsuir.fitness.util.JspConst.MAX_NUMBER_SYMBOLS_VALUE;

/**
 * The type Find coaches command.
 */
public class FindCoachesCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(FindCoachesCommand.class);
    private CoachService coachService = new CoachServiceImpl();
    private MembershipValidChecker membershipValidChecker = new MembershipValidChecker();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        HttpSession session = request.getSession();
        String role = String.valueOf(session.getAttribute(SessionAttributes.ROLE));
        Long clientId = null;
        if (role != null) {
            clientId = (Long) session.getAttribute(SessionAttributes.ID);
        }
        try {
            List<Coach> coaches = coachService.findAll();
            request.setAttribute(JspConst.COACHES, coaches);
            request.setAttribute(MAX_NUMBER_SYMBOLS_ATTRIBUTE, MAX_NUMBER_SYMBOLS_VALUE);
            request.setAttribute(JspConst.MEMBERSHIP_VALID, true);
            if (clientId != null) {
                checkAndSetIfClientHasCoach(request, clientId);
            }
            page = Page.ALL_COACHES;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.ALL_COACHES;
        }
        return new CommandResult(page);
    }

    private void checkAndSetIfClientHasCoach(HttpServletRequest request, Long clientId) throws ServiceException {
        if (membershipValidChecker.isCurrentMembershipValid(clientId)) {
            Optional<Coach> coachOptional = coachService.findByClientId(clientId);
            coachOptional.ifPresent(coach -> request.setAttribute(JspConst.ID_OF_CLIENT_COACH, coach.getId()));
        } else {
            log.info("Membership isn't valid");
        }
    }
}
