package api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.model.Basil;
import api.service.FabricNetworkService;

@RestController
@RequestMapping("/api")
public class BasilController {

    @Autowired
    private FabricNetworkService fabricNetworkService;

    @PostMapping("/basil")
    public ResponseEntity<?> createBasil(@RequestBody Basil basil) {
        try {
            String response = fabricNetworkService.createBasil(basil.getQrCode(), basil.getOrigin());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/basil/{qrCode}")
    public ResponseEntity<?> readBasil(
            @PathVariable String qrCode,
            @RequestParam(defaultValue = "false") boolean asOrg2) {
        try {
            String response = fabricNetworkService.readBasil(qrCode, asOrg2);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/basil/{qrCode}")
    public ResponseEntity<?> updateBasilState(
            @PathVariable String qrCode,
            @RequestBody Basil basil) {
        try {
            fabricNetworkService.updateBasilState(
                qrCode,
                basil.getGps(),
                basil.getTimestamp(),
                basil.getTemperature(),
                basil.getHumidity(),
                basil.getStatus()
            );
            Map<String, String> response = new HashMap<>();
            response.put("message", "Basil state updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/basil/{qrCode}")
    public ResponseEntity<?> deleteBasil(@PathVariable String qrCode) {
        try {
            String response = fabricNetworkService.deleteBasil(qrCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/basil/{qrCode}/history")
    public ResponseEntity<?> getBasilHistory(
            @PathVariable String qrCode,
            @RequestParam(defaultValue = "false") boolean asOrg2) {
        try {
            String response = fabricNetworkService.getBasilHistory(qrCode, asOrg2);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/basil/{qrCode}/transfer")
    public ResponseEntity<?> transferOwnership(
            @PathVariable String qrCode,
            @RequestParam String newOrgId,
            @RequestParam String newName) {
        try {
            String response = fabricNetworkService.transferOwnership(qrCode, newOrgId, newName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}