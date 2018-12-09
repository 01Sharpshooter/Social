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
import hu.mik.utils.UserUtils;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("serial")
public class UserListLayout extends CssLayout {
	@Autowired
	private UserService userService;
	@Autowired
	private UserUtils userUtils;

	public CssLayout createUserListLayoutFromLdap(List<LdapUser> userListLdap) {
		List<User> userList = null;
		if (userListLdap != null && !userListLdap.isEmpty()) {
			List<String> usernames = userListLdap.stream().filter(lu -> lu != null).map(lu -> lu.getUid())
					.collect(Collectors.toList());
			userList = this.userService.findAllByUsernames(usernames);
		}
		return this.createUserListLayoutFromDb(userList);
	}

	public CssLayout createUserListLayoutFromDb(List<User> userListDb) {
		this.addStyleName(ThemeConstants.MANY_USER_LAYOUT);
		this.setWidth("100%");

		if (userListDb != null && !userListDb.isEmpty()) {
			userListDb.forEach(user -> this.addComponent(
					new UserDiv(user, !user.getId().equals(this.userUtils.getLoggedInUser().getDbUser().getId()))));
		} else {
			this.addComponent(new Label("No user found"));
		}
		return this;
	}
}
