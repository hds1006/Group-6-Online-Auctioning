package edu.sru.cpsc.webshopping.secure;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import edu.sru.cpsc.webshopping.domain.user.User;
import edu.sru.cpsc.webshopping.repository.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TwoFactorAuthentication twoFactorService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String username = ((UserDetails) authentication.getPrincipal()).getUsername();
		User user = userRepository.findByUsername(username);

		if (user.isTwoFactorEnabled()) {
			twoFactorService.send2FACode(user);
			request.getSession().setAttribute("tempUsername", username);
			getRedirectStrategy().sendRedirect(request, response, "/verify2FAPage");
		} else {
			String originalUrl = (String) request.getSession().getAttribute("originalUrl");

			if (originalUrl != null && !originalUrl.isEmpty()) {
				request.getSession().removeAttribute("originalUrl");
				getRedirectStrategy().sendRedirect(request, response, originalUrl);
			} else {
				getRedirectStrategy().sendRedirect(request, response, "/index");
			}
		}
	}
}
