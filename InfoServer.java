
import RequestReply.*;
import MessageMarshaller.*;
import Registry.*;
import Commons.Address;


interface Info {
	int get_temp(String city);
	String get_road_info(int road_ID);
}

class InfoImpl implements Info {
	@Override
	public int get_temp(String city) {
		System.out.println("get_temp method is executing");
		return 20;
	}

	@Override
	public String get_road_info(int road_ID) {
		System.out.println("get_road_info method is executing");
		switch (road_ID) {
			case 1: return "Blocat";
			case 2: return "Liber";
			case 3: return "Aglomerat";
			default : return "Eroare";
		}
	}
}

class ServerTransformer implements ByteStreamTransformer {
    private MessageServer originalServer;
    public ServerTransformer(MessageServer s)
    {
	originalServer = s;
    }
    public byte[] transform(byte[] in) throws Exception{
		Message msg;
		Marshaller m = new Marshaller();
		msg = m.unmarshal(in);
		Message answer = originalServer.get_answer(msg);
		byte[] bytes = m.marshal(answer);
		return bytes;
    }
}

class MessageServer {
	private InfoImpl info;

	public MessageServer() {}

	public MessageServer(InfoImpl info) {
		this.info = info;
	}

	public Message get_answer(Message msg) throws Exception{
    	if(msg.sender.equals("InfoClientProxy")) {
			System.out.println("InfoServerProxy analyzing data");
			String [] arrOfStr = msg.data.split(":", 5);
			String parameters = arrOfStr[0];
			String opcode = arrOfStr[1];
			int opnum = Integer.parseInt(opcode);
			String [] arrParam = parameters.split(" ", 5);
			String parameter = arrParam[0];
			switch (opnum){
				//get_temp 0
				case 0: {
					System.out.println("InfoServerProxy: get_temp method");
					java.lang.String $param_String_1 = parameter;
					int $result;
					$result = info.get_temp($param_String_1);
					String dataResult = String.valueOf($result);
					System.out.println("InfoServerProxy: result is " + dataResult);
					Message answer = new Message("InfoServerProxy",dataResult);
					return answer;
				}
				//get_road_info 1
				case 1: {
					System.out.println("InfoServerProxy: get_road_info method");
					int $param_int_1 = Integer.parseInt(parameter);
					String $result;
					$result = info.get_road_info($param_int_1);
					System.out.println("InfoServerProxy: result is " + $result);
					Message answer = new Message("InfoServerProxy",$result);
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

public class InfoServer {
    public static void main(String args[]) {
		InfoImpl infoImpl = new InfoImpl();
		System.out.println("InfoServer main started");
		try {
			NamingService.registerMethod("MyInfoImpl",infoImpl);
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
    }
}

interface ServerProxy {
	public void dispatch();
}

class InfoServerProxy implements ServerProxy{
	private int portNumber;

	public InfoServerProxy(int portNumber) {
		this.portNumber = portNumber;
	}

	public void dispatch() {
		Address myAddr = new Entry("127.0.0.1",portNumber);
		Replyer rep = new Replyer("InfoServerProxy", myAddr);
		InfoImpl info = new InfoImpl();
		ByteStreamTransformer transformer = new ServerTransformer(new MessageServer(info));
		while(true) {
			try {
				rep.receive_transform_and_send_feedback(transformer);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
