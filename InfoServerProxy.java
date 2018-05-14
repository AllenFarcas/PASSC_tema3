import MessageMarshaller.*;
import Commons.Address;
import Registry.Entry;
import RequestReply.*;

class InfoMessageServer extends MessageServer {
	private InfoImpl info;
	public InfoMessageServer() {}
	public InfoMessageServer(InfoImpl info){
		this.info = info;
	}
	public Message get_answer(Message msg) throws Exception {
		if(msg.sender.equals("InfoClientProxy")) {
			System.out.println("InfoServerProxy analyzing data");
			String [] arrOfStr = msg.data.split(":", 5);
			String parameters = arrOfStr[0];
			String opcode = arrOfStr[1];
			String [] arrParam = parameters.split(" ", 5);
			switch (opcode){

				case "get_temp": {
					System.out.println("InfoServerProxy: get_temp method is executing...");
					int $result;
					java.lang.String $param_0 = arrParam[0];
					$result = info.get_temp( $param_0);
					String dataResult = String.valueOf($result);
					System.out.println("InfoServerProxy: result is " + dataResult+"\n\n");
					Message answer = new Message("InfoServerProxy", dataResult);
					return answer;
				}

				case "get_road_info": {
					System.out.println("InfoServerProxy: get_road_info method is executing...");
					java.lang.String $result;
					int $param_0 = Integer.valueOf(arrParam[0]);
					$result = info.get_road_info( $param_0);
					System.out.println("InfoServerProxy: The result is " + $result+"\n\n");
					Message answer = new Message("InfoServerProxy", $result);
					return answer;
				}
			}
		} else {
			System.out.println("InfoServerProxy Error: Somebody else is trying to communicate with me! ( "+msg.sender+" )");
			throw new Exception("Error: Somebody else is trying to communicate with me! ( "+msg.sender+" )");
		}
		Message answer = new Message("InfoServer","Error");
		return answer;
	}
}


class InfoServerProxy implements ServerProxy {
	private int portNumber;

	public InfoServerProxy(int portNumber) {
		this.portNumber = portNumber;
	}

	public void dispatch() {
		Address myAddr = new Entry("127.0.0.1",portNumber);
		Replyer rep = new Replyer("InfoServerProxy", myAddr);
		InfoImpl info = new InfoImpl();
		ByteStreamTransformer transformer = new ServerTransformer(new InfoMessageServer(info));
		while(true) {
			try {
				Message msg = new Message("Client","MyInfoImpl:InfoImpl!Check");
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
