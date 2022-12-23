@wrongTest
Feature: Query Payment

    Background: Insert some records
        Given I prepare payment
            | id    | acctNo | amt | transDtm |
            | [blank] | 012-345-6-000000-0 | 123.45 | [blank] |
        
        When I trigger "PUT" request
        Then I expect the response is 'CREATED'

        Given I prepare payment
            | id    | acctNo | amt | transDtm |
            | [blank] | 012-888-9-123456-7 | 543.21 | [blank] |
        
        When I trigger "PUT" request
        Then I expect the response is 'CREATED'               
            
    @queryTest
    Scenario Outline: <testCase> <PosNeg>
        Given I prepare query
            | id | <id> |
            | acctNo | <acctNo> |
            | amt | <amt> |
            | transDtm | <transDtm> |

        When I trigger "POST" request
        Then I expect the response is '<expectedResponse>'
        And <expectedNumOfReturn> of payment in response
        And <expectedNumOfError> of error in response
        And '<expectedErrorMsg>' in error detail
        
        Examples:
            | testCase | PosNeg | expectedResponse | id | acctNo | amt | transDtm | expectedNumOfReturn | expectedNumOfError | expectedErrorMsg |
            | Search by valid acctNo | Positive | OK | | 012-345-6-000000-0 | | | 1 | 0 | |
            | Search by wildcard acctNo | Positive | OK | | 012% | | | 2 | 0 | |
            | Search by all empty | Positive | OK | | | | | 2 | 0 | |
            | Search by negative amount | Negative | BAD_REQUEST | | | -10 | | 0 | 1 | Amount must greater than 0 |
            | Search by future date | Negative | BAD_REQUEST | | | | 2046-12-31 00:00:00 | 0 | 1 | Transaction date must be a past date |
            | Search by valid amt | Positive | OK | | | 543.21 | | 1 | 0 | |