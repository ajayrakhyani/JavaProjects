import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "Admin@1234";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            Scanner input = new Scanner(System.in);
            while (true) {
                System.out.println("Hotel Management System:");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                byte choice = input.nextByte();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, input, statement);
                        break;
                    case 2:
                        viewReservations(connection, statement);
                        break;
                    case 3:
                        getRoomNumber(connection, input, statement);
                        break;
                    case 4:
                        updateReservation(connection, input, statement);
                        break;
                    case 5:
                        deleteReservation(connection, input, statement);
                        break;
                    case 0:
                        exit();
                        input.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again!");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reserveRoom(Connection connection, Scanner input, Statement statement) {
        try {
            System.out.println("Enter the guest name: ");
            input.nextLine();
            String guestName = input.nextLine();
            System.out.println("Enter the room number: ");
            int roomNumber = input.nextInt();
            System.out.println("Enter the contact number: ");
            String contactNumber = input.next();

            String sqlQuery = "INSERT INTO reservations (guest_name, room_number, contact_number) VALUES ('" + guestName + "', '" + roomNumber + "', '" + contactNumber + "');";

            int rowsAffected = statement.executeUpdate(sqlQuery);
            if (rowsAffected > 0) {
                System.out.println("Reservation successful!");
            } else {
                System.out.println("Reservation Failed!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewReservations(Connection connection, Statement statement) {
        String sqlQuery = "SELECT * from reservations";

        try {
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");


            while (resultSet.next()) {
                int reservationID = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                Timestamp reservationDate = resultSet.getTimestamp("reservation_date");

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationID, guestName, roomNumber, contactNumber, reservationDate);
            }
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void getRoomNumber(Connection connection, Scanner input, Statement statement) {
        try {
            System.out.println("Enter the reservation ID: ");
            int reservationId = input.nextInt();

            String sqlQuery = "SELECT room_number from reservations WHERE reservation_id = " + reservationId;

            ResultSet resultSet = statement.executeQuery(sqlQuery);
            if (resultSet.next()) {
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("The room number for reservation ID: " + reservationId + " is " + roomNumber);
            } else {
                System.out.println("No room number found for the specified reservation ID.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private static void updateReservation(Connection connection, Scanner input, Statement statement) {
        try {
            System.out.println("Enter the reservation ID you need to update: ");
            int reservationID = input.nextInt();
            if (!reservationExists(connection, statement, reservationID)) {
                System.out.println("The reservation do not exist!");
                return;
            }
            System.out.println("Enter the new guest name: ");
            input.nextLine();
            String guestName = input.nextLine();
            System.out.println("Enter the new room number: ");
            int roomNumber = input.nextInt();
            System.out.println("Enter the new contact number: ");
            String contactNumber = input.next();

            String sqlQuery = "UPDATE reservations SET guest_name = '" + guestName + "', room_number = " + roomNumber + ", contact_number = '" + contactNumber + "' WHERE reservation_id = " + reservationID + ";";

            int rowsAffected = statement.executeUpdate(sqlQuery);
            if (rowsAffected > 0) {
                System.out.println("Reservation Updated!");
            } else {
                System.out.println("Reservation Update Failed!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteReservation(Connection connection, Scanner input, Statement statement) {
        try {
            System.out.println("Enter the reservation ID you need to update: ");
            int reservationID = input.nextInt();
            if (!reservationExists(connection, statement, reservationID)) {
                System.out.println("The reservation do not exist!");
                return;
            }
            String sqlQuery = "DELETE FROM reservations WHERE reservation_id = " + reservationID + ";";
            int rowsAffected = statement.executeUpdate(sqlQuery);
            if (rowsAffected > 0) {
                System.out.println("Reservation Deleted!");
            } else {
                System.out.println("Reservation Deletion failed!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(500);
            i--;
        }
        System.out.println();
        System.out.println("Thank you for using the Hotel Reservation System!!!");
    }


    private static boolean reservationExists(Connection connection, Statement statement, int reservationID) {
        try {
            String sqlQuery = "SELECT reservation_id from reservations WHERE reservation_id = " + reservationID;
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
