package remote.pojo;

import java.io.Serializable;

public class KohlsUser implements Serializable {

    private static final long serialVersionUID = 1l;

   private Long id;
   private String eventId;
   private String LoyaltyAccountEmail;
   private Long rewardId;
   private String rewardDollarAmount;
   private Double rewardValue;
   private Long loyaltyAccountId;
   private Long loyaltyServiceId;
   private String rewardDate;
   private String offerId;
   private String offerName;
   private String issueDate;
   private String pointValue;
   private String expirationDate;
   private String status;
   private String barcode;
   private Integer pin;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getLoyaltyAccountEmail() {
        return LoyaltyAccountEmail;
    }

    public void setLoyaltyAccountEmail(String loyaltyAccountEmail) {
        LoyaltyAccountEmail = loyaltyAccountEmail;
    }

    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public String getRewardDollarAmount() {
        return rewardDollarAmount;
    }

    public void setRewardDollarAmount(String rewardDollarAmount) {
        this.rewardDollarAmount = rewardDollarAmount;
    }

    public Double getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(Double rewardValue) {
        this.rewardValue = rewardValue;
    }

    public Long getLoyaltyAccountId() {
        return loyaltyAccountId;
    }

    public void setLoyaltyAccountId(Long loyaltyAccountId) {
        this.loyaltyAccountId = loyaltyAccountId;
    }

    public Long getLoyaltyServiceId() {
        return loyaltyServiceId;
    }

    public void setLoyaltyServiceId(Long loyaltyServiceId) {
        this.loyaltyServiceId = loyaltyServiceId;
    }

    public String getRewardDate() {
        return rewardDate;
    }

    public void setRewardDate(String rewardDate) {
        this.rewardDate = rewardDate;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getPointValue() {
        return pointValue;
    }

    public void setPointValue(String pointValue) {
        this.pointValue = pointValue;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return "KohlsUser{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", LoyaltyAccountEmail='" + LoyaltyAccountEmail + '\'' +
                ", rewardId=" + rewardId +
                ", rewardDollarAmount='" + rewardDollarAmount + '\'' +
                ", rewardValue=" + rewardValue +
                ", loyaltyAccountId=" + loyaltyAccountId +
                ", loyaltyServiceId=" + loyaltyServiceId +
                ", rewardDate='" + rewardDate + '\'' +
                ", offerId='" + offerId + '\'' +
                ", offerName='" + offerName + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", pointValue='" + pointValue + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", status='" + status + '\'' +
                ", barcode='" + barcode + '\'' +
                ", pin=" + pin +
                '}';
    }
}
