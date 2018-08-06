package hu.mik.views;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
import hu.mik.constants.ThemeConstants;
import hu.mik.constants.UserConstants;
import hu.mik.services.FriendshipService;
import hu.mik.services.LdapService;
import hu.mik.services.MessageBroadcastService;
import hu.mik.services.MessageService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;
import hu.mik.utils.UserUtils;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name = MessagesView.NAME)
public class MessagesView extends CssLayout implements View {
	public static final String NAME = "messages";

	@Autowired
	private MessageService messageService;
	@Autowired
	private UserService userService;
	@Autowired
	private FriendshipService friendshipService;
	@Autowired
	private LdapService ldapService;
	@Autowired
	private UserUtils userUtils;

	private List<User> friendList;
	private List<Message> messagesList;

	private Panel messagesPanel;
	private VerticalLayout messagesLayout;
	private CssLayout userList;
	private HorizontalLayout textWriter;

	private User sender;

	private Integer receiverId;

	private int scroll = 100;
	private int scrollGrowth = 50;
	private int messageNumberAtOnce = 20;

	@Override
	public void enter(ViewChangeEvent event) {
		this.sender = this.userUtils.getLoggedInUser().getDbUser();
		this.friendList = new ArrayList<>();
		this.friendshipService.findAllByUserId(this.sender.getId())
				.forEach(friendShip -> this.friendList.add(this.userService.findUserById(friendShip.getFriendId())));
		this.addStyleName(ThemeConstants.BORDERED);
		this.setSizeFull();
		this.createContent(event);

	}

	private void createContent(ViewChangeEvent event) {
		this.createBase();

		this.createTextFieldSearch();

		this.createUserList();

		this.createTextWriter();

		this.createMessagesPanel();

		this.createChat();

		this.fillUserList();

		String parameters[] = event.getParameters().split("/");
		if (parameters.length > 0) {
			User receiver = this.userService.findUserByUsername(parameters[0]);
			if (receiver != null) {
				this.receiverId = receiver.getId();
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
					Message lastMessage = this.messageService.findLastByUserIDs(this.receiverId, this.sender.getId());
					String messageString;
					if (lastMessage != null) {
						messageString = lastMessage.getMessage();
					} else {
						messageString = "";
					}
					CssLayout newDiv = this.createUserDivFromDbUser(receiver, messageString);
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

		this.messagesPanel = new Panel();
		this.messagesPanel.setContent(this.messagesLayout);
		this.messagesPanel.addStyleName(ThemeConstants.BORDERED);
		this.messagesPanel.setSizeFull();
	}

	private void createUserList() {
		this.userList = new CssLayout();
		this.userList.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		this.userList.setId("latestMessagesLayout");
		this.addComponent(this.userList);
	}

	private void createTextFieldSearch() {
		TextField tfSearch = new TextField("Search:");
		tfSearch.addValueChangeListener(this::searchValueChangeListener);
		this.addComponent(tfSearch);
	}

	private void createBase() {
		this.setId("messageBase");
		this.setSizeFull();
	}

	private List<CssLayout> fillUserList() {
		List<Message> latestMessages = this.messageService.findLastestMessagesOfUser(100, this.sender.getId());
		List<Integer> alreadyUsedIds = new ArrayList<>();
		List<CssLayout> userDivs = new ArrayList<>();
		CssLayout userDiv;
		User user;

		for (Message message : latestMessages) {
			boolean amItheSender = message.getSenderId().equals(this.sender.getId());
			if (amItheSender) {
				if (!alreadyUsedIds.contains(message.getReceiverId())) {
					alreadyUsedIds.add(message.getReceiverId());
					user = this.userService.findUserById(message.getReceiverId());
					userDiv = this.createUserDivFromDbUser(user, message.getMessage());
					this.userList.addComponent(userDiv);
					userDivs.add(userDiv);
				}
			} else {
				if (!alreadyUsedIds.contains(message.getSenderId())) {
					alreadyUsedIds.add(message.getSenderId());
					user = this.userService.findUserById(message.getSenderId());
					userDiv = this.createUserDivFromDbUser(user, message.getMessage());
					this.userList.addComponent(userDiv);
					userDivs.add(userDiv);
				}
			}
		}
		return userDivs;
	}

	private CssLayout createUserDivFromDbUser(User dbUser, String lastMessage) {
		LdapUser ldapUser = this.ldapService.findUserByUsername(dbUser.getUsername());
		return this.createUserDiv(ldapUser, dbUser, lastMessage);
	}

	private CssLayout createUserDivFromLdap(LdapUser ldapUser, String lastMessage) {
		User dbUser = this.userService.findUserByUsername(ldapUser.getUsername());
		return this.createUserDiv(ldapUser, dbUser, lastMessage);
	}

	private CssLayout createUserDiv(LdapUser ldapUser, User dbUser, String lastMessage) {
		CssLayout userDiv = new CssLayout();

		Image image = new Image(null,
				new FileResource(new File(UserConstants.PROFILE_PICTURE_LOCATION + dbUser.getImageName())));
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		userDiv.addComponent(image);
		userDiv.setId(dbUser.getId().toString());
		userDiv.addLayoutClickListener(this::userDivClickListener);
		Label lblName = new Label(ldapUser.getFullName() + "</br><span id=\"message\">" + lastMessage + "</span>",
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
		chat.addComponent(this.messagesPanel);
		chat.addComponent(this.textWriter);
		chat.setId("chatLayout");
		this.addComponent(chat);
	}

	private void createTextWriter() {
		this.textWriter = new HorizontalLayout();
		TextField textField = new TextField();
		textField.setWidth("100%");

		Button sendButton = new Button("Send", e -> {
			this.sendMessage(textField.getValue());
			textField.clear();
		});
		sendButton.addStyleName(ThemeConstants.BLUE_TEXT);
		sendButton.setClickShortcut(KeyCode.ENTER);
		sendButton.setSizeUndefined();

		this.textWriter.addComponent(textField);
		this.textWriter.addComponent(sendButton);
		this.textWriter.setExpandRatio(textField, 7);
		this.textWriter.setExpandRatio(sendButton, 3);
		this.textWriter.setSizeFull();
		this.textWriter.setEnabled(false);
	}

	private void userDivClickListener(LayoutClickEvent event) {
		this.fillChat(event.getComponent());
	}

	private void fillChat(Component userDiv) {
		this.textWriter.setEnabled(true);
		this.messagesLayout.removeAllComponents();
		this.receiverId = Integer.parseInt(userDiv.getId());
		this.messagesList = this.messageService.findAllByUserIDs(this.messageNumberAtOnce, this.sender.getId(),
				this.receiverId);
		if (this.messageService.setAllPreviousSeen(this.sender.getId(), this.receiverId) != 0) {
			((MainUI) this.getUI()).refreshUnseenMessageNumber();
		}
		this.fillMessages();
		this.messagesPanel.setScrollTop(this.scroll);
		MessageBroadcastService.register((MainUI) this.getUI(), this.sender.getId());
		this.userListSelectionChange(userDiv);

	}

	public void fillMessages() {
		this.messagesList = this.messageService.findAllByUserIDs(20, this.sender.getId(), this.receiverId);
		if (this.messagesList != null) {
			if (!this.messagesList.isEmpty()) {
				this.messagesLayout.removeAllComponents();
				for (int i = this.messagesList.size() - 1; i >= 0; i--) {
					Message message = this.messagesList.get(i);
					if (message.getSenderId().equals(this.sender.getId())) {
						this.scroll += this.scrollGrowth;
						Label newMessage = new Label("<span id=\"messageSpan\">" + message.getMessage() + "</span>",
								ContentMode.HTML);
						newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
						newMessage.setDescription(this.getMessageDateDesc(message.getTime()));
						newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_SENT);
						this.messagesLayout.addComponent(newMessage);
					} else {
						this.scroll += this.scrollGrowth;
						Label newMessage = new Label("<span id=\"messageSpan\">" + message.getMessage() + "</span>",
								ContentMode.HTML);
						newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
						newMessage.setDescription(this.getMessageDateDesc(message.getTime()));
						newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_RECEIVED);
						this.messagesLayout.addComponent(newMessage);
						this.messagesLayout.setComponentAlignment(newMessage, Alignment.MIDDLE_LEFT);
					}
				}
			}
		}
	}

	private void sendMessage(String messageText) {
		Message message = new Message();
		message.setMessage(messageText);

		if (message.getMessage().length() != 0) {
			message.setSenderId(this.sender.getId());
			message.setReceiverId(this.receiverId);
			message.setTime(new Timestamp(new Date().getTime()));
			this.messageService.saveMessage(message);
			MessageBroadcastService.sendMessage(message.getMessage(), this.sender.getId(), this.receiverId);
			this.scroll += this.scrollGrowth;
			Label newMessage = new Label("<span id=\"messageSpan\">" + message.getMessage() + "</span>",
					ContentMode.HTML);
			newMessage.setDescription(this.getMessageDateDesc(message.getTime()));
			newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_SENT);
			this.messagesLayout.addComponent(newMessage);
			this.messagesPanel.setScrollTop(this.scroll);

			for (Component userDiv : this.userList) {
				if (userDiv.getId().equals(String.valueOf(this.receiverId))) {
					this.userList.removeComponent(userDiv);
					this.changeUserDivMessage((CssLayout) userDiv, message.getMessage());
					this.userList.addComponent(userDiv, 0);
					this.userListSelectionChange(userDiv);
					break;
				}
			}
		}

	}

