package hu.mik.services;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import hu.mik.listeners.NewMessageListener;

@Service
public class MessageBroadcastService {
	static ExecutorService executorService = Executors.newSingleThreadExecutor();

	private static LinkedList<NewMessageListener> messageListeners = new LinkedList<NewMessageListener>();
	private static LinkedList<String> usernames = new LinkedList<>();

	public static synchronized void register(NewMessageListener messageListener, String username) {
		if (!usernames.contains(username)) {
			messageListeners.add(messageListener);
			usernames.add(username);
		}
	}

	public static synchronized void unregister(NewMessageListener messageListener, String username) {
		if (usernames.contains(username)) {
			messageListeners.remove(messageListener);
			usernames.remove(username);
		}
	}

	public static synchronized void sendMessage(String message, int senderId, String receiverName) {
		executorService.execute(() -> {
			if (usernames.contains(receiverName)) {
				messageListeners.get(usernames.indexOf(receiverName)).receiveMessage(message, senderId);
			}
		});
	}

}
