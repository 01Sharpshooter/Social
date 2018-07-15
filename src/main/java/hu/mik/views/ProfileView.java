package hu.mik.views;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.steinwedel.messagebox.MessageBox;
import hu.mik.beans.FriendRequest;
import hu.mik.beans.LdapUser;
import hu.mik.beans.User;
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
@SpringView(name = ProfileView.NAME)
public class ProfileView extends VerticalLayout implements View {
	public static final String NAME = "profile";

	private WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
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
		if (event.getParameters().length() > 0) {
			String ldapSessionUsername = (String) this.session
					.getAttribute(SystemConstants.SESSION_ATTRIBUTE_LDAP_USER);
			this.dbSessionUser = this.userService.findUserByUsername(ldapSessionUsername);
			String parameters[] = event.getParameters().split("/");
			this.profileUsername = parameters[0];
			LdapUser ldapProfileUser = this.ldapService.findUserByUsername(this.profileUsername);
			this.dbProfileUser = this.userService.findUserByUsername(this.profileUsername);
			if (ldapProfileUser == null) {
				Label lblMissing = new Label("Sorry, we could not find the person you were looking for.");
				this.addComponent(lblMissing);
			} else {
				this.header = new CssLayout();
				CssLayout headerButtonList = this.createHeaderBtnList();

				Image image = new Image(null, new FileResource(
						new File(UserConstants.PROFILE_PICTURE_LOCATION + this.dbProfileUser.getImageName())));
				image.addStyleName(ThemeConstants.BORDERED_IMAGE);
				Label lblName = new Label(ldapProfileUser.getFullName());
				lblName.addStyleName(ThemeConstants.BLUE_TEXT_H1);
				lblName.addStyleName(ThemeConstants.RESPONSIVE_FONT);
				StringBuilder strGroups = new StringBuilder();
				this.ldapService.findGroupsByUserId(ldapProfileUser.getId())
						.forEach(group -> strGroups.append(group.getGroupName() + ", "));
				Label lblGroups;
				if (strGroups.length() != 0) {
					lblGroups = new Label(strGroups.substring(0, strGroups.length() - 2));
				} else {
					lblGroups = new Label("Without team");
				}
				lblGroups.addStyleName(ThemeConstants.BLUE_TEXT_H3);
//				lblGroups.addStyleName(ThemeConstants.RESPONSIVE_FONT);
				this.header.setId("profileHeader");
				this.header.addComponent(image);
				this.header.addComponent(lblName);
				this.header.addComponent(lblGroups);
				this.header.addComponent(headerButtonList);
				FormLayout form = new FormLayout();
				form.addStyleName(ThemeConstants.BORDERED);
				form.setMargin(false);
				form.setId("profileBody");
				form.setSizeFull();

				this.addComponent(this.header);
				this.addComponent(form);

				this.setComponentAlignment(this.header, Alignment.MIDDLE_CENTER);

				this.setExpandRatio(this.header, 20);
				this.setExpandRatio(form, 80);

				TextField tfName = new TextField("Name:", this.checkandSetIfNull(ldapProfileUser.getFullName()));
				TextField tfMobile = new TextField("Mobile:", this.checkandSetIfNull(ldapProfileUser.getMobile()));
				TextField tfMail = new TextField("E-Mail:", this.checkandSetIfNull(ldapProfileUser.getMail()));

				form.addComponent(tfName);
				form.addComponent(tfMobile);
				form.addComponent(tfMail);

				for (Component component : form) {
					if (component.getClass().equals(TextField.class)) {
						component.addStyleName(ThemeConstants.BLUE_TEXT);
//						component.setWidth("100px");
						if (!this.profileUsername.equals(ldapSessionUsername)) {
							component.setEnabled(false);
						}
					}
				}
			}
		}

	}

	private CssLayout createHeaderBtnList() {
		this.headerButtonList = new CssLayout();

		if (this.dbSessionUser.getId() != this.dbProfileUser.getId()) {
			Button btnFriendRequest = new Button();
			btnFriendRequest.addStyleName(ThemeConstants.BLUE_TEXT);
			Button btnMessage = new Button("Message");
			btnMessage.addStyleName(ThemeConstants.BLUE_TEXT);
			btnMessage.addClickListener(this::messageClickListener);

			this.headerButtonList.addComponent(btnFriendRequest);
			this.headerButtonList.addComponent(btnMessage);
			if (this.friendShipService.findOne(this.dbProfileUser.getId(), this.dbSessionUser.getId()) != null) {
				btnFriendRequest.setCaption(StringConstants.BTN_REMOVE_FRIEND);
			} else if (!this.friendRequestService.IsAlreadyRequested(this.dbSessionUser.getId(),
					this.dbProfileUser.getId())
					&& !this.friendRequestService.IsAlreadyRequested(this.dbProfileUser.getId(),
							this.dbSessionUser.getId())) {
				btnFriendRequest.setCaption(StringConstants.BTN_FRIEND_REQUEST);
			} else if (this.friendRequestService.IsAlreadyRequested(this.dbProfileUser.getId(),
					this.dbSessionUser.getId())) {
				btnFriendRequest.setCaption(StringConstants.BTN_ACCEPT_REQUEST);
				Button btnDeclineRequest = new Button(StringConstants.BTN_DECLINE_REQUEST);
				btnDeclineRequest.addStyleName(ThemeConstants.BLUE_TEXT);
				btnDeclineRequest.addClickListener(this::declineRequestClickListener);
				this.headerButtonList.addComponent(btnDeclineRequest);
			} else {
				btnFriendRequest.setCaption(StringConstants.BTN_CANCEL_REQUEST);
			}

			btnFriendRequest.addClickListener(this::friendRequestClickListener);

		}

		return this.headerButtonList;
	}

	private String checkandSetIfNull(String text) {
		if (text == null) {
			text = "";
		}
		return text;
	}

	private void friendRequestClickListener(Button.ClickEvent event) {
		if (event.getButton().getCaption().equals(StringConstants.BTN_REMOVE_FRIEND)) {
			this.friendShipService.deleteFriendship(this.dbProfileUser.getId(), this.dbSessionUser.getId());
			this.header.replaceComponent(this.headerButtonList, this.createHeaderBtnList());
		} else if (event.getButton().getCaption().equals(StringConstants.BTN_FRIEND_REQUEST)) {
			LdapUser ldapProfile = this.ldapService.findUserByUsername(this.profileUsername);
			FriendRequest fr = new FriendRequest();
			fr.setRequestorId(this.dbSessionUser.getId());
			fr.setRequestedId(this.dbProfileUser.getId());
			this.friendRequestService.saveFriendRequest(fr);
			event.getButton().setCaption(StringConstants.BTN_CANCEL_REQUEST);
			MessageBox.createInfo().withOkButton().withCaption("Request sent")
					.withMessage("Request has been sent to " + ldapProfile.getFullName()).open();
		} else if (event.getButton().getCaption().equals(StringConstants.BTN_ACCEPT_REQUEST)) {
			this.friendRequestService.deleteFriendRequest(this.dbProfileUser.getId(), this.dbSessionUser.getId());
			this.friendShipService.saveFriendship(this.dbProfileUser.getId(), this.dbSessionUser.getId());
			this.header.replaceComponent(this.headerButtonList, this.createHeaderBtnList());
		} else {
			this.friendRequestService.deleteFriendRequest(this.dbSessionUser.getId(), this.dbProfileUser.getId());
			event.getButton().setCaption(StringConstants.BTN_FRIEND_REQUEST);
		}

	}

	private void declineRequestClickListener(Button.ClickEvent event) {
		this.friendRequestService.deleteFriendRequest(this.dbProfileUser.getId(), this.dbSessionUser.getId());
		this.header.replaceComponent(this.headerButtonList, this.createHeaderBtnList());
	}

	private void messageClickListener(Button.ClickEvent event) {
		this.getUI().getNavigator().navigateTo(MessagesView.NAME + "/" + this.profileUsername);
	}

}
