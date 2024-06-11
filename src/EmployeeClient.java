import java.rmi.Naming;
import java.util.Scanner;

public class EmployeeClient {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your name:");
            String name = scanner.nextLine();
            System.out.println("Enter your job:");
            String job = scanner.nextLine();

            Employee employee = new EmployeeImp(name, job);
            Coordinator coordinator = (Coordinator) Naming.lookup("//localhost/Coordinator");
            coordinator.registerEmployee(employee);
            System.out.println("Employee registered successfully.");
        } catch (Exception e ){
        e.printStackTrace();
    }
}
}
