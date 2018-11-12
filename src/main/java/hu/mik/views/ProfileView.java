package hu.mik.views;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.FriendRequest;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.constants.StringConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.enums.Texts;
import hu.mik.factories.NewsPanelFactory;
import hu.mik.services.FriendRequestService;
import hu.mik.services.FriendshipService;
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
	private FriendRequestService friendRequestService;
	@Autowired
	private FriendshipService friendShipService;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private NewsPanelFactory newsPanelFactory;

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

				Image image = this.socialProfileUser.getDbUser().getVaadinImage();
				image.addStyleName(ThemeConstants.BORDERED_IMAGE);
				Label lblName = new Label(this.socialProfileUser.getLdapUser().getCommonName());
				lblName.addStyleName(ThemeConstants.BLUE_TEXT_H1);
				lblName.addStyleName(ThemeConstants.RESPONSIVE_FONT);
				StringBuilder strGroups = new StringBuilder();
				this.socialProfileUser.getLdapUser().getLdapGroups()
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
				TabSheet tabSheet = this.createTabSheet();
				this.addComponent(this.header);
				this.addComponent(tabSheet);
				this.setComponentAlignment(this.header, Alignment.MIDDLE_CENTER);
				this.setExpandRatio(tabSheet, 1f);
				this.setSizeFull();
			}
		}

	}

	private FormLayout createFormLayout() {
		FormLayout form = new FormLayout();
		form.setMargin(false);
		form.setId("profileBody");
		form.setSizeFull();

		Label lblTitle = new Label(this.checkandSetIfNull(this.socialProfileUser.getLdapUser().getTitle()));
		lblTitle.setCaption(VaadinIcons.STAR.getHtml() + " Title:");
		lblTitle.setCaptionAsHtml(true);
		Label lblMobile = new Label(this.checkandSetIfNull(this.socialProfileUser.getLdapUser().getMobile()));
		lblMobile.setCaption(VaadinIcons.PHONE.getHtml() + " Mobile:");
		lblMobile.setCaptionAsHtml(true);
		Label lblMail = new Label(this.checkandSetIfNull(this.socialProfileUser.getLdapUser().getMail()));
		lblMail.setCaption(VaadinIcons.ENVELOPE.getHtml() + " E-Mail:");
		lblMail.setCaptionAsHtml(true);
		Label lblRoom = new Label(this.checkandSetIfNull(this.socialProfileUser.getLdapUser().getRoomNumber()));
		lblRoom.setCaption(VaadinIcons.HOME.getHtml() + " Room:");
		lblRoom.setCaptionAsHtml(true);
		Label lblDeptNo = new Label(this.checkandSetIfNull(this.socialProfileUser.getLdapUser().getDepartmentNumber()));
		lblDeptNo.setCaption(VaadinIcons.OFFICE.getHtml() + " Department:");
		lblDeptNo.setCaptionAsHtml(true);

		form.addComponents(lblTitle, lblMobile, lblMail, lblRoom, lblDeptNo);

		for (Component component : form) {
			if (component.getClass().equals(Label.class)) {
				component.addStyleName(ThemeConstants.BLUE_TEXT);
				component.addStyleName(ThemeConstants.RESPONSIVE_FONT);
			}
		}
		return form;
	}

	private TabSheet createTabSheet() {
		TabSheet tabSheet = new TabSheet();
		tabSheet.addTab(this.createFormLayout(), "Personal information");
		tabSheet.setSizeFull();
		tabSheet.addTab(this.newsPanelFactory.getUserInstance(this.socialProfileUser.getDbUser()), "News feed");
		return tabSheet;

	}

	private CssLayout createHeaderBtnList() {
		this.headerButtonList = new CssLayout();
		this.headerButtonList.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		if (!this.socialSessionUser.getDbUser().getId().equals(this.socialProfileUser.getDbUser().getId())) {
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
			} else if (!this.friendRequestService.IsAlreadyRequested(this.socialSessionUser.getDbUser(),
					this.socialProfileUser.getDbUser())
					&& !this.friendRequestService.IsAlreadyRequested(this.socialProfileUser.getDbUser(),
							this.socialSessionUser.getDbUser())) {
				btnFriendRequest.setCaption(StringConstants.BTN_FRIEND_REQUEST);
			} else if (this.friendRequestService.IsAlreadyRequested(this.socialProfileUser.getDbUser(),
					this.socialSessionUser.getDbUser())) {
				btnFriendRequest.setCaption(StringConstants.BTN_ACCEPT_REQUEST);
				Button btnDeclineRequest = new Button(StringConstants.BTN_DECLINE_REQUEST);
				btnDeclineRequest.addStyleName(ThemeConstants.BLUE_TEXT);
				btnDeclineRequest.addClickListener(this::declineRequestClickListener);
				this.headerButtonList.addComponent(btnDeclineRequest, 1);
			} else {
				btnFriendRequest.setCaption(StringConstants.BTN_CANCEL_REQUEST);
			}

			btnFriendRequest.addClickListener(this::friendRequestClickListener);

		} else {
			this.headerButtonList.setVisible(false);
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
			fr.setRequestor(this.socialSessionUser.getDbUser());
			fr.setRequested(this.socialProfileUser.getDbUser());
			this.friendRequestService.saveFriendRequest(fr);
			event.getButton().setCaption(StringConstants.BTN_CANCEL_REQUEST);
			Notification.show(Texts.FRIEND_REQUEST_NOTIFICATION.getText(),
					this.socialProfileUser.getLdapUser().getCommonName(), Type.TRAY_NOTIFICATION);
		} else if (event.getButton().getCaption().equals(StringConstants.BTN_ACCEPT_REQUEST)) {
			this.friendRequestService.deleteFriendRequest(this.socialProfileUser.getDbUser(),
					this.socialSessionUser.getDbUser());
			this.friendShipService.saveFriendship(this.socialProfileUser.getDbUser(),
					this.socialSessionUser.getDbUser());
			this.header.replaceComponent(this.headerButtonList, this.createHeaderBtnList());
		} else {
			this.friendRequestService.deleteFriendRequest(this.socialSessionUser.getDbUser(),
					this.socialProfileUser.getDbUser());
			event.getButton().setCaption(StringConstants.BTN_FRIEND_REQUEST);
		}

	}

	private void declineRequestClickListener(Button.ClickEvent event) {
		this.friendRequestService.deleteFriendRequest(this.socialProfileUser.getDbUser(),
				this.socialSessionUser.getDbUser());
		this.header.replaceComponent(this.headerButtonList, this.createHeaderBtnList());
	}

	private void messageClickListener(Button.ClickEvent event) {
		this.getUI().getNavigator().navigateTo(MessagesView.NAME + "/" + this.socialProfileUser.getDbUser().getId());
	}

}
