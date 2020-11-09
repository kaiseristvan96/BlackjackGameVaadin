package inf.unideb.hu.backend.entity;

public class CardEntity {
    int number;
    char suit;


    public CardEntity(int number, char suit){
        this.number = number;
        this.suit = suit;
    }

    public CardEntity(){}

    public String getSuitReadableFormat() {
        switch (this.getSuit()) {
            case ('H'):
                return "Heart";
            case ('S'):
                return "Spade";
            case ('C'):
                return "Club";
            case ('D'):
                return "Diamond";
            default:
                return this.getSuit() + "";
        }
    }

    public String getNumberReadableFormat() {
        switch (this.getNumber()) {
            case (1):
                return "A";
            case (11):
                return "J";
            case (12):
                return "Q";
            case (13):
                return "K";
            default:
                return String.valueOf(this.getNumber());
        }
    }

    public char getSuit() {
        return suit;
    }

    public void setSuit(char suit) {
        this.suit = suit;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return String.valueOf(number) + " of " + suit;
    }
}
