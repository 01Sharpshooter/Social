package hu.mik.views;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.FriendRequest;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.constants.StringConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.enums.Texts;
import hu.mik.services.FriendRequestService;
import hu.mik.services.FriendshipService;
import hu.mik.services.LdapService;
import hu.mik.utils.ProfileImageHelper;
import hu.mik.utils.UserUtils;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name = ProfileView.NAME)
public class ProfileView extends VerticalLayout implements View {
	public static final String NAME = "profile";

	private SocialUserWrapper socialSessionUser;
	private SocialUserWrapper socialProfileUser;
	private CssLayout header;
	private CssLayout headerButtonList;

	@Autowired
	private LdapService ldapService;
	@Autowired
	private FriendRequestService friendRequestService;
	@Autowired
	private FriendshipService friendShipService;
	@Autowired
	private UserUtils userUtils;

	@Override
	public void enter(ViewChangeEvent event) {
		if (event.getParameters().length() > 0) {
			this.socialSessionUser = this.userUtils.getLoggedInUser();
			String parameters[] = event.getParameters().split("/");
			String profileUsername = parameters[0];
			this.socialProfileUser = this.userUtils.initSocialUser(profileUsername);
			if (this.socialProfileUser.getLdapUser() == null) {
				Label lblMissing = new Label(Texts.NO_USER_FOUND_FROM_SEARCH.getText());
				this.addComponent(lblMissing);
			} else {
				this.header = new CssLayout();
				CssLayout headerButtonList = this.createHeaderBtnList();

				Image image = new Image(null, new FileResource(
						ProfileImageHelper.loadUserImage(this.socialProfileUser.getDbUser().getImageName())));
				image.addStyleName(ThemeConstants.BORDERED_IMAGE);
				Label lblName = new Label(this.socialProfileUser.getLdapUser().getFullName());
				lblName.addStyleName(ThemeConstants.BLUE_TEXT_H1);
				lblName.addStyleName(ThemeConstants.RESPONSIVE_FONT);
				StringBuilder strGroups = new StringBuilder();
				this.ldapService.findGroupsByUserId(this.socialProfileUser.getLdapUser().getId())
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

				TextField tfName = new TextField("Name:",
						this.checkandSetIfNull(this.socialProfileUser.getLdapUser().getFullName()));
				TextField tfMobile = new TextField("Mobile:",
						this.checkandSetIfNull(this.socialProfileUser.getLdapUser().getMobile()));
				TextField tfMail = new TextField("E-Mail:",
						this.checkandSetIfNull(this.socialProfileUser.getLdapUser().getMail()));

				form.addComponent(tfName);
				form.addComponent(tfMobile);
				form.addComponent(tfMail);

				for (Component component : form) {
					if (component.getClass().equals(TextField.class)) {
						component.addStyleName(ThemeConstants.BLUE_TEXT);
						if (!this.socialProfileUser.getUsername()
								.equals(this.socialSessionUser.getLdapUser().getUsername())) {
							component.setEnabled(false);
						}
					}
				}
			}
		}

	}

	private CssLayout createHeaderBtnList() {
		this.headerButtonList = new CssLayout();

		if (this.socialSessionUser.getDbUser().getId() != this.socialProfileUser.getDbUser().getId()) {
			Button btnFriendRequest = new Button();
			btnFriendRequest.addStyleName(ThemeConstants.BLUE_TEXT);
			Button btnMessage = new Button("Message");
			btnMessage.addStyleName(ThemeConstants.BLUE_TEXT);
			btnMessage.addClickListener(this::messageClickListener);

			this.headerButtonList.addComponent(btnFriendRequest);
			this.headerButtonList.addComponent(btnMessage);
			if (this.friendShipService.findOne(this.socialProfileUser.getDbUser().getId(),
					this.socialSessionUser.getDbUser().getId()) != null) {
				btnFriendRequest.setCaption(StringConstants.BTN_REMOVE_FRIEND);
			} else if (!this.friendRequestService.IsAlreadyRequested(this.socialSessionUser.getDbUser().getId(),
					this.socialProfileUser.getDbUser().getId())
					&& !this.friendRequestService.IsAlreadyRequested(this.socialProfileUser.getDbUser().getId(),
							this.socialSessionUser.getDbUser().getId())) {
				btnFriendRequest.setCaption(StringConstants.BTN_FRIEND_REQUEST);
			} else if (this.friendRequestService.IsAlreadyRequested(this.socialProfileUser.getDbUser().getId(),
					this.socialSessionUser.getDbUser().getId())) {
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
			this.friendShipService.deleteFriendship(this.socialProfileUser.getDbUser().getId(),
					this.socialSessionUser.getDbUser().getId());
			this.header.replaceComponent(this.headerButtonList, this.createHeaderBtnList());
		} else if (event.getButton().getCaption().equals(StringConstants.BTN_FRIEND_REQUEST)) {
			FriendRequest fr = new FriendRequest();
			fr.setRequestorId(this.socialSessionUser.getDbUser().getId());
			fr.setRequestedId(this.socialProfileUser.getDbUser().getId());
			this.friendRequestService.saveFriendRequest(fr);
			event.getButton().setCaption(StringConstants.BTN_CANCEL_REQUEST);
			Notification.show(Texts.FRIEND_REQUEST_NOTIFICATION.getText(),
					this.socialProfileUser.getLdapUser().getFullName(), Type.TRAY_NOTIFICATION);
		} else if (event.getButton().getCaption().equals(StringConstants.BTN_ACCEPT_REQUEST)) {
			this.friendRequestService.deleteFriendRequest(this.socialProfileUser.getDbUser().getId(),
					this.socialSessionUser.getDbUser().getId());
			this.friendShipService.saveFriendship(this.socialProfileUser.getDbUser().getId(),
					this.socialSessionUser.getDbUser().getId());
			this.header.replaceComponent(this.headerButtonList, this.createHeaderBtnList());
		} else {
			this.friendRequestService.deleteFriendRequest(this.socialSessionUser.getDbUser().getId(),
					this.socialProfileUser.getDbUser().getId());
			event.getButton().setCaption(StringConstants.BTN_FRIEND_REQUEST);
		}

	}

	private void declineRequestClickListener(Button.ClickEvent event) {
		this.friendRequestService.deleteFriendRequest(this.socialProfileUser.getDbUser().getId(),
				this.socialSessionUser.getDbUser().getId());
		this.header.replaceComponent(this.headerButtonList, this.createHeaderBtnList());
	}

	private void messageClickListener(Button.ClickEvent event) {
		this.getUI().getNavigator().navigateTo(MessagesView.NAME + "/" + this.socialProfileUser.getDbUser().getId());
	}

}
