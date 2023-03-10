package by.bsuir.fitness.command.impl.admin;

import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.OrderInformationService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandResult;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.entity.OrderInformation;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.service.impl.OrderInformationServiceImpl;
import by.bsuir.fitness.util.JspConst;
import by.bsuir.fitness.util.page.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * The type Admin orders command.
 */
public class AdminOrdersCommand implements ActionCommand {
    private static Logger log = LogManager.getLogger(AdminOrdersCommand.class);
    private OrderInformationService orderInformationService = new OrderInformationServiceImpl();
    private ClientService clientService = new ClientServiceImpl();

    @Override
    public CommandResult execute(HttpServletRequest request, HttpServletResponse response) {
        String page;
        List<OrderInformation> orders;
        String sortOrderString = request.getParameter(JspConst.SORT_ORDER);
        try {
            if (sortOrderString != null && sortOrderString.equals(JspConst.PRISE_ASC)) {
                orders = orderInformationService.findAscPrice();
            } else if (sortOrderString != null && sortOrderString.equals(JspConst.PRISE_DESC)) {
                orders = orderInformationService.findDescPrice();
            } else if (sortOrderString != null && sortOrderString.equals(JspConst.PAYMENT_DATA_ASC)) {
                orders = orderInformationService.findAscPaymentData();
            } else if (sortOrderString != null && sortOrderString.equals(JspConst.PAYMENT_DATA_DESC)) {
                orders = orderInformationService.findDescPaymentData();
            } else {
                orders = orderInformationService.findAll();
            }
            List<Client> clients = makeClientsListForAdmin(orders);
            if (clients.size() == orders.size()) {
                request.setAttribute(JspConst.ORDERS, orders);
                request.setAttribute(JspConst.CLIENTS, clients);
            }
            page = Page.ADMIN_ORDERS;
        } catch (ServiceException e) {
            log.error("Problem with service occurred!", e);
            page = Page.ADMIN_ORDERS;
        }
        return new CommandResult(page);
    }

    private List<Client> makeClientsListForAdmin(List<OrderInformation> orders) throws ServiceException {
        List<Client> clients = new ArrayList<>();
        for (OrderInformation order : orders) {
            Optional<Client> optionalUser = clientService.findById(order.getClientId());
            optionalUser.ifPresent(clients::add);
        }
        return clients;
    }
}
