package hu.mik.beans;


import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="t_news")
public class News {
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
	@SequenceGenerator(name="SEQ_GEN", sequenceName="s_news", allocationSize=1, initialValue=1)
	private int id;
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	private User newsUser;
	@Column(name="message")
	private String message;
	@Column(name="time",  columnDefinition="TIMESTAMP")
	private Timestamp time;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getNewsUser() {
		return newsUser;
	}
	public void setNewsUser(User newsUser) {
		this.newsUser = newsUser;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	
	
	
}
