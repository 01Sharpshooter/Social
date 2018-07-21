package hu.mik.views;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.LdapUser;
import hu.mik.beans.Message;
import hu.mik.beans.User;
import hu.mik.components.UserListLayout;
import hu.mik.constants.SystemConstants;
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.FriendshipService;
import hu.mik.services.LdapService;
import hu.mik.services.MessageBroadcastService;
import hu.mik.services.MessageService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name = MessagesView.NAME)
public class MessagesView extends Panel implements View {
	public static final String NAME = "messages";

	@Autowired
	MessageService messageService;

	@Autowired
	UserService userService;

	@Autowired
	FriendshipService friendshipService;

	@Autowired
	UserListLayout userListLayout;
	@Autowired
	LdapService ldapService;

	private List<User> friendList = new ArrayList<>();
	private Panel messagesPanel = new Panel();
	private VerticalLayout messagesLayout;
	private Message message;
	private TextField textField;
	private Button sendButton;
	private int scroll = 100;
	private int scrollGrowth = 50;
	private int senderId;
	private int receiverId;
	private User sender;
	private User receiver;
	private HorizontalLayout textWriter;
	private List<Message> messagesList;
	private CssLayout userList;

	private int messageNumberAtOnce = 20;

	private CssLayout base;

	@Override
	public void enter(ViewChangeEvent event) {
		WrappedSession session = VaadinService.getCurrentRequest().getWrappedSession();
		String username = (String) session.getAttribute(SystemConstants.SESSION_ATTRIBUTE_LDAP_USER);
		this.sender = this.userService.findUserByUsername(username);

		this.senderId = this.sender.getId();
		this.friendshipService.findAllByUserId(this.senderId)
				.forEach(friendShip -> this.friendList.add(this.userService.findUserById(friendShip.getFriendId())));
		this.addStyleName(ThemeConstants.BORDERED);
		this.setSizeFull();
//		this.container.setMargin(false);
//		this.container.setSpacing(false);
		this.createContent(event);

	}

	private void createContent(ViewChangeEvent event) {
		this.createBase();

		this.createTextFieldSearch();

		this.createSearchComboBox();

		this.createUserList();

		this.createTextWriter();

		this.createMessagesPanel();

		this.createChat();

//		addComponent(base);

		this.fillUserList();

		String parameters[] = event.getParameters().split("/");
		if (parameters.length > 0) {
			this.receiver = this.userService.findUserByUsername(parameters[0]);
			if (this.receiver != null) {
				this.receiverId = this.receiver.getId();
				boolean exists = false;
				for (Component userDiv : this.userList) {
					if (String.valueOf(this.receiverId).equals(userDiv.getId())) {
						exists = true;
						this.userList.removeComponent(userDiv);
						this.userList.addComponent(userDiv, 0);
						exists = true;
						break;
					}
				}
				if (exists) {
					this.userListSelectionChange(this.userList.getComponent(0));
				} else {
					Message lastMessage = this.messageService.findLastByUserIDs(this.receiverId, this.senderId);
					String messageString;
					if (lastMessage != null) {
						messageString = lastMessage.getMessage();
					} else {
						messageString = "";
					}
					CssLayout newDiv = this.createUserDiv(this.receiver, messageString);
					this.userList.addComponent(newDiv, 0);
					this.userListSelectionChange(newDiv);
				}
				this.fillChat(this.userList.getComponent(0));
			}
		}
	}

	private void createMessagesPanel() {
		this.messagesLayout = new VerticalLayout();
		this.messagesLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
		this.messagesPanel.setContent(this.messagesLayout);
		this.messagesPanel.addStyleName(ThemeConstants.BORDERED);
		this.messagesPanel.setSizeFull();
	}

	private void createUserList() {
		this.userList = new CssLayout();
		this.userList.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		this.userList.setId("latestMessagesLayout");
		this.base.addComponent(this.userList);
	}

	private void createSearchComboBox() {
		List<String> searchList = new ArrayList<>();
		this.ldapService.findAllUsers().forEach(user -> searchList.add(user.getFullName()));
		ComboBox<String> cb = new ComboBox<>("test", searchList);
		cb.setWidth("100%");
//		base.addComponent(cb);
	}

	private void createTextFieldSearch() {
		TextField tfSearch = new TextField("Search:");
		tfSearch.addValueChangeListener(this::searchValueChangeListener);
		this.base.addComponent(tfSearch);
	}

	private void createBase() {
		this.base = new CssLayout();
		this.setContent(this.base);
		this.base.setId("messageBase");
		this.base.setSizeFull();
	}

