Feature: Validating the API's in ReqRes
  
  Scenario: Test to verify "Users" resource return success with query parameter
    Given I make a call to "Users" of "ReqRes" API
    When Method is "Get" type is "query" with parameter "page" and value as "2"
    Then response has status code as 200
    And "per_page" in response body is 6
    And "total" in response body is 12
    And "data[0].email" in response body is "michael.lawson@reqres.in"

  @Regression
  Scenario: Test to verify "Users" resource return success with path parameter
    Given I make a call to "Users" of "ReqRes" API
    When Method is "Get" type is "path" with parameter "users" and value as "2"
    Then response has status code as 200
    And "data.id" in response body is 2
    And "ad.company" in response body is "StatusCode Weekly"

  Scenario: Test for an error scenario
    Given I make a call to "Users" of "ReqRes" API
    When Method is "Get" type is "path" with parameter "users" and value as "23"
    Then response has status code as 404

  Scenario: Test to verify "Unknown" resource return success
    Given I make a call to "Unknown" of "ReqRes" API
    When Method is "Get"
    Then response has status code as 200
    And schema matches "TestSchema"

  @Regression
  Scenario: Test to verify "Unknown" resource return success and is in sync with json in file
    Given I make a call to "Unknown" of "ReqRes" API
    When Method is "Get" type is "path" with parameter "unknown" and value as "2"
    Then response has status code as 404
    And json matches with "Matching.json"

  Scenario Outline: Test to verify "Users" resource return success when method is "Post"
    Given I make a call to "Users" of "ReqRes" API
    And inputs are "<Name>" and "<Job>"
    When Method is "Post"
    Then response has status code as 201

    Examples: 
      | Name    | Job     |
      | Wilfred | QA      |
      | Joseph  | Lead    |
      | Clement | Manager |

  @sanity
  Scenario Outline: Test to verify if we are able to post data to "Users" resource by fetching input from excel
    Given I make a call to "Users" of "ReqRes" API
    And inputs for "<TCID>" are fetched from "Sheet1" of "Users.xlsx" 
    When Method is "Post"
    Then response has status code as 201

    Examples: 
      | TCID  |
      | TC_01 |
      | TC_03 |
      | TC_02 |
