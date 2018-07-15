package hu.mik.views;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.LdapUser;
import hu.mik.components.UserListLayout;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name = UserListView.NAME)
public class UserListView extends VerticalLayout implements View {
	public static final String NAME = "userList";

	@Autowired
	UserService userService;
	@Autowired
	LdapService ldapService;
	@Autowired
	UserListLayout userListLayout;

	private Panel panel = new Panel();

	@Override
	public void enter(ViewChangeEvent event) {

	}

	public void fill(String username) {
		CssLayout layout = new CssLayout();
		this.addComponent(this.panel);
		this.setSizeFull();
		List<LdapUser> userListLdap = new ArrayList<>();

		this.panel.setSizeFull();
		this.panel.setCaption("Users with similar names:");
		userListLdap = this.ldapService.findByFullNameContaining(username);

		if (userListLdap != null) {
			layout = this.userListLayout.createUserListLayoutFromLdap(userListLdap);
		}
		this.panel.setContent(layout);
	}

}
