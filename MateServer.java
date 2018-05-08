import RequestReply.*;
import MessageMarshaller.*;
import Registry.*;
import Commons.Address;

import static java.lang.Math.*;


interface Mate {
    public double do_sqr(float a);
    public float do_add(float a, float b);
}

class MateImpl implements Mate {
    @Override
    public double do_sqr(float a) {
        System.out.println("do_sqr method is executing");
        return sqrt(a);
    }

    @Override
    public float do_add(float a, float b) {
        System.out.println("do_add method is executing");
        return a+b;
    }
}

class MateMessageServer extends MessageServer{
    private MateImpl math;

    public MateMessageServer() {}

    public MateMessageServer(MateImpl math) {
        this.math = math;
    }

    public Message get_answer(Message msg) throws Exception{
        if(msg.sender.equals("MateClientProxy")) {
            System.out.println("MateServerProxy analyzing data");
            String [] arrOfStr = msg.data.split(":", 5);
            String parameters = arrOfStr[0];
            String opcode = arrOfStr[1];
            int opnum = Integer.parseInt(opcode);
            String [] arrParam = parameters.split(" ", 5);
            switch (opnum){
                //do_sqr 0
                case 0: {
                    System.out.println("MateServerProxy: do_sqr method");
                    float $param_String_1 = Float.valueOf(arrParam[0]);
                    double $result;
                    $result = math.do_sqr($param_String_1);
                    String dataResult = String.valueOf($result);
                    System.out.println("MateServerProxy: result is " + dataResult);
                    Message answer = new Message("MateServerProxy",dataResult);
                    return answer;
                }
                //do_add 1
                case 1: {
                    System.out.println("MateServerProxy: do_add method");
                    float $param_float_1 = Float.valueOf(arrParam[0]);
                    float $param_float_2 = Float.valueOf(arrParam[1]);
                    float $result;
                    $result = math.do_add($param_float_1,$param_float_2);
                    String dataResult = String.valueOf($result);
                    System.out.println("MateServerProxy: result is " + $result);
                    Message answer = new Message("MateServerProxy",dataResult);
                    return answer;
                }
            }
        } else {
            System.out.println("MateServerProxy Error: Somebody else is trying to communicate with me! ( "+msg.sender+" )");
            throw new Exception("Error: Somebody else is trying to communicate with me! ( "+msg.sender+" )");
        }
        Message answer = new Message("MateServer","Error");

        return answer;
    }
}

public class MateServer {
    public static void main(String args[]) {
        MateImpl mateImpl = new MateImpl();
        System.out.println("MateServer main started");
        try {
            NamingService.registerMethod("MyMateImpl",mateImpl);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}

class MateServerProxy implements ServerProxy{
    private int portNumber;

    public MateServerProxy(int portNumber) {
        this.portNumber = portNumber;
    }

    public void dispatch() {
        Address myAddr = new Entry("127.0.0.1",portNumber);
        Replyer rep = new Replyer("InfoServerProxy", myAddr);
        MateImpl mate = new MateImpl();
        ByteStreamTransformer transformer = new ServerTransformer(new MateMessageServer(mate));
        while(true) {
            try {
                rep.receive_transform_and_send_feedback(transformer);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
