
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCPClient * Utilisation: * Lancer l'application par la commande java
 * tcpClient <adresse IP du serveur> <port destination>
 ** Ins�rer une ligne et taper "entrer" pour l'envoyer au serveur *	quitter
 * l'application en tapant "."
 *
 */
public class tcpClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //port et adresse
        int port = 1500;
        InetAddress adresse = null;

        //socket
        Socket socket = null;

        //input-output
        BufferedReader input;
        PrintWriter output;

        String lineToBeSent;

        System.out.println("\n\n*********************************");
        System.out.println("***********Client****************");
        System.out.println("*********************************\n\n");

        // si l'adresse et le port sont donn�s en argument!!
        if (args.length == 2) {
            //adresse
            try {
                adresse = InetAddress.getByName(args[0]);
            } catch (UnknownHostException e) {
                System.out.println("adresse du serveur = 127.0.0.1 (par defaut)");
            }

            //port
            try {
                port = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("port du serveur = 1500 (par defaut)");
                port = 1500;
            }
        }

        // on assigne l'adresse si ceci n'a pas encore �t� fait
        if (adresse == null) {
            try {
                adresse = InetAddress.getByName("127.0.0.1");
            } catch (UnknownHostException e) {
                System.out.print(e);
                System.exit(1);
            }
        }

        //connexion au serveur
        try {
            System.out.println("Etablissement de connexion vers  \\ "
                    + adresse.getHostAddress()
                    + ":" + port + ", veuillez patienter...");
            socket = new Socket(adresse, port);
            System.out.println("connect� au serveur "
                    + socket.getInetAddress()
                    + ":" + socket.getPort() + ": ins�rer du texte � envoyer");

        } catch (UnknownHostException e) {
            System.out.println("\nServeur " + adresse + ":" + port + " inconnu.");
            return; // Si serveur inconnu, la fonction arrete ici.
        } catch (IOException e) {
            System.out.println("connexion �chou�e, adresse/port incorrect");
            //erreur, on quitte
            System.exit(1);
        }

        //Envoi de message texte au serveur
        try {
            //les �changes avec le ssocket serveur se font � travers impout et output
            input = new BufferedReader(new InputStreamReader(System.in));
            output = new PrintWriter(socket.getOutputStream(), true);
            // get the input stream from the connected socket
            InputStream inputStream = socket.getInputStream();
            // create a DataInputStream so we can read data from it.
            DataInputStream serverInput = new DataInputStream(inputStream);
            DataOutputStream serverOutput1 = new DataOutputStream(socket.getOutputStream());
            DataOutputStream serverOutput2 = new DataOutputStream(socket.getOutputStream());
            // on envoi le message ins�r� sur console
            while (true) {

                printMenu();
                System.out.print("Enter the option : ");
                int option = Integer.parseInt(input.readLine());

                if (option == 1) {
                    lineToBeSent = " [p]";
                    output.println(lineToBeSent);
                    writeToServer(serverOutput1, lineToBeSent);
                    waitForServer(2000);
                    readServerResponse(serverInput);
                    System.out.println("read done");

                } else if (option == 2) {
                    //getting the name oif the txt file
                    System.out.print("Enter the name of the text file you want to send: ");
                    String filename = input.readLine().trim();
                    String canonicalPath = new File(".").getCanonicalPath();
                    File file = new File(canonicalPath + "\\src\\" + filename);
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
                    StringBuffer sb = new StringBuffer();
                    //constructs a string buffer with no characters  
                    List<String> arrList = new ArrayList<String>();
                    String line;
                    arrList.add(filename + "_CP");
                    while ((line = br.readLine()) != null) {
                        arrList.add(line);
                        //stopAndWait(line);
                    }
                    String[] lines = new String[arrList.size()];
                    lines = arrList.toArray(lines);
                    //implementing
                    boolean ack_received = true;
                    int k = 0;
                    boolean ki = true;
                    while (k < arrList.size() && ki) {
                        while (ack_received && k <= arrList.size() - 1) {
                            lineToBeSent = " [f]" + lines[k] + Integer.toString(k);
                            output.println(lineToBeSent);
                            writeToServer(serverOutput2, lineToBeSent);
                            waitForServer(2000);
                            System.out.println("Send frame: " + k);
                            if (readServerResponseAck(socket, serverInput, k)) {
                                System.out.println("Ack received for: " + k);
                                k += 1;
                            } else {
                                System.out.println("No Ack received for: " + k);
                                ack_received = false;
                            }

                        }
                        while (!ack_received && k <= arrList.size() - 1) {
                            //waitForServer(1000);
                            //System.out.println("[f]" + lines[k] + Integer.toString(k));
                            writeToServer(serverOutput2, " [f]" + lines[k] + Integer.toString(k));
                            System.out.println("Send frame: " + k);
                            if (readServerResponseAck(socket, serverInput, k)) {
                                System.out.println("Ack received for: " + k);
                                k += 1;
                                ack_received = true;
                            } else {
                                System.out.println("No Ack received for: " + k);

                            }

                        }

                    }

                } else if (option == 5) {
                    //getting the name oif the txt file
                    System.out.print("Enter the name of the text file you want to send: ");
                    String filename = input.readLine().trim();
                    String canonicalPath = new File(".").getCanonicalPath();
                    File file = new File(canonicalPath + "\\src\\" + filename);
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
                    StringBuffer sb = new StringBuffer();
                    //constructs a string buffer with no characters  
                    List<String> arrList = new ArrayList<String>();
                    String line;
                    while ((line = br.readLine()) != null) {
                        arrList.add(line);
                        //stopAndWait(line);
                    }
                    String[] lines = new String[arrList.size()];
                    lines = arrList.toArray(lines);
                    //implementing
                    boolean ack_received = true;
                    int k = 0;
                    boolean ki = true;
                    while (k < arrList.size() - 1 && ki) {
                        while (ack_received) {
                            lineToBeSent = " [s1]" + lines[k] + Integer.toString(k);
                            output.println(lineToBeSent);
                            //waitForServer(1000);
                            //System.out.println("[f]" + lines[k] + Integer.toString(k));
                            //writeToServer(serverOutput, "[f]" + lines[k] + Integer.toString(k));
                            writeToServer(serverOutput2, lineToBeSent);
                            waitForServer(2000);
                            System.out.println("Send frame: " + k);
                            if (readServerResponseAck(socket, serverInput, k)) {
                                System.out.println("Ack received for: " + k);
                                k += 1;
                            } else {
                                System.out.println("No Ack received for: " + k);
                                ack_received = false;
                            }

                        }
                        while (!ack_received) {
                            //waitForServer(1000);
                            //System.out.println("[f]" + lines[k] + Integer.toString(k));
                            writeToServer(serverOutput2, " [s1]" + lines[k] + Integer.toString(k));
                            System.out.println("Send frame: " + k);
                            if (readServerResponseAck(socket, serverInput, k)) {
                                System.out.println("Ack received for: " + k);
                                k += 1;
                                ack_received = true;
                            } else {
                                System.out.println("No Ack received for: " + k);

                            }

                        }

                    }
                } else if (option == 6) {
                    //getting the name oif the txt file
                    System.out.print("Enter the name of the text file you want to send: ");
                    String filename = input.readLine().trim();
                    String canonicalPath = new File(".").getCanonicalPath();
                    File file = new File(canonicalPath + "\\src\\" + filename);
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
                    StringBuffer sb = new StringBuffer();
                    //constructs a string buffer with no characters  
                    List<String> arrList = new ArrayList<String>();
                    String line;
                    while ((line = br.readLine()) != null) {
                        arrList.add(line);
                        //stopAndWait(line);
                    }
                    String[] lines = new String[arrList.size()];
                    lines = arrList.toArray(lines);
                    //implementing
                    boolean ack_received = true;
                    int k = 0;
                    boolean ki = true;
                    while (k < arrList.size() - 1 && ki) {
                        while (ack_received) {
                            lineToBeSent = " [f]" + lines[k] + Integer.toString(k);
                            output.println(lineToBeSent);
                            //waitForServer(1000);
                            //System.out.println("[f]" + lines[k] + Integer.toString(k));
                            //writeToServer(serverOutput, "[f]" + lines[k] + Integer.toString(k));
                            writeToServer(serverOutput2, lineToBeSent);
                            waitForServer(2000);
                            System.out.println("Send frame: " + k);
                            if (readServerResponseAckSim(serverInput, k)) {
                                System.out.println("Ack received for: " + k);
                                k += 1;
                            } else {
                                System.out.println("No Ack received for: " + k);
                                ack_received = false;
                            }

                        }
                        while (!ack_received) {
                            //waitForServer(1000);
                            //System.out.println("[f]" + lines[k] + Integer.toString(k));
                            writeToServer(serverOutput2, " [f]" + lines[k] + Integer.toString(k));
                            System.out.println("Send frame: " + k);
                            if (readServerResponseAckSim(serverInput, k)) {
                                System.out.println("Ack received for: " + k);
                                k += 1;
                                ack_received = true;
                            } else {
                                System.out.println("No Ack received for: " + k);

                            }

                        }

                    }
                } else if (option == 3) {
                    lineToBeSent = " [d]";
                    output.println(lineToBeSent);
                    writeToServer(serverOutput2, lineToBeSent);
                    waitForServer(2000);
                    readServerResponse(serverInput);
                    System.out.println("Done!");

                } else if (option == 4) {
                    lineToBeSent = " [sh]";
                    output.println(lineToBeSent);
                    writeToServer(serverOutput2, lineToBeSent);
                    waitForServer(2000);
                    //readServerResponse(serverInput);
                    System.out.println("Server closed!");
                    break;
                }

                lineToBeSent = input.readLine();

                // arr�t si ligne= "."
                if (lineToBeSent.equals(".")) {
                    break;
                }
                output.println(lineToBeSent);
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            System.out.println("fermeture de connexion avec le serveur "
                    + socket.getInetAddress()
                    + ":" + socket.getPort());
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static void printMenu() {
        System.out.println("\n1- Test the connexion to the server\n"
                + "2-Transfer a file to a Server\n"
                //+ "2.1-Simulate: 2nd frame is lost on Server side\n"
                //+ "2.2-Simulate: 5th frame is not confirmed on Client side\n"
                + "3-List a content of the directory where the file was transferred\n"
                + "4- Quit\n");
    }

    private static void readServerResponse(DataInputStream serverInput) {
        try {
            String message = serverInput.readUTF();

            if (message.startsWith("[p]")) {
                System.out.println("----WELCOME----");
            }
        } catch (IOException ex) {
            Logger.getLogger(tcpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void readServerResponseOpt3(DataInputStream serverInput) {
        try {
            waitForServer(1000);
            String message = serverInput.readUTF();
            System.out.println(message);

        } catch (IOException ex) {
            Logger.getLogger(tcpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean readServerResponseAck(Socket socket, DataInputStream serverInput, int k) throws IOException, ClassNotFoundException {
         try {
            waitForServer(1000);
            String messageStr = serverInput.readUTF().trim();
            while (messageStr.length() == 1) {
                int ack = Integer.parseInt(messageStr);
                System.out.println("Received Ack: " + ack);
                if (ack == k) {
                    return true;
                } else {
                    return false;
                }
            } 
                //System.out.println("No Ack received for: " + k);
                return false;
            
        } catch (IOException ex) {
            Logger.getLogger(tcpClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    private static boolean readServerResponseAckSim(DataInputStream serverInput, int k) throws IOException {
        try {
            waitForServer(1000);
            String messageStr = serverInput.readUTF().trim();
            if (messageStr.length() > 0 && k != 4) {
                int ack = Integer.parseInt(messageStr);
                System.out.println("Received Ack: " + ack);
                if (ack == k) {
                    return true;
                } else {
                    return false;
                }
            } else {
                System.out.println("No Ack received for: " + k);
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(tcpClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    private static void waitForServer(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException ex) {
            Logger.getLogger(tcpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void writeToServer(DataOutputStream serverInput, String message) throws IOException {
        System.out.println(message);
        serverInput.writeUTF(message);
    }

}
