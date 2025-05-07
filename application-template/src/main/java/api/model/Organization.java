package api.model;

public class Organization {
    private String id;
    private String name;
    private String mspId;
    private String accessType;
    
    public Organization() {
    }
    
    public Organization(String id, String name, String mspId) {
        this.id = id;
        this.name = name;
        this.mspId = mspId;
    }
    
    public Organization(String id, String name, String mspId, String accessType) {
        this.id = id;
        this.name = name;
        this.mspId = mspId;
        this.accessType = accessType;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMspId() {
        return mspId;
    }
    
    public void setMspId(String mspId) {
        this.mspId = mspId;
    }
    
    public String getAccessType() {
        return accessType;
    }
    
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }
}