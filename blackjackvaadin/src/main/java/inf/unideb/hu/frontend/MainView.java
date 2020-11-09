package inf.unideb.hu.frontend;


import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;


@CssImport("./styles/styles.css")
@Route("")
@PageTitle("Blackjack Main Page")
public class MainView extends VerticalLayout {

    public MainView() {

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        addClassName("main-view");

        //setting up the navigation
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

        add(topTabsPosition);

        if (VaadinSession.getCurrent().getAttribute("username") != null) {
            H2 welcomeMessage = new H2("Welcome " + VaadinSession.getCurrent().getAttribute("username") + " to the Blackjack game");
            welcomeMessage.addClassName("welcome-message");
            add(welcomeMessage);
            registerPage.setVisible(false);
            loginPage.setVisible(false);
            logoutPage.setVisible(true);
            gamePage.setVisible(true);
        } else {
            registerPage.setVisible(true);
            loginPage.setVisible(true);
            logoutPage.setVisible(false);
            gamePage.setVisible(false);
            H2 welcomeMessageDefault = new H2("Blackjack game!");
            welcomeMessageDefault.addClassName("welcome-message");
            add(welcomeMessageDefault);
        }


        VerticalLayout rules = new VerticalLayout();
        rules.add(new Paragraph("The player draws 2 cards in the beginning after placing bets and clicking the new round button. The server also draws 1 card."),
                new Paragraph("Then the player has the option to 'Hit', 'Double', 'Stand' or 'Aid'."),
                new Paragraph("\t\tHit: draw another card."),
                new Paragraph("\t\tDouble: draw exactly 1 card and double the current bet. You can't draw more cards this turn! "),
                new Paragraph("\t\tStand: hold onto your cards and let the server draw. The player can't draw more cards this turn."),
                new Paragraph("\t\tAid: if the player's score reaches 0 they have the option to loan 50 points, therefore they can continue playing. The player does not have to payback the loan!"),
                new Paragraph("If the value of the cards is exactly 21, the player wins their bet with a 2.5 multiplier."),
                new Paragraph("If the value of the cards is below 21, but higher then the server's, then the player wins with a multiplier of 2."),
                new Paragraph("If the value of the cards is below 21, but lower then the server's, then the player loses their bet."),
                new Paragraph("If the value of the cards is below 21 and it's the exact value as the server's, then it's called a push. The player wins back their bet."),
                new Paragraph("If the value of the player's cards is more than 21, the player busts, losing their bet."),
                new Paragraph("If the value of the server's cards is more than 21, the server busts, then the player wins with a multiplier of 2."));
        rules.addClassName("rules");
        rules.setSizeUndefined();
        add(rules);


    }

}
