Feature: Tests for GET time-series endpoint

  Background:
    Given the valid endpoint to GET timeseries

  Scenario: should return 401 error as unauthenticated user
    Given I am not authenticated user
    When I make a GET request
    Then API responds with 401 status code

  Scenario: should return error for GET without required parameters
    Given I am authenticated user
    When I make a GET request
    Then API responds with 400 status code
    And Error code is "no_timeframe_supplied"

  Scenario Outline: should return error for invalid format of start date
    Given I am authenticated user
    When I GET timeseries between "<startDate>" and "2023-07-03" dates
    Then API responds with 400 status code
    And Error code is "invalid_start_date"
    Examples:
      | startDate   |
      | 2023.07.01  |
      | 02-12-2023  |
      | dfsfsdfsdfs |

  Scenario Outline: should return error for invalid format of end date
    Given I am authenticated user
    When I GET timeseries between "2020-02-01" and "<endDate>" dates
    Then API responds with 400 status code
    And Error code is "invalid_end_date"
    Examples:
      | endDate     |
      | 2023.07.01  |
      | 02-12-2023  |
      | dfsfsdfsdfs |

  Scenario: should return error for invalid date period
    Given I am authenticated user
    When I GET timeseries between "2023-07-20" and "2023-07-10" dates
    Then API responds with 400 status code
    And Error code is "invalid_time_frame"

  Scenario Outline: should return error for invalid base parameter
    Given I am authenticated user
    When I GET timeseries with "<base>" as base currency
    Then API responds with 400 status code
    And Error code is "invalid_base_currency"

    Examples:
      | base |
      | WWW  |
      | ___  |
      | EU   |

  Scenario: should return data with valid time period
    Given I am authenticated user
    When I GET timeseries with params
      | startDate  | endDate    | base | symbols |
      | 2023-07-01 | 2023-07-03 | EUR  | USD,PLN |
    Then I get valid response
      | startDate  | endDate    | base | symbols |
      | 2023-07-01 | 2023-07-03 | EUR  | USD,PLN |
