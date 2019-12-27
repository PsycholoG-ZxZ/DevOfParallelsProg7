import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CacheStorage {
    public static void main(String[] args){
        //key-value
        Map<Integer,String> cache = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        for (int i = scanner.nextInt(); i <= scanner.nextInt(); i++){
            cache.put(i, "");
        }
        ZContext context = new ZContext();
        ZMQ.Socket socket = null;
        try {
            System.out.println("Connect with Cache");
            socket = context.createSocket(SocketType.DEALER);
            socket.connect("tcp://localhost:6665");
            
        }
    }
}
