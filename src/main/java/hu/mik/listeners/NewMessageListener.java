package hu.mik.listeners;

import hu.mik.beans.Conversation;

public interface NewMessageListener {
	public void receiveMessage(Conversation conversation);

	public void refreshConversation(Conversation conversation);
}
