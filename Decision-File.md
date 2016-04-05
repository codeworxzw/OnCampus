Decision File
==========

##Mobile Authentication

1. Let the user/App authenticate with Facebook using their Login Button.

2.  App gets User Access Token and Facebook ID from Facebook.

3.  Hash this with other metadata and send it to my server.

4.  Unhash it, try it against Facebook Graph API.

5.  If successful, search users database for Facebook API.

6.  If found, Authenticate user, store User Access Token, and send JWT to App.

7.  If not found, create new user with that info. Then Authenticate.

