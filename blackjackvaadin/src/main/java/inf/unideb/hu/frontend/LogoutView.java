package inf.unideb.hu.frontend;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;


@CssImport("./styles/styles.css")
@Route("logout")
@PageTitle("Blackjack logout")
public class LogoutView extends VerticalLayout {


    public LogoutView() {


    }

    protected void onAttach(AttachEvent attachEvent) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        UI ui = getUI().get();
        Button logoutBn = new Button("Logout", event -> {
            // Close the session
            VaadinSession.getCurrent().getSession().invalidate();
            // Redirect this page immediately
            ui.getPage().executeJs("window.location.href=''");
            //ui.getSession().close();
        });


        logoutBn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutBn.getStyle().set("color", "red");
        logoutBn.getStyle().set("size", "l");
        logoutBn.getStyle().set("border-style", "solid");
        logoutBn.getStyle().set("border-width", "1px");

        Anchor mainPage = new Anchor("", "Main page");
        mainPage.getStyle().set("size", "l");
        H3 information = new H3("Are you sure?");
        HorizontalLayout options = new HorizontalLayout(logoutBn, mainPage);
        options.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        add(information, options);

        // Notice quickly if other UIs are closed
        ui.setPollInterval(3000);
    }
}
