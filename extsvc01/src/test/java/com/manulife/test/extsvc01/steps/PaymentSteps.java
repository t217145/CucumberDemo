package com.manulife.test.extsvc01.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.manulife.test.extsvc01.models.ErrorResponse;
import com.manulife.test.extsvc01.models.Payment;
import com.manulife.test.extsvc01.steps.commons.CommonHTTPSteps;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class PaymentSteps extends CommonHTTPSteps {

    protected String basePath = "http://localhost:8081";

    protected String subPath = "/payment";
    
    @DataTableType(replaceWithEmptyString = "[blank]")
    public Payment transformer(Map<String, String> row) {
        String dtmString = row.get("transDtm");
        Date transDtm = null;
        if(dtmString != null && !dtmString.trim().equals("")){
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                transDtm = fmt.parse(dtmString);
            } catch (Exception e) {
                // Do Nothing
            }   
        }
        String idStr = row.get("id");
        int id = 0;
        if(idStr != null && !idStr.trim().equals("")){
            try {
                id = Integer.parseInt(idStr);
            } catch (Exception e) {
                // Do Nothing
            }   
        }
        String amtStr = row.get("amt");
        float amt = 0;
        if(amtStr != null && !amtStr.trim().equals("")){
            try {
                amt = Float.parseFloat(amtStr);
            } catch (Exception e) {
                // Do Nothing
            }   
        }        
        return new Payment(id, row.get("acctNo"), amt, transDtm);
    }

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

    @Given("I prepare payment$")
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
}