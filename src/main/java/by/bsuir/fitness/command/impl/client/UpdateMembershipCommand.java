package by.bsuir.fitness.command.impl.client;

import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.OrderInformationService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.OrderInformation;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.service.impl.OrderInformationServiceImpl;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.util.DateProducer;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.UtilException;
import by.bsuir.fitness.util.page.Page;
import by.bsuir.fitness.util.price.MembershipPriceReader;
import by.bsuir.fitness.util.sale.SaleSystem;
import by.bsuir.fitness.util.validation.DataValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import static by.bsuir.fitness.util.JspConst.*;

/**
 * The type Update membership command.
 */
public class UpdateMembershipCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(UpdateMembershipCommand.class);
    private ClientService clientService = new ClientServiceImpl();
    private OrderInformationService orderInformationService = new OrderInformationServiceImpl();
    private static final String PERIOD_PATTERN="\\D+";
    private final static SaleSystem SALE_SYSTEM = SaleSystem.getInstance();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        String cardNumber = request.getParameter(CARD_NUMBER);
        if (cardNumber==null || !DataValidator.isCardNumberValid(cardNumber)) {
            log.info("incorrect card number:" + cardNumber + " was input");
            request.setAttribute(WRONG_CARD, true);
            return new CommandResult(Page.ORDER_PAGE);
        }
        String costString = request.getParameter(COST);
        if (costString==null || !DataValidator.isCostValid(costString)){
            log.info("incorrect cost:" + costString + " was input");
            request.setAttribute(WRONG_PERIOD, true);
            return new CommandResult(Page.ORDER_PAGE);
        }
        BigDecimal cost = new BigDecimal(costString);
        HttpSession session = request.getSession();
        Long clientId = (Long) session.getAttribute(SessionAttributes.ID);
        java.sql.Date newEndMembershipDate;
        try {
            String period = request.getParameter(PERIOD);
            if (period==null || !isPeriodExist(period)){
                log.info("incorrect period:" + period + " was input");
                request.setAttribute(WRONG_PERIOD, true);
                return new CommandResult(Page.ORDER_PAGE);
            }
            newEndMembershipDate = defineNewEndMembershipEndDate(request,clientId);
            OrderInformation newOrderInformation = new OrderInformation(null, cost, new Timestamp(new Date().getTime()),
                                                                        newEndMembershipDate, clientId,cardNumber);
            orderInformationService.save(newOrderInformation);
            increaseClientVisitNumber(clientId);
            session.setAttribute(PAYMENT_SUCCESS, true);
            log.info("Gym membership of client with id = " + clientId + " has been updated");
            page = Page.CLIENT_PROFILE_COMMAND;
        } catch (ServiceException | UtilException e) {
            log.error("Exception occurred while defining NewEndMembershipEndDate", e);
            return new CommandResult(Page.ORDER_PAGE);
        }
        return new CommandResult(page, true);
    }

    private java.sql.Date defineNewEndMembershipEndDate(HttpServletRequest request, long clientID) throws ServiceException {
        String periodExtension = request.getParameter(PERIOD);
        periodExtension = periodExtension.replaceAll(PERIOD_PATTERN,"");
        Integer periodExtensionInteger = Integer.valueOf(periodExtension);
        Optional<OrderInformation> orderInformation = orderInformationService.findByClientId(clientID);
        Date membershipEndDate = new Date();
        if (orderInformation.isPresent()) {
            membershipEndDate = orderInformation.get().getMembershipEndDate();
        }
        Date newMembershipEndDate = DateProducer.getCorrectDate(membershipEndDate,periodExtensionInteger);
        return new java.sql.Date(newMembershipEndDate.getTime());
    }

    private void increaseClientVisitNumber(long clientId) throws ServiceException {
        Optional<Client> clientOptional = clientService.findById(clientId);
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            int currentVisitNumber = client.getMembershipNumber() + 1;
            client.setMembershipNumber(currentVisitNumber);
            Float newPersonalDiscount = SALE_SYSTEM.getSaleByVisitNumber(currentVisitNumber);
            client.setPersonalDiscount(newPersonalDiscount);
            clientService.save(client);
        }
    }

    private boolean isPeriodExist(String  period) throws UtilException {
        period = period.replaceAll(PERIOD_PATTERN,"");
        Integer periodInteger = Integer.valueOf(period);
        MembershipPriceReader membershipPrices = MembershipPriceReader.getInstance();
        return membershipPrices.getPrices().containsKey(periodInteger);
    }
}
