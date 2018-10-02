package hu.mik.beans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_conversations")
public class Conversation {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@OneToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.MERGE })
	@JoinColumn(name = "last_message", updatable = true)
	private Message lastMessage;
	@OneToMany(mappedBy = "conversation", orphanRemoval = true, fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
	private Set<ConversationUser> conversationUsers;

	public Conversation() {
		super();
		this.conversationUsers = new HashSet<>();
	}

	public Conversation(ConversationUser... convUsers) {
		this.conversationUsers = new HashSet<>(Arrays.asList(convUsers));
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Message getLastMessage() {
		return this.lastMessage;
	}

	public void setLastMessage(Message lastMessage) {
		this.lastMessage = lastMessage;
	}

	public Set<ConversationUser> getConversationUsers() {
		return this.conversationUsers;
	}

	public void setConversationUsers(Set<ConversationUser> conversationUsers) {
		this.conversationUsers = conversationUsers;
	}

	public void addConversationUser(ConversationUser conversationUser) {
		this.conversationUsers.add(conversationUser);
	}

	public int getConversationUserCount() {
		return this.conversationUsers.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Conversation other = (Conversation) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
