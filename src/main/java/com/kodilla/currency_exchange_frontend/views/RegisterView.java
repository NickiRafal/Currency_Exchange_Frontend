package com.kodilla.currency_exchange_frontend.views;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodilla.currency_exchange_frontend.domain.CustomersDTO;
import com.kodilla.currency_exchange_frontend.response.CheckEmailResponse;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Route("register")
@Component
@Scope("prototype")
public class RegisterView extends VerticalLayout {

    protected final TextField firstNameField = new TextField("First name");
    protected final TextField lastNameField = new TextField("Last name");
    protected final EmailField emailField = new EmailField("Email");
    protected final TextField totalSpentField = new TextField("Total spent");
    protected final DatePicker registrationDateField = new DatePicker("Registration date");
    protected final PasswordField passwordField = new PasswordField("Password");
    protected final PasswordField confirmPasswordField = new PasswordField("Confirm Password");
    protected final TextField loginField = new TextField("Login");

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("http://localhost:8081/v1/customers/add")
    private String backendUrl;

    @Autowired
    public RegisterView(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;

        H1 title = new H1("User Registration");
        add(title);
        totalSpentField.setValue("10000");
        registrationDateField.setValue(LocalDate.now());
        FormLayout formLayout = new FormLayout();

        formLayout.add(firstNameField, lastNameField, loginField, emailField, passwordField, confirmPasswordField, totalSpentField, registrationDateField);

        Button registerButton = new Button("Save", event -> {
            try {

                sendDataToBackend();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        add(formLayout, registerButton);
    }

    private void sendDataToBackend() throws JsonProcessingException {
        // Weryfikacja zgodności haseł
        if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
            Notification.show("Hasła nie pasują do siebie!");

            return;
        }
        if (!validatePasswordLength()) {
            return;
        }

        if (!firstNameField.isEmpty() && !lastNameField.isEmpty() && !emailField.isEmpty() &&
                !loginField.isEmpty() && !passwordField.isEmpty()) {

            CustomersDTO customersDTO = new CustomersDTO();
            customersDTO.setFirstName(firstNameField.getValue());
            customersDTO.setLastName(lastNameField.getValue());
            customersDTO.setEmail(emailField.getValue());
            System.out.println(emailField.getValue());
            customersDTO.setTotalSpent(new BigDecimal(totalSpentField.getValue()));
            customersDTO.setRegistrationDate(registrationDateField.getValue());
            customersDTO.setLogin(loginField.getValue());
            customersDTO.setStatus(false);


            if (!validatePasswordLength()){

               return;
            }
            if (isEmailTaken(customersDTO)) {
                showEmailTakenNotification();
                return;
            }


            customersDTO.setPassword(passwordField.getValue());
            System.out.println(passwordField.getValue());

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(customersDTO), headers);

                ResponseEntity<String> response = restTemplate.postForEntity(backendUrl, request, String.class);

                if (response.getStatusCode() == HttpStatus.CREATED) {
                    Notification notification = Notification.show("Użytkownik zarejestrował się pomyślnie! Proszę się zalogować.");
                    notification.setDuration(10000);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    UI.getCurrent().navigate("login");
                } else {
                    Notification notification = Notification.show("Nie udało się zarejestrować użytkownika. Proszę spróbuj ponownie.");
                    notification.setDuration(10000);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                Notification notification = Notification.show("An error occurred. Please try again later.");
                notification.setDuration(10000);
            }
        }
    }

    private void showEmailTakenNotification() {
        Notification notification = Notification.show("Adres e-mail jest już zajęty. Podaj inny.");
        notification.setDuration(10000);
    }

    private boolean isEmailTaken(CustomersDTO customersDTO) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(customersDTO), headers);

            ResponseEntity<CheckEmailResponse> response = restTemplate.postForEntity(
                    backendUrl + "/check-email", request, CheckEmailResponse.class);

            return response.getStatusCode() == HttpStatus.CONFLICT && response.getBody() != null && response.getBody().isEmailTaken();
        } catch (HttpClientErrorException.Conflict ex) {
            // Obsłuż błąd 409 (CONFLICT), np. w przypadku istniejącego adresu e-mail
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // Obsłuż inne błędy
            return false;
        }
    }
    private boolean validatePasswordLength() {
        String password = passwordField.getValue();
        if (password.length() < 5) {
            Notification notification = Notification.show("Hasło musi mieć co najmniej 5 znaków.");
            notification.setDuration(10000);
            return false;
        }
        return true;
    }

}
