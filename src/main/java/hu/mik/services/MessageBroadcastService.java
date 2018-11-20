package hu.mik.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import hu.mik.beans.Conversation;
import hu.mik.listeners.NewMessageListener;
import lombok.Synchronized;

@Service
public class MessageBroadcastService {
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();

	private static ConcurrentHashMap<Integer, NewMessageListener> userToListenerMap = new ConcurrentHashMap<>();

	@Synchronized
	public static void register(NewMessageListener messageListener, Integer userId) {
		if (!userToListenerMap.containsKey(userId)) {
			userToListenerMap.put(userId, messageListener);
		}
	}

	@Synchronized
	public static void unregister(NewMessageListener messageListener, Integer userId) {
		userToListenerMap.remove(userId);

	}

	@Synchronized
	public static void sendMessage(Conversation conversation) {
		executorService.execute(() -> {
			conversation.getConversationUsers().stream()
					.filter(cu -> !cu.getUser().getId().equals(conversation.getLastMessage().getSender().getId()))
					.filter(cu -> userToListenerMap.containsKey(cu.getUser().getId()))
					.forEach(cu -> userToListenerMap.get(cu.getUser().getId()).receiveMessage(conversation));
		});
	}

	@Synchronized
	public static void refreshConversationForEveryMember(Conversation conversation) {
		executorService.execute(() -> {
			conversation.getConversationUsers().stream()
					.filter(cu -> userToListenerMap.containsKey(cu.getUser().getId()))
					.forEach(cu -> userToListenerMap.get(cu.getUser().getId()).refreshConversation(conversation));
		});
	}

}
