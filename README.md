The following project is an implementation of a simple social network, containing server and client and using a binary communication protocol.
Users who have registered are able to access the system by logging in. Once logged in, they can follow other users, post messages, and send private messages to other users. The server implementation employs two approaches: Thread-Per-Client and Reactor pattern. These methods determine how the server manages client connections and handles communication between clients and the server. The goal is to enable seamless interaction and data exchange among users within the social network environment.

### Types of Supported Commands
**REGISTER**: This command is used to enroll a user in the service. The format for the birthday should be provided as "dd-mm-yyyy".
Example: REGISTER alice 1234 12-12-1212

**LOGIN**: With this command, a user can access the server. The captcha value must be set to 1 for a successful login, while it can be set to 0 otherwise.
Example: LOGIN alice 1234 1

**LOGOUT**: This command indicates to the server that a client is disconnecting from the service.
Example: LOGOUT

**FOLLOW <0/1>** (0 for follow, 1 for unfollow): This command allows a user to either add or remove another user, specified by their username, from their list of followed users.
Examples:
FOLLOW 0 bob
FOLLOW 1 bob

**POST**: This command lets a user create a post. The post is visible only to the user's followers. Additionally, the user can mention another user in the post using the format @username.
Example: POST Hello World

**PM**: This command enables a user to send a private message to another user. A user can send a PM only to someone who is following them.
Example: PM alice Hello alice

**LOGSTAT**: By using this command, a user can retrieve information about logged-in users. This includes details such as the age of each user, the number of posts they've made, the number of followers they have, and the number of users they are following.
Example: LOGSTAT

**STAT** <username1|username2|username3>: This command provides data on specific users. The list of usernames should be formatted as username1|username2|username3.
Example: STAT alice|bob

**BLOCK**: This command allows a user to block another user. When a user is blocked, they cannot send messages to the user who blocked them, and vice versa. Also, blocked users are excluded from the data displayed in the STAT and LOGSTAT commands. This ensures that blocked users' information is not visible in these statistical reports.
Examle: BLOCK alice


### Filtered Words
A word filtering mechanism is implemented for private messages. Words that are deemed inappropriate or undesirable can be filtered out from private messages.


