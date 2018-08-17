package remote.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"eventId", "kcAmount"})
public class KohlsBarcodeRequest implements Serializable {

    private static final long serialVersionUID = 1l;

    private String eventId;
    private Double kcAmount;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Double getKcAmount() {
        return kcAmount;
    }

    public void setKcAmount(Double kcAmount) {
        this.kcAmount = kcAmount;
    }

    @Override
    public String toString() {
        return "KohlsBarcodeRequest{" +
                "eventId='" + eventId + '\'' +
                ", kcAmount=" + kcAmount +
                '}';
    }
}
