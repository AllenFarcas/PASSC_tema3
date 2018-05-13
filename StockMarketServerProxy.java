import MessageMarshaller.*;
import Commons.Address;
import Registry.Entry;
import RequestReply.*;

class StockMarketMessageServer extends MessageServer {
	private StockMarketImpl stockmarket;
	public StockMarketMessageServer() {}
	public StockMarketMessageServer(StockMarketImpl stockmarket){
		this.stockmarket = stockmarket;
	}
	public Message get_answer(Message msg) throws Exception {
		if(msg.sender.equals("StockMarketClientProxy")) {
			System.out.println("StockMarketServerProxy analyzing data");
			String [] arrOfStr = msg.data.split(":", 5);
			String parameters = arrOfStr[0];
			String opcode = arrOfStr[1];
			String [] arrParam = parameters.split(" ", 5);
			switch (opcode){

				case "get_price": {
					System.out.println("StockMarketServerProxy: get_price method is executing...");
					float $result;
					java.lang.String $param_0 = arrParam[0];
					int $param_1 = Integer.valueOf(arrParam[1]);
					float $param_2 = Float.valueOf(arrParam[2]);
					boolean $param_3 = Boolean.valueOf(arrParam[3]);
					$result = stockmarket.get_price( $param_0, $param_1, $param_2, $param_3);
					String dataResult = String.valueOf($result);
					System.out.println("StockMarketServerProxy: result is " + dataResult+"\n\n");
					Message answer = new Message("StockMarketServerProxy", dataResult);
					return answer;
				}
			}
		} else {
			System.out.println("StockMarketServerProxy Error: Somebody else is trying to communicate with me! ( "+msg.sender+" )");
			throw new Exception("Error: Somebody else is trying to communicate with me! ( "+msg.sender+" )");
		}
		Message answer = new Message("StockMarketServer","Error");
		return answer;
	}
}


class StockMarketServerProxy implements ServerProxy {
	private int portNumber;

	public StockMarketServerProxy(int portNumber) {
		this.portNumber = portNumber;
	}

	public void dispatch() {
		Address myAddr = new Entry("127.0.0.1",portNumber);
		Replyer rep = new Replyer("StockMarketServerProxy", myAddr);
		StockMarketImpl stockmarket = new StockMarketImpl();
		ByteStreamTransformer transformer = new ServerTransformer(new StockMarketMessageServer(stockmarket));
		while(true) {
			try {
				Message msg = new Message("Client","NASDAQ:StockMarketImpl!Check");
				Requestor req = new Requestor("Client");
				Marshaller m = new Marshaller();
				byte[] bytes = m.marshal(msg);
				Address dest = new Entry("127.0.0.1",1110);
				bytes = req.deliver_and_wait_feedback(dest, bytes);
				Message answer = m.unmarshal(bytes);
				if(Boolean.valueOf(answer.data)) {
                    rep.receive_transform_and_send_feedback(transformer);
				} else {
					System.out.println("Serverul se opreste");
					break;
				}
			} catch (Exception e) {
				//System.out.println(e.getMessage());
			}
		}
	}
}
