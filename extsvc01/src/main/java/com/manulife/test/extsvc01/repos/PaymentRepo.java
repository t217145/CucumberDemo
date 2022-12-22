package com.manulife.test.extsvc01.repos;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.manulife.test.extsvc01.models.Payment;

public interface PaymentRepo extends JpaRepository<Payment, Integer>{
    
    @Query("select p from Payment p where (p.id = :id or :id = 0) and (p.acctNo like :acctNo or :acctNo = '') and (p.amt = :amt or :amt = 0) and (p.transDtm <= :transDtm or :transDtm is null)")
    List<Payment> findByQuery(int id, String acctNo, float amt, Date transDtm);
}
