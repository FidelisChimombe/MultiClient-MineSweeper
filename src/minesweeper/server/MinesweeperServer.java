package minesweeper.server;
/*
 * Thread safety argument
 * 
 * I used implicit locks through synchronized keyword to control access to shared resource
 * Any method that reads or writes a shared resource has an implicit lock that is provided synchronized
 * 
 */

import java.net.*;

import minesweeper.Board;
import minesweeper.Square;

import java.io.*;

public class MinesweeperServer {

	private final static int PORT = 4450;
	private ServerSocket serverSocket;
	private int numberOfConnections;
	private static Board board;
	private BufferedReader in;
	private PrintWriter out;
	
    /**
     * Make a MinesweeperServer that listens for connections on port.
     * @param port port number, requires 0 <= port <= 65535.
     */
    public MinesweeperServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.numberOfConnections=0;
        board = new Board(8);
        
    }
    
    /*increases the count of players in the game when a player joins the game*/
    public synchronized void addPlayer(){
    	this.numberOfConnections++;
    }
    /*reduces the count of players in the game when a player digs a bomb or decides to voluntarily leave*/
    public synchronized void removePlayer(){
    	this.numberOfConnections--;
    }
    
    public synchronized int getNumberOfConnections(){
    	return this.numberOfConnections;
    }
    
    /**
     * Run the server, listening for client connections and handling them.  
     * Never returns unless an exception is thrown.
     * @throws IOException if the main server socket is broken
     * (IOExceptions from individual clients do *not* terminate serve()).
     */
    public void serve() throws IOException {
    	System.out.println("Listening for client connections ...");

        while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();
            
            //create a Thread for each client that joins
            
            Thread thread = new Thread(new Runnable(){

				@Override
				public void run() {
		            try {
		                handleConnection(socket);
		            } catch (IOException e) {
		                e.printStackTrace(); // but don't terminate serve()
		            } finally {
		                try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
					
				}
            	
            });
            thread.start();
           
        }
    }
    
    /**
     * Handle a single client connection.  Returns when client disconnects.
     * @param socket  socket where client is connected
     * @throws IOException if connection has an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        this.addPlayer();
		out.println("Welcome to Minesweeper." + this.getNumberOfConnections()+" people are playing including you. Type 'help' for help.");
        try {
        	
        	for (String line = in.readLine(); line != null; line = in.readLine()) {
        		String output = handleRequest(line);
        		if(output != null) {
        			out.println(output);
        			if(output=="BOOM!" || output=="bye"){
        				this.removePlayer();
        				/*close the circuit after the player decides to leave of digs a bomb*/
        				socket.close();
        			}

        		}
        	}
        	
        }catch(IOException e){
        	
        } finally {  
        	/*
        	 * Buffered reader and PrintWriter streams have to be closed outside the loop
        	 * This is because the first cycle of the loop executes correctly and runs in.close(),out.close()
        	 * Then the second cycle the call inputLine = in.readLine() 
        	 * fails because the stream is closed and then the exception is thrown.
        	 */
        	out.close();
        	in.close();
        	
        	
        }
    }

	/**
	 * handler for client input
	 * 
	 * make requested mutations on game state if applicable, then return appropriate message to the user
	 * 
	 * @param input
	 * @return
	 */
	private static String handleRequest(String input) {

		String regex = "(look)|(dig \\d+ \\d+)|(flag \\d+ \\d+)|(deflag \\d+ \\d+)|(help)|(bye)";
		if(!input.matches(regex)) {
			//invalid input
			return null;
		}
		String[] tokens = input.split(" ");
		if(tokens[0].equals("look")) {
			// 'look' request
			return board.look();
		
		} else if(tokens[0].equals("help")) {
			// 'help' request
			return board.help_request();
		} else if(tokens[0].equals("bye")) {
			// 'bye' request
			return "bye";
			
		} else {
			int x = Integer.parseInt(tokens[1]);
			int y = Integer.parseInt(tokens[2]);
			if(tokens[0].equals("dig")) {
				// 'dig x y' request
				Square sq=board.getBoad()[x][y];
				if(sq.isBomb()){
					
					board.dig(x, y);
					return "BOOM!";
				}else{
					if(board.getNeighborsWithBomb(x, y)==0){
						board.recursiveDig(x, y);
						return board.look();
					}else{
						board.dig(x, y);
						return board.look();
					}
				}
				
				
			} else if(tokens[0].equals("flag")) {
				// 'flag x y' request
				return board.flag(x, y);
			} else if(tokens[0].equals("deflag")) {
				// 'deflag x y' request
				return board.deflag(x, y);
			}
		}
		//should never get here
		return null;
	}
	
    
    /**
     * Start a MinesweeperServer running on the default port.
     */
    public static void main(String[] args) {
        try {
            MinesweeperServer server = new MinesweeperServer(PORT);
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}