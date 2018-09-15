package hu.mik.views;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import hu.mik.beans.Conversation;
import hu.mik.beans.Message;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.beans.User;
import hu.mik.components.MessagesPanelScrollable;
import hu.mik.components.UserDiv;
import hu.mik.constants.ThemeConstants;
import hu.mik.services.ChatService;
import hu.mik.services.LdapService;
import hu.mik.services.MessageBroadcastService;
import hu.mik.services.UserService;
import hu.mik.ui.MainUI;
import hu.mik.utils.UserUtils;

@SuppressWarnings("serial")
@ViewScope
@SpringView(name = MessagesView.NAME)
public class MessagesView extends CssLayout implements View {
	public static final String NAME = "messages";

	@Autowired
	private ChatService messageService;
	@Autowired
	private UserService userService;
	@Autowired
	private LdapService ldapService;
	@Autowired
	private UserUtils userUtils;

	private MessagesPanelScrollable messagesPanel;
	private CssLayout userList;
	private HorizontalLayout textWriter;

	private SocialUserWrapper sender;

	private User receiver;

	private Conversation conversation;

	private TextField tfSearch;

	@Override
	public void enter(ViewChangeEvent event) {
		if (UI.getCurrent() instanceof MainUI) {
			this.sender = this.userUtils.getLoggedInUser();
			this.addStyleName(ThemeConstants.BORDERED);
			this.setSizeFull();
			this.createContent(event);
		}

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
		if (parameters.length > 0 && !parameters[0].isEmpty()) {
			this.receiver = this.userService.findUserById(Integer.parseInt(parameters[0]));
			if (this.receiver != null) {
				for (Component userDiv : this.userList) {
					if (this.receiver.equals(((UserDiv) userDiv).getUser())) {
						this.userList.removeComponent(userDiv);
						this.userList.addComponent(userDiv, 0);
						this.userListSelectionChange(this.userList.getComponent(0));
						this.fillChat((UserDiv) this.userList.getComponent(0));
						return;
					}
				}

//				Message lastMessage = this.messageService.findLastMessageOfUsers(this.receiver,
//						this.sender.getDbUser());
//				if (lastMessage == null) {
//					lastMessage = new Message();
//					lastMessage.setMessage("");
				// lastMessage.setSeen(false);
//			}
//			CssLayout newDiv = this.createUserDivFromDbUser(this.receiver, lastMessage);
//			this.userList.addComponent(newDiv, 0);
//			this.userListSelectionChange(newDiv);
//			this.fillChat((UserDiv) this.userList.getComponent(0));
			}
		}

	}

	private void createMessagesPanel() {
		this.messagesPanel = new MessagesPanelScrollable();
	}

	private void createUserList() {
		this.userList = new CssLayout();
		this.userList.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		this.userList.setId("latestMessagesLayout");
		this.addComponent(this.userList);
	}

	private void createTextFieldSearch() {
		this.tfSearch = new TextField("Search:");
		this.tfSearch.addValueChangeListener(this::searchValueChangeListener);
		this.addComponent(this.tfSearch);
	}

	private void createBase() {
		this.setId("messageBase");
		this.setSizeFull();
	}

	private List<UserDiv> fillUserList() {
		List<Conversation> latestConversations = this.messageService
				.findLatestConversationsOfUser(this.sender.getDbUser());
		List<UserDiv> userDivs = new ArrayList<>();
		UserDiv userDiv;

		for (Conversation conversation : latestConversations) {
			if (conversation.getLastMessage().getSender().equals(this.sender.getDbUser())) {
				userDiv = this.createUserDivFromDbUser(conversation.getConversationPartner(this.sender.getDbUser()),
						conversation.getLastMessage());
			} else {
				userDiv = this.createUserDivFromDbUser(conversation.getLastMessage().getSender(),
						conversation.getLastMessage());
			}
			this.userList.addComponent(userDiv);
			userDivs.add(userDiv);
		}
		return userDivs;
	}

	private UserDiv createUserDivFromDbUser(User dbUser, Message lastMessage) {
		return this.createUserDiv(dbUser, lastMessage);
	}

	private UserDiv createUserDiv(User dbUser, Message lastMessage) {
		UserDiv userDiv = new UserDiv(dbUser, lastMessage, this.sender.getDbUser().getId());
		userDiv.addLayoutClickListener(e -> {
			if (lastMessage.getConversation() != null) {
				this.conversation = lastMessage.getConversation();
			} else {
				this.conversation = new Conversation(this.sender.getDbUser(), dbUser);
			}
			this.fillChat((UserDiv) e.getComponent());
		});
		return userDiv;
	}

