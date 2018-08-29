package hu.mik.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_user")
public class User {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "username")
	private String username;
	@Column(name = "image")
	private String imageName;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getImageName() {
		return this.imageName;
	}

	public void setImageName(String image) {
		this.imageName = image;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.imageName == null) ? 0 : this.imageName.hashCode());
		result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());
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
		User other = (User) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.imageName == null) {
			if (other.imageName != null) {
				return false;
			}
		} else if (!this.imageName.equals(other.imageName)) {
			return false;
		}
		if (this.username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!this.username.equals(other.username)) {
			return false;
		}
		return true;
	}

}
