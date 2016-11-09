package asteroidsserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextArea textArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new Thread(() -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(80);

                while (true) {
                    // Listen for a new connection request
                    Socket socket = serverSocket.accept();
                    
                    Platform.runLater(() -> {
                        textArea.appendText("Client connected to server.");
                    });
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }).start();
    }    
    
}
