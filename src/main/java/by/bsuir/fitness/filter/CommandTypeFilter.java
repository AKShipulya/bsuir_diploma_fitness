package by.bsuir.fitness.filter;

import by.bsuir.fitness.command.access.CommandAccess;
import by.bsuir.fitness.entity.Client;
import by.bsuir.fitness.service.ClientService;
import by.bsuir.fitness.service.ServiceException;
import by.bsuir.fitness.command.ActionCommand;
import by.bsuir.fitness.command.CommandEnum;
import by.bsuir.fitness.entity.UserRole;
import by.bsuir.fitness.service.impl.ClientServiceImpl;
import by.bsuir.fitness.util.SessionAttributes;
import by.bsuir.fitness.util.page.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * The type Command type filter.
 */
public class CommandTypeFilter implements Filter {
    private static Logger log = LogManager.getLogger(CommandTypeFilter.class);
    private static final String COMMAND = "command";
    private CommandAccess commandAccess = new CommandAccess();
    private ClientService clientService = new ClientServiceImpl();

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        Optional<String> userRole = getUserRoleByRequest(httpServletRequest);
        List<ActionCommand> commandTypes = commandAccess.getAvailableCommandTypesByUser(userRole);
        String action = servletRequest.getParameter(COMMAND);
        ActionCommand currentCommand;
        try {
            currentCommand= CommandEnum.getCurrentCommand(action);
        } catch (IllegalArgumentException exception) {
            log.warn("Action with incorrect command:" + action);
            RequestDispatcher requestDispatcher = servletRequest.getRequestDispatcher(Page.ERROR_PAGE);
            requestDispatcher.forward(servletRequest, servletResponse);
            return;
        }
        if (commandTypes.contains(currentCommand)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            RequestDispatcher requestDispatcher = servletRequest.getRequestDispatcher(Page.NO_ACCESS_PAGE);
            requestDispatcher.forward(servletRequest, servletResponse);
        }
    }

    private Optional<String> getUserRoleByRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Optional<String> roleOptional = Optional.empty();
        try {
            if (session != null) {
                String role = (String)session.getAttribute(SessionAttributes.ROLE);
                roleOptional = Optional.ofNullable(role);
                if (role == null) {
                    if (getClientByCookie(request).isPresent()) {
                        Client client = getClientByCookie(request).get();
                        setClientToSession(request, client);
                        roleOptional = Optional.of(UserRole.CLIENT);
                    }
                }
            } else {
                if (getClientByCookie(request).isPresent()) {
                    Client client = getClientByCookie(request).get();
                    setClientToSession(request, client);
                    roleOptional = Optional.of(UserRole.CLIENT);
                }
            }
        } catch (ServiceException e) {
            log.error("Service exception occurred here", e);
        }
        return roleOptional;
    }

    private Optional<Client> getClientByCookie(HttpServletRequest request) throws ServiceException {
        String login = null;
        String hash = null;
        Optional<Client> user = Optional.empty();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("clientLogin")) {
                    login = cookie.getValue();
                }
                if (cookie.getName().equals("token")) {
                    hash = cookie.getValue();
                }
            }
        }
        if (login != null && hash != null) {
            user = clientService.getUserByCookieData(login, hash);
        }
        return user;
    }

    private void setClientToSession(HttpServletRequest request, Client client) {
        request.getSession().setAttribute(SessionAttributes.PROFILE_IMAGE, client.getImage());
        request.getSession().setAttribute(SessionAttributes.USER, client.getLogin());
        request.getSession().setAttribute(SessionAttributes.ROLE, UserRole.CLIENT);
        request.getSession().setAttribute(SessionAttributes.ID, client.getId());
    }

    @Override
    public void destroy() {}
}
