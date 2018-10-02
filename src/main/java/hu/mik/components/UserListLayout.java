package hu.mik.components;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

import hu.mik.beans.LdapUser;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;
import hu.mik.views.ProfileView;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("serial")
public class UserListLayout extends CssLayout {

	@Autowired
	private LdapService ldapService;
	@Autowired
	private UserService userService;

	public CssLayout createUserListLayoutFromLdap(List<LdapUser> userListLdap) {
		this.addStyleName(ThemeConstants.MANY_USER_LAYOUT);
		this.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		this.setWidth("100%");

		if (userListLdap != null) {
			for (LdapUser user : userListLdap) {
				User dbUser = this.userService.findUserByUsername(user.getUsername());
				Image image = dbUser.getVaadinImage();
				image.setHeight("100%");
				image.addStyleName(ThemeConstants.BORDERED_IMAGE);
				Label lblName = new Label(user.getFullName());
				HorizontalLayout userDiv = new HorizontalLayout();
				userDiv.setHeight("60px");
				userDiv.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
				userDiv.addComponent(image);
				userDiv.addComponent(lblName);
				userDiv.setId(user.getUsername());
				userDiv.addStyleName(ThemeConstants.BORDERED);
				userDiv.addLayoutClickListener(this::layoutClickListener);
				this.addComponent(userDiv);

			}
		}
		return this;
	}

	public CssLayout createUserListLayoutFromDb(List<User> userListDb) {
		this.addStyleName(ThemeConstants.MANY_USER_LAYOUT);
		this.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		this.setWidth("100%");

		if (userListDb != null) {
			for (User dbUser : userListDb) {
				Image image = dbUser.getVaadinImage();
				image.setHeight("100%");
				image.addStyleName(ThemeConstants.BORDERED_IMAGE);
				Label lblName = new Label(dbUser.getFullName());
				HorizontalLayout userDiv = new HorizontalLayout();
				userDiv.setHeight("60px");
				userDiv.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
				userDiv.addComponent(image);
				userDiv.addComponent(lblName);
				userDiv.setId(dbUser.getUsername());
				userDiv.addStyleName(ThemeConstants.BORDERED);
				userDiv.addLayoutClickListener(this::layoutClickListener);
				this.addComponent(userDiv);

			}
		}
		return this;
	}

	private void layoutClickListener(LayoutClickEvent event) {
		((MainUI) this.getUI()).getNavigator().navigateTo(ProfileView.NAME + "/" + event.getComponent().getId());
	}
}
