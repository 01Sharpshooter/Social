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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_messages")
public class Message {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@NonNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "sender_id", updatable = false)
	private User sender;
	@NonNull
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "conversation_id")
	private Conversation conversation;
	@NonNull
	@Column(name = "message")
	private String message;
	@NonNull
	@Column(name = "time", columnDefinition = "TIMESTAMP")
	private Timestamp time;

}
