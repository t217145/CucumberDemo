package com.manulife.test.extsvc01.models;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QueryObject implements Serializable{
    private String id;
    private String acctNo;
    private String amt;
    private String transDtm;
}