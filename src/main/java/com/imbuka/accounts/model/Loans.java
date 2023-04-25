package com.imbuka.accounts.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class Loans {

    private int loanNumber;

    private int customerId;

    private Date startDt;

    private String loanType;

    private int totalLoan;

    private int amountPaid;

    private int outstandingAmount;

    private String createDt;
}
