import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class CoordinatorMain {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            Coordinator coordinator = new CoordinatorImp();
            Naming.rebind("Coordinator", coordinator);
            System.out.println("Coordinator is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
