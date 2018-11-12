package hu.mik.components;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import hu.mik.beans.SocialUserWrapper;
import hu.mik.constants.SystemConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.ui.MainUI;
import hu.mik.views.AdminView;
import hu.mik.views.ContactListView;
import hu.mik.views.MainView;
import hu.mik.views.MessagesView;
import hu.mik.views.PictureUploadView;
import hu.mik.views.ProfileView;
import hu.mik.views.UserListView;

@SuppressWarnings("serial")
public class NavigationBar extends CssLayout {
	private CssLayout naviPerson;
	private CssLayout naviItemList;
	private Image naviBarImage;

	private SocialUserWrapper socialUser;

	private boolean dropDownShown;

	private TextField nameSearchTf;
	private Label lblMessages;

	public NavigationBar(SocialUserWrapper socialUser, Long unseenConversationCount) {
		this.socialUser = socialUser;
		this.addLayoutClickListener(this::naviBarClickListener);
		this.setId("navigationBar");
		this.createNaviBarPerson();
		this.createNaviBarLabelList(unseenConversationCount);
		this.createNaviSearchField();
		this.createNaviMenuIcon();
	}

	public void removeActiveLabelStyle() {
		this.naviItemList.forEach(c -> c.removeStyleName(ThemeConstants.NAVI_ITEM_ACTIVE));
	}

	private void createNaviBarPerson() {
		this.naviPerson = new CssLayout();
		this.naviPerson.addStyleName(ThemeConstants.NAVI_PERSON);
		this.naviBarImage = this.socialUser.getDbUser().getVaadinImage();
		this.naviBarImage.setId("profilePicture");
		this.naviBarImage.addStyleName(ThemeConstants.BORDERED_IMAGE);
		this.naviBarImage.addStyleName(ThemeConstants.NAVIGATION_BAR_ICON);
		this.naviBarImage.addClickListener(this::profileImageClickListener);
		this.addComponent(this.naviBarImage);
		Label name = new Label();
		name.setValue(this.socialUser.getLdapUser().getCommonName());
		name.setId("username");
		this.naviPerson.addComponents(this.naviBarImage, name);
		this.addComponent(this.naviPerson);
	}

	private void createNaviBarLabelList(Long unseenConversationCount) {
		this.naviItemList = new CssLayout();
		Label lblMain = new Label(VaadinIcons.HOME.getHtml() + "<span class=\"folding\">Main</span>", ContentMode.HTML);
		lblMain.addStyleName(ThemeConstants.NAVI_ITEM_ACTIVE);
		this.lblMessages = new Label(VaadinIcons.CHAT.getHtml() + "<span class=\"folding\">Messages ("
				+ unseenConversationCount + ")</span>", ContentMode.HTML);

		Label lblContacts = new Label(VaadinIcons.USERS.getHtml() + "<span class=\"folding\">Contacts</span>",
				ContentMode.HTML);

		Label lblLogout = new Label(VaadinIcons.EXIT.getHtml() + "<span class=\"folding\">Logout</span>",
				ContentMode.HTML);

		this.naviItemList.addComponents(lblMain, this.lblMessages, lblContacts, lblLogout);
		this.naviItemList.addStyleName(ThemeConstants.NAVI_ITEM_LIST);
		this.addComponent(this.naviItemList);
	}

	private void createNaviMenuIcon() {
		Image naviMenuIcon = new Image(null, new ThemeResource(SystemConstants.SYSTEM_IMAGE_MENU_ICON));
		naviMenuIcon.addStyleName(ThemeConstants.NAVIGATION_BAR_ICON);
		naviMenuIcon.setId("menuIcon");
		naviMenuIcon.addClickListener(e -> this.changeDropDownVisibility());
		this.addComponent(naviMenuIcon);
	}

	private void createNaviSearchField() {
		CssLayout naviSearchField = new CssLayout();
		naviSearchField.addStyleName(ThemeConstants.NAVI_SEARCH_FIELD);
		this.nameSearchTf = new TextField();
		this.nameSearchTf.setStyleName(ValoTheme.BUTTON_SMALL);
		this.nameSearchTf.setPlaceholder("Search for a user...");
		Button namesearchButton = new Button("Search", this::nameSearchClickListener);
		namesearchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		namesearchButton.addStyleName(ThemeConstants.BLUE_TEXT);
		naviSearchField.addComponents(this.nameSearchTf, namesearchButton);
		this.addComponent(naviSearchField);

	}

	public void refreshImage(Resource resource) {
		((Image) this.naviPerson.getComponent(0)).setSource(resource);
	}

	public void refreshUnseenConversationNumber(Long unseenNumber) {
		this.naviItemList.forEach(e -> {
			if (e instanceof Label && ((Label) e).getValue().equals(this.lblMessages.getValue())) {
				((Label) e).setValue(
						VaadinIcons.CHAT.getHtml() + "<span class=\"folding\">Messages (" + unseenNumber + ")</span>");
			}
		});
	}

	private void nameSearchClickListener(Button.ClickEvent event) {
		if (!this.nameSearchTf.isEmpty()) {
			UI.getCurrent().getNavigator().navigateTo(UserListView.NAME + "/" + this.nameSearchTf.getValue());
			this.nameSearchTf.clear();
			this.removeActiveLabelStyle();
		}
	}

	private void profileImageClickListener(com.vaadin.event.MouseEvents.ClickEvent event) {
		this.removeActiveLabelStyle();
		UI.getCurrent().getNavigator().navigateTo(PictureUploadView.NAME);
	}

	private void changeDropDownVisibility() {
		if (!this.dropDownShown) {
			this.addStyleName(ThemeConstants.SHOW_DROPDOWN);
		} else {
			this.removeStyleName(ThemeConstants.SHOW_DROPDOWN);
		}
		this.dropDownShown = !this.dropDownShown;
	}

	private void naviBarClickListener(LayoutClickEvent event) {
		if (event.getClickedComponent() != null && event.getClickedComponent() instanceof Label) {
			Label lblClicked = (Label) event.getClickedComponent();
			this.removeActiveLabelStyle();
			lblClicked.addStyleName(ThemeConstants.NAVI_ITEM_ACTIVE);
			this.changeDropDownVisibility();
			String label = lblClicked.getValue().replaceFirst("<span.*</span><", "<").replaceFirst("<.?span>", "")
					.replaceFirst("<span.*>", "");
			switch (label) {
			case "Main":
				UI.getCurrent().getNavigator().navigateTo(MainView.NAME);
				break;
			case "Contacts":
				UI.getCurrent().getNavigator()
						.navigateTo(ContactListView.NAME + "/" + this.socialUser.getDbUser().getUsername());
				break;
			case "Logout":
				UI.getCurrent().getPage().setLocation("/logout");
				MainUI.getOnlineUsers().remove(this.socialUser.getDbUser());
			case "Admin":
				UI.getCurrent().getNavigator().navigateTo(AdminView.NAME);
				break;
			default:
				if (label.contains("Messages")) {
					UI.getCurrent().getNavigator().navigateTo(MessagesView.NAME);
					break;
				} else {
					this.removeActiveLabelStyle();
					UI.getCurrent().getNavigator()
							.navigateTo(ProfileView.NAME + "/" + this.socialUser.getDbUser().getUsername());
					break;
				}
			}
		}
	}
}
