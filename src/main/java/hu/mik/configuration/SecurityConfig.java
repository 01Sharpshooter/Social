package hu.mik.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private static final String LOGIN_PAGE = "/login";
	private static final String REGISTRATION_PAGE = "/registration";
	private static final String[] FREE_PAGES_ANT = { "/VAADIN/**", "/vaadinServlet/**", LOGIN_PAGE, REGISTRATION_PAGE};
	private static final String DEFAULT_PAGE_URL = "/main";
	private static final String SECURITY_URL = "/j_spring_security_check";
	
	@Autowired
	private DataSource dataSource;
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers(FREE_PAGES_ANT).permitAll()
		.anyRequest().hasAuthority("user").
		and().formLogin().loginPage(LOGIN_PAGE)
		.loginProcessingUrl(SECURITY_URL)
		.defaultSuccessUrl(DEFAULT_PAGE_URL, true)
		.permitAll();
		http.headers().frameOptions().sameOrigin();
		http.csrf().disable();
		
//		SecurityContextPersistenceFilter filter=new SecurityContextPersistenceFilter(http.getSharedObject(SecurityContextRepository.class));
////		filter.setForceEagerSessionCreation(true);
//		http.addFilter(filter);
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		  auth.jdbcAuthentication().dataSource(dataSource)
		  .passwordEncoder(passwordEncoder())
		  .usersByUsernameQuery("select username, passwd, enabled from t_user where username=?")
		  .authoritiesByUsernameQuery("select username, role from t_role where username=?");
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
}