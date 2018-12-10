package client2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

public class MonClient {

	static int ComEnPort;
    static String compositionEngineHostname;
    static int delai;
    static int NumClient;
    static String message="";
    static int NumberOfParameters;
    
    public static void main(String[] args) throws IOException {
    	
    	
    	//LOAD PROPERTIES
    	Properties prop = new Properties();
    	InputStream input = MonClient.class.getClassLoader().getResourceAsStream("Properties/conf.properties");
    	prop.load(input);

    	
    	//GET VALUES FROM THE PROPERTY FILE
    	NumClient = Integer.parseInt(prop.getProperty("nombreClients"));
    	ComEnPort = Integer.parseInt(prop.getProperty("compositionEnginePort"));
    	compositionEngineHostname = prop.getProperty("compositionEngineHostname");
    	delai = Integer.parseInt(prop.getProperty("delai"));
    	NumberOfParameters = Integer.parseInt(prop.getProperty("NumberOfParameters"));
    	
    	System.out.println("Number of Clients created : "+NumClient);
    	System.out.println("Composition Engine Port Number : " + ComEnPort);
    	System.out.println("Composition Engine Hostname : "+ compositionEngineHostname);
    	System.out.println("Time that the client will sleep before terminating the thread : "+delai);
    	System.out.println("Number of parameters : "+NumberOfParameters);
    	System.out.println("--------------------------------------------------------------------------");
    	
    	
    	//CREATE A TABLE OF CLIENTS
    	Client[] tab=new Client[NumClient];
		
		//INITIATE CLIENTS, EACH CLIENT HAS AN ID AND A RANDOM MESSAGE TO SEND.
		int n1=0;
		int n2=0;
		int i;
		for(i=0;i<tab.length;i++) {
			n1=(int)(Math.random()*NumberOfParameters);
			n2=(int)(Math.random()*NumberOfParameters);
			message = "exist "+"P"+n1+" P"+n2;
			tab[i]=new Client(compositionEngineHostname,ComEnPort,delai,message,i);
		}
		
		//START CLIENTS SEQUENTIALLY
		for(i=0;i<tab.length;i++) {
			tab[i].start();
		}
		
		//Wait the thread to finish and then re-do the same thing
		for(i=0;i<tab.length;i++) {
			do {
				try {
					Thread.sleep(100);	
				}catch (Exception e) {
					System.out.println("Can't Sleep");
				}				
			}while(tab[i].isInterrupted());
		}
    					
	}
}    
    
//================================================CLIENT THREAD============================================
    
class Client extends Thread {


	int compositionEnginePort;
	String compositionEngineHostname;
	int delai;
	BufferedReader in;
	PrintWriter out;
	String message;
	Socket SocClient=null;
	int ID;

	public Client(String compositionEngineHostname, int compositionEnginePort,int delai, String message, int ID) {
		this.ID=ID;
		this.compositionEnginePort=compositionEnginePort;
		this.compositionEngineHostname=compositionEngineHostname;
		this.delai=delai;
		this.message=message;
	}
	
	@Override
	public void run() {
		try {
			SocClient = new Socket(compositionEngineHostname, compositionEnginePort);	
		}catch (Exception e) {
			System.out.println("Can't create the Socket!!");
			return;
		}				
		try {
			out = new PrintWriter(SocClient.getOutputStream());
			in = new BufferedReader(new InputStreamReader(SocClient.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(true) {
			
			//SEND THE MESSAGE
			out.println(message);
			out.flush();
			
			//READ THE RESPONSE
			try {
				String Response = in.readLine();
				System.out.println("Command (ID Client = "+(ID+1)+") : "+ message +" ||| Response : "+Response);
			}catch (Exception e) {
			}			

			
			//When finished, wait some time.
			try {
				sleep(delai);
			}catch (Exception e) {
				System.out.println("Can't Sleep!!");
			}
			

		}
	}
}