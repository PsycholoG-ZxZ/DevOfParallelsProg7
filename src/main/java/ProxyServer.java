import org.zeromq.*;

import javax.xml.crypto.Data;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProxyServer {

    public static final String GET_CONST = "GET";
    public static final String PUT_CONST = "PUT";

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

            long time = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted()){
                poller.poll();
                if (!frameAndCacheMap.isEmpty() && System.currentTimeMillis() - time > 5000){
                    for (Iterator<Map.Entry<ZFrame, DataCache>> iter = frameAndCacheMap.entrySet().iterator(); iter.hasNext();){
                        Map.Entry<ZFrame, DataCache> entry = iter.next();
                        if (Math.abs(entry.getValue().getTime() - time) > 5000 * 100){
                            System.out.println("Cache deleted" + entry.getKey());
                            iter.remove();
                        }
                    } time = System.currentTimeMillis();
                }
                if (poller.pollin(0)){
                    ZMsg msg = ZMsg.recvMsg(frontend);
                    if (msg == null) break;

                    if (frameAndCacheMap.isEmpty()){
                        System.out.println("EMPTY MAP");
                        ZMsg err = new ZMsg();
                        err.add(msg.getFirst());
                        err.add("");
                        err.add("No Cache");
                        err.send(frontend);
                    }else {
                        String[] msgInStr = msg.getLast().toString().split(" ");
                        if (msgInStr[0].equals(GET_CONST )){
                            for (Map.Entry<ZFrame,DataCache> mapZD : frameAndCacheMap.entrySet()){
                                if ((Integer.parseInt(msgInStr[1]) >= mapZD.getValue().getBegin()) &&
                                        (Integer.parseInt(msgInStr[1]) <= mapZD.getValue().getEnd())){
                                    System.out.println("Go Throw GET in proxy");
                                    ZMsg msgForGet = msg.duplicate();
                                    ZFrame newFrme = mapZD.getKey();
                                    msgForGet.addFirst(newFrme);
                                    msgForGet.send(backend);
                                }else{
                                    if (msgInStr[1].isEmpty()){ ZMsg err = new ZMsg();
                                        err.add("Errror");
                                        err.send(frontend);
                                    }
                                    ZMsg err = new ZMsg();
                                    err.add("Errror on Proxy side");
                                    err.send(frontend);
                                    System.out.println("WRONG INP");
                                }
                            }
                        }
                        if (msgInStr[0].equals(PUT_CONST)){
                            for (Map.Entry<ZFrame,DataCache> mapZD : frameAndCacheMap.entrySet()){
                                if ((Integer.parseInt(msgInStr[1]) >= mapZD.getValue().getBegin()) &&
                                        (Integer.parseInt(msgInStr[1]) <= mapZD.getValue().getEnd())){
                                    System.out.println("Go Throw Put in proxy");
                                    ZFrame newFrme = mapZD.getKey();
                                    ZMsg msgForBack = msg.duplicate();
                                    msgForBack.addFirst(newFrme);
                                    msgForBack.send(backend);
                                }
                            }
                        }else {
                            ZMsg err = new ZMsg();
                            err.add("Errror on Proxy side");
                            err.send(frontend);
                        }

                    }
                }
                if (poller.pollin(1)){

                    ZMsg msg = ZMsg.recvMsg(backend);
                    //System.out.println("Get msg from Server");
                    if (msg.toString().contains("Hearthbeat")) {
                        if (!frameAndCacheMap.containsKey(msg.getFirst())) {
                            System.out.println("Get msg from Cache in Proxy");
                            ZFrame frame = msg.getLast();
                            String[] splitedFrame = frame.toString().split(" ");
                            DataCache data = new DataCache(Integer.parseInt(splitedFrame[1])
                                    , Integer.parseInt(splitedFrame[2])
                                    , System.currentTimeMillis());
                            frameAndCacheMap.put(msg.getFirst(), data);
                        } else {
                            frameAndCacheMap.get(msg.getFirst()).changeTime(System.currentTimeMillis());
                        }
                    }else{
                        System.out.println("No heartbeat ");
                        msg.pop();
                        msg.send(frontend);
                    }
                }
            }
        } catch (ZMQException exception){
            System.out.println("Error on Proxy side");
            exception.printStackTrace();
        }

    }

}

