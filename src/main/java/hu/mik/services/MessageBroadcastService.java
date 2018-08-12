package hu.mik.services;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import hu.mik.beans.Message;
import hu.mik.beans.SocialUserWrapper;
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

	public static synchronized void sendMessage(Message message, SocialUserWrapper sender) {
		executorService.execute(() -> {
			if (userIDs.contains(message.getReceiverId())) {
				messageListeners.get(userIDs.indexOf(message.getReceiverId())).receiveMessage(message, sender);
			}
		});
	}

	public static synchronized void messageSeen(Integer senderId, Integer seenSourceId) {
		executorService.execute(() -> {
			if (userIDs.contains(senderId)) {
				messageListeners.get(userIDs.indexOf(senderId)).messageSeen(seenSourceId);
			}
		});
	}

}
