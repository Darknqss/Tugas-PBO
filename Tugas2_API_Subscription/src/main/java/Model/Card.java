package Model;

public class Card {

    private int id;
    private int customerId;
    private CardType cardType;
    private String maskedNumber;
    private int expiryMonth;
    private int expiryYear;
    private CardStatus status;
    private boolean isPrimary;

    public enum CardType {
        VISA, MASTERCARD
    }

    public enum CardStatus {
        VALID, EXPIRING, EXPIRED
    }

    // Constructors
    public Card() {
    }

    public Card(int id, int customerId, CardType cardType, String maskedNumber, int expiryMonth, int expiryYear, CardStatus status, boolean isPrimary) {
        this.id = id;
        this.customerId = customerId;
        this.cardType = cardType;
        this.maskedNumber = maskedNumber;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.status = status;
        this.isPrimary = isPrimary;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    // Override toString() method to represent the object as a JSON-like string
    @Override
    public String toString() {
        return String.format("{\"id\":%d,\"customerId\":%d,\"cardType\":\"%s\",\"maskedNumber\":\"%s\",\"expiryMonth\":%d,\"expiryYear\":%d,\"status\":\"%s\",\"isPrimary\":%b}",
                id, customerId, cardType.name(), maskedNumber, expiryMonth, expiryYear, status.name(), isPrimary);
    }
}
