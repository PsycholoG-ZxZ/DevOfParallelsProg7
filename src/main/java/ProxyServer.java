import org.zeromq.*;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ProxyServer {

    public static void main(String[] args){
        ZContext context = new ZContext();

        try {
            Map<ZFrame, DataCache> frameAndCacheMap = new HashMap<>();

            ZMQ.Socket frontend = context.createSocket(SocketType.ROUTER);
            ZMQ.Socket backend = context.createSocket(SocketType.ROUTER);

            frontend.bind("tcp://localhost:5555");
            backend.bind("tcp://localhost:6665");
            ZMQ.Poller poller =context.createPoller(2);

            poller.register(frontend, ZMQ.Poller.POLLIN);
            poller.register(backend, ZMQ.Poller.POLLIN);
            while (!Thread.currentThread().isInterrupted()){
                poller.poll();
                if (poller.pollin(0)){
                    ZMsg msg = ZMsg.recvMsg(frontend);
                    String[] msgInStr =     msg.getLast().toString().split(" ");
                }
                if (poller.pollin(1)){

                    ZMsg msg = ZMsg.recvMsg(backend);
                    if (!frameAndCacheMap.containsKey(msg.getFirst())) {
                        ZFrame frame = msg.getLast();
                        String[] splitedFrame = frame.toString().split(" ");
                        DataCache data = new DataCache(Integer.parseInt(splitedFrame[1])
                                , Integer.parseInt(splitedFrame[2])
                                , System.currentTimeMillis());
                        frameAndCacheMap.put(msg.getFirst(), data);
                    }else{
                        frameAndCacheMap.get(msg.getFirst()).changeTime(System.currentTimeMillis());
                    }
                }
            }
        } catch (ZMQException exception){
            System.out.println("Error on Proxy side");
        }

    }

}

