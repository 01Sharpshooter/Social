package hu.mik.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import hu.mik.beans.Message;
import hu.mik.beans.SocialUserWrapper;
import hu.mik.listeners.NewMessageListener;

@Service
public class MessageBroadcastService {
	static ExecutorService executorService = Executors.newSingleThreadExecutor();

	private static ConcurrentHashMap<Integer, NewMessageListener> userToListenerMap = new ConcurrentHashMap<>();

	public static synchronized void register(NewMessageListener messageListener, Integer userId) {
		if (!userToListenerMap.containsKey(userId)) {
			userToListenerMap.put(userId, messageListener);
		}
	}

	public static synchronized void unregister(NewMessageListener messageListener, Integer userId) {
		userToListenerMap.remove(userId);

	}

	public static synchronized void sendMessage(Message message, SocialUserWrapper sender) {
//		executorService.execute(() -> {
//			if (userToListenerMap
//					.containsKey((message.getConversation().getConversationPartner(sender.getDbUser()).getId()))) {
//				userToListenerMap.get(message.getConversation().getConversationPartner(sender.getDbUser()).getId())
//						.receiveMessage(message, sender);
//			}
//		});
	}

	public static synchronized void messageSeen(Integer senderId, Integer seenSourceId) {
		executorService.execute(() -> {
			if (userToListenerMap.containsKey(senderId)) {
				userToListenerMap.get(senderId).messageSeen(seenSourceId);
			}
		});
	}

}
