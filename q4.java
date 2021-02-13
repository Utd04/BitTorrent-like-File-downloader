import java.io.*;
import java.util.*; 
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

class assemble{
    int size;
    char[] al;
           
    assemble(int size){
        this.size= size;
        al = new char[this.size];
    }
    synchronized void modify(int index, char c){
        al[index]=c;
    }
    void printoutput(){
        for(int i=0;i<size;i++){
            System.out.print(al[i]);
        }
        return;
    }
}

class assignRange{
    int size;
    int[] hasAssigned;
    assignRange(int size){
        this.size=size;
        hasAssigned = new int[size];
        for(int i =0;i<size;i++){
            hasAssigned[i]=0;
        }
    }

    synchronized int[] getrange(){
        int jump = 10000;
        for(int i=0;i<size;i=i+jump){
            if(hasAssigned[i]==0){
                hasAssigned[i]=1;
                if(size - i <= jump){
                    int[] ans = {i,size-1}; 
                    return ans;
                }
                int[] ans = {i,i+jump-1}; 
                return ans;
            }
        }
        int[] ans = {-1}; 
                return ans;
    }


}


class Connection extends Thread{
    String host;
    int port;
    assignRange r;
    String data ="";
    ArrayList<String> arr;
    Connection (String host, int port, assignRange r, ArrayList<String> arr){
        this.host = host;
        this.port = port;
        this.r = r;
        this.arr = arr; 
    }
    public void run(){
        try
        {      
            ArrayList<Integer> start = new ArrayList<>(); 
            ArrayList<Integer> end = new ArrayList<>(); 
            
            Socket clientSocket = new Socket(this.host, this.port);
            // clientSocket.setSoTimeout(10000);
            DataOutputStream outToServer= new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            
            // make the sentences to be sent and send them...
            while(true){
                int[] p = r.getrange();
                if(p[0]==-1)
                break;
                start.add(p[0]);
                end.add(p[1]+1);
                String current ="";
                current= current+ "GET /big.txt HTTP/1.1\r\nHost: "+this.host+"\r\nConnection: keep-alive\r\nRange: bytes=";
                current = current + p[0] + "-" + p[1]+"\r\n\r\n";
                outToServer.writeBytes(current);
            }
            

            //  receive the page...
            int size = start.size();
            if(size>0){
            int i=0;
            int count = start.get(i);
            int flag = 0;
            int character;
            
            while ((character = inFromServer.read()) != -1) {
                if(flag!=0){
                  data=   data+(char)character;
                count++;
                if(count == end.get(i)){
                    int index = start.get(i)/10000;
                    
                    arr.set(index, data);
                    data = "";
                    i++;
                    if(i<size)
                    count = start.get(i);
                    // else
                    // break;
                    flag =0;
                }
                }
                else{
                    if(character == 13){
                        character = inFromServer.read();
                        if( character == 10 ){
                            character = inFromServer.read();
                            if( character == 13 ){
                                character = inFromServer.read();
                                if( character == 10 ){
                                    flag =1;
                                }
                            }
                        }
                    }
                }
            }

                if(i<size && start.get(i)==6480000){
                    arr.set(648, data);
                    data="";
                }
            }
            clientSocket.close();
        } 
        catch (Exception e) 
        {   
            while(true){
                try{
                ArrayList<Integer> start = new ArrayList<>(); 
                ArrayList<Integer> end = new ArrayList<>(); 
                
                Socket clientSocket = new Socket(this.host, this.port);
                clientSocket.setSoTimeout(10000);
                DataOutputStream outToServer= new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                
                // make the sentences to be sent and send them...
                while(true){
                    int[] p = r.getrange();
                    if(p[0]==-1)
                    break;
                    start.add(p[0]);
                    end.add(p[1]+1);
                    String current ="";
                    current= current+ "GET /big.txt HTTP/1.1\r\nHost: "+this.host+"\r\nConnection: keep-alive\r\nRange: bytes=";
                    current = current + p[0] + "-" + p[1]+"\r\n\r\n";
                    outToServer.writeBytes(current);
                }
                
    
                //  receive the page...
                int size = start.size();
                if(size>0){
                int i=0;
                int count = start.get(i);
                int flag = 0;
                int character;
                
                while ((character = inFromServer.read()) != -1) {
                    if(flag!=0){
                      data=   data+(char)character;
                    count++;
                    if(count == end.get(i)){
                        int index = start.get(i)/10000;
                        
                        arr.set(index, data);
                        data = "";
                        i++;
                        if(i<size)
                        count = start.get(i);
                        // else
                        // break;
                        flag =0;
                    }
                    }
                    else{
                        if(character == 13){
                            character = inFromServer.read();
                            if( character == 10 ){
                                character = inFromServer.read();
                                if( character == 13 ){
                                    character = inFromServer.read();
                                    if( character == 10 ){
                                        flag =1;
                                    }
                                }
                            }
                        }
                    }
                }
    
                    if(i<size && start.get(i)==6480000){
                        arr.set(648, data);
                        data="";
                    }
                }
                clientSocket.close();
                break;}
                catch(Exception ex){
                    
                }
            }
        } 
    }



}


public class q4 {
    public static void main(String[] args) throws Exception{

        ArrayList<String> arr= new ArrayList<String>(650);
        for(int i=0;i<650;i++){
            arr.add("");
        }
        int numBytes = 6488666;
        // int numBytes  = 1000;
        assignRange r = new assignRange(numBytes+1);
        // assemble out = new assemble(numBytes+1);

        int num1 = 20;
        int num2 = 0;
        int total = num1 + num2;


        Connection[] sockets = new Connection[total];
        for(int i=0;i<num1;i++){
            sockets[i] = new Connection("vayu.iitd.ac.in",80,r,arr);
            sockets[i].start();
        }
        // for(int i=num1;i<total;i++){
        //     sockets[i] = new Connection("norvig.com",80,r,arr);
        //     sockets[i].start();
        // }
        for(int i=0;i<total;i++){
            sockets[i].join();
        }

        int s = arr.size();
        for(int i=0;i<s;i++){
            System.out.print(arr.get(i));
        }

    }
}
