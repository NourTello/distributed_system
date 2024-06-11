import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Employee extends Remote {
    String getName() throws RemoteException;
    String getJob() throws RemoteException;
    byte[] takeScreenshot() throws RemoteException;
    byte[] capturePhoto() throws RemoteException;
    String getIPAddress() throws RemoteException;
    int getPortNumber() throws RemoteException;
    void startChatServer() throws RemoteException;
}
