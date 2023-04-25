package com.imbuka.accounts.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CustomerDetails {

    private Accounts accounts;
    private List<Loans> loans;
    private List<Cards> cards;
}
