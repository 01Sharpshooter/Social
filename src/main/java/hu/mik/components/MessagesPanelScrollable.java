package hu.mik.components;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.Message;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;
import hu.mik.enums.ScrollDirection;
import hu.mik.services.MessageService;

@SuppressWarnings("serial")
public class MessagesPanelScrollable extends AbstractScrollablePanel {

	private VerticalLayout content;

	private MessageService messageService;

	private List<Message> messagesList;

	private SocialUserWrapper sender;
	private User receiver;

	private int scrollForLoaded = 40;

	public MessagesPanelScrollable() {
		super(ScrollDirection.UP);
		this.content = new VerticalLayout();
		this.content.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
		this.content.setSizeFull();
		this.pageSize = 20;
		this.addStyleName(ThemeConstants.BORDERED);
		this.setContent(this.content);

		this.messageService = this.appCtx.getBean(MessageService.class);
	}

	public void setConversationParticipants(SocialUserWrapper sender, User receiver) {
		this.sender = sender;
		this.receiver = receiver;
	}

	@Override
	protected void loadNextPage() {
		this.fillMessages();
	}

	private void fillMessages() {
		this.messagesList = this.messageService.findAllPagedByUsers(this.offset, this.pageSize, this.sender.getDbUser(),
				this.receiver);
		if (this.messagesList != null) {
			if (!this.messagesList.isEmpty()) {
				for (Message message : this.messagesList) {
					Label newMessage = new Label("<span id=\"messageSpan\">" + message.getMessage() + "</span>",
							ContentMode.HTML);
					this.content.addComponent(newMessage, 0);
					if (message.getSender().equals(this.sender.getDbUser())) {
						newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_SENT);
					} else {
						newMessage.addStyleName(ThemeConstants.CHAT_MESSAGE_RECEIVED);
						this.content.setComponentAlignment(newMessage, Alignment.MIDDLE_LEFT);
					}
					newMessage.setWidth(this.getWidth() / 2, this.getWidthUnits());
					newMessage.setDescription(this.getMessageDateDesc(message.getTime()));
					this.offset += this.pageSize;
				}
				this.scrollAfterLoad();
			}
		}
	}

	public void firstFill() {
		this.content.removeAllComponents();
		this.offset = 0;
		this.fillMessages();
		this.scrollToBottom();
	}

	private String getMessageDateDesc(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
		return sdf.format(date);

	}

	public void addMessage(Label message) {
		this.content.addComponent(message);
		this.scrollToBottom();
	}

	public void addReceivedMessage(Label message) {
		this.addMessage(message);
		this.content.setComponentAlignment(message, Alignment.MIDDLE_LEFT);
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
