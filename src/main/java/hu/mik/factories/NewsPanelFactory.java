package hu.mik.factories;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;

import hu.mik.beans.LdapGroup;
import hu.mik.beans.User;
import hu.mik.components.NewsPanelScrollable;
import hu.mik.services.LdapService;
import hu.mik.services.NewsService;
import hu.mik.utils.UserUtils;

@SpringComponent
public class NewsPanelFactory {

	private NewsService newsService;
	private LdapService ldapService;
	private UserUtils userUtils;

	@Autowired
	private NewsPanelFactory(UserUtils userUtils, LdapService ldapService, NewsService newsService) {
		this.newsService = newsService;
		this.ldapService = ldapService;
		this.userUtils = userUtils;
	}

	public NewsPanelScrollable getDefaultInstance() {
		NewsPanelScrollable panel = new NewsPanelScrollable(this.userUtils, this.ldapService, this.newsService);
		return panel.init();
	}

	public NewsPanelScrollable getUserInstance(User user) {
		NewsPanelScrollable panel = new NewsPanelScrollable(this.userUtils, this.ldapService, this.newsService);
		return panel.init(user);
	}

	public NewsPanelScrollable getLdapGroupInstance(LdapGroup ldapGroup) {
		NewsPanelScrollable panel = new NewsPanelScrollable(this.userUtils, this.ldapService, this.newsService);
		return panel.init(ldapGroup);
	}

}
