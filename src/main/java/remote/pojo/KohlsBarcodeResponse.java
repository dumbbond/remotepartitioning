package remote.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"barcode", "pin"})
public class KohlsBarcodeResponse {

    private String barcode;
    private Integer pin;

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
        return "KohlsBarcodeResponse{" +
                "barcode='" + barcode + '\'' +
                ", pin=" + pin +
                '}';
    }
}
