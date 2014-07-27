package deckbuilder.mtg.http;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;

import com.google.common.base.Strings;

import deckbuilder.mtg.facebook.FacebookException;
import deckbuilder.mtg.facebook.FacebookService;

public class FacebookAuthServlet extends HttpServlet implements HttpRequestHandler {
	private static final long serialVersionUID = -4564799401018750115L;

	@Inject
	private FacebookService facebookService;
	
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		URL currentUrl = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getRequestURI());
		
		//if the code is provided, it means that the OAuth was successful
		String code = request.getParameter("code");
		if(!Strings.isNullOrEmpty(code)) {
			try {
				String accessToken = facebookService.getAccessToken(code, currentUrl.toString());
				response.setContentType("text/plain");
				response.setStatus(HttpServletResponse.SC_OK);
				response.getOutputStream().println(accessToken);
				response.getOutputStream().flush();
			} catch(FacebookException exc) {
				response.setContentType("text/plain");
				response.setStatus(HttpServletResponse.SC_OK);
				response.getOutputStream().println(exc.getErrorMessage());
				response.getOutputStream().flush();
			}
		} else {
			String requestCodeUrl = facebookService.getRequestCodeUrl(currentUrl.toString());
			response.sendRedirect(requestCodeUrl);
		}
	}
}
