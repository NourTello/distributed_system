import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
public class CoordinatorImp extends UnicastRemoteObject implements Coordinator {
    private List<Employee> activeEmployees;

    protected CoordinatorImp() throws RemoteException {
        activeEmployees = new ArrayList<>();
    }
    @Override
    public List<Employee> getActiveEmployees() throws RemoteException {
        return activeEmployees;
    }
    @Override
    public Employee getEmployeeByName(String name) throws RemoteException {
        for (Employee employee : activeEmployees) {
            if (employee.getName().equals(name)) {
                return employee;
            }
        }
        return null;
    }
    @Override
    public void registerEmployee(Employee employee) throws RemoteException {
        activeEmployees.add(employee);
    }
}
