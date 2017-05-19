package hu.mik.listeners;

import hu.mik.beans.User;

public interface NewMessageListener {
	public void receiveMessage(String message, int id);
}
