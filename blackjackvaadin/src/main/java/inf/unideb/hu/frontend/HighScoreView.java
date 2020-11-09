package inf.unideb.hu.frontend;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import inf.unideb.hu.backend.entity.PersonEntity;
import inf.unideb.hu.backend.service.PersonService;

@CssImport("./styles/styles.css")
@CssImport(value = "./styles/grid-styles.css", themeFor = "vaadin-grid")
@Route("highscores")
@PageTitle("Blackjack scores")
public class HighScoreView extends VerticalLayout {
    Grid<PersonEntity> personEntityGrid = new Grid<>(PersonEntity.class);
    private PersonService personService;

    public HighScoreView(PersonService personService) {
        this.personService = personService;

        addClassName("highscore-view");
        setSizeFull();

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

        H3 h3Introduction = new H3("Current high scores!");
        h3Introduction.addClassName("welcome-message");
        h3Introduction.getStyle().set("align-self", "center");
        add(h3Introduction);

        //personEntityGrid.addClassName("person-grid");
        modifyGridElements();
        add(personEntityGrid);
        updateHighScoreGrid();
    }

    private void modifyGridElements() {
        personEntityGrid.setSizeFull();
        personEntityGrid.setColumns("username", "creationDateTime", "score");
        personEntityGrid.getColumnByKey("creationDateTime").setHeader("Date");
        personEntityGrid.getColumns().forEach(width -> width.setAutoWidth(true));
    }

    private void updateHighScoreGrid() {
        personEntityGrid.setItems(personService.findAll());
        personEntityGrid.addThemeName("grid-selection-theme");
        personEntityGrid.setClassNameGenerator(item -> "highscore-grid");
    }

}
