package com.manulife.test.extsvc01.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.manulife.test.extsvc01.models.ErrorResponse;
import com.manulife.test.extsvc01.models.Payment;
import com.manulife.test.extsvc01.models.QueryObject;
import com.manulife.test.extsvc01.repos.PaymentRepo;
import jakarta.validation.Valid;

@RestController
public class dummyController {
    
    @Autowired
    private PaymentRepo repo;

    @PutMapping("/payment")
    public ResponseEntity<Payment> InsertPayments(@Valid @RequestBody Payment payment){
        payment.setTransDtm(new Date());
        repo.save(payment);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @PostMapping("/payment/query")
    public ResponseEntity<Payment> UpdatePayments(@Valid @RequestBody Payment payment){
        payment.setTransDtm(new Date());
        repo.save(payment);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }   
    
    @GetMapping("/payment")
    public ResponseEntity<List<Payment>> GetAllPayments(){
        return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
    }

    @PostMapping("/payment")
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ResponseEntity<List<Payment>> GetPayments(@RequestBody QueryObject query){
        System.out.println(query.toString());
        int id = (query.getId() == null) ? 0 : Integer.parseInt(query.getId());
        String acct = (query.getAcctNo() == null) ? "" : query.getAcctNo();
        float amt = (query.getAmt() == null) ? 0 : Float.parseFloat(query.getAmt());
        Date transDate = null;
        if(query.getTransDtm() != null){
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(query.getTransDtm());
            try {
                transDate = fmt.parse(query.getTransDtm());
            } catch (Exception e) {
                // Do Nothing
            }   
        }

        //custom validation
        List<String> details = new ArrayList<>();
        ErrorResponse error = new ErrorResponse("Validation Failed", details);
        boolean isValid = true;
        if(amt < 0){
            details.add("Amount must greater than 0");
            isValid = false;
        }
        if(transDate != null && transDate.after(new Date())){
            details.add("Transaction date must be a past date");
            isValid = false;
        }

        if(!isValid){
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }

        List<Payment> results = repo.findByQuery(id, acct, amt, transDate);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @DeleteMapping("/payment/{id}")
    public ResponseEntity<Payment> DeletePayment(@PathVariable("id") int id){
        Optional<Payment> oPayment = repo.findById(id);
        if(!oPayment.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        repo.deleteById(id);
        return new ResponseEntity<>(oPayment.get(), HttpStatus.OK);
    }

    @DeleteMapping("/payment/all")
    public ResponseEntity<Payment> DeleteAllPayments(){
        repo.deleteAll();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}