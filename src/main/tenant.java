package main;

import config.config;
import java.util.*;

public class tenant {

    private final config con = new config();
    private final int tenantId;

    public tenant(int tenantId) {
        this.tenantId = tenantId;
    }

    public void show() {
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n===== TENANT DASHBOARD =====");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Create Reservation");
            System.out.println("3. View My Reservations");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Send Leave Request");
            System.out.println("6. View Leave Request Status");
            System.out.println("7. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewRooms();
                    break;
                case 2:
                    createReservation(sc);
                    break;
                case 3:
                    viewMyReservations();
                    break;
                case 4:
                    cancelReservation(sc);
                    break;
                case 5:
                    sendLeaveRequest(sc);
                    break;
                case 6:
                    viewLeaveStatus();
                    break;
                case 7:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        } while (choice != 7);
    }

    
    private void viewRooms() {
        System.out.println("\n=== AVAILABLE ROOMS ===");
        String sql = "SELECT r.r_id, r.r_name, r.r_type, r.r_price, r.r_desc, r.r_loc, u.u_name AS landlord_name "
                   + "FROM tbl_rooms r "
                   + "JOIN tbl_users u ON r.landlord_id = u.u_id "
                   + "WHERE r.r_status = 'Available'";

        List<Map<String, Object>> rooms = con.fetchRecords(sql);

        if (rooms.isEmpty()) {
            System.out.println("No available rooms right now.");
        } else {

            System.out.printf("%-8s %-15s %-10s %-10s %-25s %-15s %-15s%n",
                    "ROOM ID", "ROOM NAME", "TYPE", "PRICE", "DESCRIPTION", "LANDLORD", "LOCATION");
            System.out.println("---------------------------------------------------------------------------------------------");

            for (Map<String, Object> r : rooms) {
                System.out.printf("%-8s %-15s %-10s %-10s %-25s %-15s %-15s%n",
                        r.get("r_id"), r.get("r_name"), r.get("r_type"),
                        r.get("r_price"), r.get("r_desc"),
                        r.get("landlord_name"), r.get("r_loc"));
            }
        }
    }

    
    private void createReservation(Scanner sc) {
        viewRooms();
        System.out.print("\nEnter Room ID to reserve: ");
        int roomId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter move-in date (YYYY-MM-DD): ");
        String moveIn = sc.nextLine();
        System.out.print("Enter duration (months): ");
        int duration = sc.nextInt();

        String sql = "INSERT INTO tbl_reservations (tenant_id, r_id, mvin_date, dur, stat) "
                   + "VALUES (?, ?, ?, ?, 'Pending')";
        con.addRecord(sql, tenantId, roomId, moveIn, duration);
        System.out.println("✅ Reservation submitted successfully! Please wait for landlord approval.");
    }

    
    private void viewMyReservations() {
        System.out.println("\n=== MY RESERVATIONS ===");
        String sql = "SELECT r.res_id, rm.r_name, u.u_name AS landlord_name, r.mvin_date, r.dur, r.stat "
                   + "FROM tbl_reservations r "
                   + "JOIN tbl_rooms rm ON r.r_id = rm.r_id "
                   + "JOIN tbl_users u ON rm.landlord_id = u.u_id "
                   + "WHERE r.tenant_id = ?";

        List<Map<String, Object>> res = con.fetchRecords(sql, tenantId);

        if (res.isEmpty()) {
            System.out.println("You have no reservations yet.");
        } else {

            System.out.printf("%-8s %-15s %-15s %-12s %-10s %-10s%n",
                    "RES ID", "ROOM", "LANDLORD", "MOVE-IN", "MONTHS", "STATUS");
            System.out.println("-----------------------------------------------------------------------");

            for (Map<String, Object> r : res) {
                System.out.printf("%-8s %-15s %-15s %-12s %-10s %-10s%n",
                        r.get("res_id"), r.get("r_name"), r.get("landlord_name"),
                        r.get("mvin_date"), r.get("dur"), r.get("stat"));
            }
        }
    }


    private void cancelReservation(Scanner sc) {
        viewMyReservations();
        System.out.print("\nEnter Reservation ID to cancel: ");
        int id = sc.nextInt();

        String sql = "UPDATE tbl_reservations SET stat = 'Cancelled' "
                   + "WHERE res_id = ? AND tenant_id = ? AND stat = 'Pending'";
        con.updateRecord(sql, id, tenantId);
        System.out.println("✅ Reservation cancelled successfully (if it was pending).");
    }

    
    private void sendLeaveRequest(Scanner sc) {
        viewMyReservations();

        System.out.print("\nEnter Reservation ID you want to leave: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter your reason: ");
        String reason = sc.nextLine();

        String sql = "INSERT INTO tbl_leave_requests (res_id, reason, status) "
                   + "VALUES (?, ?, 'Pending')";
        con.addRecord(sql, id, reason);

        System.out.println("✔ Leave request sent. Please wait for landlord response.");
    }

   
    private void viewLeaveStatus() {
        System.out.println("\n=== MY LEAVE REQUESTS ===");

        String sql = "SELECT lr.req_id, r.r_name, lr.reason, lr.status "
                   + "FROM tbl_leave_requests lr "
                   + "JOIN tbl_reservations res ON lr.res_id = res.res_id "
                   + "JOIN tbl_rooms r ON res.r_id = r.r_id "
                   + "WHERE res.tenant_id = ?";

        List<Map<String, Object>> list = con.fetchRecords(sql, tenantId);

        if (list.isEmpty()) {
            System.out.println("You have no leave requests.");
            return;
        }

        System.out.printf("%-8s %-15s %-30s %-10s%n",
                "REQ ID", "ROOM", "REASON", "STATUS");
        System.out.println("--------------------------------------------------------------------");

        for (Map<String, Object> lr : list) {
            System.out.printf("%-8s %-15s %-30s %-10s%n",
                    lr.get("req_id"), lr.get("r_name"),
                    lr.get("reason"), lr.get("status"));
        }
    }
}
