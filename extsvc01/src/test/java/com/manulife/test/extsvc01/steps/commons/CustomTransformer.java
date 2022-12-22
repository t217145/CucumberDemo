package com.manulife.test.extsvc01.steps.commons;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import com.manulife.test.extsvc01.models.Payment;
import com.manulife.test.extsvc01.models.QueryObject;

import io.cucumber.java.DataTableType;

public class CustomTransformer {

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

    @DataTableType
    public QueryObject query(Map<String, String> entry){
        return new QueryObject(
            entry.get("id"),
            entry.get("acctNo"),
            entry.get("amt"),
            entry.get("transDtm")
        );
    }
}