	private CssLayout changeUserDivMessage(CssLayout userDiv, String lastMessage) {
		String name;
		String[] stringArray = ((Label) userDiv.getComponent(1)).getValue().split("<");
		name = stringArray[0];
		userDiv.removeComponent(userDiv.getComponent(1));
		Label lblName = new Label(name + "</br><span id=\"message\">" + lastMessage + "</span>", ContentMode.HTML);
		userDiv.addComponent(lblName, 1);
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

	private void fillChat(UserDiv userDiv) {
		this.textWriter.setEnabled(true);
		this.receiver = userDiv.getUser();
		if (this.messageService.setConversationSeen(this.conversation) != 0) {
			((MainUI) this.getUI()).refreshUnseenConversationNumber();
		}
		userDiv.removeStyleName(ThemeConstants.UNSEEN_MESSAGE);
		MessageBroadcastService.messageSeen(this.receiver.getId(), this.sender.getDbUser().getId());
		this.messagesPanel.setLoggedUserAndConversation(this.sender, this.conversation);
		this.messagesPanel.firstFill();
		this.messagesPanel.scrollToBottom();
		this.userListSelectionChange(userDiv);

	}

	private void sendMessage(String messageText) {
		Message message = new Message();
		message.setMessage(messageText);

		if (message.getMessage().length() != 0) {
			message.setSender(this.sender.getDbUser());
			this.conversation.setLastMessage(message);
			message.setConversation(this.conversation);
			message.setTime(new Timestamp(new Date().getTime()));
			this.messageService.saveMessage(message);
			MessageBroadcastService.sendMessage(message, this.sender);
			Label newMessage = new Label("<span id=\"messageSpan\">" + message.getMessage() + "</span>",
					ContentMode.HTML);
			newMessage.setDescription(this.getMessageDateDesc(message.getTime()));
			newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_SENT);
			this.messagesPanel.addMessage(newMessage);
			this.messagesPanel.scrollToBottom();

			if (!this.tfSearch.isEmpty()) {
				this.tfSearch.clear();
				return;
			}

			for (Component userDiv : this.userList) {
				if (((UserDiv) userDiv).getUser().equals(this.receiver)) {
					CssLayout div = (CssLayout) userDiv;
					this.userList.removeComponent(userDiv);
					this.changeUserDivMessage(div, message.getMessage());
					div.replaceComponent(div.getComponent(2),
							new Label(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), ContentMode.HTML));
					this.userList.addComponent(userDiv, 0);
					this.userListSelectionChange(userDiv);
					break;
				}
			}
		}

	}

	public void receiveMessage(Message message) {
		if (message.getSender().equals(this.receiver)) {
			Label newMessage = new Label("<span id=\"messageSpan\">" + message.getMessage() + "</span>",
					ContentMode.HTML);
			newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_RECEIVED);
			newMessage.setDescription(this.getMessageDateDesc(new Date()));
			this.messagesPanel.addReceivedMessage(newMessage);
			this.messagesPanel.scrollToBottom();
			this.messageService.setConversationSeen(this.conversation);
			MessageBroadcastService.messageSeen(this.receiver.getId(), this.sender.getDbUser().getId());

		} else {
			((MainUI) UI.getCurrent()).refreshUnseenConversationNumber();
			Notification notification = Notification.show(this.sender.getLdapUser().getFullName(), message.getMessage(),
					Notification.Type.TRAY_NOTIFICATION);
			notification.setIcon(VaadinIcons.COMMENT);
		}
		boolean exists = false;
		for (Component userDiv : this.userList) {
			if (((UserDiv) userDiv).getUser().equals(message.getSender())) {
				this.userList.removeComponent(userDiv);
				this.changeUserDivMessage((CssLayout) userDiv, message.getMessage());
				((CssLayout) userDiv).replaceComponent(((CssLayout) userDiv).getComponent(2), new Label());
				this.userList.addComponent(userDiv, 0);
				if (!message.getSender().equals(this.receiver)) {
					userDiv.addStyleName(ThemeConstants.UNSEEN_MESSAGE);
				}
				exists = true;
				break;
			}
		}
		if (!exists) {
			User user = this.userService.findUserById(message.getSender().getId());
			CssLayout newDiv = this.createUserDivFromDbUser(user, message);
			newDiv.addStyleName(ThemeConstants.UNSEEN_MESSAGE);
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
			this.userService.findByFullNameContaining(event.getValue()).forEach(user -> {
				Message message = new Message();
				message.setMessage("");
				// message.setSeen(true);
				this.userList.addComponent(this.createUserDivFromDbUser(user, message));
			});
		}
	}

	private String getMessageDateDesc(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
		return sdf.format(date);

	}

	public void messageSeen(Integer receiverId) {
		for (Component c : this.userList) {
			if (((UserDiv) c).getUser().getId().equals(receiverId)) {
				((CssLayout) c).replaceComponent(((CssLayout) c).getComponent(2),
						new Label(VaadinIcons.EYE.getHtml(), ContentMode.HTML));
			}
		}
	}

	public User getReceiver() {
		return this.receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

}
