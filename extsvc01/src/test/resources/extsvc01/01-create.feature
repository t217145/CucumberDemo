Feature: Create Payment

    Background: Empty DB
        Given No records return from GET
    
    @createTest
    Scenario Outline: <testCase> <PosNeg> : ESS-3795
        Given I prepare payment
            | id    | acctNo | amt | transDtm |
            | [blank] | <acctNo> | <amt> | [blank] |

        When I trigger "PUT" request
        Then I expect the response is '<expectedResponse>'
        And following payment in response
            | id    | acctNo | amt | transDtm |
            | [blank] | <expectedAcctNo> | <expectedAmt> | [blank] |
        
        Examples:
            | testCase | PosNeg | expectedResponse |acctNo | amt | expectedAcctNo | expectedAmt |
            | Normal Value | Positive | CREATED | 012-345-6-000000-0 | 123.45 | 012-345-6-000000-0 | 123.45 |
            | Negative Amount | Negative | BAD_REQUEST | 012-345-6-000000-0 | -123.45 | [blank] | [blank] |
            | Null Account | Negative | BAD_REQUEST | [blank] | 123.45 | [blank] | [blank] |