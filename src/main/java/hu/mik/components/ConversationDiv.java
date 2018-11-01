package hu.mik.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
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
	private ConversationUser conversationPartner;

	private Map<Integer, Image> userImageCache;
	private User loggedUser;

	public ConversationDiv(Conversation conversation, User loggedUser) {
		super();
		this.conversation = conversation;
		this.loggedUser = loggedUser;
		this.userImageCache = new HashMap<>();

		this.refresh();
	}

	private void refreshConversationUsers() {
		Supplier<Stream<ConversationUser>> streamSupplier = () -> this.conversation.getConversationUsers().stream();
		Predicate<? super ConversationUser> predicate = cu -> cu.getUser().getId().equals(this.loggedUser.getId());

		this.loggedConvUser = streamSupplier.get().filter(predicate).findFirst().get();

		if (this.conversation.getConversationUserCount() == 2) {
			this.conversationPartner = streamSupplier.get().filter(predicate.negate()).findFirst().get();
		}
	}

	private void refreshIcon() {
		Message lastMessage = this.conversation.getLastMessage();
		Label icon;
		if (lastMessage == null || this.conversation.getConversationUserCount() != 2
				|| !lastMessage.getSender().getId().equals(this.loggedConvUser.getUser().getId())) {
			icon = new Label();
		} else {
			if (this.conversationPartner.isSeen()) {
				icon = new Label(VaadinIcons.EYE.getHtml(), ContentMode.HTML);
			} else {
				icon = new Label(VaadinIcons.ANGLE_DOUBLE_RIGHT.getHtml(), ContentMode.HTML);
			}
		}
		this.addComponentToIndex(icon, 2);
	}

	private void setConversationName() {
		if (this.conversation.getConversationUserCount() == 2) {
			this.conversationName = this.conversationPartner.getUser().getFullName();
		} else {
			this.conversationName = this.conversation.getConversationUsers().stream()
					.filter(cu -> !cu.getId().equals(this.loggedConvUser.getId())).map(cu -> cu.getUser().getFullName())
					.collect(Collectors.joining(", "));
		}
		this.setDescription(this.conversationName);

	}

	public void refresh() {
		this.refreshConversationUsers();
		this.refreshConversationImage();
		this.setConversationName();
		this.refreshDivLabel();
		this.refreshIcon();
	}

	private void refreshDivLabel() {
		Label divLabel;
		if (this.conversation.getLastMessage() != null) {
			divLabel = new Label(this.conversationName + "</br><span id=\"message\">"
					+ this.conversation.getLastMessage().getMessage() + "</span>", ContentMode.HTML);

			this.addStyleToLabel(divLabel);
		} else {
			divLabel = new Label(this.conversationName);
		}
		this.addComponentToIndex(divLabel, 1);
	}

	private void addStyleToLabel(Label divLabel) {
		if (!this.loggedConvUser.isSeen()) {
			this.addStyleName(ThemeConstants.UNSEEN_MESSAGE);
		} else {
			this.removeStyleName(ThemeConstants.UNSEEN_MESSAGE);
		}
	}

	private void refreshConversationImage() {
		Image image;
		if (this.conversation.getConversationUserCount() == 2) {
			image = this.getUserImage(this.conversationPartner.getUser());
		} else {
			image = this.getUserImage(this.conversation.getLastMessage().getSender());
		}

		this.addComponentToIndex(image, 0);
		image.addStyleName(ThemeConstants.BORDERED_IMAGE);
	}

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

	private void addComponentToIndex(Component component, int index) {
		try {
			this.removeComponent(this.getComponent(index));
			this.addComponent(component, index);
		} catch (IndexOutOfBoundsException e) {
			this.addComponent(component);
		}
	}

	public Conversation getConversation() {
		return this.conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	public String getConversationName() {
		return this.conversationName;
	}

	public void setConversationName(String conversationName) {
		this.conversationName = conversationName;
	}

}
