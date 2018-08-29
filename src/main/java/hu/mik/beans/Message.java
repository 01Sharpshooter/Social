package hu.mik.beans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_messages")
public class Message {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "senderid", updatable = false)
	private User sender;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "receiverid", updatable = false)
	private User receiver;
	@Column(name = "message")
	private String message;
	@Column(name = "time", columnDefinition = "TIMESTAMP")
	private Timestamp time;
	@Column(name = "seen")
	private boolean seen;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getSender() {
		return this.sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return this.receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Timestamp getTime() {
		return this.time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public boolean isSeen() {
		return this.seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	@Override
	public String toString() {
		return "Message [id=" + this.id + ", senderId=" + this.sender + ", receiverId=" + this.receiver + ", message="
				+ this.message + ", time=" + this.time + ", seen=" + this.seen + "]";
	}

}
