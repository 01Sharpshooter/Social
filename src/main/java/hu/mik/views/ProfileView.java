package hu.mik.views;

import java.io.File;

import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.steinwedel.messagebox.MessageBox;
import hu.mik.beans.FriendRequest;
import hu.mik.beans.User;
import hu.mik.beans.UserLdap;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.FriendRequestService;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name=ProfileView.NAME)
public class ProfileView extends VerticalLayout implements View{
	public static final String NAME="profile";
	
	private WrappedSession session=VaadinService.getCurrentRequest().getWrappedSession();
	
	@Autowired
	UserService userService;
	@Autowired
	LdapService ldapService;
	@Autowired
	FriendRequestService friendRequestService;
	@Override
	public void enter(ViewChangeEvent event) {
		if(event.getParameters().length()>0){
			String ldapSessionUsername=(String) session.getAttribute("LdapUserUsername");
			UserLdap ldapSessionUser=ldapService.findUserByUsername(ldapSessionUsername);
			String parameters[]=event.getParameters().split("/");
			String profileUsername=parameters[0];
			UserLdap ldapProfileUser=ldapService.findUserByUsername(profileUsername);
			User dbUser=userService.findUserByUsername(profileUsername);
			if(ldapProfileUser==null) {
				Label lblMissing=new Label("Sorry, we could not find the person you were looking for.");
				this.addComponent(lblMissing);
			}else {
				CssLayout header=new CssLayout();
				CssLayout headerButtonList=new CssLayout();
				Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+dbUser.getImageName())));
				image.addStyleName(ThemeConstants.BORDERED_IMAGE);
				Label lblName=new Label(ldapProfileUser.getFullName());
				lblName.addStyleName(ThemeConstants.BLUE_TEXT_H1);
				header.setId("profileHeader");
				header.addComponent(image);
				header.addComponent(lblName);
				FormLayout form=new FormLayout();
				form.addStyleName(ThemeConstants.BORDERED);
				form.setMargin(true);
				form.setSizeFull();
				
				this.addComponent(header);
				this.addComponent(form);
				
				this.setComponentAlignment(header, Alignment.MIDDLE_CENTER);
				
				this.setExpandRatio(header, 20);
				this.setExpandRatio(form, 80);
								
				TextField tfName=new TextField("Name:", checkandSetIfNull(ldapProfileUser.getFullName()));
				TextField tfMobile=new TextField("Mobile:", checkandSetIfNull(ldapProfileUser.getMobile()));
				TextField tfMail=new TextField("E-Mail:", checkandSetIfNull(ldapProfileUser.getMail()));
				
				form.addComponent(tfName);
				form.addComponent(tfMobile);
				form.addComponent(tfMail);
				
				for (Component component : form) {
					if(component.getClass().equals(TextField.class)) {
						component.addStyleName(ThemeConstants.BLUE_TEXT);
						component.setWidth("30%");	
						if(profileUsername!=ldapSessionUsername) {
							component.setEnabled(false);
						}
					}
				}
			}
		}
		
	}
	private String checkandSetIfNull(String text) {
		if(text==null) {
			text="";
		}
		return text;
	}
	
	private void friendRequestClickListener(Button.ClickEvent event){
		FriendRequest fr=new FriendRequest();
//		fr.setRequestorId(user.getId());
//		fr.setRequestedId(sideUser.getId());
		friendRequestService.saveFriendRequest(fr);
		MessageBox.createInfo()
			.withOkButton()
			.withCaption("Request sent")
//			.withMessage("Request has been sent to "+sideUser.getUsername())
			.open();
//		refreshImage();
	}
	
}