	public void receiveMessage(String message, Integer senderId) {
		if (senderId.equals(this.receiverId)) {
			Label newMessage = new Label("<span id=\"messageSpan\">" + message + "</span>", ContentMode.HTML);
			newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_RECEIVED);
			newMessage.setDescription(this.getMessageDateDesc(new Date()));
			this.messagesLayout.addComponent(newMessage);
			this.messagesLayout.setComponentAlignment(newMessage, Alignment.MIDDLE_LEFT);
			this.messagesPanel.setScrollTop(this.scroll);

		}
		boolean exists = false;
		for (Component userDiv : this.userList) {
			if (userDiv.getId().equals(String.valueOf(senderId))) {
				this.userList.removeComponent(userDiv);
				this.changeUserDivMessage((CssLayout) userDiv, message);
				this.userList.addComponent(userDiv, 0);
				exists = true;
				break;
			}
		}
		if (!exists) {
			User user = this.userService.findUserById(senderId);
			CssLayout newDiv = this.createUserDivFromDbUser(user, message);
			this.userList.addComponent(newDiv, 0);
		}
	}

	private void userListSelectionChange(Component userDiv) {
		this.userList.forEach(userDivr -> userDivr.removeStyleName(ThemeConstants.BORDERED_GREEN));
		userDiv.addStyleName(ThemeConstants.BORDERED_GREEN);
	}

	private void searchValueChangeListener(ValueChangeEvent<String> event) {
		if (event.getValue().equals("")) {
			this.userList.removeAllComponents();
			this.userList.setSizeUndefined();
			this.fillUserList();
		} else {
			this.userList.removeAllComponents();
			this.ldapService.findByFullNameContaining(event.getValue())
					.forEach(user -> this.userList.addComponent(this.createUserDivFromLdap(user, "")));
		}
	}

	private String getMessageDateDesc(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
		return sdf.format(date);

	}

}
