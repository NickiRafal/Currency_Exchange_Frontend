package com.kodilla.currency_exchange_frontend.domain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CustomersDTO {

    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal totalSpent;
    private LocalDate registrationDate;
    private String password;
    private String login;
    private Boolean status;
}

