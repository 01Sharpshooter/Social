package hu.mik.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="t_friendrequests")
public class FriendRequest {
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_GEN")
	@SequenceGenerator(name="SEQ_GEN", sequenceName="s_friendrequests", allocationSize=1, initialValue=1)
	private int id;
	@Column(name="requestorid")
	private int requestorId;
	@Column(name="requestedid")
	private int requestedId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRequestorId() {
		return requestorId;
	}
	public void setRequestorId(int requestorId) {
		this.requestorId = requestorId;
	}
	public int getRequestedId() {
		return requestedId;
	}
	public void setRequestedId(int requestedId) {
		this.requestedId = requestedId;
	}
	
	
	
}
