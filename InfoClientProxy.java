import MessageMarshaller.*;
import Commons.Address;
import Registry.Entry;
import RequestReply.*;

class InfoClientProxy implements Info,ClientProxy {
	private Requestor req = new Requestor("InfoClientProxy");
	private Marshaller m = new Marshaller();
	private int portNumber;

	public InfoClientProxy(int portNumber) {
		this.portNumber = portNumber;
	}

	public int get_temp(java.lang.String $param_0) {
		String msgData = $param_0+":get_temp";
		//scrie in mesaj toti parametri
		Message msg = new Message("InfoClientProxy",msgData);
		byte[] bytes = m.marshal(msg);
		Address dest = new Entry("127.0.0.1",portNumber);
		//asteapta rezutatul
		bytes = req.deliver_and_wait_feedback(dest,bytes);
		//despacheteaza rezultatul
		Message answer = m.unmarshal(bytes);
		//rezultatul este convertiti la tipul care este returnat de metoda curenta
		int $result = Integer.parseInt(answer.data);
		return $result;
	}

	public java.lang.String get_road_info(int $param_0) {
		String msgData = String.valueOf($param_0)+":get_road_info";
		//scrie in mesaj toti parametri
		Message msg = new Message("InfoClientProxy",msgData);
		byte[] bytes = m.marshal(msg);
		Address dest = new Entry("127.0.0.1",portNumber);
		//asteapta rezutatul
		bytes = req.deliver_and_wait_feedback(dest,bytes);
		//despacheteaza rezultatul
		Message answer = m.unmarshal(bytes);
		//rezultatul este convertiti la tipul care este returnat de metoda curenta
		java.lang.String $result = answer.data;
		return $result;
	}

}
