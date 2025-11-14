import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SumClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);
            System.out.println("[CLIENT] connected to server");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            String response;
            while ((userInput = systemIn.readLine()) != null) {
                if (userInput.toLowerCase().equals("exit"))
                    break;

                System.out.println("[CLIENT] sending: " + userInput);
                out.println(userInput);
                out.flush();

                response = in.readLine();
                System.out.println("[CLIENT] server response: " + response);
            }

            socket.shutdownOutput();

            System.out.println("[CLIENT] stoped sending data");
            response = in.readLine();
            System.out.println("[CLIENT] server response: " + response);

            socket.shutdownInput();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
