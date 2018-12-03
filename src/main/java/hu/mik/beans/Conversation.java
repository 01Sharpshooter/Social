package hu.mik.beans;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "t_conversations")
public class Conversation {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@EqualsAndHashCode.Exclude
	@OneToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.ALL })
	@JoinColumn(name = "last_message", updatable = true)
	private Message lastMessage;
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "conversation", orphanRemoval = true, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private Set<ConversationUser> conversationUsers = new HashSet<>();

	public boolean seenByUser(Integer userId) {
		return this.conversationUsers.stream().filter(cu -> cu.getUser().getId().equals(userId)).findFirst().get()
				.isSeen();
	}

	public void addConversationUser(ConversationUser conversationUser) {
		this.conversationUsers.add(conversationUser);
	}

	public void addConversationUsers(List<ConversationUser> lstConversationUser) {
		lstConversationUser.forEach(cu -> this.conversationUsers.add(cu));
	}

	public List<Integer> getlistOfUserIds() {
		return this.conversationUsers.stream().map(cu -> cu.getUser().getId()).collect(Collectors.toList());
	}

	public List<User> getlistOfUsers() {
		return this.conversationUsers.stream().map(cu -> cu.getUser()).collect(Collectors.toList());
	}

	public int getConversationUserCount() {
		return this.conversationUsers.size();
	}

}
