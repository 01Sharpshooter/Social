package hu.mik.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_conversations")
public class Conversation {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user1", updatable = false)
	private User user1;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user2", updatable = false)
	private User user2;
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "last_message", updatable = true)
	private Message lastMessage;
	@Column(name = "seen")
	private boolean seen;

	public Conversation() {
		super();
	}

	public Conversation(User user1, User user2) {
		super();
		this.user1 = user1;
		this.user2 = user2;
	}

	public User getConversationPartner(User user) {
		if (this.getUser1().equals(user)) {
			return this.getUser2();
		} else {
			return this.getUser1();
		}
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser1() {
		return this.user1;
	}

	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return this.user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}

	public Message getLastMessage() {
		return this.lastMessage;
	}

	public void setLastMessage(Message lastMessage) {
		this.lastMessage = lastMessage;
	}

	public boolean isSeen() {
		return this.seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	@Override
	public String toString() {
		return "Conversation [id=" + this.id + ", user1=" + this.user1 + ", user2=" + this.user2 + ", lastMessage="
				+ this.lastMessage + ", seen=" + this.seen + "]";
	}

}
