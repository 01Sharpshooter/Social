package hu.mik.components;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
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

	private Image previousImage;
	private Integer previousSenderId;

	public MessagesPanelScrollable() {
		super(ScrollDirection.UP);
		this.content = new VerticalLayout();
		this.content.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
		this.content.setSizeFull();
		this.content.setMargin(false);
		this.content.setSpacing(false);
		this.pageSize = 30;
		this.setWidthUndefined();
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
		if (this.messagesList != null && !this.messagesList.isEmpty()) {
			for (Message message : this.messagesList) {
				this.addMessage(message, false);
			}
			this.offset = this.messagesList.get(this.messagesList.size() - 1).getId();
			this.scrollAfterLoad();
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
		Label newMessage = new Label(message.getMessage());
		CssLayout messageZone = new CssLayout();

		if (isBottomMessage) {
			this.content.addComponent(messageZone);
		} else {
			this.content.addComponent(messageZone, 0);
		}

		if (message.getSender().equals(this.loggedUser.getDbUser())) {
			messageZone.addStyleName(ThemeConstants.CHAT_MESSAGE_SENT);
		} else {
			messageZone.addStyleName(ThemeConstants.CHAT_MESSAGE_RECEIVED);
			this.addImageIfNecessary(message, messageZone, isBottomMessage);
			this.addMarginIfNecessary(message, isBottomMessage, messageZone);
			this.content.setComponentAlignment(messageZone, Alignment.MIDDLE_LEFT);
		}
		messageZone.addComponent(newMessage);
		messageZone
				.setDescription(message.getSender().getFullName() + ", " + this.getMessageDateDesc(message.getTime()));
		this.previousSenderId = message.getSender().getId();
		this.scrollToBottom();
	}

	private void addMarginIfNecessary(Message message, boolean isBottomMessage, CssLayout messageZone) {
		if (this.previousSenderId != null && !this.previousSenderId.equals(message.getSender().getId())) {
			if (isBottomMessage) {
				messageZone.addStyleName(ThemeConstants.MARGIN_TOP);
			} else {
				messageZone.addStyleName(ThemeConstants.MARGIN_BOTTOM);
			}
		}
	}

	private void addImageIfNecessary(Message message, CssLayout messageZone, boolean isBottomMessage) {
		if (this.previousSenderId == null || !this.previousSenderId.equals(message.getSender().getId())) {
			Image image = message.getSender().getVaadinImage();
			messageZone.addComponent(image);
			this.previousImage = image;
		} else if (isBottomMessage) {
			messageZone.addComponent(this.previousImage);
		}
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
