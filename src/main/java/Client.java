import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Client {
    public static void main(String[] args){
        ZContext context = new ZContext();
        ZMQ.Socket socket = null;
        try{
            System.out.println("connect");
            socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp")
        }

    }
}
