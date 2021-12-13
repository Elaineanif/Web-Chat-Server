/* Notes: 
 * This code is modified from the original to work with 
 * the CS 352 chat client:
 *
 * 1. added args to allow for a command line to the port 
 * 2. Added 200 OK code to the sendResponse near line 77
 * 3. Changed default file name in getFilePath method to ./ from www 
 */ 

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Read the full article https://dev.to/mateuszjarzyna/build-your-own-http-server-in-java-in-less-than-one-hour-only-get-method-2k02
public class Server {

    public static void main( String[] args ) throws Exception {

	if (args.length != 1) 
        {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }
        //create server socket given port number
        int portNumber = Integer.parseInt(args[0]);
	
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    handleClient(client);
                }
            }
        }
    }

    private static void handleClient(Socket client) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while (!(line = br.readLine()).isBlank()) {
            requestBuilder.append(line + "\r\n");
        }

        String request = requestBuilder.toString();

        System.out.printf("The request is: %s \n", request);

	String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String version = requestLine[2];
        String host = requestsLines[1].split(" ")[1];
        String usernamemix = null;

        if(method.equals("POST")){
            while(br.ready()){
                char c = (char)br.read();
                usernamemix = usernamemix + c;
            }
        String[] nameline = usernamemix.split("\\&");
        String mixusername = nameline[0];
        String[] musername = mixusername.split("\\=");
        String username = musername[1];
        String mixpassword = nameline[1];
        String[] mpassword = mixpassword.split("\\=");
        String password = mpassword[1];
        boolean rightlogin = rightLogin(username, password);
        if (rightlogin == true){
            
        } else{
            
        }


        }


	// build the reponse here 
        List<String> headers = new ArrayList<>();
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            headers.add(header);
        }

        String accessLog = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s",
                client.toString(), method, path, version, host, headers.toString());
        System.out.println(accessLog);


        Path filePath = getFilePath(path);
        if (Files.exists(filePath)) {
            // file exist
            String contentType = guessContentType(filePath);
            sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath));
        } else {
            // 404
            byte[] notFoundContent = "<h1>Not found :(</h1>".getBytes();
            sendResponse(client, "404 Not Found", "text/html", notFoundContent);
        }

    }

    private static void sendResponse(Socket client, String status, String contentType, byte[] content) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 200 OK" + status + "\r\n").getBytes());
        clientOutput.write(("ContentType: " + contentType + "\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write(content);
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
        client.close();
    }

    private static Path getFilePath(String path) {
        if ("/".equals(path)) {
            path = "/index.html";
        }

        return Paths.get("./", path);
    }

    private static String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }

    private static boolean rightLogin(String username, String password) throws IOException{
    	File f=new File("credentials.txt");
	String readline;
	BufferedReader read=new BufferedReader(new FileReader(f));
	readline=read.readLine();
	while(readline!=null){
		if(readline.equals(username+","+password)){
			return true;
		}
		readline=read.readLine();
	}
	return false;

    }

//    public boolean haveCookie(){
//        if(haveCookie)
//    }

}
