package hu.mik.views;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import hu.mik.beans.ConversationUser;
import hu.mik.beans.Message;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.beans.User;
import hu.mik.components.ConversationDiv;
import hu.mik.components.MessagesPanelScrollable;
import hu.mik.constants.ThemeConstants;
import hu.mik.services.ChatService;
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
	private ChatService chatService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserUtils userUtils;

	private MessagesPanelScrollable messagesPanel;
	private CssLayout conversationListLayout;
	private HorizontalLayout textWriter;

	private SocialUserWrapper loggedUser;

	private ConversationDiv selectedConversationDiv;

	private TextField tfSearch;

	@Override
	public void enter(ViewChangeEvent event) {
		if (UI.getCurrent() instanceof MainUI) {
			this.loggedUser = this.userUtils.getLoggedInUser();
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

		this.fillConversationList();

		this.addConversationByURLParam(event);

	}

	private void addConversationByURLParam(ViewChangeEvent event) {
		String parameters[] = event.getParameters().split("/");
		if (parameters.length > 0 && !parameters[0].isEmpty()) {
			Conversation conversation = this.chatService
					.findOrCreateConversationWithUser(Integer.parseInt(parameters[0]));
			ConversationDiv convDiv = this.createConversationDiv(conversation);
			this.removeConversationDivIfExists(convDiv);
			this.conversationListLayout.addComponent(convDiv, 0);
			this.conversationListSelectionChange(convDiv);
		}
	}

	private void removeConversationDivIfExists(ConversationDiv convDiv) {
		ConversationDiv divToDelete = null;
		// Basic equals does not seem to work..
		for (Component c : this.conversationListLayout) {
			if (((ConversationDiv) c).getConversation().getId().equals(convDiv.getConversation().getId())) {
				divToDelete = (ConversationDiv) c;
				break;
			}
		}
		if (divToDelete != null) {
			this.conversationListLayout.removeComponent(divToDelete);
		}
	}

	private void createMessagesPanel() {
		this.messagesPanel = new MessagesPanelScrollable();
	}

	private void createUserList() {
		this.conversationListLayout = new CssLayout();
		this.conversationListLayout.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		this.conversationListLayout.setId("latestMessagesLayout");
		this.addComponent(this.conversationListLayout);
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

	private void fillConversationList() {
		List<Conversation> latestConversation = this.chatService
				.findLatestConversationsOfUser(this.loggedUser.getDbUser());
		ConversationDiv conversationDiv;

		for (Conversation conversation : latestConversation) {
			conversationDiv = this.createConversationDiv(conversation);
			this.conversationListLayout.addComponent(conversationDiv);
		}
	}

	private ConversationDiv createConversationDiv(Conversation conversation) {
		ConversationDiv userDiv = new ConversationDiv(conversation, this.loggedUser.getDbUser());
		userDiv.addLayoutClickListener(e -> {
			this.conversationListSelectionChange(userDiv);
		});
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

	private void fillChat() {
		this.messagesPanel.setLoggedUserAndConversation(this.loggedUser,
				this.selectedConversationDiv.getConversation());
		if (this.selectedConversationDiv.getConversation().getId() != null) {
			if (this.chatService.setConversationSeen(this.selectedConversationDiv.getConversation(),
					this.loggedUser.getDbUser()) != 0) {
				((MainUI) this.getUI()).refreshUnseenConversationNumber();
			}
//		MessageBroadcastService.messageSeen(this.receiver.getId(), this.sender.getDbUser().getId());
			this.messagesPanel.firstFill();
			this.messagesPanel.scrollToBottom();
		}
	}

	private void sendMessage(String messageText) {
		if (!messageText.isEmpty()) {
			Message message = this.initAndSaveMessageToConversation(messageText);
			MessageBroadcastService.sendMessage(message, this.loggedUser);
			this.addNewMessageLabelToChatPanel(message);
			this.messagesPanel.scrollToBottom();

//			if (!this.tfSearch.isEmpty()) {
//				this.tfSearch.clear();
//				return;
//			}

			this.moveConversationToTopOfList(message);

		}

	}

	private void moveConversationToTopOfList(Message message) {
		this.conversationListLayout.removeComponent(this.selectedConversationDiv);
		this.selectedConversationDiv.changeLastMessage(message);
		this.selectedConversationDiv.replaceComponent(this.selectedConversationDiv.getComponent(2),
				new Label(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), ContentMode.HTML));
		this.conversationListLayout.addComponent(this.selectedConversationDiv, 0);
	}

	private Message initAndSaveMessageToConversation(String messageText) {
		Message message = new Message();
		message.setMessage(messageText);
		message.setSender(this.loggedUser.getDbUser());
		message.setConversation(this.selectedConversationDiv.getConversation());
		message.setTime(new Timestamp(new Date().getTime()));
		for (ConversationUser cu : this.selectedConversationDiv.getConversation().getConversationUsers()) {
			System.err.println("!!! " + cu.getConversation());
			if (!cu.getUser().equals(this.loggedUser.getDbUser())) {
				cu.setSeen(false);
			} else {
				cu.setSeen(true);
			}
		}
		this.selectedConversationDiv.getConversation().setLastMessage(message);
		this.selectedConversationDiv
				.setConversation(this.chatService.saveConversation(this.selectedConversationDiv.getConversation()));
		return message;
	}

	private void addNewMessageLabelToChatPanel(Message message) {
		Label newMessage = new Label("<span id=\"messageSpan\">" + message.getMessage() + "</span>", ContentMode.HTML);
		newMessage.setDescription(this.getMessageDateDesc(message.getTime()));
		newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
		newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_SENT);
		this.messagesPanel.addMessage(newMessage);
	}

	public void receiveMessage(Message message) {
//		if (message.getSender().equals(this.receiver)) {
		Label newMessage = new Label("<span id=\"messageSpan\">" + message.getMessage() + "</span>", ContentMode.HTML);
		newMessage.setWidth(this.messagesPanel.getWidth() / 2, this.messagesPanel.getWidthUnits());
		newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_RECEIVED);
		newMessage.setDescription(this.getMessageDateDesc(new Date()));
		this.messagesPanel.addReceivedMessage(newMessage);
		this.messagesPanel.scrollToBottom();
		this.chatService.setConversationSeen(this.selectedConversationDiv.getConversation(),
				this.loggedUser.getDbUser());
//			MessageBroadcastService.messageSeen(this.receiver.getId(), this.loggedUser.getDbUser().getId());

//		} else {
		((MainUI) UI.getCurrent()).refreshUnseenConversationNumber();
		Notification notification = Notification.show(this.loggedUser.getLdapUser().getFullName(), message.getMessage(),
				Notification.Type.TRAY_NOTIFICATION);
		notification.setIcon(VaadinIcons.COMMENT);
//		}
		boolean exists = false;
		for (Component userDiv : this.conversationListLayout) {
			if (((ConversationDiv) userDiv).getConversation().equals(message.getConversation())) {
				this.conversationListLayout.removeComponent(userDiv);
				((ConversationDiv) userDiv).changeLastMessage(message);
				((ConversationDiv) userDiv).replaceComponent(((CssLayout) userDiv).getComponent(2), new Label());
				this.conversationListLayout.addComponent(userDiv, 0);
//				if (!message.getSender().equals(this.receiver)) {
				userDiv.addStyleName(ThemeConstants.UNSEEN_MESSAGE);
//				}
				exists = true;
				break;
			}
		}
		if (!exists) {
			User user = this.userService.findUserById(message.getSender().getId());
//			CssLayout newDiv = this.createUserDivFromDbUser(user, message);
//			newDiv.addStyleName(ThemeConstants.UNSEEN_MESSAGE);
//			this.userList.addComponent(newDiv, 0);
		}
	}

	private void conversationListSelectionChange(ConversationDiv userDiv) {
		this.selectedConversationDiv = userDiv;
		this.textWriter.setEnabled(true);
		this.fillChat();
		this.conversationListLayout.forEach(userDivr -> userDivr.removeStyleName(ThemeConstants.BORDERED_GREEN));
		userDiv.removeStyleName(ThemeConstants.UNSEEN_MESSAGE);
		userDiv.addStyleName(ThemeConstants.BORDERED_GREEN);
	}

	private void searchValueChangeListener(ValueChangeEvent<String> event) {
		if (event.getValue().equals("")) {
			this.conversationListLayout.removeAllComponents();
			this.conversationListLayout.setSizeUndefined();
			this.fillConversationList();
		} else {
			this.conversationListLayout.removeAllComponents();
			this.userService.findByFullNameContaining(event.getValue()).forEach(user -> {
				Message message = new Message();
				message.setMessage("");
				// message.setSeen(true);
//				this.userList.addComponent(this.createUserDivFromDbUser(user, message));
			});
		}
	}

	private String getMessageDateDesc(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
		return sdf.format(date);

	}

	public void messageSeen(Integer receiverId) {
//		for (Component c : this.userList) {
//			if (((UserDiv) c).getUser().getId().equals(receiverId)) {
//				((CssLayout) c).replaceComponent(((CssLayout) c).getComponent(2),
//						new Label(VaadinIcons.EYE.getHtml(), ContentMode.HTML));
//			}
//		}
	}

}
