package by.bsuir.fitness.command.impl;

import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.NutritionService;
import by.bsuir.fitness.service.ProgramService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.entity.Nutrition;
import by.bsuir.fitness.entity.Program;
import by.bsuir.fitness.mail.SendingEmail;
import by.bsuir.fitness.service.impl.NutritionServiceImpl;
import by.bsuir.fitness.service.impl.ProgramServiceImpl;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.sale.SaleSystem;
import by.bsuir.fitness.util.validation.DataValidator;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Random;

import static by.bsuir.fitness.util.JspConst.*;

/**
 * The type Register command.
 */
public class RegisterCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(RegisterCommand.class);
    private ClientService clientService = new ClientServiceImpl();
    private NutritionService nutritionService = new NutritionServiceImpl();
    private ProgramService programService = new ProgramServiceImpl();
    private final static SaleSystem SALE_SYSTEM = SaleSystem.getInstance();
    private final static Integer START_VISIT_NUMBER = 0;
    private final static Integer STANDARD_TRAINS_PER_WEEK = 3;

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        String name = request.getParameter(PARAM_NAME);
        if (name==null || !DataValidator.isNameValid(name)) {
            log.info("invalid name format was received:" + name);
            request.setAttribute(INVALID_NAME, true);
            return new CommandResult(Page.REGISTER_PAGE);
        }
        String surname = request.getParameter(PARAM_SURNAME);
        if (surname==null || !DataValidator.isSurnameValid(surname)) {
            log.info("invalid name format was received:" + surname);
            request.setAttribute(INVALID_SURNAME, true);
            return new CommandResult(Page.REGISTER_PAGE);
        }
        String login = request.getParameter(PARAM_LOGIN);
        if (login==null || !DataValidator.isLoginValid(login)) {
            log.info("invalid login format was received:" + login);
            request.setAttribute(INVALID_LOGIN, true);
            return new CommandResult(Page.REGISTER_PAGE);
        }
        String email = request.getParameter(PARAM_EMAIL);
        if (email==null || !DataValidator.isEmailValid(email)){
            log.info("invalid email format was received:" + email);
            request.setAttribute(INVALID_EMAIL, true);
            return new CommandResult(Page.REGISTER_PAGE);
        }
        String password = request.getParameter(PARAM_PASSWORD);
        if (password==null || !DataValidator.isPasswordValid(password)){
            log.info("invalid password format was received:" + password);
            request.setAttribute(INVALID_PASSWORD, true);
            return new CommandResult(Page.REGISTER_PAGE);
        }
        Random random = new SecureRandom();
        String userHash = DigestUtils.sha512Hex("" + random.nextInt(999999));
        Client client;
        try {
            client = buildUser(request, userHash);
            if (clientService.registerUser(client)) {
                SendingEmail.verify(login, email, userHash);
                log.info("client with login = " + login + " was registered. Activation Link was sent.");
                page = Page.VERIFY_PAGE;
            } else {
                request.setAttribute(WRONG_DATA, true);
                return new CommandResult(Page.REGISTER_PAGE);
            }
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.REGISTER_PAGE;
        }
        return new CommandResult(page, true);
    }

    private Client buildUser(HttpServletRequest request, String userHash) throws ServiceException {
        String name = request.getParameter(PARAM_NAME);
        String surname = request.getParameter(PARAM_SURNAME);
        String login = request.getParameter(PARAM_LOGIN);
        String email = request.getParameter(PARAM_EMAIL);
        String password = request.getParameter(PARAM_PASSWORD);
        String newPassword = DigestUtils.sha512Hex(password);
        float personalDiscount = SALE_SYSTEM.getSaleByVisitNumber(START_VISIT_NUMBER);
        Program program = buildProgram();
        return new Client(null, null, name, surname, login, newPassword, email, userHash, false, START_VISIT_NUMBER,
                personalDiscount, program.getId(), null, null);
    }

    private Program buildProgram() throws ServiceException {
        Program program = new Program();
        Nutrition nutrition = buildNutrition();
        program.setNutritionId(nutrition.getId());
        program.setTrainsPerWeek(STANDARD_TRAINS_PER_WEEK);
        Long programId = programService.save(program);
        program.setId(programId);
        return program;
    }

    private Nutrition buildNutrition() throws ServiceException {
        Nutrition nutrition = new Nutrition();
        Long nutritionId  = nutritionService.save(nutrition);
        nutrition.setId(nutritionId);
        return nutrition;
    }
}
