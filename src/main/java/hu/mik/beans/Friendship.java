package hu.mik.beans;

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
@Table(name = "t_friendships")
public class Friendship {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "userid")
	private User user;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "friendid")
	private User friend;

	public Friendship() {
		super();
	}

	public Friendship(User user, User friend) {
		super();
		this.user = user;
		this.friend = friend;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getFriend() {
		return this.friend;
	}

	public void setFriend(User friend) {
		this.friend = friend;
	}

}
