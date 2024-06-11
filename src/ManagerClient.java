import java.rmi.Naming;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.util.List;

public class ManagerClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            Coordinator coordinator = (Coordinator) Naming.lookup("//localhost/Coordinator");

            while (true) {
                // Display active employees
                System.out.println("Active Employees:");
                List<Employee> activeEmployees = coordinator.getActiveEmployees();
                for (int i = 0; i < activeEmployees.size(); i++) {
                    System.out.println((i + 1) + ". " + activeEmployees.get(i).getName());
                }

                // Choose an employee
                System.out.println("Choose an employee by entering the corresponding number:");
                int employeeChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (employeeChoice < 1 || employeeChoice > activeEmployees.size()) {
                    System.out.println("Invalid choice.");
                    continue;
                }
                String employeeName = activeEmployees.get(employeeChoice - 1).getName();

                // Choose an operation
                while (true) {
                    System.out.println("Choose an operation:");
                    System.out.println("1. Take Screenshot");
                    System.out.println("2. Capture Photo");
                    System.out.println("3. Start Chat");
                    System.out.println("4. Choose another employee");
                    System.out.println("5. Stop");

                    int choice = scanner.nextInt();
                    scanner.nextLine();  // Consume newline

                    Employee employee = coordinator.getEmployeeByName(employeeName);

                    if (employee == null) {
                        System.out.println("Employee not found.");
                        break;
                    }

                    switch (choice) {
                        case 1:
                            byte[] screenshot = employee.takeScreenshot();
                            displayImage(screenshot);
                            break;
                        case 2:
                            byte[] photo = employee.capturePhoto();
                            displayImage(photo);
                            break;
                        case 3:
                            employee.startChatServer();
                            System.out.println("Employee IP: " + employee.getIPAddress());
                            System.out.println("Employee Port: " + employee.getPortNumber());
                            startChat(employee.getIPAddress(), employee.getPortNumber(), scanner);
                            break;
                        case 4:
                            // Break the inner loop to choose another employee
                            break;
                        case 5:
                            // Exit the program
                            System.out.println("Goodbye!");
                            System.exit(0);
                        default:
                            System.out.println("Invalid choice.");
                    }
                    if (choice == 4) {
                        break;
                    }
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("No more input available.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void displayImage(byte[] imageData) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(bis);

            // Display the image in a JFrame
            JFrame frame = new JFrame();
            frame.setSize(image.getWidth(), image.getHeight());
            JLabel label = new JLabel(new ImageIcon(image));
            frame.add(label);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startChat(String ipAddress, int portNumber, Scanner scanner) {
        try (Socket socket = new Socket(ipAddress, portNumber);
             BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Start chatting. Type 'exit' to end the chat.");
            Thread readThread = new Thread(() -> {
                String message;
                try {
                    while ((message = socketReader.readLine()) != null) {
                        System.out.println("Employee: " + message);
                    }
                } catch (Exception e) {
                    if (!socket.isClosed()) {
                        e.printStackTrace();
                    }
                }
            });

            readThread.start();

            String message;
            while (!(message = scanner.nextLine()).equalsIgnoreCase("exit")) {
                writer.println(message);
            }

            // Close the socket and stop the reading thread
            socket.close();
            readThread.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
