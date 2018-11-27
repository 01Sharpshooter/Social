The runnable JAR can be found in the /target folder. 
The application must be configured before first run! To do so, please follow these instructions:

There is an application.yml file next to the JAR, in which you have to configure the connections. 
Important! If you recompile the project, the YAML file may be deleted. 
In this case, there is one sample in the root folder which can be copied back into the /target folder.

Requirements: 
	JRE: 1.8 or higher
	PostgreSQL: 10.5 or higher
	Directory server: Anything that supports X.500 LDAP	

The configuration file:

server:
  port: The port to which the application is going to listen to, e.g., 8888
spring:
  datasource:
    password: The password used for database connection, e.g., Password123
    url: The jdbc url of the PostgreSQL database connection, e.g., jdbc:postgresql://localhost:5432/SocialDB
    username: The username used for database connection, e.g., social
  ldap:
    password: The password of the user responsible for LDAP connection, e.g., secret
    urls: The connection url of the directory server, e.g., ldap://localhost:10389/
    username: The distinguished name of the user responsible for LDAP connection, e.g., uid=admin, ou=system
    base: The starting point of LDAP queries, e.g., dc=social,dc=com    
folder:
  image: The folder responsible for storing user images, absolute path preferred, e.g., C:/social/images/

After setting all these values, the application should start without a problem.
