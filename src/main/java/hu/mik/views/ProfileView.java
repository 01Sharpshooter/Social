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
import hu.mik.beans.Friendship;
import hu.mik.beans.User;
import hu.mik.beans.LdapUser;
import hu.mik.constants.StringConstants;
import hu.mik.constants.SystemConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.FriendRequestService;
import hu.mik.services.FriendshipService;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name=ProfileView.NAME)
public class ProfileView extends VerticalLayout implements View{
	public static final String NAME="profile";
	
	private WrappedSession session=VaadinService.getCurrentRequest().getWrappedSession();
	private User dbSessionUser;
	private String profileUsername;
	private User dbProfileUser;
	private CssLayout header;
	private CssLayout headerButtonList;
	
	@Autowired
	UserService userService;
	@Autowired
	LdapService ldapService;
	@Autowired
	FriendRequestService friendRequestService;
	@Autowired
	FriendshipService friendShipService;
	
	@Override
	public void enter(ViewChangeEvent event) {
		if(event.getParameters().length()>0){
			String ldapSessionUsername=(String) session.getAttribute(SystemConstants.SESSION_ATTRIBUTE_LDAP_USER);
			dbSessionUser=userService.findUserByUsername(ldapSessionUsername);
			String parameters[]=event.getParameters().split("/");
			profileUsername=parameters[0];
			LdapUser ldapProfileUser=ldapService.findUserByUsername(profileUsername);
			dbProfileUser=userService.findUserByUsername(profileUsername);
			if(ldapProfileUser==null) {
				Label lblMissing=new Label("Sorry, we could not find the person you were looking for.");
				this.addComponent(lblMissing);
			}else {
				header=new CssLayout();
				CssLayout headerButtonList=createHeaderBtnList();
				
				Image image=new Image(null, new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION+dbProfileUser.getImageName())));
				image.addStyleName(ThemeConstants.BORDERED_IMAGE);
				Label lblName=new Label(ldapProfileUser.getFullName());
				lblName.addStyleName(ThemeConstants.BLUE_TEXT_H1);
				lblName.addStyleName(ThemeConstants.RESPONSIVE_FONT);
				StringBuilder strGroups=new StringBuilder();
				ldapService.findGroupsByUserId(ldapProfileUser.getId())
				.forEach(group->strGroups.append(group.getGroupName()+", "));
				Label lblGroups;
				if(strGroups.length()!=0) {
					lblGroups=new Label(strGroups.substring(0, strGroups.length()-2));
				}else {
					lblGroups=new Label("Without team");
				}
				lblGroups.addStyleName(ThemeConstants.BLUE_TEXT_H3);
//				lblGroups.addStyleName(ThemeConstants.RESPONSIVE_FONT);
				header.setId("profileHeader");
				header.addComponent(image);
				header.addComponent(lblName);
				header.addComponent(lblGroups);
				header.addComponent(headerButtonList);
				FormLayout form=new FormLayout();
				form.addStyleName(ThemeConstants.BORDERED);
				form.setMargin(false);
				form.setId("profileBody");
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
//						component.setWidth("100px");	
						if(!profileUsername.equals(ldapSessionUsername)) {
							component.setEnabled(false);
						}
					}
				}
			}
		}
		
	}
	private CssLayout createHeaderBtnList() {
		headerButtonList=new CssLayout();
		
		if(dbSessionUser.getId()!=dbProfileUser.getId()) {
			Button btnFriendRequest=new Button();
			btnFriendRequest.addStyleName(ThemeConstants.BLUE_TEXT);
			Button btnMessage=new Button("Message");
			btnMessage.addStyleName(ThemeConstants.BLUE_TEXT);
			btnMessage.addClickListener(this::messageClickListener);
			
			headerButtonList.addComponent(btnFriendRequest);
			headerButtonList.addComponent(btnMessage);
			if(friendShipService.findOne(dbProfileUser.getId(), dbSessionUser.getId())!=null){
				btnFriendRequest.setCaption(StringConstants.BTN_REMOVE_FRIEND);
			}else if(!friendRequestService.IsAlreadyRequested(dbSessionUser.getId(), dbProfileUser.getId()) &&
					!friendRequestService.IsAlreadyRequested(dbProfileUser.getId(), dbSessionUser.getId())) {
				btnFriendRequest.setCaption(StringConstants.BTN_FRIEND_REQUEST);
			}else if(friendRequestService.IsAlreadyRequested(dbProfileUser.getId(), dbSessionUser.getId())) {
				btnFriendRequest.setCaption(StringConstants.BTN_ACCEPT_REQUEST);
				Button btnDeclineRequest=new Button(StringConstants.BTN_DECLINE_REQUEST);
				btnDeclineRequest.addStyleName(ThemeConstants.BLUE_TEXT);
				btnDeclineRequest.addClickListener(this::declineRequestClickListener);
				headerButtonList.addComponent(btnDeclineRequest);
			}else {
				btnFriendRequest.setCaption(StringConstants.BTN_CANCEL_REQUEST);
			}
			
			btnFriendRequest.addClickListener(this::friendRequestClickListener);
			
		}
		
		return headerButtonList;
	}
	private String checkandSetIfNull(String text) {
		if(text==null) {
			text="";
		}
		return text;
	}
	
	private void friendRequestClickListener(Button.ClickEvent event){
		if(event.getButton().getCaption().equals(StringConstants.BTN_REMOVE_FRIEND)) {
			friendShipService.deleteFriendship(dbProfileUser.getId(), dbSessionUser.getId());
			header.replaceComponent(headerButtonList, createHeaderBtnList());
		}else if(event.getButton().getCaption().equals(StringConstants.BTN_FRIEND_REQUEST)) {
			LdapUser ldapProfile=ldapService.findUserByUsername(profileUsername);
			FriendRequest fr=new FriendRequest();
			fr.setRequestorId(dbSessionUser.getId());
			fr.setRequestedId(dbProfileUser.getId());
			friendRequestService.saveFriendRequest(fr);
			event.getButton().setCaption(StringConstants.BTN_CANCEL_REQUEST);
			MessageBox.createInfo()
				.withOkButton()
				.withCaption("Request sent")
				.withMessage("Request has been sent to "+ldapProfile.getFullName())
				.open();
		}else if(event.getButton().getCaption().equals(StringConstants.BTN_ACCEPT_REQUEST)){
			friendRequestService.deleteFriendRequest(dbProfileUser.getId(), dbSessionUser.getId());
			friendShipService.saveFriendship(dbProfileUser.getId(), dbSessionUser.getId());
			header.replaceComponent(headerButtonList, createHeaderBtnList());
		}
		else {
			friendRequestService.deleteFriendRequest(dbSessionUser.getId(), dbProfileUser.getId());
			event.getButton().setCaption(StringConstants.BTN_FRIEND_REQUEST);
		}

	}
	private void declineRequestClickListener(Button.ClickEvent event) {
		friendRequestService.deleteFriendRequest(dbProfileUser.getId(), dbSessionUser.getId());
		header.replaceComponent(headerButtonList, createHeaderBtnList());
	}
	private void messageClickListener(Button.ClickEvent event) {
		getUI().getNavigator().navigateTo(MessagesView.NAME+"/"+profileUsername);
	}
	
}
