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
	private static LinkedList<Integer> userIDs = new LinkedList<>();

	public static synchronized void register(NewMessageListener messageListener, Integer userId) {
		if (!userIDs.contains(userId)) {
			messageListeners.add(messageListener);
			userIDs.add(userId);
		}
	}

	public static synchronized void unregister(NewMessageListener messageListener, Integer userId) {
		if (userIDs.contains(userId)) {
			messageListeners.remove(messageListener);
			userIDs.remove(userId);
		}
	}

	public static synchronized void sendMessage(String message, Integer senderId, Integer receiverId) {
		executorService.execute(() -> {
			if (userIDs.contains(receiverId)) {
				messageListeners.get(userIDs.indexOf(receiverId)).receiveMessage(message, senderId);
			}
		});
	}

}
