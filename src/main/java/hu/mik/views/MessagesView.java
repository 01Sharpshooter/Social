package hu.mik.views;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.HasValue.ValueChangeEvent;
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

	private TextField tfSearch;

	private Button btnShowConvs;

	private Label conversationName;

	private List<User> choosableUsers;

	private Button btnAddMember;

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

//		this.createTextFieldSearch();

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

	private void createConversationList() {
		this.btnShowConvs = new Button(VaadinIcons.ANGLE_DOUBLE_DOWN);
		this.btnShowConvs.addClickListener(e -> this.showOrHideConversations());
		this.btnShowConvs.setId("btn-show-conv");
		this.btnShowConvs.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		this.addComponent(this.btnShowConvs);
		this.conversationListLayout = new CssLayout();
		this.conversationListLayout.addStyleName(ThemeConstants.HOVER_GREEN_LAYOUTS);
		this.conversationListLayout.setId("latestMessagesLayout");
		this.addComponent(this.conversationListLayout);
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
		this.createConversationNameLabel();
		this.createBtnAddMember();
	}

	private void createConversationNameLabel() {
		this.conversationName = new Label();
		this.conversationName.addStyleName(ThemeConstants.CHAT_CONVERSATION_NAME);
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
			UI.getCurrent().addWindow(new AddMemberToConvWindow(choosableUsers, this::addMembersToConversation));
		}
	}

	public void addMembersToConversation(List<User> usersToAdd) {
		usersToAdd.forEach(user -> this.selectedConversationDiv.getConversation()
				.addConversationUser(new ConversationUser(this.selectedConversationDiv.getConversation(), user)));
		this.selectedConversationDiv
				.setConversation(this.chatService.saveConversation(this.selectedConversationDiv.getConversation()));
		this.selectedConversationDiv.refresh();
		this.conversationName.setValue(this.selectedConversationDiv.getConversationName());
	}

	private void createTextWriter() {
		this.textWriter = new HorizontalLayout();
		TextField textField = new TextField();

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
		this.messagesPanel.setLoggedUserAndConversation(this.loggedUser,
				this.selectedConversationDiv.getConversation());
		if (this.selectedConversationDiv.getConversation().getId() != null) {
			if (this.chatService.setConversationSeen(this.selectedConversationDiv.getConversation(),
					this.loggedUser.getDbUser()) != 0) {
				((MainUI) this.getUI()).refreshUnseenConversationNumber();
			}
			this.messagesPanel.firstFill();
			this.messagesPanel.scrollToBottom();
		}
	}

	private void sendMessage(String messageText) {
		if (!messageText.isEmpty()) {
			this.initAndSaveMessageToConversation(messageText);
			MessageBroadcastService.sendMessage(this.selectedConversationDiv.getConversation());
			this.refreshAndMoveConversationDiv(this.selectedConversationDiv);
		}

	}

	private void refreshAndMoveConversationDiv(ConversationDiv conversationDiv) {
		this.conversationListLayout.removeComponent(conversationDiv);
		conversationDiv.refresh();
		this.conversationListLayout.addComponent(conversationDiv, 0);
	}

	private void initAndSaveMessageToConversation(String messageText) {
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
		this.addMessageLabelToPanel(message);
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

		for (Component convDiv : this.conversationListLayout) {
			if (((ConversationDiv) convDiv).getConversation().getId().equals(conversation.getId())) {
				((ConversationDiv) convDiv).setConversation(conversation);
				this.refreshAndMoveConversationDiv((ConversationDiv) convDiv);
				return;
			}
		}
		Conversation newConv = this.chatService.findOrCreateConversationWithUser(
				conversation.getLastMessage().getSender().getId(), this.loggedUser.getDbUser());
		this.conversationListLayout.addComponent(this.createConversationDiv(newConv), 0);
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
		MessageBroadcastService.messageSeen(this.selectedConversationDiv.getConversation());
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
		if (this.selectedConversationDiv.getConversation().getId() != null) {
			this.setConversationSeenByUser(this.selectedConversationDiv.getConversation());
		}
		this.messagesPanel.scrollToBottom();
		this.btnAddMember.setVisible(true);
		this.hideConversations();
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
			});
		}
	}

	public void messageSeen(Conversation conversation) {
		for (Component c : this.conversationListLayout) {
			if (((ConversationDiv) c).getConversation().getId().equals(conversation.getId())) {
				((ConversationDiv) c).setConversation(conversation);
				((ConversationDiv) c).refresh();
				break;
			}
		}
	}

}
