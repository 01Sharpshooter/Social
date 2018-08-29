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
@Table(name = "t_news")
public class News {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", updatable = false)
	private User user;
	@Column(name = "message")
	private String message;
	@Column(name = "time", columnDefinition = "TIMESTAMP")
	private Timestamp time;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
		result = prime * result + ((this.time == null) ? 0 : this.time.hashCode());
		result = prime * result + ((this.user == null) ? 0 : this.user.hashCode());
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
		News other = (News) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!this.message.equals(other.message)) {
			return false;
		}
		if (this.time == null) {
			if (other.time != null) {
				return false;
			}
		} else if (!this.time.equals(other.time)) {
			return false;
		}
		if (this.user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!this.user.equals(other.user)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "News [id=" + this.id + ", user=" + this.user + ", message=" + this.message + ", time=" + this.time
				+ "]";
	}

}
