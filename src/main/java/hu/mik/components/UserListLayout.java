package hu.mik.components;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import hu.mik.beans.LdapUser;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.services.UserService;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("serial")
public class UserListLayout extends CssLayout {
	@Autowired
	private UserService userService;

	public CssLayout createUserListLayoutFromLdap(List<LdapUser> userListLdap) {
		if (userListLdap != null && !userListLdap.isEmpty()) {
			List<String> usernames = userListLdap.stream().filter(lu -> lu != null).map(lu -> lu.getUid())
					.collect(Collectors.toList());
			List<User> userList = this.userService.findAllByUsernames(usernames);
			this.createUserListLayoutFromDb(userList);
		}
		// TODO check
		return this;
	}

	public CssLayout createUserListLayoutFromDb(List<User> userListDb) {
		this.addStyleName(ThemeConstants.MANY_USER_LAYOUT);
		// this.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		this.setWidth("100%");

		if (userListDb != null && !userListDb.isEmpty()) {
			userListDb.forEach(user -> this.addComponent(new UserDiv(user)));
		} else {
			this.addComponent(new Label("No user found"));
		}
		return this;
	}
}
