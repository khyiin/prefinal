package main;

import config.config;
import java.util.Scanner;
import java.util.List;
import java.util.Map;

public class main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        config con = new config();
        main app = new main();
        String resp;

        do {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1: 
                    System.out.print("Enter email: ");
                    String email = sc.next();
                    System.out.print("Enter Password: ");
                    String pass = sc.next();
                    String hashedPass = con.hashPassword(pass); 

                    String qry = "SELECT * FROM tbl_users WHERE u_email = ? AND u_pass = ?";
                    List<Map<String, Object>> result = con.fetchRecords(qry, email, hashedPass);

                    if (result.isEmpty()) {
                        System.out.println("INVALID CREDENTIALS");
                    } else {
                        Map<String, Object> user = result.get(0);
                        // ✅ Get the user ID and other info
                        int userId = Integer.parseInt(user.get("u_id").toString()); // <-- make sure your column is 'u_id'
                        String type = user.get("u_type").toString();
                        String name = user.get("u_name").toString();
                        String status = user.get("u_status").toString();

                        if (status.equalsIgnoreCase("Pending")) {
                            System.out.println("Your account is still pending approval. Please wait for the admin to approve it.");
                            break;
                        }

                        System.out.println("LOGIN SUCCESS! Welcome, " + name);

                        // ✅ Correctly call dashboards with the actual user ID
                        if (type.equalsIgnoreCase("Admin")) {
                            new admin().show();
                        } else if (type.equalsIgnoreCase("Landlord")) {
                            new landlord(userId).show(); // pass landlord ID
                        } else {
                            new tenant(userId).show(); // pass tenant ID
                        }
                    }
                    break;

                case 2: 
                    System.out.print("Enter user name: ");
                    sc.nextLine(); 
                    String name = sc.nextLine();

                    System.out.print("Enter user email: ");
                    String newEmail = sc.next();

                    while (true) {
                        String checkQry = "SELECT * FROM tbl_users WHERE u_email = ?";
                        List<Map<String, Object>> exists = con.fetchRecords(checkQry, newEmail);
                        if (exists.isEmpty()) {
                            break;
                        } else {
                            System.out.print("Email already exists, Enter another Email: ");
                            newEmail = sc.next();
                        }
                    }

                    System.out.print("Enter Gender: ");
                    String gender = sc.next();
                    System.out.print("Enter Address: ");
                    sc.nextLine();
                    String address = sc.nextLine();
                    System.out.print("Enter Contact: ");
                    String contact = sc.next();

                    System.out.print("Enter user Type (1 - Admin / 2 - Landlord / 3 - Tenant): ");
                    int typeNum = sc.nextInt();
                    while (typeNum < 1 || typeNum > 3) {
                        System.out.print("Invalid, choose 1, 2, or 3 only: ");
                        typeNum = sc.nextInt();
                    }

                    String tp = (typeNum == 1) ? "Admin" : (typeNum == 2 ? "Landlord" : "Tenant");

                    System.out.print("Enter Password: ");
                    String newPass = sc.next();
                    String hashedNewPass = con.hashPassword(newPass); 

                    String adminCheck = "SELECT COUNT(*) AS admin_count FROM tbl_users WHERE u_type = 'Admin'";
                    List<Map<String, Object>> countResult = con.fetchRecords(adminCheck);

                    int adminCount = 0;
                    if (!countResult.isEmpty() && countResult.get(0).get("admin_count") != null) {
                        adminCount = Integer.parseInt(countResult.get(0).get("admin_count").toString());
                    }

                    String status;
                    if (tp.equals("Admin") && adminCount == 0) {
                        status = "Approved"; 
                        System.out.println("Registration successful! Your account is approved!");
                    } else {
                        status = "Pending";
                        System.out.println("Registration successful! Your account is pending approval by the admin.");
                    }

                    String sql = "INSERT INTO tbl_users (u_name, u_email, u_gender, u_address, u_contact, u_type, u_pass, u_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    con.addRecord(sql, name, newEmail, gender, address, contact, tp, hashedNewPass, status);

                    break;

                case 3: 
                    System.out.println("Exiting program...");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
                    break;
            }

            System.out.print("\nDo you want to go back to MAIN MENU? (yes/no): ");
            resp = sc.next();

        } while (resp.equalsIgnoreCase("yes"));

        System.out.println("Thank you! Program ended.");
        sc.close();
    }
}
