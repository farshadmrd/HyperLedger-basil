package api.model;

import java.util.List;

public class Basil {
    private String qrCode;
    private String origin;
    private String gps;
    private String timestamp;
    private String temperature;
    private String humidity;
    private String status;
    private String orgId;
    private String owner;
    
    // Default constructor
    public Basil() {
    }
    
    // Constructor with fields
    public Basil(String qrCode, String origin, String gps, String timestamp, 
                String temperature, String humidity, String status, 
                String orgId, String owner) {
        this.qrCode = qrCode;
        this.origin = origin;
        this.gps = gps;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.status = status;
        this.orgId = orgId;
        this.owner = owner;
    }
    
    // Getters and Setters
    public String getQrCode() {
        return qrCode;
    }
    
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getGps() {
        return gps;
    }
    
    public void setGps(String gps) {
        this.gps = gps;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getTemperature() {
        return temperature;
    }
    
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
    
    public String getHumidity() {
        return humidity;
    }
    
    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getOrgId() {
        return orgId;
    }
    
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
}