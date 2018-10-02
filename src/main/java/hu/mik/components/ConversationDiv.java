package hu.mik.components;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

import hu.mik.beans.Conversation;
import hu.mik.beans.ConversationUser;
import hu.mik.beans.Message;
import hu.mik.beans.User;
import hu.mik.constants.ThemeConstants;

@SuppressWarnings("serial")
public class ConversationDiv extends CssLayout {
	private String conversationName;
	private Conversation conversation;
	private ConversationUser loggedConvUser;

	private Map<Integer, Image> userImageCache;

	public ConversationDiv(Conversation conversation, User loggedUser) {
		super();
		this.conversation = conversation;
		this.loggedConvUser = conversation.getConversationUsers().stream().filter(cu -> cu.getUser().equals(loggedUser))
				.findFirst().get();

		this.userImageCache = new HashMap<>();

		this.setConversationLook(conversation);
		Message lastMessage = conversation.getLastMessage();
		if (lastMessage != null) {
			Label userDivLbl = new Label(
					this.conversationName + "</br><span id=\"message\">" + lastMessage.getMessage() + "</span>",
					ContentMode.HTML);
			if (conversation.getLastMessage().getSender() == null) {
				this.addLabelsToUserDiv(userDivLbl, null);
			} else if (!this.loggedConvUser.isSeen()) {
				this.addLabelsToUserDiv(userDivLbl, null);
				this.addStyleName(ThemeConstants.UNSEEN_MESSAGE);
			}
//			else if (!conversation.isSeen() && lastMessage.getSender().getId().equals(loggedId)) {
//			this.addLabelsToUserDiv(userDivLbl, VaadinIcons.ANGLE_DOUBLE_RIGHT);
//		} else if (conversation.isSeen() && lastMessage.getSender().getId().equals(loggedId)) {
//			this.addLabelsToUserDiv(userDivLbl, VaadinIcons.EYE);
//		}

			else {
				this.addLabelsToUserDiv(userDivLbl, null); // TODO
			}
		} else {
			this.addLabelsToUserDiv(new Label(this.conversationName), null);
		}
	}

	private void addLabelsToUserDiv(Label userDivLbl, VaadinIcons icon) {
		this.addComponent(userDivLbl);
		if (icon != null) {
			this.addComponent(new Label(icon.getHtml(), ContentMode.HTML));
		} else {
			this.addComponent(new Label());
		}
	}

	private void setConversationLook(Conversation conversation) {
		Image image;
		// @formatter:off
		if (conversation.getConversationUserCount() == 2) {
			ConversationUser convUser =
					conversation.getConversationUsers()
					.stream()
					.filter(cu -> !cu.equals(this.loggedConvUser))
					.findFirst()
					.get();
			// @formatter:on
			image = convUser.getUser().getVaadinImage();
			this.conversationName = convUser.getUser().getFullName();
		} else {
			image = conversation.getLastMessage().getSender().getVaadinImage();
			this.conversationName = conversation.getConversationUsers().stream()
					.filter(cu -> !cu.equals(this.loggedConvUser)).map(cu -> cu.getUser().getFullName())
					.collect(Collectors.joining(", "));
		}
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
		this.setDescription(conversation.getConversationUsers().stream().filter(cu -> !cu.equals(this.loggedConvUser))
				.map(cu -> cu.getUser().getFullName()).collect(Collectors.joining(", ")));

		this.addComponent(image);

	}

	public void changeLastMessage(Message lastMessage) {
		String name;
		String[] stringArray = ((Label) this.getComponent(1)).getValue().split("<");
		name = stringArray[0];
		this.removeComponent(this.getComponent(1));
		Label lblName = new Label(name + "</br><span id=\"message\">" + lastMessage.getMessage() + "</span>",
				ContentMode.HTML);
		this.addComponent(lblName, 1);
		if (this.conversation.getConversationUserCount() > 2) {
			this.changeConversationImage(lastMessage);
		}
		this.getComponent(0).addStyleName(ThemeConstants.BORDERED_IMAGE);
	}

	private void changeConversationImage(Message lastMessage) {
		Image image;
		if (this.userImageCache.containsKey(lastMessage.getSender().getId())) {
			image = this.userImageCache.get(lastMessage.getSender().getId());
		} else {
			image = lastMessage.getSender().getVaadinImage();
			this.userImageCache.put(lastMessage.getSender().getId(), image);
		}

		this.replaceComponent(this.getComponent(0), image);
	}

	public Conversation getConversation() {
		return this.conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

}
