The runnable JAR can be found in the /target folder. 
The application must be configured before first run! To do so, please follow these instructions:

-----------------------------
Requirements:
----------------------------- 
JRE: 1.8 or higher
PostgreSQL: 10.5 or higher
Directory server: Anything that supports X.500 LDAP
-----------------------------

There is an application.yml file next to the JAR, in which you have to configure the connections. 
Important! If you recompile the project, the YAML file may get deleted. 
In that case, there is one sample in the root folder which can be copied back into the /target folder.

-----------------------------
Database installation steps:
-----------------------------
 1. Make sure that you have the above mentioned PostgreSQL version installed.
 2. Move to /sql/Postgre/Installation
 3. run Create_Database.sql
 4. run	Create_User.sql
 5. connect to the freshly created SocialDB database
 6. run Init_Database.sql
----------------------------
The configuration file:
----------------------------
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
folder:
  image: The folder responsible for storing user images, absolute path preferred, e.g., C:/social/images/  
----------------------------
LDAP:
----------------------------
Unfortunately, because of some Spring limitations, the LDAP search base is not externally configurable in this application.
At the moment, it is configured to read from the dc=social,dc=com partition. If you would like to change that, you will have to 
change the SEARCH_BASE constant in hu/mik/constants/LdapConstants.java then recompile the project. Sorry for the inconvenience.

----------------------------
MAVEN CLEAN AND INSTALL
----------------------------
In case you are trying to build the project from command line with maven clean install,
you might have to run the build process more times before Vaadin is able to copy the widgetset into the JAR.

-----------------------------
After setting all these values, the application should start without a problem.
