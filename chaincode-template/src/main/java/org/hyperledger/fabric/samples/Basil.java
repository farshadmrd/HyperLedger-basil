package org.hyperledger.fabric.samples;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import com.owlike.genson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@DataType()
public final class Basil {

    @Property()
    private final String qrCode;

    @Property()
    private final Long creationTimestamp; 

    @Property()
    private final String station; // Changed from location/origin to station

    @Property()
    private final String currentStatus;

    @Property()
    private final String currentGps;

    @Property()
    private final Owner currentOwner;

    @Property()
    private final List<BasilLeg> transportHistory;

    public Basil(
            @JsonProperty("qrCode") String qrCode,
            @JsonProperty("creationTimestamp") Long creationTimestamp,
            @JsonProperty("station") String station,
            @JsonProperty("currentStatus") String currentStatus,
            @JsonProperty("currentGps") String currentGps,
            @JsonProperty("currentOwner") Owner currentOwner,
            @JsonProperty("transportHistory") List<BasilLeg> transportHistory) {
        this.qrCode = qrCode;
        this.creationTimestamp = creationTimestamp;
        this.station = station;
        this.currentStatus = currentStatus;
        this.currentGps = currentGps;
        this.currentOwner = currentOwner;
        this.transportHistory = transportHistory;
    }
    
    // Simplified constructor for creation (initializes currentGps = station)
    public Basil(String qrCode, Long creationTimestamp, String station, 
                String currentStatus, Owner currentOwner, List<BasilLeg> transportHistory) {
        this(qrCode, creationTimestamp, station, currentStatus, station, currentOwner, transportHistory);
    }

    public String getQrCode() {
        return qrCode;
    }

    public Long getCreationTimestamp() {
        return creationTimestamp;
    }

    public String getStation() {
        return station;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getCurrentGps() {
        return currentGps;
    }

    public Owner getCurrentOwner() {
        return currentOwner;
    }

    public List<BasilLeg> getTransportHistory() {
        return transportHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Basil)) return false;
        Basil basil = (Basil) o;
        return Objects.equals(qrCode, basil.qrCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qrCode);
    }

    @Override
    public String toString() {
        return "Basil{" +
                "qrCode='" + qrCode + '\'' +
                ", creationTimestamp=" + creationTimestamp +
                ", station='" + station + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", currentGps='" + currentGps + '\'' +
                ", currentOwner=" + currentOwner +
                ", transportHistory=" + transportHistory +
                '}';
    }
}