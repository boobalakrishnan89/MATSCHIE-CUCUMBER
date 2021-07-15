Feature: Get Account Details

Scenario: Get Account Details
Given enable logs
When Authorized using header
And Get the account details with user details
Then Verify the status code is 200
And Print the account number