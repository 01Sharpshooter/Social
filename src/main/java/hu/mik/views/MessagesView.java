package hu.mik.views;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
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
import com.vaadin.ui.themes.ValoTheme;

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
import hu.mik.windows.AddMemberToConvWindow;

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
	private CssLayout chatHeader;
	private HorizontalLayout textWriter;

	private SocialUserWrapper loggedUser;

	private ConversationDiv selectedConversationDiv;

	private Button btnShowConvs;

	private Label conversationName;

	private List<User> choosableUsers;

	private Button btnAddMember;

	private boolean convNameFullyShown;

	@Override
	public void enter(ViewChangeEvent event) {
		if (UI.getCurrent() instanceof MainUI) {
			this.choosableUsers = this.userService.listAll();
			this.loggedUser = this.userUtils.getLoggedInUser();
			this.setSizeFull();
			this.createContent(event);
		}

	}

	@Override
	public void beforeLeave(ViewBeforeLeaveEvent event) {
		View.super.beforeLeave(event);
		this.selectedConversationDiv = null;
	}

	private void createContent(ViewChangeEvent event) {
		this.createBase();

		this.createConversationList();

		this.createChatLayout();

		this.fillConversationList();

		this.addConversationByURLParam(event);

		if (this.selectedConversationDiv == null) {
			this.showOrHideConversations();
		}

	}

	private void addConversationByURLParam(ViewChangeEvent event) {
		String parameters[] = event.getParameters().split("/");
		if (parameters.length > 0 && !parameters[0].isEmpty()) {
			Integer userId = Integer.parseInt(parameters[0]);
			this.findOrCreateConversationWithUser(userId);
		}
	}

	private Conversation findOrCreateConversationWithUser(Integer userId) {
		Conversation conversation = this.chatService.findOrCreateConversationWithUser(userId,
				this.loggedUser.getDbUser());
		ConversationDiv convDiv = this.createConversationDiv(conversation);
		this.removeConversationDivIfExists(convDiv);
		this.conversationListLayout.addComponent(convDiv, 0);
		this.conversationListSelectionChange(convDiv);
		return conversation;
	}

	private void removeConversationDivIfExists(ConversationDiv convDiv) {
		ConversationDiv divToDelete = null;
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
		this.messagesPanel = new MessagesPanelScrollable(this.loggedUser);
	}

	private void createConversationList() {
		this.btnShowConvs = new Button(VaadinIcons.ANGLE_DOUBLE_DOWN);
		this.btnShowConvs.addClickListener(e -> this.showOrHideConversations());
		this.btnShowConvs.setId("btn-show-conv");
		this.btnShowConvs.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		this.addComponent(this.btnShowConvs);
		CssLayout leftLayout = new CssLayout();
		leftLayout.addStyleName(ThemeConstants.CHAT_LEFT_LAYOUT);
		leftLayout.addComponent(this.createBtnNewConversation());
		this.conversationListLayout = new CssLayout();
		this.conversationListLayout.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		this.conversationListLayout.setId("latestMessagesLayout");
		leftLayout.addComponent(this.conversationListLayout);
		this.addComponent(leftLayout);
	}

	private void showOrHideConversations() {
		if (this.btnShowConvs.getIcon().equals(VaadinIcons.ANGLE_DOUBLE_DOWN)) {
			this.addStyleName(ThemeConstants.SHOW_CONVERSATIONS);
			this.btnShowConvs.setIcon(VaadinIcons.ANGLE_DOUBLE_UP);
		} else {
			this.hideConversations();
		}
	}

	private void hideConversations() {
		this.removeStyleName(ThemeConstants.SHOW_CONVERSATIONS);
		this.btnShowConvs.setIcon(VaadinIcons.ANGLE_DOUBLE_DOWN);

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

	private Button createBtnNewConversation() {
		Button btnNew = new Button("New conversation", VaadinIcons.PLUS);
		btnNew.setWidth("100%");
		btnNew.setHeight("55px");
		btnNew.addClickListener(e -> this.btnNewConversationClick());
		return btnNew;

	}

	private void btnNewConversationClick() {
		//@formatter:off
		List<User> choosableUsers = this.choosableUsers
				.stream()
				.filter(user -> !user.getId().equals(this.loggedUser.getDbUser().getId()))
				.collect(Collectors.toList());
		//@formatter:on
		UI.getCurrent().addWindow(new AddMemberToConvWindow(choosableUsers, this::createNewConversation, true));
	}

	public void createNewConversation(List<User> usersToAdd, String messageText) {
		Conversation conversation;
		if (usersToAdd.size() == 1) {
			conversation = this.findOrCreateConversationWithUser(usersToAdd.get(0).getId());
		} else {
			conversation = new Conversation();
			usersToAdd.forEach(user -> conversation.addConversationUser(new ConversationUser(conversation, user)));
			conversation.addConversationUser(new ConversationUser(conversation, this.loggedUser.getDbUser()));
			this.selectedConversationDiv = this.createConversationDiv(conversation);
			this.conversationListLayout.addComponent(this.selectedConversationDiv, 0);
			this.conversationListSelectionChange(this.selectedConversationDiv);
		}

		this.sendMessage(messageText);
	}

	private ConversationDiv createConversationDiv(Conversation conversation) {
		ConversationDiv userDiv = new ConversationDiv(conversation, this.loggedUser.getDbUser());
		userDiv.addLayoutClickListener(e -> {
			this.conversationListSelectionChange(userDiv);
		});
		return userDiv;
	}

	private void createChatLayout() {
		CssLayout chatLayout = new CssLayout();
		this.createChatHeader();
		this.createMessagesPanel();
		this.createTextWriter();
		chatLayout.addComponent(this.chatHeader);
		chatLayout.addComponent(this.messagesPanel);
		chatLayout.addComponent(this.textWriter);
		chatLayout.setId("chatLayout");
		this.addComponent(chatLayout);
	}

	private void createChatHeader() {
		this.chatHeader = new CssLayout();
		this.chatHeader.setWidth("100%");
		this.chatHeader.addLayoutClickListener(e -> this.switchConversationNameShowMode());
		this.chatHeader.addStyleName(ThemeConstants.CHAT_CONVERSATION_HEADER);
		this.createConversationNameLabel();
		this.createBtnAddMember();
	}

	private void switchConversationNameShowMode() {
		if (this.convNameFullyShown) {
			this.chatHeader.removeStyleName(ThemeConstants.SHOW_FULL_CONVERSATION_NAME);
		} else {
			this.chatHeader.addStyleName(ThemeConstants.SHOW_FULL_CONVERSATION_NAME);
		}
		this.convNameFullyShown = !this.convNameFullyShown;
	}

	private void createConversationNameLabel() {
		this.conversationName = new Label();
		this.conversationName.setVisible(false);
		this.chatHeader.addComponent(this.conversationName);
	}

	private void createBtnAddMember() {
		this.btnAddMember = new Button("+ Add member");
		this.btnAddMember.setStyleName(ValoTheme.BUTTON_LINK);
		this.btnAddMember.setVisible(false);

		this.btnAddMember.addClickListener(e -> this.addMemberBtnClick());

		this.chatHeader.addComponent(this.btnAddMember);
	}

	private void addMemberBtnClick() {
		if (this.selectedConversationDiv != null) {
			//@formatter:off
			List<User> choosableUsers = this.choosableUsers
					.stream()
					.filter(user -> !this.selectedConversationDiv.getConversation().getlistOfUserIds().contains(user.getId()))
					.collect(Collectors.toList());
			//@formatter:on
			UI.getCurrent().addWindow(new AddMemberToConvWindow(choosableUsers, this::addMembersToConversation, false));
		}
	}

	public void addMembersToConversation(List<User> usersToAdd, String messageText) {
		usersToAdd.forEach(user -> this.selectedConversationDiv.getConversation()
				.addConversationUser(new ConversationUser(this.selectedConversationDiv.getConversation(), user)));
		if (messageText.isEmpty()) {
			this.selectedConversationDiv
					.setConversation(this.chatService.saveConversation(this.selectedConversationDiv.getConversation()));
			MessageBroadcastService.refreshConversationForEveryMember(this.selectedConversationDiv.getConversation());
		} else {
			this.sendMessage(messageText);
		}
		this.conversationName.setValue(this.selectedConversationDiv.getConversationName());
	}

	private void createTextWriter() {
		this.textWriter = new HorizontalLayout();
		TextField textField = new TextField();
		textField.setPlaceholder("Send a message...");

		Button sendButton = new Button("Send", e -> {
			this.sendMessage(textField.getValue());
			textField.clear();
		});
		sendButton.addStyleName(ThemeConstants.BLUE_TEXT);
		sendButton.setClickShortcut(KeyCode.ENTER);
		sendButton.setSizeUndefined();

		this.textWriter.addComponent(textField);
		this.textWriter.addComponent(sendButton);
		this.textWriter.setExpandRatio(textField, 1);
		this.textWriter.setWidth("100%");
		this.textWriter.setEnabled(false);
	}

	private void fillChat() {
		this.messagesPanel.setConversation(this.selectedConversationDiv.getConversation());
		if (this.selectedConversationDiv.getConversation().getId() != null) {
			this.messagesPanel.firstFill();
			this.messagesPanel.scrollToBottom();
		}
	}

	private void sendMessage(String messageText) {
		if (!messageText.isEmpty()) {
			this.initMessageToSend(messageText);
			MessageBroadcastService.sendMessage(this.selectedConversationDiv.getConversation());
			this.moveConversationDivToTop(this.selectedConversationDiv);
			this.selectedConversationDiv.refresh();
		}

	}

	private void moveConversationDivToTop(ConversationDiv conversationDiv) {
		this.conversationListLayout.removeComponent(conversationDiv);
		this.conversationListLayout.addComponent(conversationDiv, 0);
	}

	private void initMessageToSend(String messageText) {
		Message message = this.convertStringToMessage(messageText);
		this.setSeenFalseForEverybody();
		message.setConversation(this.selectedConversationDiv.getConversation());
		this.selectedConversationDiv.getConversation().setLastMessage(message);
		this.selectedConversationDiv
				.setConversation(this.chatService.saveConversation(this.selectedConversationDiv.getConversation()));
		this.addMessageLabelToPanel(message);
	}

	private void setSeenFalseForEverybody() {
		for (ConversationUser cu : this.selectedConversationDiv.getConversation().getConversationUsers()) {
			if (!cu.getUser().equals(this.loggedUser.getDbUser())) {
				cu.setSeen(false);
			} else {
				cu.setSeen(true);
			}
		}
	}

	private Message convertStringToMessage(String messageText) {
		Message message = new Message();
		message.setMessage(messageText.trim());
		message.setSender(this.loggedUser.getDbUser());
		message.setTime(new Timestamp(new Date().getTime()));
		return message;
	}

	public void receiveMessage(Conversation conversation) {
		if (this.selectedConversationDiv != null
				&& this.selectedConversationDiv.getConversation().getId().equals(conversation.getId())) {
			this.addMessageLabelToPanel(conversation.getLastMessage());
			this.setConversationSeenByUser(conversation);
		} else if (!this.loggedUser.getDbUser().getId().equals(conversation.getLastMessage().getSender().getId())) {
			((MainUI) UI.getCurrent()).refreshUnseenConversationNumber();
			this.showNewMessageNotification(conversation);
		}
		this.refreshOrCreateConversation(conversation, true);
	}

	private void addMessageLabelToPanel(Message lastMessage) {
		this.messagesPanel.addMessage(lastMessage, true);
		this.messagesPanel.scrollToBottom();
	}

	private void setConversationSeenByUser(Conversation conversation) {
		conversation.getConversationUsers().stream()
				.filter(cu -> cu.getUser().getId().equals(this.loggedUser.getDbUser().getId())).findFirst().get()
				.setSeen(true);
		this.selectedConversationDiv.setConversation(this.chatService.saveConversation(conversation));
		((MainUI) UI.getCurrent()).refreshUnseenConversationNumber();
		MessageBroadcastService.refreshConversationForEveryMember(this.selectedConversationDiv.getConversation());
	}

	private void showNewMessageNotification(Conversation conversation) {
		Notification notification = Notification.show(conversation.getLastMessage().getSender().getFullName(),
				conversation.getLastMessage().getMessage(), Notification.Type.TRAY_NOTIFICATION);
		notification.setIcon(VaadinIcons.COMMENT);
	}

	private void conversationListSelectionChange(ConversationDiv conversationDiv) {
		this.selectedConversationDiv = conversationDiv;
		this.textWriter.setEnabled(true);
		this.conversationName.setValue(this.selectedConversationDiv.getConversationName());
		this.conversationName.setVisible(true);
		this.fillChat();
		this.conversationListLayout.forEach(userDivr -> userDivr.removeStyleName(ThemeConstants.BORDERED_GREEN));
		conversationDiv.addStyleName(ThemeConstants.BORDERED_GREEN);
		if (this.selectedConversationDiv.getConversation().getId() != null
				&& !this.selectedConversationDiv.getConversation().seenByUser(this.loggedUser.getDbUser().getId())) {
			this.setConversationSeenByUser(this.selectedConversationDiv.getConversation());
		}
		this.messagesPanel.scrollToBottom();
		this.btnAddMember.setVisible(true);
		this.hideConversations();
	}

	public void refreshOrCreateConversation(Conversation conversation, boolean moveToTop) {
		ConversationDiv conversationDiv;
		for (Component c : this.conversationListLayout) {
			if (((ConversationDiv) c).getConversation().getId().equals(conversation.getId())) {
				conversationDiv = (ConversationDiv) c;
				conversationDiv.setConversation(conversation);
				conversationDiv.refresh();
				if (moveToTop) {
					this.moveConversationDivToTop(conversationDiv);
				}
				if (this.selectedConversationDiv != null && conversationDiv.getConversation().getId()
						.equals(this.selectedConversationDiv.getConversation().getId())) {
					this.conversationName.setValue(conversationDiv.getConversationName());
				}
				return;
			}
		}

		Conversation newConv = this.chatService.findOrCreateConversationWithUser(
				conversation.getLastMessage().getSender().getId(), this.loggedUser.getDbUser());
		conversationDiv = this.createConversationDiv(newConv);
		this.conversationListLayout.addComponent(conversationDiv, 0);
	}

}
