import org.zeromq.*;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        ZContext context = new ZContext();
        ZMQ.Socket socket = null;


        try {
            System.out.println("connect");
            socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:5555");
            //while(!Thread.currentThread().isInterrupted()){ // or
            Scanner scanner = new Scanner(System.in);

            while (true) {
                String mes = scanner.nextLine();
                if (mes.equals("EXIT")) {
                    break;
                }
                if (!mes.contains("GET") && !mes.contains("PUT")) {
                    System.out.println("Incorrect input");
                }else{
                    ZMsg par = new ZMsg();
                    ZMsg req = new ZMsg();
                    par.addString(mes);
                    System.out.println(par.toString());
                    par.send(socket);
                    req = ZMsg.recvMsg(socket);
                    if (req == null){
                        System.out.println("BREAK");
                        break;
                    }
                    System.out.println(req.popString());
                    req.destroy();
                }
            }

        } catch (ZMQException exception) {
            System.out.println("Error on clients side");
            exception.printStackTrace();
        }
    }
}
