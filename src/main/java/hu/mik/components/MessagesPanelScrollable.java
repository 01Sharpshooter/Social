package hu.mik.components;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.Conversation;
import hu.mik.beans.Message;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.constants.ThemeConstants;
import hu.mik.enums.ScrollDirection;
import hu.mik.services.ChatService;

@SuppressWarnings("serial")
public class MessagesPanelScrollable extends AbstractScrollablePanel {

	private VerticalLayout content;

	private ChatService messageService;

	private List<Message> messagesList;

	private SocialUserWrapper loggedUser;

	private Conversation conversation;

	private int scrollForLoaded = 40;

	public MessagesPanelScrollable() {
		super(ScrollDirection.UP);
		this.content = new VerticalLayout();
		this.content.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
		this.content.setSizeFull();
		this.pageSize = 20;
		this.addStyleName(ThemeConstants.BORDERED);
		this.setContent(this.content);

		this.messageService = this.appCtx.getBean(ChatService.class);
	}

	public void setLoggedUserAndConversation(SocialUserWrapper loggedUser, Conversation conversation) {
		this.loggedUser = loggedUser;
		this.conversation = conversation;
	}

	@Override
	protected void loadNextPage() {
		this.fillMessages();
	}

	private void fillMessages() {
		this.messagesList = this.messageService.findAllPagedByConversation(this.offset, this.pageSize,
				this.conversation);
		if (this.messagesList != null) {
			if (!this.messagesList.isEmpty()) {
				for (Message message : this.messagesList) {
					this.addMessage(message, false);
				}
				this.offset = this.messagesList.get(this.messagesList.size() - 1).getId();
				this.scrollAfterLoad();
			}
		}
	}

	public void firstFill() {
		this.content.removeAllComponents();
		if (this.conversation.getLastMessage() != null) {
			this.offset = this.conversation.getLastMessage().getId() + 1;
		}
		this.fillMessages();
		this.scrollToBottom();
	}

	private String getMessageDateDesc(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
		return sdf.format(date);

	}

	public void addMessage(Message message, boolean isBottomMessage) {
		Label newMessage = new Label("<span id=\"messageSpan\">" + message.getMessage() + "</span>", ContentMode.HTML);

		if (isBottomMessage) {
			this.content.addComponent(newMessage);
		} else {
			this.content.addComponent(newMessage, 0);
		}

		if (message.getSender().equals(this.loggedUser.getDbUser())) {
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_SENT);
		} else {
			newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_RECEIVED);
			this.content.setComponentAlignment(newMessage, Alignment.MIDDLE_LEFT);
		}
		newMessage.setWidth(this.getWidth() / 2, this.getWidthUnits());
		newMessage.setDescription(this.getMessageDateDesc(message.getTime()));
		this.scrollToBottom();
	}

	public void scrollAfterLoad() {
		switch (this.scrollForLoaded) {
		case 40:
			this.scrollForLoaded++;
			break;
		case 41:
			this.scrollForLoaded--;
			break;
		}
		this.setScrollTop(this.pageSize * this.scrollForLoaded);
	}

}
