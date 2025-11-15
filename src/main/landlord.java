package main;

import config.config;
import java.util.*;

public class landlord {

    private final config con = new config();
    private final int landlordId;

    public landlord(int landlordId) {
        this.landlordId = landlordId;
    }

    public void show() {
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n===== LANDLORD DASHBOARD =====");
            System.out.println("1. Add Room");
            System.out.println("2. View My Rooms");
            System.out.println("3. Update Room");
            System.out.println("4. Delete Room");
            System.out.println("5. View Reservations");
            System.out.println("6. View Leave Requests");
            System.out.println("7. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addRoom(sc);
                    break;
                case 2:
                    viewMyRooms();
                    break;
                case 3:
                    updateRoom(sc);
                    break;
                case 4:
                    deleteRoom(sc);
                    break;
                case 5:
                    viewReservations(sc);
                    break;
                case 6:
                    viewLeaveRequests(sc);
                    break;
                case 7:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 7);
    }

    private void addRoom(Scanner sc) {
        System.out.println("\n=== ADD ROOM ===");
        System.out.print("Enter room name: ");
        String name = sc.nextLine();
        System.out.print("Enter room type: ");
        String type = sc.nextLine();
        System.out.print("Enter price: ");
        double price = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter description: ");
        String desc = sc.nextLine();
        System.out.print("Enter location: ");
        String loc = sc.nextLine();

        String sql = "INSERT INTO tbl_rooms (landlord_id, r_name, r_type, r_price, r_desc, r_status, r_loc) VALUES (?, ?, ?, ?, ?, 'Available', ?)";
        con.addRecord(sql, landlordId, name, type, price, desc, loc);

        System.out.println("✅ Room added successfully.");
    }

   
    private void viewMyRooms() {
        System.out.println("\n=== MY ROOMS ===");

        String sql = "SELECT r_id, r_name, r_price, r_loc, r_status "
                   + "FROM tbl_rooms WHERE landlord_id = ?";
        List<Map<String, Object>> rooms = con.fetchRecords(sql, landlordId);

        if (rooms.isEmpty()) {
            System.out.println("You have no rooms listed.");
        } else {

            System.out.printf("%-10s %-20s %-10s %-20s %-12s%n",
                    "ROOM ID", "ROOM NAME", "PRICE", "LOCATION", "STATUS");
            System.out.println("-----------------------------------------------------------------------");

            for (Map<String, Object> r : rooms) {
                System.out.printf("%-10s %-20s %-10s %-20s %-12s%n",
                        r.get("r_id"),
                        r.get("r_name"),
                        r.get("r_price"),
                        r.get("r_loc"),
                        r.get("r_status"));
            }
        }
    }

    private void updateRoom(Scanner sc) {
        viewMyRooms();
        System.out.print("\nEnter Room ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter new price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter new description: ");
        String desc = sc.nextLine();

        String sql = "UPDATE tbl_rooms SET r_price = ?, r_desc = ? WHERE r_id = ? AND landlord_id = ?";
        con.updateRecord(sql, price, desc, id, landlordId);

        System.out.println("✅ Room updated successfully.");
    }

    private void deleteRoom(Scanner sc) {
        viewMyRooms();
        System.out.print("\nEnter Room ID to delete: ");
        int id = sc.nextInt();

        String sql = "DELETE FROM tbl_rooms WHERE r_id = ? AND landlord_id = ?";
        con.deleteRecord(sql, id, landlordId);

        System.out.println("✅ Room deleted.");
    }

    
    private void viewReservations(Scanner sc) {
        System.out.println("\n=== RESERVATIONS ===");

        String sql = "SELECT r.res_id, u.u_name AS tenant_name, rm.r_name, r.mvin_date, r.dur, r.stat " +
                     "FROM tbl_reservations r " +
                     "JOIN tbl_rooms rm ON r.r_id = rm.r_id " +
                     "JOIN tbl_users u ON r.tenant_id = u.u_id " +
                     "WHERE rm.landlord_id = ?";

        List<Map<String, Object>> res = con.fetchRecords(sql, landlordId);

        if (res.isEmpty()) {
            System.out.println("No reservations yet.");
            return;
        }

        System.out.printf("%-8s %-15s %-15s %-12s %-10s %-12s%n",
                "ID", "TENANT", "ROOM", "MOVE-IN", "DURATION", "STATUS");
        System.out.println("-------------------------------------------------------------------------------");

        for (Map<String, Object> r : res) {
            System.out.printf("%-8s %-15s %-15s %-12s %-10s %-12s%n",
                    r.get("res_id"),
                    r.get("tenant_name"),
                    r.get("r_name"),
                    r.get("mvin_date"),
                    r.get("dur"),
                    r.get("stat"));
        }

        System.out.print("\nEnter Reservation ID to Approve/Reject (0 to cancel): ");
        int id = sc.nextInt();
        sc.nextLine();
        if (id == 0) return;

        System.out.print("Approve (A) or Reject (R)? ");
        String action = sc.nextLine().toUpperCase();

        if (action.equals("A")) {
            con.updateRecord("UPDATE tbl_reservations SET stat = 'Approved' WHERE res_id = ?", id);
            con.updateRecord("UPDATE tbl_rooms SET r_status = 'Reserved' WHERE r_id = (SELECT r_id FROM tbl_reservations WHERE res_id = ?)", id);

            System.out.println("✅ Reservation approved!");
        } 
        else if (action.equals("R")) {
            con.updateRecord("UPDATE tbl_reservations SET stat = 'Rejected' WHERE res_id = ?", id);
            System.out.println("❌ Reservation rejected.");
        }
    }

    
    private void viewLeaveRequests(Scanner sc) {
        System.out.println("\n=== TENANT LEAVE REQUESTS ===");

        String sql = "SELECT lr.req_id, u.u_name AS tenant, r.r_name, lr.reason, lr.status "
                   + "FROM tbl_leave_requests lr "
                   + "JOIN tbl_reservations res ON lr.res_id = res.res_id "
                   + "JOIN tbl_rooms r ON res.r_id = r.r_id "
                   + "JOIN tbl_users u ON res.tenant_id = u.u_id "
                   + "WHERE r.landlord_id = ?";

        List<Map<String, Object>> list = con.fetchRecords(sql, landlordId);

        if (list.isEmpty()) {
            System.out.println("No leave requests yet.");
            return;
        }

        System.out.printf("%-8s %-15s %-15s %-30s %-12s%n",
                "ID", "TENANT", "ROOM", "REASON", "STATUS");
        System.out.println("---------------------------------------------------------------------------------");

        for (Map<String, Object> lr : list) {
            System.out.printf("%-8s %-15s %-15s %-30s %-12s%n",
                    lr.get("req_id"),
                    lr.get("tenant"),
                    lr.get("r_name"),
                    lr.get("reason"),
                    lr.get("status"));
        }

        System.out.print("\nEnter Request ID to Approve/Reject (0 to cancel): ");
        int id = sc.nextInt();
        sc.nextLine();
        if (id == 0) return;

        System.out.print("Approve (A) or Reject (R)? ");
        String action = sc.nextLine().toUpperCase();

        if (action.equals("A")) {
            con.updateRecord("UPDATE tbl_leave_requests SET status = 'Approved' WHERE req_id = ?", id);
            System.out.println("✔ Leave request approved.");
        } 
        else if (action.equals("R")) {
            con.updateRecord("UPDATE tbl_leave_requests SET status = 'Rejected' WHERE req_id = ?", id);
            System.out.println("❌ Leave request rejected.");
        }
    }
}
