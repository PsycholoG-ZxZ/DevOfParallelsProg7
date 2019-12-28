import jdk.nashorn.internal.ir.WhileNode;
import org.zeromq.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CacheStorage {
    public static void main(String[] args){
        //key-value
        Map<Integer,String> cache = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        int l = scanner.nextInt();
        int r = scanner.nextInt();
        for (int i = l; i <= r; i++){
            cache.put(i, "!");
        }
        System.out.println("scanner accept data");
        ZContext context = new ZContext();
        ZMQ.Socket socket = null;
        try {
            System.out.println("Connect with Cache");
            socket = context.createSocket(SocketType.DEALER);
            socket.connect("tcp://localhost:6665");

            ZMQ.Poller poller = context.createPoller(1);
            poller.register(socket, ZMQ.Poller.POLLIN);

            long timeCut = System.currentTimeMillis();

            while (true){
                poller.poll(1);
                if (System.currentTimeMillis() - timeCut > 5000){
                    ZMsg mess = new ZMsg();
                    mess.addLast("Hearthbeat"+ " " + l + " " + r);
                    mess.send(socket);
                }
                if (poller.pollin(0)){
                    ZMsg mess_poller_0 = ZMsg.recvMsg(socket);
                    String[] contentStrings = mess_poller_0.getLast().toString().split(" ");
                    System.out.println(contentStrings[0]);
                    if (contentStrings[0].contains("GET")) {
                        int pos = Integer.parseInt(contentStrings[1]);
                        mess_poller_0.pollLast();
                        mess_poller_0.addLast(cache.get(pos)).send(socket);
                        System.out.println("GET из Cache  - " + contentStrings[0]+ " " +contentStrings[1]);
                    }
                    if(contentStrings[0].contains("PUT")){
                        int pos = Integer.parseInt(contentStrings[1]);
                        String val = contentStrings[2].toString();
                        cache.put(pos,val);
                        mess_poller_0.send(socket);
                        System.out.println("PUT из Cache" + contentStrings[0]+ " " +contentStrings[1]);
                    }
                }
            }
        }catch (ZMQException exception){
            System.out.println("Error on CacheStorage side");
            exception.printStackTrace();
        }
    }
}
