package com.manulife.test.extsvc01.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.Arrays;
import java.util.List;
import com.manulife.test.extsvc01.models.ErrorResponse;
import com.manulife.test.extsvc01.models.Payment;
import com.manulife.test.extsvc01.models.QueryObject;
import com.manulife.test.extsvc01.steps.commons.CommonHTTPSteps;
import io.cucumber.java.Before;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.But;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class PaymentSteps extends CommonHTTPSteps {

    protected String basePath = "http://localhost:8081";

    protected String subPath = "/payment";

    @Before
    public void cleanUp(){
        super.subPath = subPath;
        super.basePath = basePath;
        executeDelete("/all");
        Response response = getContext().getResponse();
        assertEquals(202 ,response.getStatusCode());
    }

    @Given("No records return from GET")
    public void NoRecord(){
        executeGet("");
        Response response = getContext().getResponse();
        assertEquals(200 ,response.getStatusCode());
    }

    @Given("I prepare payment")
    public void preparePayment(List<Payment> payments) {
        Payment payment = payments.get(0);
        getContext().setPayload(payment);
    }

    @When("I trigger {string} request")
    public void SendRequest(String httpAction){
        switch(httpAction){
            case "GET":
                executeGet("");
            break;
            case "PUT":
                executePut("");
            break;
            case "POST":
                executePost("");
            break;
            case "DELETE":
                Payment payment = (Payment)(getContext().getPayload());
                executeDelete(String.format("/%d",payment.getId()));
            break;
            case "DELETEALL":
                executeDelete("/all");
            break;            
            default:
                break;                        
        }
    }

    @Then("I expect the response is {string}")
    public void CheckResponse(String responseType){
        Response response = getContext().getResponse();
        int statusCode = response.getStatusCode();
        switch(responseType){
            case "OK":
                assertEquals(200, statusCode);
            break;
            case "CREATED":
                assertEquals(201, statusCode);
            break;                
            case "ACCEPTED":
                assertEquals(202, statusCode);
            break;            
            case "BAD_REQUEST":
                assertEquals(400, statusCode);
            break;
            case "ERROR":
                assertEquals(500, statusCode);
            break;            
            default:
                break;                        
        }
    }

    @Given("Following records exists in DB")
    @SuppressWarnings("unchecked")
    public void PreparePaymentInDB(List<Payment> payments){
        for (Payment payment : payments) {
            getContext().setPayload(payment);
            executePut("");
        }
        executeGet("");

        Response response = getContext().getResponse();
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
        List<Payment> actualPayments = response.as(List.class);
        assertEquals(2, actualPayments.size());    
    }

    @And("following payment in response")
    public void CompareResponseBody(List<Payment> payments){
        Response response = getContext().getResponse();
        int statusCode = response.getStatusCode();
        
        if(statusCode >= 200 && statusCode <= 299){
            Payment expectedPayment = payments.get(0);
            List<Payment> actualPayments = Arrays.asList(response.as(Payment.class));
            Payment actualPayment = actualPayments.get(0);

            assertNotEquals(0, actualPayment.getId());
            assertEquals(expectedPayment.getAcctNo(), actualPayment.getAcctNo());
            assertEquals(expectedPayment.getAmt(), actualPayment.getAmt());
        } else if(statusCode >= 400 && statusCode <= 499) {
            ErrorResponse actualError = response.as(ErrorResponse.class);
            assertEquals("Validation Failed", actualError.getErrMsg());
        } else if(statusCode >= 500 && statusCode <= 599) {
            ErrorResponse actualError = response.as(ErrorResponse.class);
            assertEquals("Server Error", actualError.getErrMsg());
        }  else {
            Payment actualPayment = getContext().getPayload(Payment.class);
            assertNull(actualPayment);
        }
    }

    @Given("I prepare query")
    public void PrepareQuery(@Transpose QueryObject query){
        getContext().setPayload(query);
    }

    @And("{int} of payment in response")
    @But("{int} of payment in response testing")
    @SuppressWarnings("unchecked")
    public void CheckNumberOfPayment(int numOfPayment){
        Response response = getContext().getResponse();
        int statusCode = response.getStatusCode();

        if(statusCode >= 200 && statusCode <= 299){
            List<Payment> actualPayments = response.as(List.class);
            assertEquals(numOfPayment, actualPayments.size());
        } else {
            ErrorResponse actualError = null;
            try{
                actualError = response.as(ErrorResponse.class);
            } catch(Exception e) {
                //cast failed
            }
            assertNotNull(actualError);
        }
    }

    @And("{int} of error in response")
    public void CheckNumberOfError(int numOfError){
        Response response = getContext().getResponse();
        int statusCode = response.getStatusCode();

        if(numOfError == 0){
            if(statusCode >= 200 && statusCode <= 299){
                ErrorResponse actualError = null;
                try{
                    actualError = response.as(ErrorResponse.class);
                } catch(Exception e) {
                    //cast failed
                }
                assertNull(actualError);
            } else {
                ErrorResponse actualError = null;
                try{
                    actualError = response.as(ErrorResponse.class);
                } catch(Exception e) {
                    //cast failed
                }
                assertNotNull(actualError);
            }
        }
    }
    
    @And("{string} in error detail")
    public void CheckErroDetails(String errorDetails){
        Response response = getContext().getResponse();
        int statusCode = response.getStatusCode();

        if(statusCode >= 400 && statusCode <= 499){
            ErrorResponse actualError = null;
            try{
                actualError = response.as(ErrorResponse.class);
            } catch(Exception e) {
                //cast failed
            }
            assertNotNull(actualError);

            if(actualError != null){
                List<String> details = actualError.getErrDetails();
                assertEquals(errorDetails, details.get(0));
            }
        }
    } 

}