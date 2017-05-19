package hu.mik.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hu.mik.beans.User;
import hu.mik.listeners.NewMessageListener;

public class MessageBroadcastService {
	static ExecutorService executorService=Executors.newSingleThreadExecutor();
	
	private static LinkedList<NewMessageListener> messageListeners=new LinkedList<NewMessageListener>();
//	private static LinkedList<User> users=new LinkedList<User>();
	private static LinkedList<String> usernames=new LinkedList<>();
	
	public static synchronized void register(NewMessageListener messageListener, String username){
		if(!usernames.contains(username)){
			System.out.println(username);
			messageListeners.add(messageListener);
			usernames.add(username);
	//		users.add(user);
		}
	}
	
	public static synchronized void unregister(NewMessageListener messageListener, String username){
		if(usernames.contains(username)){
			messageListeners.remove(messageListener);
			usernames.remove(username);
	//		users.remove(user);
		}
	}
	
	public static synchronized void sendMessage(String message, int senderId, String receiverName){
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				System.out.println(receiverName);
				messageListeners.get(usernames.indexOf(receiverName)).receiveMessage(message, senderId);	
			}
		});
	}

}
