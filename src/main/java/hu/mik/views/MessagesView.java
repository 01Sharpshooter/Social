package hu.mik.views;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import hu.mik.beans.Conversation;
import hu.mik.beans.ConversationUser;
import hu.mik.beans.Message;
import hu.mik.beans.SocialUserWrapper;
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
					.findOrCreateConversationWithUser(Integer.parseInt(parameters[0]), this.loggedUser.getDbUser());
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
			this.initAndSaveMessageToConversation(messageText);
			MessageBroadcastService.sendMessage(this.selectedConversationDiv.getConversation());
			this.messagesPanel.scrollToBottom();

		}

	}

	private void refreshAndMoveConversationDiv(ConversationDiv conversationDiv) {
		this.conversationListLayout.removeComponent(conversationDiv);
		conversationDiv.refresh();
//		conversationDiv.replaceComponent(conversationDiv.getComponent(2),
//				new Label(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), ContentMode.HTML));
		this.conversationListLayout.addComponent(conversationDiv, 0);
	}

	private Message initAndSaveMessageToConversation(String messageText) {
		Message message = new Message();
		message.setMessage(messageText);
		message.setSender(this.loggedUser.getDbUser());
		message.setConversation(this.selectedConversationDiv.getConversation());
		message.setTime(new Timestamp(new Date().getTime()));
		for (ConversationUser cu : this.selectedConversationDiv.getConversation().getConversationUsers()) {
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

	public void receiveMessage(Conversation conversation) {
		if (this.selectedConversationDiv != null
				&& this.selectedConversationDiv.getConversation().getId().equals(conversation.getId())) {
			this.messagesPanel.addMessage(conversation.getLastMessage(), true);
			this.messagesPanel.scrollToBottom();
			this.chatService.setConversationSeen(this.selectedConversationDiv.getConversation(),
					this.loggedUser.getDbUser());
		} else if (!this.loggedUser.getDbUser().equals(conversation.getLastMessage().getSender())) {
			((MainUI) UI.getCurrent()).refreshUnseenConversationNumber();
			this.showNewMessageNotification(conversation);
		}
//		MessageBroadcastService.messageSeen(this.receiver.getId(), this.loggedUser.getDbUser().getId());

		for (Component userDiv : this.conversationListLayout) {
			if (((ConversationDiv) userDiv).getConversation().getId().equals(conversation.getId())) {
				((ConversationDiv) userDiv).setConversation(conversation);
				this.refreshAndMoveConversationDiv((ConversationDiv) userDiv);
				return;
			}
		}
		Conversation newConv = this.chatService.findOrCreateConversationWithUser(
				conversation.getLastMessage().getSender().getId(), this.loggedUser.getDbUser());
		this.conversationListLayout.addComponent(this.createConversationDiv(newConv), 0);
	}

	private void showNewMessageNotification(Conversation conversation) {
		Notification notification = Notification.show(conversation.getLastMessage().getSender().getFullName(),
				conversation.getLastMessage().getMessage(), Notification.Type.TRAY_NOTIFICATION);
		notification.setIcon(VaadinIcons.COMMENT);
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

	public void messageSeen(Integer receiverId) {
//		for (Component c : this.userList) {
//			if (((UserDiv) c).getUser().getId().equals(receiverId)) {
//				((CssLayout) c).replaceComponent(((CssLayout) c).getComponent(2),
//						new Label(VaadinIcons.EYE.getHtml(), ContentMode.HTML));
//			}
//		}
	}

}