	private List<CssLayout> fillUserList() {
		List<Message> latestMessages = this.messageService.findLastestMessagesOfUser(100, this.senderId);
		List<Integer> alreadyUsedIds = new ArrayList<>();
		List<CssLayout> userDivs = new ArrayList<>();
		CssLayout userDiv;
		User user;

		for (Message message : latestMessages) {
			boolean amItheSender = message.getSenderId() == this.senderId;
			if (amItheSender) {
				if (!alreadyUsedIds.contains(message.getReceiverId())) {
					alreadyUsedIds.add(message.getReceiverId());
					user = this.userService.findUserById(message.getReceiverId());
					userDiv = this.createUserDiv(user, message.getMessage());
					this.userList.addComponent(userDiv);
					userDivs.add(userDiv);
				}
			} else {
				if (!alreadyUsedIds.contains(message.getSenderId())) {
					alreadyUsedIds.add(message.getSenderId());
					user = this.userService.findUserById(message.getSenderId());
					userDiv = this.createUserDiv(user, message.getMessage());
					this.userList.addComponent(userDiv);
					userDivs.add(userDiv);
				}
			}
		}
		return userDivs;
	}

	private CssLayout createUserDiv(User user, String lastMessage) {
		CssLayout userDiv = new CssLayout();
		LdapUser ldapUser = this.ldapService.findUserByUsername(user.getUsername());
		Image image = new Image(null,
				new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION + user.getImageName())));
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		userDiv.addComponent(image);
		userDiv.setId(user.getId().toString());
		userDiv.addLayoutClickListener(this::userDivClickListener);
		Label lblName = new Label(ldapUser.getFullName() + "</br><span id=\"message\">" + lastMessage + "</span>",
				ContentMode.HTML);
		userDiv.addComponent(lblName);

		return userDiv;

	}

	private CssLayout createUserDiv(LdapUser user, String lastMessage) {
		CssLayout userDiv = new CssLayout();
		User dbUser = this.userService.findUserByUsername(user.getUsername());
		Image image = new Image(null,
				new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION + dbUser.getImageName())));
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		userDiv.addComponent(image);
		userDiv.setId(dbUser.getId().toString());
		userDiv.addLayoutClickListener(this::userDivClickListener);
		Label lblName = new Label(user.getFullName() + "</br><span id=\"message\">" + lastMessage + "</span>",
				ContentMode.HTML);
		userDiv.addComponent(lblName);

		return userDiv;

	}

	private CssLayout changeUserDivMessage(CssLayout userDiv, String lastMessage) {
		String name;
		String[] test = ((Label) userDiv.getComponent(1)).getValue().split("<");
		name = test[0];
		userDiv.removeComponent(userDiv.getComponent(1));
		Label lblName = new Label(name + "</br><span id=\"message\">" + lastMessage + "</span>", ContentMode.HTML);
		userDiv.addComponent(lblName);
		return userDiv;
	}

	private void createChat() {
		CssLayout chat = new CssLayout();
//		chat.setCaption(receiver.getUsername());
//		chat.setWidth("75%");
//		chat.setHeight("100%");
//		chat.setMargin(false);
		chat.addComponent(this.messagesPanel);
		chat.addComponent(this.textWriter);
//		chat.setExpandRatio(messagesPanel, 9);
//		chat.setExpandRatio(textWriter, 1);
		chat.setId("chatLayout");
		this.base.addComponent(chat);
	}

	private void createTextWriter() {
		this.textWriter = new HorizontalLayout();
		this.textField = new TextField();
		this.textField.setWidth("100%");
		this.sendButton = new Button("Send", this::sendButtonClicked);
		this.sendButton.addStyleName(ThemeConstants.BLUE_TEXT);
		this.sendButton.setClickShortcut(KeyCode.ENTER);
		this.sendButton.setSizeUndefined();
		this.textWriter.addComponent(this.textField);
		this.textWriter.addComponent(this.sendButton);
		this.textWriter.setExpandRatio(this.textField, 7);
		this.textWriter.setExpandRatio(this.sendButton, 3);
		this.textWriter.setSizeFull();
		this.textWriter.setEnabled(false);
	}

	private void userDivClickListener(LayoutClickEvent event) {
//		messagesLayout.setCaption(event.getButton().getCaption());
		this.fillChat(event.getComponent());
	}

	private void fillChat(Component userDiv) {
		this.textWriter.setEnabled(true);
		this.messagesLayout.removeAllComponents();
		this.receiverId = Integer.parseInt(userDiv.getId());
		this.receiver = this.userService.findUserById(this.receiverId);
		this.messagesList = this.messageService.findAllByUserIDs(this.messageNumberAtOnce, this.senderId,
				this.receiverId);
		this.fillMessages();
		this.messagesPanel.setScrollTop(this.scroll);
		MessageBroadcastService.register((MainUI) this.getUI(), this.sender.getUsername());
		this.userListSelectionChange(userDiv);

	}

	public void fillMessages() {
		this.messagesList = this.messageService.findAllByUserIDs(20, this.senderId, this.receiverId);
		if (this.messagesList != null) {
			if (!this.messagesList.isEmpty()) {
				this.messagesLayout.removeAllComponents();
				for (int i = this.messagesList.size() - 1; i >= 0; i--) {
					this.message = this.messagesList.get(i);
					if (this.message.getSenderId() == this.senderId) {
						this.scroll += this.scrollGrowth;
						Label newMessage = new Label(
								"<span id=\"messageSpan\">" + this.message.getMessage() + "</span>", ContentMode.HTML);
						newMessage.setHeight(this.messagesPanel.getHeight() / 6, this.messagesPanel.getHeightUnits());
						newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
						newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_SENT);
						this.messagesLayout.addComponent(newMessage);
//						panel.setScrollTop(scroll);
					} else {
						this.scroll += this.scrollGrowth;
						Label newMessage = new Label(
								"<span id=\"messageSpan\">" + this.message.getMessage() + "</span>", ContentMode.HTML);
						newMessage.setHeight(this.messagesPanel.getHeight() / 6, this.messagesPanel.getHeightUnits());
						newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
						newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_RECEIVED);
						this.messagesLayout.addComponent(newMessage);
						this.messagesLayout.setComponentAlignment(newMessage, Alignment.MIDDLE_LEFT);
//						panel.setScrollTop(scroll);
					}
				}
			}
		}
	}

	private void sendButtonClicked(Button.ClickEvent event) {
		this.sendMessage();

	}

	private void sendMessage() {
		this.message = new Message();
		this.message.setMessage(this.textField.getValue());

		if (this.message.getMessage().length() != 0) {
			this.message.setSenderId(this.senderId);
			this.message.setReceiverId(this.receiverId);
			java.util.Date date = new java.util.Date();
			this.message.setTime(new Timestamp(date.getTime()));
			this.textField.clear();
			this.messageService.saveMessage(this.message);
			MessageBroadcastService.sendMessage(this.message.getMessage(), this.senderId, this.receiver.getUsername());
//			messagesList.add(message);
			this.scroll += this.scrollGrowth;
			Label newMessage = new Label("<span id=\"messageSpan\">" + this.message.getMessage() + "</span>",
					ContentMode.HTML);
			newMessage.setHeight(this.messagesPanel.getHeight() / 6, this.messagesPanel.getHeightUnits());
			newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_SENT);
			this.messagesLayout.addComponent(newMessage);
			this.messagesPanel.setScrollTop(this.scroll);

			for (Component userDiv : this.userList) {
				if (userDiv.getId().equals(String.valueOf(this.receiverId))) {
					this.userList.removeComponent(userDiv);
					this.changeUserDivMessage((CssLayout) userDiv, this.message.getMessage());
					this.userList.addComponent(userDiv, 0);
					this.userListSelectionChange(userDiv);
					break;
				}
			}
		}

	}

	public void receiveMessage(String message2, int senderId) {
		if (senderId == this.receiverId) {
			Label newMessage = new Label("<span id=\"messageSpan\">" + message2 + "</span>", ContentMode.HTML);
			newMessage.setHeight(this.messagesPanel.getHeight() / 6, this.messagesPanel.getHeightUnits());
			newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_RECEIVED);
			this.messagesLayout.addComponent(newMessage);
			this.messagesLayout.setComponentAlignment(newMessage, Alignment.MIDDLE_LEFT);
			this.messagesPanel.setScrollTop(this.scroll);

		}
		boolean exists = false;
		for (Component userDiv : this.userList) {
			if (userDiv.getId().equals(String.valueOf(senderId))) {
				this.userList.removeComponent(userDiv);
				this.changeUserDivMessage((CssLayout) userDiv, message2);
				this.userList.addComponent(userDiv, 0);
				exists = true;
				break;
			}
		}
		if (!exists) {
			User user = this.userService.findUserById(senderId);
			CssLayout newDiv = this.createUserDiv(user, message2);
			this.userList.addComponent(newDiv, 0);
		}
	}

	private void userListSelectionChange(Component userDiv) {
		this.userList.forEach(userDivr -> userDivr.removeStyleName(ThemeConstants.BORDERED_GREEN));
		userDiv.addStyleName(ThemeConstants.BORDERED_GREEN);
//		changeUserDivMessage(
//				(CssLayout)userDiv,
//				messageService.findLastByUserIDs(senderId, Integer.parseInt(userDiv.getId())).getMessage());
	}

	private void searchValueChangeListener(ValueChangeEvent<String> event) {
		if (event.getValue().equals("")) {
			this.userList.removeAllComponents();
			this.userList.setSizeUndefined();
//			userList.removeStyleName(ThemeConstants.MESSAGES_USER_LIST_SHOW_LABEL);
			this.fillUserList();
		} else {
			this.userList.removeAllComponents();
			this.ldapService.findByFullNameContaining(event.getValue())
					.forEach(user -> this.userList.addComponent(this.createUserDiv(user, "")));
//			userList.setWidth("100%");
//			userList.addStyleName(ThemeConstants.MESSAGES_USER_LIST_SHOW_LABEL);
		}
	}

}
