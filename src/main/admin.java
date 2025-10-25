package main;

import config.config;
import java.util.Scanner;

public class admin {

    private config con = new config();

    public void show() {
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. Manage Users");
            System.out.println("2. Approve Pending Accounts");
            System.out.println("3. Exit to Main Menu");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    manageUsers();
                    break;
                case 2:
                    approvePendingAccounts();
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

    private void manageUsers() {
    Scanner sc = new Scanner(System.in);
    int userChoice;
    do {
        System.out.println("\n===== USER MENU =====");
        
        System.out.println("1. View User");
        System.out.println("2. Update User");
        System.out.println("3. Delete User");
        System.out.println("4. Back");
        System.out.print("Enter choice: ");
        userChoice = sc.nextInt();

        switch (userChoice) {
            case 1:
                viewUsers();
                break;
            case 2:
                viewUsers();
                updateUser();
                break;
            case 3:
                viewUsers();
                deleteUser();
                break;
            case 4:
                System.out.println("Going back to Admin Dashboard...");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    } while (userChoice != 4);
}

    public void viewUsers() {
        String qry = "SELECT * FROM tbl_users";
        String[] hdrs = {"UID", "Name", "Email", "Gender", "Address", "Contact", "Type", "Pass", "Status"};
        String[] clms = {"u_id", "u_name", "u_email", "u_gender", "u_address", "u_contact", "u_type", "u_pass", "u_status"};

        config conf = new config();
        conf.viewRecords(qry, hdrs, clms);
    }

    public void updateUser() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.print("Enter User ID to Update: ");
        int id = sc.nextInt();
        sc.nextLine();

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
        System.out.print("New Status (Approved/Pending): ");
        String status = sc.nextLine();

        String qry = "UPDATE tbl_users SET u_name=?, u_email=?, u_gender=?, u_address=?, u_contact=?, u_type=?, u_pass=?, u_status=? WHERE u_id=?";
        conf.updateRecord(qry, name, email, gender, address, contact, type, pass, status, id);
    }

    public void deleteUser() {
        Scanner sc = new Scanner(System.in);
        config conf = new config();

        System.out.print("Enter User ID to Delete: ");
        int id = sc.nextInt();

        String qry = "DELETE FROM tbl_users WHERE u_id=?";
        conf.deleteRecord(qry, id);
    }


    private void approvePendingAccounts() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n===== PENDING ACCOUNTS =====");
        String qry = "SELECT u_id, u_name, u_email, u_type, u_status FROM tbl_users WHERE u_status = 'Pending'";
        String[] hdrs = {"ID", "Name", "Email", "Type", "Status"};
        String[] clms = {"u_id", "u_name", "u_email", "u_type", "u_status"};
        con.viewRecords(qry, hdrs, clms);

        System.out.print("Enter ID to approve (or 0 to cancel): ");
        int id = sc.nextInt();

        if (id != 0) {
            String sql = "UPDATE tbl_users SET u_status = ? WHERE u_id = ?";
            con.updateRecord(sql, "Approved", id);
            System.out.println("User ID " + id + " has been approved!");
        } else {
            System.out.println("Approval canceled.");
        }
    }
}
