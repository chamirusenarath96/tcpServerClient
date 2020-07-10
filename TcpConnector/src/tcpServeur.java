// tcpServer.java 

// usage : java tcpServer <port number>.
// default port is 1500.
// connection to be closed by client.
// this server handles only 1 connection.
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TCPServeur * Utilisation: * Lancer l'application par la commande java
 * tcpServeur <port d'ecoute>
 ** Le serveur reste en attente d'un client. Quand la connexion est �tablie, *
 * il va afficher les messages envoy�es par ce client *	quitter l'application
 * avec ctrl+C
 *
 */
public class tcpServeur {

    public static void main(String args[]) {

        int port = 1500;
        ServerSocket socket_serveur;
        BufferedReader input;

        System.out.println("\n\n*********************************");
        System.out.println("***********Serveur***************");
        System.out.println("*********************************\n\n");
        
        // si le port est donn� en argument!!
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.out.println("port d'ecoute= 1500 (par defaut)");
                port = 1500;
            }
        }

        //Ouverture du socket en attente de connexions
        try {
            socket_serveur = new ServerSocket(port);
            System.out.println("Serveur en attente de clients sur le port "
                    + socket_serveur.getLocalPort());

            // boucle infinie: traitement d'une connexion client
            while (true) {
                Socket socket = socket_serveur.accept();
                System.out.println("nouvelle connexion acceptee "
                        + socket.getInetAddress()
                        + ":" + socket.getPort());
                //new code for initilization
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos1 = new DataOutputStream(socket.getOutputStream());
                DataOutputStream dos2 = new DataOutputStream(socket.getOutputStream());
                InputStreamReader isr = new InputStreamReader(dis);
                input = new BufferedReader(isr);
               
                List<String> arrList = new ArrayList<String>();
                int s1Count = 0;
                
                try {
                    while (true) {
                        //System.out.println("init 2"); 
                        String[] messages = input.readLine().split(" ");
                        //System.out.println(Arrays.asList(messages));
                        String message = messages[messages.length - 1];
                        // System.out.println(message);
                        if (message.startsWith("[p]")) {
                            System.out.println("ping received");
                            dos1.writeUTF("[p]|welcome");
                            dos1.flush();
                            System.out.println("response sent");
                            message = "";
                            //messages = {};
                            
                            
                        }
                        else if(message.startsWith("[f]")){
                            System.out.println("Frame received");
                            arrList.add(message.substring(3, message.length()-1));
                            System.out.println(arrList);
                            
                            //System.out.println(arrList.size());
                            if(arrList.size() - 1 == Integer.parseInt(String.valueOf(message.charAt(message.length() - 1)))){
                                System.out.println("Sending Ack: " + message.charAt(message.length() - 1));
                                //System.out.println(message);
                                //fileWrite(arrList.get(0), message.substring(3, message.length()-1));
                                if(message.charAt(message.length() - 1) > 0 && !(message.substring(3, message.length()-1).startsWith(arrList.get(0))) ){
                                fileWrite(arrList.get(0), message.substring(3, message.length()-1));
                                }
                                dos2.writeUTF(" " + message.charAt(message.length() - 1) + " ");
                                dos2.flush();
                                System.out.println("Ack sent");
                            }
                            else{
                                System.out.println("Ack not sent");
                            }
                            
                            
                        }
                        else if(message.startsWith("[d]")){
                            System.out.println("Request for directory received");
                            String currentDirectory = System.getProperty("user.dir");
                            File directoryPath = new File(currentDirectory + "//src");
                            String contents[] = directoryPath.list();
                            String delim = "\n";

                            String res = String.join(delim, contents);
                            dos2.writeUTF(res);
                            dos2.flush();
                            System.out.println("Request done!");
                            
                        }
                        else if(message.startsWith("[s1]") && s1Count <1){
                            
                            System.out.println("Frame received");
                            arrList.add(message.substring(3, message.length()-1));
                            System.out.println("Sending Ack: " + message.charAt(message.length() - 1));
                            dos2.writeUTF(" " + message.charAt(message.length() - 1) + " ");
                            dos2.flush();
                            System.out.println("Ack sent");
                            s1Count += 1;
                            
                        }
                        
                        else if(message.startsWith("[sh]")){
                            socket.close();
                            System.out.println("The server is shut down!");
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
                
                // connexion ferm�e par client
                try {
                    socket.close();
                    System.out.println("connexion ferm�e par le client");
                } catch (IOException e) {
                    System.out.println(e);
                }
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void fileWrite(String filename, String data) throws IOException{
        filename = filename.substring(0, filename.length() - 7) + "_CP" + ".txt";
        Files.write(Paths.get(filename), (data+"\n").getBytes(), StandardOpenOption.APPEND);
        
        //FileWriter writer = new FileWriter(filename); 
        //writer.write(data + System.lineSeparator());
        //writer.close();
    }
}
