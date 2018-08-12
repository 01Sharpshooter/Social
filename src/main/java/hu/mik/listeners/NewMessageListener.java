package hu.mik.listeners;

import hu.mik.beans.Message;
import hu.mik.beans.SocialUserWrapper;

public interface NewMessageListener {
	public void receiveMessage(Message message, SocialUserWrapper sender);
}
