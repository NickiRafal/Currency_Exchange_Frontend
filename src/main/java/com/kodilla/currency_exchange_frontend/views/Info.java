package com.kodilla.currency_exchange_frontend.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("info")
public class Info extends VerticalLayout {

    private final int charLimit = 100; // Przykładowy limit znaków

    public Info() {
        TextArea textArea = new TextArea();
        textArea.setLabel("Comment");
        textArea.setMaxLength(charLimit);
        textArea.setValueChangeMode(ValueChangeMode.EAGER);
        textArea.addValueChangeListener(e -> {
            e.getSource().setHelperText(e.getValue().length() + "/" + charLimit);
        });
        textArea.setValue("Jest to system z wirtualną walutą");

        add(textArea); // Dodajemy TextArea do kontenera
        RouterLink back = new RouterLink("Powrót",MainView.class);
        add(back);
    }
}
