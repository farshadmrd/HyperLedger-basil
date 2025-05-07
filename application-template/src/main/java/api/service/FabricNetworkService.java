package api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import javax.annotation.PreDestroy;

@Service
public class FabricNetworkService {

    // path to your test-network directory
    private static final Path PATH_TO_TEST_NETWORK = Paths
            .get("/home/farshad/go/src/github.com/farshadmrd/fabric-samples/test-network");

    private static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", "mychannel");
    private static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", "basic");

    // Gateway peer end points
    private static final String ORG1_PEER_ENDPOINT = "localhost:7051";
    private static final String ORG1_OVERRIDE_AUTH = "peer0.org1.example.com";
    private static final String ORG2_PEER_ENDPOINT = "localhost:9051";
    private static final String ORG2_OVERRIDE_AUTH = "peer0.org2.example.com";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private ManagedChannel channelOrg1;
    private ManagedChannel channelOrg2;
    private Gateway gatewayOrg1;
    private Gateway gatewayOrg2;
    private Contract contractOrg1;
    private Contract contractOrg2;

    public FabricNetworkService() {
        try {
            initializeOrg1Connection();
            initializeOrg2Connection();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Fabric network connections", e);
        }
    }

    private void initializeOrg1Connection() throws Exception {
        ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
                .trustManager(PATH_TO_TEST_NETWORK.resolve(Paths.get(
                        "organizations/peerOrganizations/org1.example.com/" +
                                "peers/peer0.org1.example.com/tls/ca.crt"))
                        .toFile())
                .build();

        channelOrg1 = Grpc.newChannelBuilder(ORG1_PEER_ENDPOINT, credentials)
                .overrideAuthority(ORG1_OVERRIDE_AUTH)
                .build();

        Gateway.Builder builderOrg1 = Gateway.newInstance()
                .identity(new X509Identity("Org1MSP",
                        Identities.readX509Certificate(
                                Files.newBufferedReader(
                                        PATH_TO_TEST_NETWORK.resolve(Paths.get(
                                                "organizations/peerOrganizations/org1.example.com/" +
                                                        "users/User1@org1.example.com/msp/signcerts/cert.pem"))))))
                .signer(
                        Signers.newPrivateKeySigner(
                                Identities.readPrivateKey(
                                        Files.newBufferedReader(
                                                Files.list(PATH_TO_TEST_NETWORK.resolve(
                                                        Paths.get(
                                                                "organizations/peerOrganizations/org1.example.com/"
                                                                        +
                                                                        "users/User1@org1.example.com/msp/keystore")))
                                                        .findFirst().orElseThrow()))))
                .connection(channelOrg1)
                // Default timeouts for different gRPC calls
                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

        gatewayOrg1 = builderOrg1.connect();
        contractOrg1 = gatewayOrg1.getNetwork(CHANNEL_NAME).getContract(CHAINCODE_NAME);
    }

    private void initializeOrg2Connection() throws Exception {
        ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
                .trustManager(PATH_TO_TEST_NETWORK.resolve(Paths.get(
                        "organizations/peerOrganizations/org2.example.com/" +
                                "peers/peer0.org2.example.com/tls/ca.crt"))
                        .toFile())
                .build();

        channelOrg2 = Grpc.newChannelBuilder(ORG2_PEER_ENDPOINT, credentials)
                .overrideAuthority(ORG2_OVERRIDE_AUTH)
                .build();

        Gateway.Builder builderOrg2 = Gateway.newInstance()
                .identity(new X509Identity("Org2MSP",
                        Identities
                                .readX509Certificate(Files.newBufferedReader(PATH_TO_TEST_NETWORK.resolve(Paths.get(
                                        "organizations/peerOrganizations/org2.example.com/users/User1@org2.example.com/msp/signcerts/cert.pem"))))))
                .signer(Signers.newPrivateKeySigner(Identities.readPrivateKey(Files.newBufferedReader(Files
                        .list(PATH_TO_TEST_NETWORK.resolve(Paths
                                .get("organizations/peerOrganizations/org2.example.com/users/User1@org2.example.com/msp/keystore")))
                        .findFirst().orElseThrow()))))
                .connection(channelOrg2)
                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

        gatewayOrg2 = builderOrg2.connect();
        contractOrg2 = gatewayOrg2.getNetwork(CHANNEL_NAME).getContract(CHAINCODE_NAME);
    }

    @PreDestroy
    public void close() {
        try {
            if (gatewayOrg1 != null) {
                gatewayOrg1.close();
            }
            if (gatewayOrg2 != null) {
                gatewayOrg2.close();
            }
            if (channelOrg1 != null) {
                channelOrg1.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            }
            if (channelOrg2 != null) {
                channelOrg2.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            // Just log the exception
            System.err.println("Error closing Fabric connections: " + e.getMessage());
        }
    }

    // Method to create a new basil
    public String createBasil(String qrCode, String origin) throws Exception {
        byte[] result = contractOrg1.submitTransaction("createBasil", qrCode, origin);
        return new String(result, StandardCharsets.UTF_8);
    }

    // Method to read basil details
    public String readBasil(String qrCode, boolean asOrg2) throws Exception {
        Contract contract = asOrg2 ? contractOrg2 : contractOrg1;
        byte[] result = contract.evaluateTransaction("readBasil", qrCode);
        return prettyJson(result);
    }

    // Method to update basil state
    public void updateBasilState(String qrCode, String gps, String timestamp, String temp, String humidity, String status)
            throws Exception {
        contractOrg1.submitTransaction("updateBasilState", qrCode, gps, timestamp, temp, humidity, status);
    }

    // Method to delete a basil
    public String deleteBasil(String qrCode) throws Exception {
        byte[] result = contractOrg1.submitTransaction("deleteBasil", qrCode);
        return new String(result, StandardCharsets.UTF_8);
    }

    // Method to get basil history
    public String getBasilHistory(String qrCode, boolean asOrg2) throws Exception {
        Contract contract = asOrg2 ? contractOrg2 : contractOrg1;
        byte[] result = contract.evaluateTransaction("getHistory", qrCode);
        return prettyJson(result);
    }

    // Method to transfer ownership
    public String transferOwnership(String qrCode, String newOrgId, String newName) throws Exception {
        byte[] result = contractOrg1.submitTransaction("transferOwnership", qrCode, newOrgId, newName);
        return new String(result, StandardCharsets.UTF_8);
    }

    // Helper method to format JSON
    private String prettyJson(final byte[] json) {
        return prettyJson(new String(json, StandardCharsets.UTF_8));
    }

    private String prettyJson(final String json) {
        try {
            var parsedJson = JsonParser.parseString(json);
            return gson.toJson(parsedJson);
        } catch (Exception e) {
            // In case of invalid JSON, return the original string
            return json;
        }
    }
}