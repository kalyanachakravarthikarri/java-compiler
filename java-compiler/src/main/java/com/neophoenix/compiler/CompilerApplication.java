package com.neophoenix.compiler;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

@SpringBootApplication
@RestController
@MultipartConfig(fileSizeThreshold = 20971520)
public class CompilerApplication {

	static Logger logger = Logger.getLogger(CompilerApplication.class.getName());

	@RequestMapping("/user")
	public Principal user(Principal user) {
		logger.entering(CompilerApplication.class.getName(), "user");
		return user;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/compile", produces = "application/json")
	@ResponseBody
	public CompilerOutput handleFileUpload(@RequestParam("file") MultipartFile file, 
            HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		logger.entering(CompilerApplication.class.getName(), "handleFileUpload");
		CompilerOutput response = null;
		if (!file.isEmpty()) {
			logger.info("File to be compiled : "+file.getOriginalFilename());
			String uploadedFileLocation = new File("").getAbsolutePath()
					+ "/uploaded/" + file.getOriginalFilename();
			logger.info("File uploaded to "+uploadedFileLocation);

			try {
				CompilerUtils.writeToFile(file.getInputStream(),
						uploadedFileLocation);
				logger.fine("File written successfully to "+uploadedFileLocation);
				logger.info("Compiling "+file.getOriginalFilename());
				response = CompilerUtils.compile(uploadedFileLocation);
				if(response.isError()){
					logger.info("Error while compiling "+file.getOriginalFilename());
					logger.fine(response.getDescription());
					httpResponse.setStatus( HttpServletResponse.SC_BAD_REQUEST  );
				}else{
					logger.info("Successfully compiled "+file.getOriginalFilename());
				}
			} catch (IOException e) {
			}
		}
		logger.exiting(CompilerApplication.class.getName(), "handleFileUpload");
		return response;
	}
	

	public static void main(String[] args) {
		SpringApplication.run(CompilerApplication.class, args);
	}

	/**
	 * SpringConfiguration
	 * @author Kalyan
	 *
	 */
	@Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.httpBasic().and().authorizeRequests()
					.antMatchers("/index.html", "/").permitAll().anyRequest()
					.authenticated().and().csrf()
					.csrfTokenRepository(csrfTokenRepository()).and()
					.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
		}

		private Filter csrfHeaderFilter() {
			return new OncePerRequestFilter() {
				@Override
				protected void doFilterInternal(HttpServletRequest request,
						HttpServletResponse response, FilterChain filterChain)
						throws ServletException, IOException {
					CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class
							.getName());
					if (csrf != null) {
						Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
						String token = csrf.getToken();
						if (cookie == null || token != null
								&& !token.equals(cookie.getValue())) {
							cookie = new Cookie("XSRF-TOKEN", token);
							cookie.setPath("/");
							response.addCookie(cookie);
						}
					}
					filterChain.doFilter(request, response);
				}
			};
		}

		private CsrfTokenRepository csrfTokenRepository() {
			HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
			repository.setHeaderName("X-XSRF-TOKEN");
			return repository;
		}
	}

}
