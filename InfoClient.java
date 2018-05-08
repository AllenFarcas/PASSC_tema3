import RequestReply.*;
import MessageMarshaller.*;
import Registry.*;
import Commons.Address;

public class InfoClient {
    public static void main(String args[]) {
		try {
			Info myInfo = (Info) NamingService.getObjectReference("MyInfoImpl:InfoImpl");
			int retVal = myInfo.get_temp("Timisoara");
			System.out.println("Returned value is: "+retVal);
			String retValS = myInfo.get_road_info(2);
			System.out.println("Returned value is: "+retValS);
		} catch (Exception e){
			System.out.println(e.getMessage());
		}
    }

}

class InfoClientProxy implements Info {
	Requestor req = new Requestor("InfoServerProxy");
	Marshaller m = new Marshaller();
	int portNumber;

	public InfoClientProxy(int portNumber) {
		this.portNumber = portNumber;
	}

	//public int get_temp(String city);
	public int get_temp(java.lang.String $param_String_1) {
		String msgData = $param_String_1+":0";
		//scrie in mesaj toti parametri
		Message msg = new Message("InfoClientProxy",msgData);
		byte[] bytes = m.marshal(msg);
		Address dest = new Entry("127.0.0.1",portNumber);
		//asteapta rezutatul
		bytes = req.deliver_and_wait_feedback(dest,bytes);
		//si apoi rezultatul il despacheteaza
		Message answer = m.unmarshal(bytes);
		//rezultatul este convertiti la tipul care este returnat de metoda curenta
		int $result = Integer.parseInt(answer.data);
		return $result;
	}

	//public String get_road_info(int road_ID);
	public String get_road_info(int $param_int_1) {
		String msgData = String.valueOf($param_int_1)+":1";
		//scrie in mesaj toti parametri
		Message msg = new Message("InfoClientProxy",msgData);
		byte[] bytes = m.marshal(msg);
		Address dest = new Entry("127.0.0.1",portNumber);
		//asteapta rezutatul
		bytes = req.deliver_and_wait_feedback(dest,bytes);
		//si apoi rezultatul il despacheteaza
		Message answer = m.unmarshal(bytes);
		//rezultatul este convertiti la tipul care este returnat de metoda curenta
		String $result = answer.data;
		return $result;
	}
}
