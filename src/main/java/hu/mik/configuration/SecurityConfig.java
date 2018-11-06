package hu.mik.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;

import hu.mik.constants.LdapConstants;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private static final String LOGIN_PAGE = "/login";
	private static final String REGISTRATION_PAGE = "/registration";
	private static final String[] FREE_PAGES_ANT = { "/VAADIN/**", "/vaadinServlet/**", "/HEARTBEAT/**", "/UIDL/**",
			"/resources/**", LOGIN_PAGE, REGISTRATION_PAGE };
	private static final String DEFAULT_PAGE_URL = "/main#!home";
	private static final String ADMIN_PAGE_URL = "/**admin**";
	private static final String SECURITY_URL = "/j_spring_security_check";

	@Value("${SOCIAL_LDAP_URL}")
	private String ldapUrl;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests()
				.antMatchers(FREE_PAGES_ANT).permitAll()
				.antMatchers(ADMIN_PAGE_URL).hasRole("ADMINS")
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage(LOGIN_PAGE)
				.loginProcessingUrl(SECURITY_URL)
				.defaultSuccessUrl(DEFAULT_PAGE_URL, true)
				.permitAll();
		http.headers().frameOptions().sameOrigin();
		http.csrf().disable();
		// @formatter:on
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// @formatter:off
		auth.ldapAuthentication()
			.userSearchBase(LdapConstants.OU_USERS).userSearchFilter("uid={0}")
			.groupSearchBase(LdapConstants.OU_GROUPS).groupSearchFilter("member={0}")
			.contextSource()
				.url(this.ldapUrl + LdapConstants.SEARCH_BASE)
			.and()
			.passwordCompare()
				.passwordEncoder(new LdapShaPasswordEncoder())
				.passwordAttribute("userPassword");
		// @formatter:on
	}
}