import MessageMarshaller.*;
import Commons.Address;
import Registry.Entry;
import RequestReply.*;

class MateClientProxy implements Mate,ClientProxy {
	private Requestor req = new Requestor("MateClientProxy");
	private Marshaller m = new Marshaller();
	private int portNumber;

	public MateClientProxy(int portNumber) {
		this.portNumber = portNumber;
	}

	public double do_sqr(float $param_0) {
		String msgData = String.valueOf($param_0)+":do_sqr";
		//scrie in mesaj toti parametri
		Message msg = new Message("MateClientProxy",msgData);
		byte[] bytes = m.marshal(msg);
		Address dest = new Entry("127.0.0.1",portNumber);
		//asteapta rezutatul
		bytes = req.deliver_and_wait_feedback(dest,bytes);
		//despacheteaza rezultatul
		Message answer = m.unmarshal(bytes);
		//rezultatul este convertiti la tipul care este returnat de metoda curenta
		double $result = Double.parseDouble(answer.data);
		sendStopMessage();
		return $result;
	}

	public float do_add(float $param_0, float $param_1) {
		String msgData = String.valueOf($param_0)+ " " + String.valueOf($param_1)+":do_add";
		//scrie in mesaj toti parametri
		Message msg = new Message("MateClientProxy",msgData);
		byte[] bytes = m.marshal(msg);
		Address dest = new Entry("127.0.0.1",portNumber);
		//asteapta rezutatul
		bytes = req.deliver_and_wait_feedback(dest,bytes);
		//despacheteaza rezultatul
		Message answer = m.unmarshal(bytes);
		//rezultatul este convertiti la tipul care este returnat de metoda curenta
		float $result = Float.parseFloat(answer.data);
		sendStopMessage();
		return $result;
	}

	public int do_diff(int $param_0, int $param_1) {
		String msgData = String.valueOf($param_0)+ " " + String.valueOf($param_1)+":do_diff";
		//scrie in mesaj toti parametri
		Message msg = new Message("MateClientProxy",msgData);
		byte[] bytes = m.marshal(msg);
		Address dest = new Entry("127.0.0.1",portNumber);
		//asteapta rezutatul
		bytes = req.deliver_and_wait_feedback(dest,bytes);
		//despacheteaza rezultatul
		Message answer = m.unmarshal(bytes);
		//rezultatul este convertiti la tipul care este returnat de metoda curenta
		int $result = Integer.parseInt(answer.data);
		sendStopMessage();
		return $result;
	}

	public void sendStopMessage() {
        Message msg = new Message("Client", "MyMateImpl:MateImpl!TurnOff");
        Requestor req = new Requestor("Client");
        Marshaller m = new Marshaller();
        byte[] bytes = m.marshal(msg);
        Address dest = new Entry("127.0.0.1", 1110);
        bytes = req.deliver_and_wait_feedback(dest, bytes);
        Message answer = m.unmarshal(bytes);

        if (Boolean.valueOf(answer.data)) {
            System.out.println("Server still on");
        } else {
            System.out.println("Server is off.");
        }
    }
}
