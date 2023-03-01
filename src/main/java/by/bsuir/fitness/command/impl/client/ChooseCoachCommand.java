package by.bsuir.fitness.command.impl.client;

import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.entity.Coach;
import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.CoachService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.MembershipValidChecker;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.validation.DataValidator;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.service.impl.CoachServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * The type Choose coach command.
 */
public class ChooseCoachCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(ChooseCoachCommand.class);
    private ClientService clientService = new ClientServiceImpl();
    private MembershipValidChecker membershipValidChecker = new MembershipValidChecker();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        String coachIdString = request.getParameter(JspConst.COACH_ID);
        if (coachIdString == null || !DataValidator.isIdentifiableIdValid(coachIdString)){
            log.info("invalid coach id format: coach_id:" + coachIdString);
            request.setAttribute(JspConst.INVALID_COACH, true);
            return new CommandResult(Page.ALL_COACHES_COMMAND);
        }
        Long coachId = Long.valueOf(coachIdString);
        try {
            if (!isCoachIdExist(coachId)) {
                log.info("coach with id = " + coachId + " doesn't exist");
                request.setAttribute(JspConst.NOT_EXIST_ID, true);
                return new CommandResult(Page.ALL_COACHES_COMMAND);
            }
            HttpSession session = request.getSession();
            long clientId = (long) session.getAttribute(SessionAttributes.ID);
            Optional<Client> user = clientService.findById(clientId);
            if (user.isPresent()) {
                if (!membershipValidChecker.isCurrentMembershipValid(clientId)) {
                    log.info("Membership isn't valid");
                    request.setAttribute(JspConst.MEMBERSHIP_VALID, false);
                    return new CommandResult(Page.ALL_COACHES);
                } else {
                    request.setAttribute(JspConst.MAX_NUMBER_SYMBOLS_ATTRIBUTE, JspConst.MAX_NUMBER_SYMBOLS_VALUE);
                    request.setAttribute(JspConst.MEMBERSHIP_VALID, true);
                }
                user.get().setCoachId(coachId);
                clientService.save(user.get());
            }
            log.info("coach with id  = " + coachId + " was chosen");
            session.setAttribute(JspConst.COACH_CHOSEN, true);
            page = Page.WELCOME_PAGE;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.ALL_COACHES_COMMAND;
        }
        return new CommandResult(page, true);
    }

    private boolean isCoachIdExist(Long coachId) throws ServiceException {
        CoachService coachService = new CoachServiceImpl();
        Optional<Coach> coach = coachService.findById(coachId);
        return coach.isPresent();
    }
}
