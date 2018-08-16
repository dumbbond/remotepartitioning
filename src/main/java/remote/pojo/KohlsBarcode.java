package remote.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KohlsBarcode implements Serializable {

    private static final long serialVersionUID = 1l;

    private String setLoyaltyAccountEmail;
    private String barcode;

    public String getSetLoyaltyAccountEmail() {
        return setLoyaltyAccountEmail;
    }

    public void setSetLoyaltyAccountEmail(String setLoyaltyAccountEmail) {
        this.setLoyaltyAccountEmail = setLoyaltyAccountEmail;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Override
    public String toString() {
        return "KohlsBarcode{" +
                "setLoyaltyAccountEmail='" + setLoyaltyAccountEmail + '\'' +
                ", barcode='" + barcode + '\'' +
                '}';
    }
}
