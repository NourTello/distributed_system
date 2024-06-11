import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Coordinator extends Remote {
    List<Employee> getActiveEmployees() throws RemoteException;
    Employee getEmployeeByName(String name) throws RemoteException;
    void registerEmployee(Employee employee) throws RemoteException;
}
