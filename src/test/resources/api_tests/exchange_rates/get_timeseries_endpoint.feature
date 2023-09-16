Feature: Tests for GET time-series endpoint

  Background:
    Given the valid endpoint to GET timeseries

  Scenario: should return 401 error without required auth header
    Given I am not authenticated user
    When I make GET request to timeseries with params
      | startDate  | endDate    | base | symbols |
      | 2023-07-01 | 2023-07-05 |      | USD,PLN |
    Then API responds with 401 status code

  Scenario: should return 401 error for wrong auth tokens
    Given I use "some_random_string" to authenticate
    When I make GET request to timeseries with params
      | startDate  | endDate    | base | symbols |
      | 2023-07-01 | 2023-07-05 |      | USD,PLN |
    Then API responds with 401 status code

  Scenario: should return error for GET without required parameters
    Given I am authenticated user
    When I make GET request to timeseries with params
      | startDate | endDate | base | symbols |
      |           |         |      |         |
    Then API responds with 400 status code
    And Error code is "no_timeframe_supplied"

  Scenario Outline: should return error for invalid format of start date
    Given I am authenticated user
    When I make GET request to timeseries with params
      | startDate   | endDate    | base | symbols |
      | <startDate> | 2023-07-03 |      | USD,PLN |
    Then API responds with 400 status code
    And Error code is "invalid_start_date"

    Examples:
      | startDate   |
      | 2023.07.01  |
      | 02-12-2023  |
      | 2023-07-02,2023-07-05 |

  Scenario Outline: should return error for invalid format of end date
    Given I am authenticated user
    When I make GET request to timeseries with params
      | startDate  | endDate   | base | symbols |
      | 2023-07-01 | <endDate> |      | USD,PLN |
    Then API responds with 400 status code
    And Error code is "invalid_end_date"

    Examples:
      | endDate     |
      | 2023.07.01  |
      | 02-12-2023  |
      | 2023-07-03,2023-07-04 |

  Scenario: should return error for invalid date period
    Given I am authenticated user
    When I make GET request to timeseries with params
      | startDate  | endDate    | base | symbols |
      | 2023-07-04 | 2023-07-01 |      | USD,PLN |
    Then API responds with 400 status code
    And Error code is "invalid_time_frame"

  Scenario: should return error when rates are not available
    Given I am authenticated user
    When I make GET request to timeseries with params
      | startDate  | endDate    | base | symbols |
      | 1955-07-01 | 1955-07-01 |      | USD,PLN |
    Then API responds with 400 status code
    And Error code is "no_rates_available"

  Scenario Outline: should return error for invalid base parameter
    Given I am authenticated user
    When I make GET request to timeseries with params
      | startDate  | endDate    | base   | symbols |
      | 2023-07-01 | 2023-07-03 | <base> | USD,PLN |
    Then API responds with 400 status code
    And Error code is "invalid_base_currency"

    Examples:
      | base    |
      | WWW     |
      | USD,EUR |

  Scenario: should return error for invalid symbol parameter
    Given I am authenticated user
    When I make GET request to timeseries with params
      | startDate  | endDate    | base | symbols |
      | 2023-07-01 | 2023-07-03 |      | WWW     |
    Then API responds with 400 status code
    And Error code is "invalid_currency_codes"

  Scenario: should be idempotent for GET requests
    Given I am authenticated user
    When I make GET request two times with params
      | startDate  | endDate    | base | symbols |
      | 2023-07-01 | 2023-07-03 |      | USD     |
    Then Responses are the same

  Scenario Outline: should return proper data with valid parameters
    Given I am authenticated user
    When I make GET request to timeseries with params
      | startDate   | endDate   | base       | symbols       |
      | <startDate> | <endDate> | <sentBase> | <sentSymbols> |
    Then API responds with 200 status code
    And I get valid response
      | startDate   | endDate   | base           | symbols           |
      | <startDate> | <endDate> | <expectedBase> | <expectedSymbols> |

    Examples:
      | startDate  | endDate    | sentBase | expectedBase | sentSymbols   | expectedSymbols |
      | 2023-07-01 | 2023-07-03 | AFN      | AFN          | USD,PLN       | USD,PLN         |
#      check if params are case sensitive
      | 2023-07-01 | 2023-07-05 | afn      | AFN          | usd,pln       | USD,PLN         |
#      check if EUR is default base param with symbols params
      | 2023-07-01 | 2023-07-07 |          | EUR          | AED, AFN, AMD | AED, AFN, AMD   |
#      check if EUR is default base param without symbols params
      | 2023-07-01 | 2023-07-09 |          | EUR          |               |                 |
#      check if response will be valid for rate between the same currency
      | 2023-07-01 | 2023-07-13 | EUR      | EUR          | EUR           | EUR             |
#      check if API removes not valid symbols
      | 2023-07-01 | 2023-07-23 | EUR      | EUR          | EUR,WWW       | EUR             |
#      check if API does not duplicate data with duplicated symbols
      | 2023-07-01 | 2023-07-13 | EUR      | EUR          | USD,USD       | USD             |



