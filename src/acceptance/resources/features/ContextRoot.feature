
Feature: Context Root of this API
  In order to use the API, it must be available

  Scenario: ContextRoot on dns
    Given the application is alive
    When I navigate to "https://github.project.trevorism.com"
    Then then a link to the help page is displayed

  Scenario: Ping on dns
    Given the application is alive
    When I ping the application deployed to "https://github.project.trevorism.com"
    Then pong is returned, to indicate the service is alive