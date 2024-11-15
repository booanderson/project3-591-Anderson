/*-----Usage----- 
 * 1. compile with javac server.java client.java
 *
 * 2. run java EmployeeLeaveServer from a terminal 
 *
 * 3. run java EmployeeLeaveClient from a different terminal
 *
 * 4. Enter EMP001, EMP002, or EMP003 for the 3 employees
 *
 * 5. Enter the number of leaves the employee would like to take
 */



import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

class EmployeeLeaveServer {
    private static final int PORT = 12345;
    private static Map<String, LeaveDetails> leaveRecords = new HashMap<>();

    //class to store the leave details
    static class LeaveDetails {
        int totalLeaves;
        int currentBalance;


	//constructor for each employee
        LeaveDetails(int totalLeaves) {
            this.totalLeaves = totalLeaves;
            this.currentBalance = totalLeaves;
        }
    }

    public static void main(String[] args) {
        //initializing the employees leave data
        leaveRecords.put("EMP001", new LeaveDetails(20));
        leaveRecords.put("EMP002", new LeaveDetails(15));
        leaveRecords.put("EMP003", new LeaveDetails(25));

	//creating a server socket
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running on port " + PORT);

	    //servers continues until broken
            while (true) {
		//when a client requests to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
		//creating a new client handler, passes the clinets socket 
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //each client connection will run its own thread
    static class ClientHandler extends Thread {
        private Socket socket;

	//setter method for clients socket
        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
	    //creating buffer to read the clients input
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//creating printwriter to send the output to the client
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
		//creating employee ID
                String employeeId;
		//reading all characters from client input for employee ID
                while ((employeeId = in.readLine()) != null) {
                    //parsing # leaves from client input
		    int requestedLeaves = Integer.parseInt(in.readLine());

	            //building responce so be sent back to client
                    String response = processLeaveRequest(employeeId, requestedLeaves);
		    //sending the response back to the client
                    out.println(response);

                    //checking for "exit" from client
                    if ("exit".equalsIgnoreCase(employeeId)) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
		    //ensuring socket is closed, otherwise error
                    socket.close();
                    System.out.println("Client disconnected.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

	//method to return a string value of the approve or deny message
        private String processLeaveRequest(String employeeId, int requestedLeaves) {
	    //making sure the employee exists
            if (leaveRecords.containsKey(employeeId)) {
		//getting designated employees information
                LeaveDetails details = leaveRecords.get(employeeId);
		//testing whether employee has enough leaves to us for the request
                if (requestedLeaves <= details.currentBalance) {
                    details.currentBalance -= requestedLeaves;
                    return "Approved. Remaining balance: " + details.currentBalance;
		  //if the employee does not have enough leaves for the request
                } else {
                    return "Denied. Not enough leave balance.";
                }
	      //the employee does not exist under that code
            } else {
                return "Employee not found.";
            }
        }
    }
}

