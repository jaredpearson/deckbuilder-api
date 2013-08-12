package deckbuilder.mtg.http;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import deckbuilder.mtg.facebook.FacebookService;

@Singleton
public class FacebookAuthServlet extends HttpServlet{
	private static final long serialVersionUID = -4564799401018750115L;

	@Inject
	private Provider<FacebookService> facebookService;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		URL currentUrl = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), request.getRequestURI());
		
		//if the code is provided, it means that the OAuth was successful
		String code = request.getParameter("code");
		if(!Strings.isNullOrEmpty(code)) {
			String accessToken = facebookService.get().getAccessToken(code, currentUrl.toString());
			response.setContentType("text/plain");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getOutputStream().println(accessToken);
			response.getOutputStream().flush();
			return;
		}
		
		String requestCodeUrl = facebookService.get().getRequestCodeUrl(currentUrl.toString());
		response.sendRedirect(requestCodeUrl);
	}
}
