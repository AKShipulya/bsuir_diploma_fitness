package by.bsuir.fitness.command.impl.exercise;

import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.entity.Exercise;
import by.bsuir.fitness.entity.ExerciseProgram;
import by.bsuir.fitness.entity.Program;
import by.bsuir.fitness.entity.UserRole;
import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.CoachService;
import by.bsuir.fitness.service.ExerciseProgramService;
import by.bsuir.fitness.service.ExerciseService;
import by.bsuir.fitness.service.ProgramService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.service.impl.CoachServiceImpl;
import by.bsuir.fitness.service.impl.ExerciseProgramServiceImpl;
import by.bsuir.fitness.service.impl.ExerciseServiceImpl;
import by.bsuir.fitness.service.impl.ProgramServiceImpl;
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

import static by.bsuir.fitness.util.JspConst.COACH_CLIENT_ID;

/**
 * The type Show client exercises command.
 */
public class ShowClientExercisesCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(ShowClientExercisesCommand.class);
    private ClientService clientService = new ClientServiceImpl();
    private ProgramService programService = new ProgramServiceImpl();
    private CoachService coachService = new CoachServiceImpl();
    private ExerciseProgramService exerciseProgramService = new ExerciseProgramServiceImpl();
    private ExerciseService exerciseService = new ExerciseServiceImpl();
    private MembershipValidChecker membershipValidChecker = new MembershipValidChecker();
    private static final int TOTAL_PER_PAGE = 3;

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page = null;
        String pageNumberString = request.getParameter(JspConst.PAGE_NUMBER);
        if (pageNumberString==null || !DataValidator.isIdentifiableIdValid(pageNumberString)) {
            log.info("invalid page number format was received:" + pageNumberString);
            pageNumberString = "1";
        }
        int pageNumber = Integer.parseInt(pageNumberString);
        int start = pageNumber * TOTAL_PER_PAGE - TOTAL_PER_PAGE;
        HttpSession session = request.getSession();
        String role = String.valueOf(session.getAttribute(SessionAttributes.ROLE));
        Long userId;
        try {
            if (role.equals(UserRole.COACH)) {
                userId = getClientIdForAppropriateCoach(session,request);
            } else {
                userId = (Long) session.getAttribute(SessionAttributes.ID);
                if (!membershipValidChecker.isCurrentMembershipValid(userId)) {
                    request.setAttribute(JspConst.MEMBERSHIP_VALID, false);
                    return new CommandResult(Page.EXERCISES);
                } else {
                    request.setAttribute(JspConst.MEMBERSHIP_VALID, true);
                }
            }
            Optional<Client> user = clientService.findById(userId);
            if (user.isPresent()) {
                if (coachService.findByClientId(userId).isEmpty()) {
                    request.setAttribute(JspConst.NO_COACH, true);
                }
                Long programId = user.get().getProgramId();
                Optional<Program> program = programService.findProgramById(programId);
                request.getSession().setAttribute(JspConst.PROGRAM, program.get()); //exactly initialized in registerCommand
                List<ExerciseProgram> clientExercises = exerciseProgramService.findExercisesByProgramId(programId);
                request.setAttribute(JspConst.CLIENT_EXERCISES, clientExercises);
                int numberOfPages = exerciseService.getNumberOfPages(TOTAL_PER_PAGE);
                request.setAttribute(JspConst.NUMBER_OF_PAGES, numberOfPages);
                List<Exercise> allExercises = exerciseService.findAll(start, TOTAL_PER_PAGE);
                request.setAttribute(JspConst.PAGE_NUMBER, pageNumber);
                request.setAttribute(JspConst.All_EXERCISES, allExercises);
                page = Page.EXERCISES;
            }
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.EXERCISES;
        }
        return new CommandResult(page);
    }

    private Long getClientIdForAppropriateCoach(HttpSession session, HttpServletRequest request) {
        String clientIdString = request.getParameter(COACH_CLIENT_ID);
        Long clientId;
        if (clientIdString == null) {
            clientId = (Long) session.getAttribute(COACH_CLIENT_ID);
        } else {
            clientId = Long.valueOf(request.getParameter(COACH_CLIENT_ID));
            session.setAttribute(COACH_CLIENT_ID,clientId);
        }
        return clientId;
    }
}
