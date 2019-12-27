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
            socket.connect("tcp://localhost:5555");
            for (int i = 0; i < 10; i++){
                socket.send("request" + i, 0);
                String reply = socket.recvStr();
                System.out.println("reply" + i +" result = "+ reply);
            }
        }finally {
            context.destroySocket(socket);
            context.destroy();
        }

    }
}
