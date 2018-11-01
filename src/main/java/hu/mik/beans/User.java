package hu.mik.beans;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.vaadin.server.FileResource;
import com.vaadin.ui.Image;

import hu.mik.constants.UserConstants;
import lombok.Data;

@Data
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
	@Transient
	private Image image;

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

}
