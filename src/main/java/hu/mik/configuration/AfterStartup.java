package hu.mik.configuration;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import hu.mik.beans.LdapUser;
import hu.mik.beans.User;
import hu.mik.services.LdapService;
import hu.mik.services.UserService;

@Configuration
public class AfterStartup {
	@Autowired
	private LdapService ldapService;
	@Autowired
	private UserService userService;

	@EventListener(value = ApplicationReadyEvent.class)
	private void initializeApp() {
		this.registerAllUsersInDatabase();
	}

	public void registerAllUsersInDatabase() {
		Map<String, User> dbUserMap = this.userService.listAll().stream()
				.collect(Collectors.toMap(User::getUsername, Function.identity()));
		Map<String, LdapUser> ldapUserMap = this.ldapService.findAllUsers().stream()
				.collect(Collectors.toMap(LdapUser::getUsername, Function.identity()));

		for (Entry<String, User> dbUserEntry : dbUserMap.entrySet()) {
			if (!ldapUserMap.containsKey(dbUserEntry.getKey())) {
				this.userService.disable(dbUserEntry.getValue());

			} else {
				LdapUser ldapUser = ldapUserMap.get(dbUserEntry.getKey());
				dbUserEntry.getValue().setFullName(ldapUser.getFullName());
				dbUserEntry.getValue().setEnabled(true);
				this.userService.save(dbUserEntry.getValue());
			}
		}
		ldapUserMap.keySet().stream().filter(username -> !dbUserMap.keySet().contains(username)).forEach(username -> {
			User user = new User();
			user.setUsername(username);
			user.setFullName(ldapUserMap.get(username).getFullName());
			this.userService.registerDefaultUser(user);
		});

	}

}
