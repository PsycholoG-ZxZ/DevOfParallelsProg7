import org.zeromq.*;

import java.net.Socket;

public class ProxyServer {

    public static void main(String[] args){

        try {
            ZContext context = new ZContext();
            Socket frontend = context.createSocket(SocketType.ROUTER);
            Socket backend = context.createSocket(SocketType.ROUTER);
        }

    }

}

