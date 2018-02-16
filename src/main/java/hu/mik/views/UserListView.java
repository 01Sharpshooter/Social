package hu.mik.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

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
	
	private Panel panel=new Panel();
	private HorizontalLayout userDiv;
//	private CssLayout layout=new CssLayout();

	@Override
	public void enter(ViewChangeEvent event) {
		
	}

	public void fill(String username) {
		CssLayout layout=new CssLayout();
		this.addComponent(panel);
//		this.setMargin(false);
		this.setSizeFull();
		List<LdapUser> userListLdap=new ArrayList<>();
		
		panel.setSizeFull();
		panel.setContent(layout);
		Responsive.makeResponsive(layout);
		layout.addStyleName(ThemeConstants.MANY_USER_LAYOUT);
		layout.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		layout.setWidth("100%");
		panel.setCaption("Users with similar names:");
//		userList=userService.findAllLike(username);
		userListLdap=ldapService.findByFullNameContaining(username);		
		
		int i=0;		
		if(userListLdap!=null){
			for(LdapUser user : userListLdap){
				i++;
				User DbUser=userService.findUserByUsername(user.getUsername());
				Image image=new Image(null, new FileResource(
						new File(UserConstants.PROFILE_PICTURE_LOCATION+DbUser.getImageName())));
				image.setHeight("100%");
				image.addStyleName(ThemeConstants.BORDERED_IMAGE);
				Label lblName=new Label(user.getFullName());	
				userDiv=new HorizontalLayout();
				userDiv.setHeight("60px");
				userDiv.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
				userDiv.addComponent(image);
				userDiv.addComponent(lblName);
				userDiv.setId(user.getUsername());
				userDiv.addStyleName(ThemeConstants.BORDERED);
//				lblName.addStyleName(ThemeConstants.RESPONSIVE_FONT);
				userDiv.addLayoutClickListener(this::layoutClickListener);
//				Responsive.makeResponsive(lblName);
				layout.addComponent(userDiv);
				
			}		
		}
	}
	
	private void layoutClickListener(LayoutClickEvent event){
		((MainUI)getUI()).getNavigator().navigateTo(ProfileView.NAME+"/"+event.getComponent().getId());
	}
	
}
