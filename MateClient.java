import RequestReply.*;
import MessageMarshaller.*;
import Registry.*;
import Commons.Address;

public class MateClient {
    public static void main(String args[]) {
        try {
            Mate myMate = (Mate) NamingService.getObjectReference("MyMateImpl:MateImpl");
            double retValD = myMate.do_sqr(4);
            System.out.println("Returned value is: "+retValD);
            float retValF = myMate.do_add(2,10);
            System.out.println("Returned value is: "+retValF);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}

class MateClientProxy implements Mate, ClientProxy {
    Requestor req = new Requestor("MateClientProxy");
    Marshaller m = new Marshaller();
    int portNumber;

    public MateClientProxy(int portNumber) {
        this.portNumber = portNumber;
    }

    //public int get_temp(String city);
    public double do_sqr(float $param_float_1) {
        String msgData = String.valueOf($param_float_1)+":0";
        //scrie in mesaj toti parametri
        Message msg = new Message("MateClientProxy",msgData);
        byte[] bytes = m.marshal(msg);
        Address dest = new Entry("127.0.0.1",portNumber);
        //asteapta rezutatul
        bytes = req.deliver_and_wait_feedback(dest,bytes);
        //si apoi rezultatul il despacheteaza
        Message answer = m.unmarshal(bytes);
        //rezultatul este convertiti la tipul care este returnat de metoda curenta
        float $result = Float.parseFloat(answer.data);
        return $result;
    }

    //public String get_road_info(int road_ID);
    public float do_add(float $param_float_1,float $param_float_2) {
        String msgData = String.valueOf($param_float_1)+" "+String.valueOf($param_float_2)+":1";
        //scrie in mesaj toti parametri
        Message msg = new Message("MateClientProxy",msgData);
        byte[] bytes = m.marshal(msg);
        Address dest = new Entry("127.0.0.1",portNumber);
        //asteapta rezutatul
        bytes = req.deliver_and_wait_feedback(dest,bytes);
        //si apoi rezultatul il despacheteaza
        Message answer = m.unmarshal(bytes);
        //rezultatul este convertiti la tipul care este returnat de metoda curenta
        float $result = Float.parseFloat(answer.data);
        return $result;
    }
}

