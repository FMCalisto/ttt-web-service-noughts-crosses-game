package ttt.cli;

import java.util.*;
import javax.xml.ws.*;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import example.ws.uddi.UDDINaming;

import ttt.*; // classes generated from WSDL

public class Game {

    public static void main(String[] args) throws Exception {
        // Check arguments
		
		
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name%n", Game.class.getName());
            return;
        }

        String uddiURL = args[0];
        String name = args[1];

        System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);

        System.out.printf("Looking for '%s'%n", name);
        String endpointAddress = uddiNaming.lookup(name);

        if (endpointAddress == null) {
            System.out.println("Not found!");
            return;
        } else {
            System.out.printf("Found %s%n", endpointAddress);
        }

        System.out.println("Creating stub ...");
        //Creates an instance of the class that handles the access to the remote services (JAX-WS)
        service = new TTTImplService();
        //Obtains the service interface with the remote methods defined (see TTT.java)
        TTT port = service.getTTTImplPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

        // Start game
        Game g = new Game(port);
        g.playGame();
        g.congratulate();
    }

    private static TTTImplService service;
    private static TTT ttt;
	int player;
    private Scanner keyboardSc;
    private int winner = 0;
	
	
	

    public Game(TTT port) {
	this.ttt = port;
	keyboardSc = new Scanner(System.in);
		
	System.out.printf("Digite o seu user: \n"); 
	keyboardSc = new Scanner(System.in);
	player = keyboardSc.nextInt();
    }

    public int readPlay() {
        int play;
        do {
            System.out.printf("\nPlayer %d, please enter the number of the square "
                            + "where you want to place your %c (or 0 to refresh the board): %n",
                            player, (player == 1) ? 'X' : 'O');
            play = keyboardSc.nextInt();
        } while (play > 10 || play < 0);
        return play;
    }

    public void playGame() {
        int play;
        boolean playAccepted;

        do {
            //player = ++player % 2;
            do {
                System.out.println(ttt.currentBoard());
                play = readPlay();
                if (play != 0) {
					
					playAccepted = ttt.play( --play / 3, play % 3, player);
					
                    if (!playAccepted)
                        System.out.println("Invalid play! Try again.");
                } else {
					if(winner = ttt.checkWinner() != -1)
						break;
					
                    playAccepted = false;
                }
            } while (!playAccepted);
            winner = ttt.checkWinner();
        } while (winner == -1);
    }

    public void congratulate() {
        if (winner == 2)
            System.out.printf("\nHow boring, it is a draw\n");
        else
            System.out.printf(
                    "\nCongratulations, player %d, YOU ARE THE WINNER!\n",
                    winner);
    }

}
