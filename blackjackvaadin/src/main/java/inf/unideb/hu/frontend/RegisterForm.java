package inf.unideb.hu.frontend;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import inf.unideb.hu.backend.entity.PersonEntity;
import inf.unideb.hu.backend.service.PersonService;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Random;


@Route("register")
@PageTitle("Blackjack Registration Page")
@CssImport("../frontend/styles/styles.css")
public class RegisterForm extends VerticalLayout {
    private PersonService personService;

    TextField usernameField = new TextField();
    TextField emailField = new TextField("Email");
    PasswordField pwfield = new PasswordField();
    PasswordField pwfieldAgain = new PasswordField();


    public RegisterForm(PersonService personService) {
        addClassName("main-view");
        this.setSizeFull();
        this.personService = personService;

        HorizontalLayout topTabs = new HorizontalLayout();
        topTabs.addClassName("top-bar");
        VerticalLayout topTabsPosition = new VerticalLayout();
        Anchor mainPage = new Anchor("", "Main page");
        Anchor loginPage = new Anchor("login", "Login page");
        Anchor registerPage = new Anchor("register", "Register page");
        Anchor gamePage = new Anchor("game", "Game page");
        Anchor highscoresPage = new Anchor("highscores", "High Scores page");
        Anchor logoutPage = new Anchor("logout", "Logout page");
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
        } else {
            registerPage.setVisible(true);
            loginPage.setVisible(true);
            logoutPage.setVisible(false);
            gamePage.setVisible(false);

        }

        add(topTabsPosition);

        Button saveButton = new Button("Register");

        VerticalLayout mainLayout = new VerticalLayout();

        usernameField.setLabel("Username");
        usernameField.setPlaceholder("Username...");
        usernameField.getElement().setProperty("title", "Enter your desired username.");


        emailField.setClearButtonVisible(true);
        emailField.setPlaceholder("email@email.com...");
        emailField.setErrorMessage("Please enter a valid email address.");
        emailField.getElement().setProperty("title", "Enter your e-mail address.");


        pwfield.setLabel("Password");
        pwfield.setPlaceholder("Enter password...");
        pwfield.setMinLength(5);
        pwfield.getElement().setProperty("title", "Enter your desired password.\nIt must be at least 5 characters long.");


        pwfieldAgain.setLabel("Password repeat");
        pwfieldAgain.setPlaceholder("Enter password again...");
        pwfieldAgain.setMinLength(5);
        pwfieldAgain.getElement().setProperty("title", "Enter your desired password again.");


        mainLayout.addClassName("register-form");
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainLayout.add(usernameField, emailField, pwfield, pwfieldAgain);
        mainLayout.setSizeUndefined();

        saveButton.addClickListener(event -> {
            if (checkCorrectPasswords() && isUsernameCorrect(usernameField.getValue()) && isEmailCorrect(emailField.getValue())) {
                String generatedSalt = generateRandomSalt();
                String passw = pwfield.getValue() + generatedSalt;
                //hashing the password
                passw = new DigestUtils("SHA3-256").digestAsHex(passw);
                //create new person with hashed password
                PersonEntity user = new PersonEntity(usernameField.getValue(), emailField.getValue(), 100L, passw, generatedSalt);
                personService.save(user);
                Notification.show("Registration successful", 5000, Notification.Position.MIDDLE);
                clearAll();
                UI.getCurrent().navigate("login");
            } else {
                Notification.show("Registration unsuccessful", 5000, Notification.Position.BOTTOM_CENTER);
            }

        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);


        mainLayout.add(saveButton);

        add(mainLayout);
    }

    public String generateRandomSalt() {
        String salt = "";
        char[] letterArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4','5','6','7','8','9','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T','U', 'V', 'W', 'X', 'Y', 'Z'};
        Random rngenerator = new Random();
        int randomNumber;
        for (int i = 0; i < 14; i++) {
            randomNumber = rngenerator.nextInt() % 61;
            if (randomNumber < 0) {
                randomNumber = randomNumber * -1;
            }
            salt += String.valueOf(letterArray[randomNumber]);
        }
        return salt;
    }

    public boolean checkCorrectPasswords() {
        if (pwfield.getValue().length() < 5) {
            Notification.show("Password length has to be 5 characters or more", 5000, Notification.Position.MIDDLE);
            return false;
        }

        if (!(pwfield.getValue().equals(pwfieldAgain.getValue()))) {
            Notification.show("Passwords don't match", 5000, Notification.Position.MIDDLE);
            return false;
        }
        return true;
    }

    public boolean isEmailCorrect(String email) {
        PersonEntity foundEmails = personService.findByEmail(email);
        if (email.isEmpty() || (!email.contains("@"))) {
            Notification.show("Empty or bad e-mail.\nPlease try again.", 5000, Notification.Position.MIDDLE);
            return false;
        } else if (!(foundEmails == null)) {
            Notification.show("This e-mail is already registered in our database.\nPlease try again with a different one.", 10000, Notification.Position.MIDDLE);
            return false;
        } else {
            return true;
        }
    }

    public boolean isUsernameCorrect(String username) {
        PersonEntity foundNames = personService.findByUsername(username);
        if (username.isEmpty()) {
            Notification.show("Empty username.\nPlease try again.", 5000, Notification.Position.MIDDLE);
            return false;
        } else if (!(foundNames == null)) {
            Notification.show("This username is already registered in our database.\nPlease try again with a different one.", 10000, Notification.Position.MIDDLE);
            return false;
        } else {
            return true;
        }
    }


    public void clearAll() {
        usernameField.clear();
        emailField.clear();
        pwfield.clear();
        pwfieldAgain.clear();
    }


}
