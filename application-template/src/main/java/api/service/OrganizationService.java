package api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import api.model.Organization;

@Service
public class OrganizationService {

    // In a real-world application, this might come from a database or configuration
    public List<Organization> getAllOrganizations() {
        List<Organization> organizations = new ArrayList<>();
        
        // Add Org1
        Organization org1 = new Organization();
        org1.setId("1");
        org1.setName("Pittaluga & fratelli");
        org1.setMspId("Org1MSP");
        organizations.add(org1);
        
        // Add Org2
        Organization org2 = new Organization();
        org2.setId("2");
        org2.setName("Supermarket");
        org2.setMspId("Org2MSP");
        // Add note about limited access
        org2.setAccessType("Limited Access");
        organizations.add(org2);
        
        return organizations;
    }
    
    public Organization getOrganizationById(String id) {
        return getAllOrganizations().stream()
                .filter(org -> org.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public Organization getOrganizationByMspId(String mspId) {
        return getAllOrganizations().stream()
                .filter(org -> org.getMspId().equals(mspId))
                .findFirst()
                .orElse(null);
    }
}