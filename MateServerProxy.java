import MessageMarshaller.*;
import Commons.Address;
import Registry.Entry;
import RequestReply.*;

class MateMessageServer extends MessageServer {
	private MateImpl mate;
	public MateMessageServer() {}
	public MateMessageServer(MateImpl mate){
		this.mate = mate;
	}
	public Message get_answer(Message msg) throws Exception {
		if(msg.sender.equals("MateClientProxy")) {
			System.out.println("MateServerProxy analyzing data");
			String [] arrOfStr = msg.data.split(":", 5);
			String parameters = arrOfStr[0];
			String opcode = arrOfStr[1];
			String [] arrParam = parameters.split(" ", 5);
			switch (opcode){

				case "do_diff": {
					System.out.println("MateServerProxy: do_diff method is executing...");
					int $result;
					int $param_0 = Integer.valueOf(arrParam[0]);
					int $param_1 = Integer.valueOf(arrParam[1]);
					$result = mate.do_diff( $param_0, $param_1);
					String dataResult = String.valueOf($result);
					System.out.println("MateServerProxy: result is " + dataResult+"\n\n");
					Message answer = new Message("MateServerProxy", dataResult);
					return answer;
				}

				case "do_sqr": {
					System.out.println("MateServerProxy: do_sqr method is executing...");
					double $result;
					float $param_0 = Float.valueOf(arrParam[0]);
					$result = mate.do_sqr( $param_0);
					String dataResult = String.valueOf($result);
					System.out.println("MateServerProxy: result is " + dataResult+"\n\n");
					Message answer = new Message("MateServerProxy", dataResult);
					return answer;
				}

				case "do_add": {
					System.out.println("MateServerProxy: do_add method is executing...");
					float $result;
					float $param_0 = Float.valueOf(arrParam[0]);
					float $param_1 = Float.valueOf(arrParam[1]);
					$result = mate.do_add( $param_0, $param_1);
					String dataResult = String.valueOf($result);
					System.out.println("MateServerProxy: result is " + dataResult+"\n\n");
					Message answer = new Message("MateServerProxy", dataResult);
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


class MateServerProxy implements ServerProxy {
	private int portNumber;

	public MateServerProxy(int portNumber) {
		this.portNumber = portNumber;
	}

	public void dispatch() {
		Address myAddr = new Entry("127.0.0.1",portNumber);
		Replyer rep = new Replyer("MateServerProxy", myAddr);
		MateImpl mate = new MateImpl();
		ByteStreamTransformer transformer = new ServerTransformer(new MateMessageServer(mate));
		while(true) {
			try {
				Message msg = new Message("Client","MyMateImpl:MateImpl!Check");
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
