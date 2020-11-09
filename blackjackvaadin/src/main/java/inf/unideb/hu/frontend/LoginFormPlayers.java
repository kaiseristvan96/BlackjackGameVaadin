package inf.unideb.hu.frontend;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import inf.unideb.hu.backend.entity.PersonEntity;
import inf.unideb.hu.backend.service.PersonService;
import org.apache.commons.codec.digest.DigestUtils;


@CssImport("./styles/styles.css")
@Route("login")
@PageTitle("Blackjack Login Page")
public class LoginFormPlayers extends VerticalLayout implements BeforeEnterObserver {
    private PersonService personService;

    LoginForm playerLoginForm = new LoginForm();
    //when login is completed redirect to main page

    PersonEntity user = new PersonEntity();

    public LoginFormPlayers(PersonService personService) {
        if (VaadinSession.getCurrent().getAttribute("username") != null) {
            UI.getCurrent().navigate("login");
        } else {
            this.personService = personService;
            addClassName("main-view");

            //navigation tabs
            HorizontalLayout topTabs = new HorizontalLayout();
            topTabs.addClassName("top-bar");
            VerticalLayout topTabsPosition = new VerticalLayout();
            Anchor mainPage = new Anchor("", "Main page");
            Anchor loginPage = new Anchor("login", "Login page");
            Anchor registerPage = new Anchor("register", "Register page");
            Anchor gamePage = new Anchor("game", "Game page");
            Anchor logoutPage = new Anchor("logout", "Logout page");
            Anchor highscoresPage = new Anchor("highscores", "High Scores page");
            mainPage.addClassName("anchors");
            loginPage.addClassName("anchors");
            registerPage.addClassName("anchors");
            gamePage.addClassName("anchors");
            highscoresPage.addClassName("anchors");
            logoutPage.addClassName("anchors");

            topTabs.add(mainPage, gamePage, highscoresPage, loginPage, registerPage, logoutPage);

            //Move everything to the top middle
            topTabs.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            topTabs.setAlignItems(FlexComponent.Alignment.CENTER);
            topTabsPosition.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
            topTabsPosition.getStyle().set("padding-top", "20px");
            topTabsPosition.getStyle().set("margin-top", "0px");
            topTabsPosition.add(topTabs);
            if (VaadinSession.getCurrent().getAttribute("username") != null) {
                registerPage.setVisible(false);
                loginPage.setVisible(false);
                logoutPage.setVisible(true);
                gamePage.setVisible(true);
                UI.getCurrent().navigate("");
            } else {
                registerPage.setVisible(true);
                loginPage.setVisible(true);
                logoutPage.setVisible(false);
                gamePage.setVisible(false);
            }


            setSizeFull();

            setAlignItems(Alignment.CENTER);

            playerLoginForm.setForgotPasswordButtonVisible(false);

            playerLoginForm.addLoginListener(event -> {
                user = personService.findByUsername(event.getUsername());
                if (user != null) {
                    String hashedPass = event.getPassword() + user.getSalt();
                    //hashing the password
                    hashedPass = new DigestUtils("SHA3-256").digestAsHex(hashedPass);
                    //check if hashed passwords match
                    if (hashedPass.equals(user.getPassword())) {
                        VaadinSession.getCurrent().setAttribute("username", user.getUsername().toString());
                        UI.getCurrent().navigate("");
                    } else {
                        Notification.show("Wrong username or password. Try again.", 5000, Notification.Position.BOTTOM_CENTER);
                        playerLoginForm.setEnabled(true);

                    }
                } else {
                    Notification.show("Wrong username or password. Try again.", 5000, Notification.Position.BOTTOM_CENTER);
                    playerLoginForm.setEnabled(true);
                }
            });

            H2 pleaseLogin = new H2("Please log in before playing the game.");
            pleaseLogin.addClassName("welcome-message");
            add(topTabsPosition, pleaseLogin, playerLoginForm);

        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (VaadinSession.getCurrent().getAttribute("username") != null) {
            beforeEnterEvent.rerouteTo("");
        }
    }

}
