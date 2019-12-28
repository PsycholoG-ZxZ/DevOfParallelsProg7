import org.zeromq.*;

import java.net.Socket;

public class ProxyServer {

    public static void main(String[] args){
        ZContext context = new ZContext();
        try {

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
                    
                }
            }
        }

    }

}

