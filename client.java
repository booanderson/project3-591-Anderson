import java.io.*;
import java.net.*;
import java.util.Scanner;

//main class for the client application
class EmployeeLeaveClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    //main
    public static void main(String[] args) {
        try (
	    //connects to the server using specified port
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
	    //creating a bugger to read data from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    //creating a printwriter to send data to the server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    //creating scanner for input
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to the server.");


            while (true) {
                System.out.print("Enter Employee ID (EMP001, EMP002, or EMP003)   (or type 'exit' to quit): ");
		//getting user input
                String employeeId = scanner.nextLine();
		//sending emp ID to server
                out.println(employeeId);

		//if "exit" break for loop
                if ("exit".equalsIgnoreCase(employeeId)) {
                    break;
                }

                System.out.print("Enter number of leaves to request: ");
		//getting # leaves from user for designated employee
                int leaveRequest = scanner.nextInt();
                //making sure there's no white space
		scanner.nextLine(); 
                out.println(leaveRequest);

                //reading the response from the server
                String response = in.readLine();
                System.out.println("Server Response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

