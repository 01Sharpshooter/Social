package hu.mik.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.Responsive;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.User;
import hu.mik.components.UserListLayout;
import hu.mik.beans.LdapUser;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.FriendshipService;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;

@ViewScope
@SpringView(name=UserListView.NAME)
public class UserListView extends VerticalLayout implements View{
	public static final String NAME="userList";
	
	@Autowired
	UserService userService;	
	@Autowired
	LdapService ldapService;
	@Autowired
	UserListLayout userListLayout;
	
	private Panel panel=new Panel();
	private HorizontalLayout userDiv;
//	private CssLayout layout=new CssLayout();

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

	public void fill(String username) {
		CssLayout layout=new CssLayout();
		this.addComponent(panel);
		this.setSizeFull();
		List<LdapUser> userListLdap=new ArrayList<>();
		
		panel.setSizeFull();
		panel.setCaption("Users with similar names:");
		userListLdap=ldapService.findByFullNameContaining(username);		
			
		if(userListLdap!=null){
			layout=userListLayout.createUserListLayoutFromLdap(userListLdap);
		}
		panel.setContent(layout);
	}
	
	private void layoutClickListener(LayoutClickEvent event){
		((MainUI)getUI()).getNavigator().navigateTo(ProfileView.NAME+"/"+event.getComponent().getId());
	}
	
}
