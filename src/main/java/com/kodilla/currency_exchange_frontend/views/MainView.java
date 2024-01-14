package com.kodilla.currency_exchange_frontend.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Route("main")
public class MainView extends VerticalLayout {

    private final RestTemplate restTemplate;
    private final Grid<JSONObject> grid;

    @Autowired
    public MainView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.grid = new Grid<>();
        init();
    }

    private void init() {
        // Przycisk zaloguj
        Button loginButton = new Button("Zaloguj");
        loginButton.addClickListener(e -> {
            // Nawigacja do widoku LoginView
            UI.getCurrent().navigate(LoginView.class);
        });
        // Przycisk rejestracja
        Button regButton = new Button("Utwórz konto");
        regButton.addClickListener(e -> {
            // Nawigacja do widoku Registration
            UI.getCurrent().navigate(RegisterView.class);
        });
        // Kontener do umieszczenia przycisku logowania na prawej stronie
        HorizontalLayout loginLayout = new HorizontalLayout();
        loginLayout.setWidthFull();
        loginLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END); // Ustawienie przycisku logowania na końcu

        loginLayout.add(loginButton);
        loginLayout.add(regButton);
        H1 banner = new H1("Kantor wymiany walut");

        // Kontener dla banera
        VerticalLayout bannerLayout = new VerticalLayout();
        bannerLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        bannerLayout.add(banner);

        // Przyciski menu
        RouterLink homeTab = new RouterLink("Home", MainView.class);

        RouterLink infoLink = new RouterLink("Info", Info.class);




        // Kontener dla menu
        HorizontalLayout menu = new HorizontalLayout();
        menu.setWidthFull();
        menu.setSpacing(true);
        menu.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        menu.add(homeTab);
        menu.add(infoLink);



        add(loginLayout,bannerLayout, menu,  grid); // Dodanie banera, menu, przycisku logowania i siatki danych do layoutu
        grid.setWidth("800px");
        grid.setHeight("600px");

        grid.addColumn((ValueProvider<JSONObject, String>) jsonObject -> jsonObject.getString("currency"))
                .setHeader("Waluta")
                .setKey("currency")
                .setWidth("150px");
        grid.addColumn((ValueProvider<JSONObject, String>) jsonObject -> jsonObject.getString("code"))
                .setHeader("Code")
                .setKey("code")
                .setWidth("50px");
        grid.addColumn((ValueProvider<JSONObject, Double>) jsonObject -> jsonObject.getDouble("bid"))
                .setHeader("Sprzedaż")
                .setKey("bid")
                .setWidth("80px");
        grid.addColumn((ValueProvider<JSONObject, Double>) jsonObject -> jsonObject.getDouble("ask"))
                .setHeader("Kupno")
                .setKey("ask")
                .setWidth("80px");
// Dodanie kolumny z przyciskiem

        grid.addComponentColumn(item -> {

            Button bayButton = new Button("Kup");
            bayButton.addClickListener(e -> {
                // Obsługa logiki dla przycisku "Wymień"
                // Uzyskaj dostęp do danych wiersza, np. item.getItem().getString("currency")
            });
            return bayButton;
        }).setHeader("Kup");
// Dodanie kolumny z przyciskiem

        grid.addComponentColumn(item -> {

            Button askButton = new Button("Sprzedaj");
            askButton.addClickListener(e -> {
                // Obsługa logiki dla przycisku "Wymień"
                // Uzyskaj dostęp do danych wiersza, np. item.getItem().getString("currency")
            });
            return askButton;
        }).setHeader("Sprzedaj");



        fetchCurrencyData();
    }

    private void fetchCurrencyData() {
        String apiUrl = "http://api.nbp.pl/api/exchangerates/tables/C/";

        String response = restTemplate.getForObject(apiUrl, String.class);

        // Dodaj log, aby sprawdzić, czy otrzymujesz odpowiedź
        System.out.println("Odpowiedź z API: " + response);

        JSONArray jsonArray = new JSONArray(response);
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        JSONArray rates = jsonObject.getJSONArray("rates");

        // Pobieranie danych z rates i dodanie do listy
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = 0; i < rates.length(); i++) {
            dataList.add(rates.getJSONObject(i));
        }

        // Dodaj log, aby sprawdzić, czy dane są dodawane do listy
        System.out.println("Liczba danych: " + dataList.size());

        // Ustawienie danych w siatce poprzez ListDataProvider
        ListDataProvider<JSONObject> dataProvider = new ListDataProvider<>(dataList);
        grid.setDataProvider(dataProvider);
    }
}
