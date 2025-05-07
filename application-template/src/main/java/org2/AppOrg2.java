package org2;
/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;

public final class AppOrg2 {

	// path to your test-network directory included, e.g.: Paths.get("..", "..",
	// "test-network")
	private static final Path PATH_TO_TEST_NETWORK = Paths
			.get("/home/farshad/go/src/github.com/farshadmrd/fabric-samples/test-network");

	private static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", "mychannel");
	private static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", "basic");

	// Gateway peer end point.
	private static final String PEER_ENDPOINT = "localhost:7051";
	private static final String OVERRIDE_AUTH = "peer0.org1.example.com";

	public static void main(final String[] args) throws Exception {
		try {
			ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
					.trustManager(PATH_TO_TEST_NETWORK.resolve(Paths.get(
							"organizations/peerOrganizations/org1.example.com/" +
									"peers/peer0.org1.example.com/tls/ca.crt"))
							.toFile())
					.build();
			// The gRPC client connection should be shared by all Gateway connections to
			// this endpoint.
			ManagedChannel channel = Grpc.newChannelBuilder(PEER_ENDPOINT, credentials)
					.overrideAuthority(OVERRIDE_AUTH)
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
					.connection(channel)
					// Default timeouts for different gRPC calls
					.evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
					.endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
					.submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
					.commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

			// notice that we can share the grpc connection since we don't use private date,
			// otherwise we should create another connection
			Gateway.Builder builderOrg2 = Gateway.newInstance()
					.identity(new X509Identity("Org2MSP",
							Identities
									.readX509Certificate(Files.newBufferedReader(PATH_TO_TEST_NETWORK.resolve(Paths.get(
											"organizations/peerOrganizations/org2.example.com/users/User1@org2.example.com/msp/signcerts/cert.pem"))))))
					.signer(Signers.newPrivateKeySigner(Identities.readPrivateKey(Files.newBufferedReader(Files
							.list(PATH_TO_TEST_NETWORK.resolve(Paths
									.get("organizations/peerOrganizations/org2.example.com/users/User1@org2.example.com/msp/keystore")))
							.findFirst().orElseThrow()))))
					.connection(channel)
					.evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
					.endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
					.submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
					.commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

			try (Gateway gatewayOrg1 = builderOrg1.connect();
					Gateway gatewayOrg2 = builderOrg2.connect()) {

				Contract contractOrg1 = gatewayOrg1
						.getNetwork(CHANNEL_NAME)
						.getContract(CHAINCODE_NAME);

				Contract contractOrg2 = gatewayOrg2
						.getNetwork(CHANNEL_NAME)
						.getContract(CHAINCODE_NAME);

				 // Start interactive menu
				runInteractiveMenu(contractOrg1, contractOrg2);

			} finally {
				channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			System.err.println("❌ ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void runInteractiveMenu(Contract contractOrg1, Contract contractOrg2) {
		Scanner scanner = new Scanner(System.in);
		boolean exit = false;

		while (!exit) {
			System.out.println("\n=== 🌿 Basil Management System 🌿 ===");
			System.out.println("1. Create new basil");
			System.out.println("2. Read basil details");
			System.out.println("3. Update basil state");
			System.out.println("4. Delete basil");
			System.out.println("5. Get basil history");
			System.out.println("6. Transfer ownership");
			System.out.println("7. Read basil as Org2");
			System.out.println("8. Get basil history as Org2");
			System.out.println("0. Exit");
			System.out.print("\nEnter your choice: ");

			try {
				int choice = Integer.parseInt(scanner.nextLine().trim());
				
				switch (choice) {
					case 0:
						exit = true;
						System.out.println("Exiting application. Goodbye!");
						break;
						
					case 1:
						createBasil(scanner, contractOrg1);
						break;
						
					case 2:
						readBasil(scanner, contractOrg1);
						break;
						
					case 3:
						updateBasilState(scanner, contractOrg1);
						break;
						
					case 4:
						deleteBasil(scanner, contractOrg1);
						break;
						
					case 5:
						getBasilHistory(scanner, contractOrg1);
						break;
						
					case 6:
						transferOwnership(scanner, contractOrg1);
						break;
						
					case 7:
						readBasil(scanner, contractOrg2);
						break;
						
					case 8:
						getBasilHistory(scanner, contractOrg2);
						break;
						
					default:
						System.out.println("Invalid choice. Please try again.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Please enter a valid number.");
			} catch (Exception e) {
				System.out.println("An error occurred: " + e.getMessage());
				e.printStackTrace();
			}
			
			if (!exit) {
				System.out.println("\nPress Enter to continue...");
				scanner.nextLine();
			}
		}
	}
	
	private static void createBasil(Scanner scanner, Contract contract) {
		System.out.println("\n=== Create New Basil ===");
		
		System.out.print("Enter QR Code: ");
		String qrCode = scanner.nextLine().trim();
		
		System.out.print("Enter Origin: ");
		String origin = scanner.nextLine().trim();
		
		try {
			byte[] result = contract.submitTransaction("createBasil", qrCode, origin);
			System.out.println("✅ Basil created successfully: " + new String(result));
		} catch (Exception e) {
			System.out.println("❌ Error creating basil: " + e.getMessage());
		}
	}
	
	private static void readBasil(Scanner scanner, Contract contract) {
		System.out.println("\n=== Read Basil Details ===");
		
		System.out.print("Enter QR Code: ");
		String qrCode = scanner.nextLine().trim();
		
		try {
			byte[] result = contract.evaluateTransaction("readBasil", qrCode);
			System.out.println("Basil details: " + prettyJson(result));
		} catch (Exception e) {
			System.out.println("❌ Error reading basil: " + e.getMessage());
		}
	}
	
	private static void updateBasilState(Scanner scanner, Contract contract) {
		System.out.println("\n=== Update Basil State ===");
		
		System.out.print("Enter QR Code: ");
		String qrCode = scanner.nextLine().trim();
		
		System.out.print("Enter GPS Location (e.g. 48.8566,2.3522): ");
		String gps = scanner.nextLine().trim();
		
		System.out.print("Enter Timestamp: ");
		String timestamp = scanner.nextLine().trim();
		
		System.out.print("Enter Temperature (e.g. 22C): ");
		String temp = scanner.nextLine().trim();
		
		System.out.print("Enter Humidity (e.g. 68%): ");
		String humidity = scanner.nextLine().trim();
		
		System.out.print("Enter Status (e.g. In Transit): ");
		String status = scanner.nextLine().trim();
		
		try {
			contract.submitTransaction("updateBasilState", qrCode, gps, timestamp, temp, humidity, status);
			System.out.println("✅ Basil state updated successfully");
		} catch (Exception e) {
			System.out.println("❌ Error updating basil state: " + e.getMessage());
		}
	}
	
	private static void deleteBasil(Scanner scanner, Contract contract) {
		System.out.println("\n=== Delete Basil ===");
		
		System.out.print("Enter QR Code: ");
		String qrCode = scanner.nextLine().trim();
		
		try {
			byte[] result = contract.submitTransaction("deleteBasil", qrCode);
			System.out.println("✅ Basil deleted successfully: " + new String(result));
		} catch (Exception e) {
			System.out.println("❌ Error deleting basil: " + e.getMessage());
		}
	}
	
	private static void getBasilHistory(Scanner scanner, Contract contract) {
		System.out.println("\n=== Get Basil History ===");
		
		System.out.print("Enter QR Code: ");
		String qrCode = scanner.nextLine().trim();
		
		try {
			byte[] result = contract.evaluateTransaction("getHistory", qrCode);
			System.out.println("Basil history: " + prettyJson(result));
		} catch (Exception e) {
			System.out.println("❌ Error getting basil history: " + e.getMessage());
		}
	}
	
	private static void transferOwnership(Scanner scanner, Contract contract) {
		System.out.println("\n=== Transfer Ownership ===");
		
		System.out.print("Enter QR Code: ");
		String qrCode = scanner.nextLine().trim();
		
		System.out.print("Enter New Org ID (e.g. Org2MSP): ");
		String newOrgId = scanner.nextLine().trim();
		
		System.out.print("Enter New Name: ");
		String newName = scanner.nextLine().trim();
		
		try {
			byte[] result = contract.submitTransaction("transferOwnership", qrCode, newOrgId, newName);
			System.out.println("✅ Ownership transferred successfully: " + new String(result));
		} catch (Exception e) {
			System.out.println("❌ Error transferring ownership: " + e.getMessage());
		}
	}

	private static String prettyJson(final byte[] json) {
		return prettyJson(new String(json, StandardCharsets.UTF_8));
	}

	private static String prettyJson(final String json) {
		var parsedJson = JsonParser.parseString(json);
		return new GsonBuilder().setPrettyPrinting().create().toJson(parsedJson);
	}
}
