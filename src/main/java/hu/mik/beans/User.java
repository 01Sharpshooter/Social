package hu.mik.beans;

import java.io.File;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.vaadin.server.FileResource;
import com.vaadin.ui.Image;

import hu.mik.constants.UserConstants;

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
	@Column(name = "full_name")
	private String fullName;
	@Column(name = "enabled")
	private boolean enabled;
	@OneToMany(mappedBy = "user")
	private Set<ConversationUser> conversationUserSet;

	@Transient
	private Image image;

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

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Image getVaadinImage() {
		File file = new File(UserConstants.PROFILE_PICTURE_LOCATION + this.imageName);
//		if (this.image == null /* || !this.image.getSource().equals(new FileResource(file)) */) { // TODO caching issues
		if (!file.exists()) {
			file = new File(UserConstants.PROFILE_PICTURE_LOCATION + UserConstants.DEFAULT_PROFILE_PICTURE_NAME);
		}
		this.image = new Image(null, new FileResource(file));
//		}
		return this.image;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
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
		if (this.username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!this.username.equals(other.username)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + this.id + ", username=" + this.username + ", imageName=" + this.imageName + ", fullName="
				+ this.fullName + ", enabled=" + this.enabled + "]";
	}

}
