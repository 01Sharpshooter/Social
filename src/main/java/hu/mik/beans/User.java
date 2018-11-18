package hu.mik.beans;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Image;

import hu.mik.constants.UserConstants;
import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "t_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "users")
public class User implements Serializable {
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

	public Image getVaadinImage() {
		if (this.imageName != null) {
			File file = new File(UserConstants.getImageLocation() + this.imageName);
			if (file.exists()) {
				return new Image(null, new FileResource(file));
			}
		}
		return new Image(null, new ThemeResource(UserConstants.DEFAULT_PROFILE_PICTURE));
	}

}
