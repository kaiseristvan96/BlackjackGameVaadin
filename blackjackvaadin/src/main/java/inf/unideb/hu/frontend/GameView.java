package inf.unideb.hu.frontend;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import inf.unideb.hu.backend.entity.CardEntity;
import inf.unideb.hu.backend.entity.PersonEntity;
import inf.unideb.hu.backend.util.GenerateDeckUtil;
import inf.unideb.hu.backend.service.PersonService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@CssImport("./styles/game-view.css")
@Route("game")
@PageTitle("Blackjack game")
@PreserveOnRefresh
public class GameView extends VerticalLayout implements BeforeEnterObserver {
    ArrayList<CardEntity> playerCards = new ArrayList<>();
    ArrayList<CardEntity> serverCards = new ArrayList<>();

    Grid<CardEntity> serverGrid = new Grid<>();
    Grid.Column<CardEntity> serverNumbercolumn;

    Grid<CardEntity> cardsGrid = new Grid<>();
    Grid.Column<CardEntity> numbercolumn;
    GenerateDeckUtil generateDeckUtil = new GenerateDeckUtil();

    Button hit = new Button("Hit");
    Button doublehit = new Button("Double");
    Button stay = new Button("Stand");
    Button loan = new Button("Aid of 50 points");
    Button newRoundButton = new Button("New round");
    IntegerField placeBetField = new IntegerField("Place your bet");
    H3 currMoney = new H3();

    Button one = new Button("+ 1");
    Button ten = new Button("+ 10");
    Button hundred = new Button("+ 100");
    Button thousand = new Button("+ 1000");
    Button tenThousand = new Button("+ 10000");


    private PersonService personService;
    PersonEntity currPlayer = new PersonEntity();

    int playerBet = 0;


    boolean isRoundOver = false;


