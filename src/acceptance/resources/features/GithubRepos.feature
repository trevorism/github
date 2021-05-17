Feature: The github repos are accessible by this user

  Scenario: Getting my repositories
    Given the application is alive
    When the list of repositories is requested
    Then over 20 repositories are returned
    And less than 100 are returned