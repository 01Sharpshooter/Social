package hu.mik.components;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import hu.mik.beans.Conversation;
import hu.mik.beans.Message;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.beans.User;
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

	private Map<Integer, Image> userImageCache;

	public MessagesPanelScrollable() {
		super(ScrollDirection.UP);
		this.content = new VerticalLayout();
		this.content.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
		this.content.setSizeFull();
		this.content.setMargin(false);
		this.pageSize = 30;
		this.addStyleName(ThemeConstants.BORDERED);
		this.setContent(this.content);

		this.messageService = this.appCtx.getBean(ChatService.class);
		this.userImageCache = new HashMap<>();
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
			messageZone.addComponent(message.getSender().getVaadinImage());
			this.content.setComponentAlignment(messageZone, Alignment.MIDDLE_LEFT);
		}
		messageZone.addComponent(newMessage);
		messageZone
				.setDescription(message.getSender().getFullName() + ", " + this.getMessageDateDesc(message.getTime()));
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

	// TODO check vaadin cache
	private Image getUserImage(User user) {
		Image image;
		if (this.userImageCache.containsKey(user.getId())) {
			image = this.userImageCache.get(user.getId());
		} else {
			image = user.getVaadinImage();
			this.userImageCache.put(user.getId(), image);
		}
		return image;
	}

}
