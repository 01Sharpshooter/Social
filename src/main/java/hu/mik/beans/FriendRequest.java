package hu.mik.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "t_friendrequests")
public class FriendRequest {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
	@SequenceGenerator(name = "SEQ_GEN", sequenceName = "s_friendrequests", allocationSize = 1, initialValue = 1)
	private Integer id;
	@Column(name = "requestorid")
	private Integer requestorId;
	@Column(name = "requestedid")
	private Integer requestedId;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRequestorId() {
		return this.requestorId;
	}

	public void setRequestorId(Integer requestorId) {
		this.requestorId = requestorId;
	}

	public Integer getRequestedId() {
		return this.requestedId;
	}

	public void setRequestedId(Integer requestedId) {
		this.requestedId = requestedId;
	}

}
