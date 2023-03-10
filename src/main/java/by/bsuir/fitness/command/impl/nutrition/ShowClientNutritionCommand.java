package by.bsuir.fitness.command.impl.nutrition;

import by.bsuir.fitness.service.CoachService;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Nutrition;
import by.bsuir.fitness.entity.UserRole;
import by.bsuir.fitness.service.NutritionService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.service.impl.CoachServiceImpl;
import by.bsuir.fitness.service.impl.NutritionServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.MembershipValidChecker;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static by.bsuir.fitness.util.JspConst.*;

/**
 * The type Show client nutrition command.
 */
public class ShowClientNutritionCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(ShowClientNutritionCommand.class);
    private MembershipValidChecker membershipValidChecker = new MembershipValidChecker();
    private NutritionService nutritionService = new NutritionServiceImpl();
    private CoachService coachService = new CoachServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        HttpSession session = request.getSession();
        String role = String.valueOf(session.getAttribute(SessionAttributes.ROLE));
        request.setAttribute(MAX_NUMBER_SYMBOLS_ATTRIBUTE,MAX_NUMBER_SYMBOLS_VALUE);
        Long clientId;
        try {
            if (role.equals(UserRole.COACH)) {
                clientId = getClientIdForAppropriateCoach(session,request);
            } else {
                clientId = (Long) session.getAttribute(SessionAttributes.ID);
                if (!membershipValidChecker.isCurrentMembershipValid(clientId)) {
                    request.setAttribute(MEMBERSHIP_VALID, false);
                    return new CommandResult(Page.NUTRITION);
                } else {
                    if (coachService.findByClientId(clientId).isEmpty()) {
                        request.setAttribute(JspConst.NO_COACH, true);
                        return new CommandResult(Page.NUTRITION);
                    }
                    request.setAttribute(MEMBERSHIP_VALID, true);
                }
            }
            Optional<Nutrition> nutritionOptional = nutritionService.findByClientId(clientId);
            if (nutritionOptional.isPresent()) {
                if (!nutritionOptional.get().isActive()) {
                    request.setAttribute(NO_NUTRITION, true);
                    request.setAttribute(NUTRITION, nutritionOptional.get()); //for activation
                } else {
                    request.setAttribute(NUTRITION, nutritionOptional.get());
                }
            }
            page = Page.NUTRITION;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.NUTRITION;
        }
        return new CommandResult(page);
    }

    private Long getClientIdForAppropriateCoach(HttpSession session, HttpServletRequest request) {
        String clientIdString = request.getParameter(COACH_CLIENT_ID);
        Long clientId;
        if (clientIdString == null) {
            clientId = (Long) session.getAttribute(COACH_CLIENT_ID);
        } else {
            clientId= Long.valueOf(request.getParameter(COACH_CLIENT_ID));
            session.setAttribute(COACH_CLIENT_ID,clientId);
        }
        return clientId;
    }
}
