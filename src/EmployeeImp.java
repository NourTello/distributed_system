import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.MatOfByte;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class EmployeeImp extends UnicastRemoteObject implements Employee {
    private String name;
    private String job;
    private String ipAddress;
    private int portNumber;
    private ServerSocket serverSocket;

    public EmployeeImp(String name, String job) throws RemoteException {
        this.name = name;
        this.job = job;
        try {
            this.ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            this.ipAddress = "Unknown";
        }
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public String getJob() throws RemoteException {
        return job;
    }

    @Override
    public byte[] takeScreenshot() throws RemoteException {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenCapture = robot.createScreenCapture(screenRect);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenCapture, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] capturePhoto() throws RemoteException {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        Mat frame = new Mat();
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Error: Camera not available");
            return null;
        }
        camera.read(frame);
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", frame, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        camera.release();
        return byteArray;
    }

    @Override
    public String getIPAddress() throws RemoteException {
        return ipAddress;
    }

    @Override
    public int getPortNumber() throws RemoteException {
        return portNumber;
    }

    @Override
    public void startChatServer() throws RemoteException {
        this.portNumber = generateAvailablePort();
        System.out.println("Starting chat server on port: " + portNumber);
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(portNumber);
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(new ChatHandler(socket)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private int generateAvailablePort() {
        Random random = new Random();
        int port;
        while (true) {
            port = 1024 + random.nextInt(65535 - 1024);
            if (isPortAvailable(port)) {
                break;
            }
        }
        return port;
    }

    private boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static class ChatHandler implements Runnable {
        private Socket socket;

        public ChatHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                Scanner scanner = new Scanner(System.in);
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Manager: " + message);
                    System.out.print("Reply: ");
                    String reply = scanner.nextLine();
                    writer.println(reply);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
