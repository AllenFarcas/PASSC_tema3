import MessageMarshaller.*;
import Commons.Address;
import Registry.Entry;
import RequestReply.*;

class StockMarketClientProxy implements StockMarket,ClientProxy {
	private Requestor req = new Requestor("StockMarketClientProxy");
	private Marshaller m = new Marshaller();
	private int portNumber;

	public StockMarketClientProxy(int portNumber) {
		this.portNumber = portNumber;
	}

	public float get_price(java.lang.String $param_0, int $param_1, float $param_2, boolean $param_3) {
		String msgData = $param_0+ " " + String.valueOf($param_1)+ " " + String.valueOf($param_2)+ " " + String.valueOf($param_3)+":get_price";
		//scrie in mesaj toti parametri
		Message msg = new Message("StockMarketClientProxy",msgData);
		byte[] bytes = m.marshal(msg);
		Address dest = new Entry("127.0.0.1",portNumber);
		//asteapta rezutatul
		bytes = req.deliver_and_wait_feedback(dest,bytes);
		//despacheteaza rezultatul
		Message answer = m.unmarshal(bytes);
		//rezultatul este convertiti la tipul care este returnat de metoda curenta
		float $result = Float.parseFloat(answer.data);
		return $result;
	}

}
