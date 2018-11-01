package hu.mik.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserWrapper {
	private User dbUser;
	private LdapUser ldapUser;

}
