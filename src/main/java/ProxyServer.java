import org.zeromq.*;

import java.net.Socket;

public class ProxyServer {

    public static void main(String[] args){
        ZContext context = new ZContext();
        try {

            ZMQ.Socket frontend = context.createSocket(SocketType.ROUTER);
            ZMQ.Socket backend = context.createSocket(SocketType.ROUTER);
            
        }

    }

}