    public GameView(PersonService personService) {
        if (VaadinSession.getCurrent().getAttribute("username") == null) {
            UI.getCurrent().navigate("login");
        } else {
            setSizeFull();
            setAlignItems(Alignment.CENTER);

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
            currMoney.setClassName("welcome-message");

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
            } else {
                registerPage.setVisible(true);
                loginPage.setVisible(true);
            }

            add(topTabsPosition);

            //navigation over

            addClassName("game-view");
            setSizeFull();


            one.addClassName("static-buttons");
            ten.addClassName("static-buttons");
            hundred.addClassName("static-buttons");
            thousand.addClassName("static-buttons");
            tenThousand.addClassName("static-buttons");

            try {
                currPlayer = personService.findByUsername((String) VaadinSession.getCurrent().getAttribute("username"));
                if (currPlayer.getScore() == 0) {
                    loan.setEnabled(true);
                    loan.setClassName("enabled-button");
                } else {
                    loan.setEnabled(false);
                    loan.setClassName("disabled-button");
                }
            } catch (NullPointerException error) {
                System.out.println(error);
            }


            HorizontalLayout sides = new HorizontalLayout();

            sides.add(cardsGrid, serverGrid);


            HorizontalLayout setBetButtonsLayout = new HorizontalLayout();
            setBetButtonsLayout.add(one, ten, hundred, thousand, tenThousand);

            int betSteps = 10;

            placeBetField.setHasControls(true);
            placeBetField.setValue(50);
            placeBetField.setStep(betSteps);
            placeBetField.setMin(0);
            placeBetField.addClassName("placebet-field");

            HorizontalLayout betandNewRound = new HorizontalLayout();
            betandNewRound.setDefaultVerticalComponentAlignment(Alignment.END);
            betandNewRound.add(setBetButtonsLayout, placeBetField, newRoundButton);

            isRoundOver = true;
            changeButtonClickable();

            //show starting card
            cardsGrid.setItems(playerCards);
            cardsGrid.getStyle().set("text-align", "center");


            serverGrid.setItems(serverCards);
            serverGrid.getStyle().set("text-align", "center");


            Grid.Column<CardEntity> suitcolumn = cardsGrid
                    .addColumn(CardEntity::getSuitReadableFormat).setHeader("Suit").setFooter("Total value: ");
            numbercolumn = cardsGrid.addColumn(CardEntity::getNumberReadableFormat)
                    .setHeader("Number").setFooter(String.valueOf(playersCurrValue()));
            Grid.Column imagesPlayer = cardsGrid.addComponentColumn(card -> insertImage(cardsGrid, card)).setHeader("Card");
            cardsGrid.setSelectionMode(Grid.SelectionMode.NONE);
            cardsGrid.setWidth("45vw");

            HeaderRow topRowPlayer = cardsGrid.prependHeaderRow();
            topRowPlayer.join(suitcolumn, numbercolumn, imagesPlayer).setComponent(new Label(currPlayer.getUsername() + "'s hand"));


            Grid.Column<CardEntity> serverSuitcolumn = serverGrid
                    .addColumn(CardEntity::getSuitReadableFormat).setHeader("Suit").setFooter("Total value: ");
            serverNumbercolumn = serverGrid.addColumn(CardEntity::getNumberReadableFormat)
                    .setHeader("Number").setFooter(String.valueOf(serverCurrentValue()));
            Grid.Column<CardEntity> imagesServer = serverGrid.addComponentColumn(card -> insertImage(serverGrid, card)).setHeader("Card");
            serverGrid.setSelectionMode(Grid.SelectionMode.NONE);
            serverGrid.setWidth("45vw");

            HeaderRow topRowServer = serverGrid.prependHeaderRow();
            topRowServer.join(serverSuitcolumn, serverNumbercolumn, imagesServer).setComponent(new Label("Server's hand"));

            currMoney.setText("Current score: " + personService.findScoreByUsername((String) VaadinSession.getCurrent().getAttribute("username")));


            HorizontalLayout bothGridHorizontalLayout = new HorizontalLayout(cardsGrid, serverGrid);

            HorizontalLayout playerOptions = new HorizontalLayout(hit, doublehit, stay, loan, currMoney);
            playerOptions.setDefaultVerticalComponentAlignment(Alignment.END);


            hit.addClickListener(event -> {
                playerCards.add(generateDeckUtil.getSingleCardFromDeck(0));
                System.out.println(playerCards);
                refreshGrid(personService);
            });
            doublehit.addClickListener(event -> {
                if (currPlayer.getScore() >= playerBet) {
                    currPlayer.setScore(currPlayer.getScore() - playerBet);
                    currMoney.setText("Current score: " + currPlayer.getScore());
                    playerBet = playerBet * 2;
                    playerCards.add(generateDeckUtil.getSingleCardFromDeck(0));
                    System.out.println(playerCards);
                    refreshGrid(personService);
                    //stay/stand
                    serverDraws();

                    hit.setEnabled(false);
                    hit.setClassName("disabled-button");
                    doublehit.setEnabled(false);
                    doublehit.setClassName("disabled-button");
                } else {
                    Notification.show("Insufficient score. You can't double!", 5000, Notification.Position.MIDDLE);
                }
            });

            stay.addClickListener(event -> {
                System.out.println(playerCards);
                refreshGrid(personService);
                serverDraws();
                checkWinner(personService);
            });


            newRoundButton.addClickListener(event -> {
                if(placeBetField.getValue() < 1){
                    Notification.show("You are not allowed to go below 1 point when betting", 2000, Notification.Position.MIDDLE);
                }
                else if (currPlayer.getScore() - placeBetField.getValue() < 0) {
                    Notification.show("Insufficient points", 2000, Notification.Position.MIDDLE);
                } else {
                    Notification.show("Starting new round", 2000, Notification.Position.TOP_CENTER);
                    newRound(personService);
                }

            });

            loan.addClickListener(event -> {
                if (currPlayer.getScore() == 0L) {
                    Notification.show("The bank gave you 50 points", 4000, Notification.Position.MIDDLE);
                    loan.setEnabled(false);
                    loan.setClassName("disabled-button");
                    currPlayer.setScore(50L);
                    personService.save(currPlayer);
                    currMoney.setText("Current score: " + currPlayer.getScore());
                } else {
                    loan.setClassName("enabled-button");
                }

            });

            one.addClickListener(event -> changeBetSteps(1));
            ten.addClickListener(event -> changeBetSteps(10));
            hundred.addClickListener(event -> changeBetSteps(100));
            thousand.addClickListener(event -> changeBetSteps(1000));
            tenThousand.addClickListener(event -> changeBetSteps(10000));


            add(sides, bothGridHorizontalLayout, playerOptions, betandNewRound);

        }
    }

    public void changeBetSteps(int steps) {
        placeBetField.setValue(placeBetField.getValue() + steps);
    }

    //s
    private Image insertImage(Grid<CardEntity> cardsGrid, CardEntity card) {
        Image cardImage = new Image("images/PNG/" + card.getNumber() + card.getSuit() + ".png", "Image not available");
        return cardImage;
    }


    private void refreshGrid(PersonService personService) {
        numbercolumn.setFooter(String.valueOf(playersCurrValue()));
        serverNumbercolumn.setFooter(String.valueOf(serverCurrentValue()));
        cardsGrid.getDataProvider().refreshAll();
        serverGrid.getDataProvider().refreshAll();


        if (playersCurrValue() > 21) {
            Notification.show("Player busted! You had a total of " + playersCurrValue() + " card value.", 5000, Notification.Position.MIDDLE);
            playerBusted();
        } else if (playersCurrValue() == 21) {
            Notification.show("Player hit the blackjack!", 5000, Notification.Position.MIDDLE);
            playerBJack(personService);
        }
    }

    //money+bet*2.5
    private void playerBJack(PersonService personService) {
        //adding bets to player's score
        currPlayer.setScore(currPlayer.getScore() + Math.round(playerBet * 2.5));
        personService.save(currPlayer);

        isRoundOver = true;
        currMoney.setText("Current score: " + currPlayer.getScore());
        changeButtonClickable();
    }


    private void playerBusted() {
        isRoundOver = true;
        currMoney.setText("Current score: " + currPlayer.getScore());
        changeButtonClickable();
    }


    private int playersCurrValue() {
        int currValue = 0;
        int aces = 0;

        for (int i = 0; i < playerCards.size(); i++) {
            if (playerCards.get(i).getNumber() == 11 || playerCards.get(i).getNumber() == 12 || playerCards.get(i).getNumber() == 13) {
                currValue += 10;
            } else if (playerCards.get(i).getNumber() == 1) {
                aces++;
            } else {
                currValue = currValue + playerCards.get(i).getNumber();
            }
        }

        int hard = 0;
        int soft = 0;

        if(aces > 0) {
            hard = aces + currValue + 10;
        }else{
            //no aces
            hard = aces + currValue;
        }
        soft = aces + currValue;

        if(hard <=21){
            return hard;
        }else {
            return soft;
        }
    }

    private int serverCurrentValue() {
        int currValue = 0;
        int aces = 0;
        for (int i = 0; i < serverCards.size(); i++) {
            if (serverCards.get(i).getNumber() == 11 || serverCards.get(i).getNumber() == 12 || serverCards.get(i).getNumber() == 13) {
                currValue += 10;
            } else if (serverCards.get(i).getNumber() == 1) {
                aces++;
            } else {
                currValue = currValue + serverCards.get(i).getNumber();
            }
        }

        int hard = 0;
        int soft = 0;

        if(aces > 0) {
            hard = aces + currValue + 10;
        }else{
            //no aces
            hard = aces + currValue;
        }
        soft = aces + currValue;

        if(hard <=21){
            return hard;
        }else {
            return soft;
        }
    }


    public void serverDraws() {
        while (serverCurrentValue() < 17) {
            serverCards.add(generateDeckUtil.getSingleCardFromDeck(0));

        }
        refreshGrid(personService);
    }

    public void checkWinner(PersonService personService) {

        if (playersCurrValue() > serverCurrentValue()) {
            Notification.show("The Player won this round!\nCongratulations.", 4500, Notification.Position.MIDDLE);

            currPlayer.setScore(currPlayer.getScore() + Math.round(playerBet * 2));
            personService.save(currPlayer);

            isRoundOver = true;
            currMoney.setText("Current score: " + currPlayer.getScore());
            changeButtonClickable();
        } else if (playersCurrValue() == serverCurrentValue()) {
            Notification.show("It's a tie!", 4500, Notification.Position.MIDDLE);

            currPlayer.setScore(currPlayer.getScore() + Math.round(playerBet * 1));
            personService.save(currPlayer);

            isRoundOver = true;
            currMoney.setText("Current score: " + currPlayer.getScore());
            changeButtonClickable();
        } else if (serverCurrentValue() > 21) {
            Notification.show("Server busted!\nPlayer won", 4500, Notification.Position.MIDDLE);

            currPlayer.setScore(currPlayer.getScore() + Math.round(playerBet * 2));
            personService.save(currPlayer);
            isRoundOver = true;
            currMoney.setText("Current score: " + currPlayer.getScore());
            changeButtonClickable();

        } else {
            Notification.show("The Server won this round!", 4500, Notification.Position.MIDDLE);
            currMoney.setText("Current score: " + currPlayer.getScore());
            isRoundOver = true;
            changeButtonClickable();
        }
    }

    public void newRound(PersonService personService) {
        if (isRoundOver == true) {
            isRoundOver = false;
            playerCards.clear();
            serverCards.clear();
            generateDeckUtil = new GenerateDeckUtil();
            playerCards.add(generateDeckUtil.getSingleCardFromDeck(0));
            playerCards.add(generateDeckUtil.getSingleCardFromDeck(0));
            serverCards.add(generateDeckUtil.getSingleCardFromDeck(0));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            currPlayer.setCreationDateTime(simpleDateFormat.format(date));

            playerBet = placeBetField.getValue();

            currPlayer.setScore(currPlayer.getScore() - playerBet);
            personService.save(currPlayer);

            currMoney.setText("Current score: " + currPlayer.getScore());
            System.out.println("player placed bet: " + playerBet);

            hit.setEnabled(true);
            doublehit.setEnabled(true);
            stay.setEnabled(true);
            newRoundButton.setEnabled(false);


            //this is for the css only
            hit.setClassName("enabled-button");
            doublehit.setClassName("enabled-button");
            stay.setClassName("enabled-button");
            newRoundButton.setClassName("disabled-button");

            refreshGrid(personService);

        }
    }

    public void changeButtonClickable() {
        if (isRoundOver == true) {
            hit.setEnabled(false);
            doublehit.setEnabled(false);
            stay.setEnabled(false);
            newRoundButton.setEnabled(true);
            //if the round is over AND the player has no points left give them 50 points

            if (currPlayer.getScore() == 0) {
                loan.setEnabled(true);
                loan.setClassName("enabled-button");
            }

            hit.setClassName("disabled-button");
            doublehit.setClassName("disabled-button");
            stay.setClassName("disabled-button");
            newRoundButton.setClassName("enabled-button");

        } else {
            hit.setEnabled(true);
            doublehit.setEnabled(true);
            stay.setEnabled(true);
            loan.setEnabled(false);
            newRoundButton.setEnabled(false);


            hit.setClassName("enabled-button");
            doublehit.setClassName("enabled-button");
            stay.setClassName("enabled-button");
            loan.setClassName("disabled-button");
            newRoundButton.setClassName("disabled-button");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (VaadinSession.getCurrent().getAttribute("username") == null) {
            beforeEnterEvent.rerouteTo("login");
        }
    }

}
