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
                case 1: // LOGIN
                    System.out.print("Enter email: ");
                    String email = sc.next();
                    System.out.print("Enter Password: ");
                    String pass = sc.next();

                    String qry = "SELECT * FROM tbl_boarder WHERE u_email = ? AND u_pass = ?";
                    List<Map<String, Object>> result = con.fetchRecords(qry, email, pass);

                    if (result.isEmpty()) {
                        System.out.println("INVALID CREDENTIALS");
                    } else {
                        Map<String, Object> user = result.get(0);
                        String type = user.get("u_type").toString();
                        String name = user.get("u_name").toString();

                        System.out.println("LOGIN SUCCESS! Welcome, " + name);

                        if (type.equalsIgnoreCase("Admin")) {
                            app.adminDashboard();
                        } else {
                            app.tenantDashboard();
                        }
                    }
                    break;

                case 2: // REGISTER
                    System.out.print("Enter user name: ");
                    sc.nextLine(); // consume newline
                    String name = sc.nextLine();

                    System.out.print("Enter user email: ");
                    String newEmail = sc.next();

                    // VALIDATION: check if email exists
                    while (true) {
                        String checkQry = "SELECT * FROM tbl_boarder WHERE u_email = ?";
                        List<Map<String, Object>> exists = con.fetchRecords(checkQry, newEmail);
                        if (exists.isEmpty()) {
                            break;
                        } else {
                            System.out.print("Email already exists, Enter other Email: ");
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

                    System.out.print("Enter user Type (1 - Admin / 2 - Tenant): ");
                    int type = sc.nextInt();
                    while (type < 1 || type > 2) {
                        System.out.print("Invalid, choose 1 or 2 only: ");
                        type = sc.nextInt();
                    }
                    String tp = (type == 1) ? "Admin" : "Tenant";

                    System.out.print("Enter Password: ");
                    String newPass = sc.next();

                    // Default status is "Approved"
                    String sql = "INSERT INTO tbl_boarder (u_name, u_email, u_gender, u_address, u_contact, u_type, u_pass, u_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    con.addRecord(sql, name, newEmail, gender, address, contact, tp, newPass, "Approved");

                    System.out.println("Registration successful! Your account is now approved.");
                    break;

                case 3: // EXIT
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

    // ---------------------- ADMIN DASHBOARD ----------------------
    public void adminDashboard() {
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. Manage Users");
            System.out.println("2. Manage Reservations");
            System.out.println("3. Exit to Main Menu");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    manageUsers();
                    break;
                case 2:
                    System.out.println("Manage Reservation - Functionality coming soon...");
                    break;
                case 3:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        } while (choice != 3);
    }

    // ---------------------- TENANT DASHBOARD ----------------------
    public void tenantDashboard() {
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n===== TENANT DASHBOARD =====");
            System.out.println("1. View Rooms");
            System.out.println("2. View Reservations");
            System.out.println("3. Log out");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("View Rooms - Functionality coming soon...");
                    break;
                case 2:
                    System.out.println("View Reservations - Functionality coming soon...");
                    break;
                case 3:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        } while (choice != 3);
    }

    // ---------------------- USER MANAGEMENT ----------------------
    public void manageUsers() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();
        int userChoice;

        do {
            System.out.println("\n===== USER MENU =====");
            System.out.println("1. Add User");
            System.out.println("2. View User");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. Back");
            System.out.print("Enter choice: ");
            userChoice = sc.nextInt();

            switch (userChoice) {
                case 1:
                    addBoarder();
                    break;
                case 2:
                    viewBoarders();
                    break;
                case 3:
                    viewBoarders();
                    updateBoarder();
                    break;
                case 4:
                    viewBoarders();
                    deleteBoarder();
                    break;
                case 5:
                    System.out.println("Going back to Admin Dashboard...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        } while (userChoice != 5);
    }

    // ---------------------- CRUD FUNCTIONS ----------------------
    public void addBoarder() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        System.out.print("Enter Gender: ");
        String gender = sc.nextLine();
        System.out.print("Enter Address: ");
        String address = sc.nextLine();
        System.out.print("Enter Contact: ");
        String contact = sc.nextLine();
        System.out.print("Enter Type: ");
        String type = sc.nextLine();
        System.out.print("Enter Pass: ");
        String pass = sc.nextLine();

        // Default status is "Approved"
        String sql = "INSERT INTO tbl_boarder (u_name, u_email, u_gender, u_address, u_contact, u_type, u_pass, u_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        conf.addRecord(sql, name, email, gender, address, contact, type, pass, "Approved");
    }

    public void viewBoarders() {
        String qry = "SELECT * FROM tbl_boarder";
        String[] hdrs = {"UID", "Name", "Email", "Gender", "Address", "Contact", "Type", "Pass", "Status"};
        String[] clms = {"u_id", "u_name", "u_email", "u_gender", "u_address", "u_contact", "u_type", "u_pass", "u_status"};

        config conf = new config();
        conf.viewRecords(qry, hdrs, clms);
    }

    public void updateBoarder() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.print("Enter Boarder ID to Update: ");
        int id = sc.nextInt(); sc.nextLine();

        System.out.print("New Name: ");
        String name = sc.nextLine();
        System.out.print("New Email: ");
        String email = sc.nextLine();
        System.out.print("New Gender: ");
        String gender = sc.nextLine();
        System.out.print("New Address: ");
        String address = sc.nextLine();
        System.out.print("New Contact: ");
        String contact = sc.nextLine();
        System.out.print("New Type: ");
        String type = sc.nextLine();
        System.out.print("New Pass: ");
        String pass = sc.nextLine();
        System.out.print("New Status (Approved/Rejected): ");
        String status = sc.nextLine();

        String qry = "UPDATE tbl_boarder SET u_name=?, u_email=?, u_gender=?, u_address=?, u_contact=?, u_type=?, u_pass=?, u_status=? WHERE u_id=?";
        conf.updateRecord(qry, name, email, gender, address, contact, type, pass, status, id);
    }

    public void deleteBoarder() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.print("Enter Boarder ID to Delete: ");
        int id = sc.nextInt();

        String qry = "DELETE FROM tbl_boarder WHERE u_id=?";
        conf.deleteRecord(qry, id);
    }
}
