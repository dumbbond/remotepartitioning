package remote.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KohlsBarcode implements Serializable {

    private static final long serialVersionUID = 1l;

    private String email;
    private String barcode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "KohlsBarcode{" +
                "email='" + email + '\'' +
                ", barcode='" + barcode + '\'' +
                '}';
    }
}
