Feature: Login to UiBank

Scenario: Login to UiBank with right credentials
Given enable logs
When Login is posted with credentials file: login.json
Then Verify the status code is 200
And Print the userId