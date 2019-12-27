import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        ZContext context = new ZContext();
        ZMQ.Socket socket = null;
        Scanner scanner = new Scanner(System.in);

        try{
            System.out.println("connect");
            socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:5555");
            //while(!Thread.currentThread().isInterrupted()){ // or

            while (true){
                String mes = scanner.nextLine();
                if (mes.equals("EXIT")){
                    break;
                }
                if (mes.contains("GET") && mes.contains("PUT")){
                    ZMsg par = new ZMsg();
                    ZMsg req
                    par.addString(mes);
                    par.send(socket);

                }


            }
        }finally {
            context.destroySocket(socket);
            context.destroy();
        }

    }
}
