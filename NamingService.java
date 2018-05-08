import Commons.Address;
import MessageMarshaller.Marshaller;
import MessageMarshaller.Message;
import Registry.*;
import RequestReply.ByteStreamTransformer;
import RequestReply.Replyer;
import RequestReply.Requestor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class NamingServiceMessageServer extends MessageServer {
	private int i=1112;
	public Message get_answer(Message msg) throws Exception {
		Message answer;
		answer = new Message("NamingService", "Error");
		if(msg.sender.equals("Server")) {
			System.out.println("Naming service registering " + msg.data + " from " + msg.sender);
			String [] arrOfStr = msg.data.split(":", 5);
			String theDestName = arrOfStr[0];
			String theDestObject = arrOfStr[1];
			Entry createNewEntry = new Entry(NamingService.address,i);
			i++;
			Registry.instance().put(msg.data,createNewEntry);
			Address addr = Registry.instance().get(msg.data);
			if(addr == null) {
				System.out.println("Naming service couldn't put " + theDestName + " in the database.");
				//answer = new Message("Naming service", "Error putting data in database ");
				throw new Exception("Error putting the data in the database.");
			} else {
				System.out.println("Naming service registered the object " + theDestObject +
						" with the name "+ theDestName +" in the database.");
				answer = new Message("Naming service", String.valueOf(addr.port()));
			}
		} else if (msg.sender.equals("Client")) {
			System.out.println("Naming service is searching for " + msg.data + " in database.");
			Address addr = Registry.instance().get(msg.data);
			if(addr == null) {
				System.out.println("Naming service didn't find " + msg.data + " in database.");
				//answer = new Message("Naming service", "Error finding data in database ");
				throw new Exception("Error finding data in database");
			} else {
				System.out.println("Naming service found " + msg.data + " in database.");
				answer = new Message("Naming service", String.valueOf(addr.port()));
				//answer = new Message("Addr.dest = "+addr.dest() + " addr.")
			}
		}
		return answer;
	}
}

public class NamingService {
	public static final String address = "127.0.0.1";
	public static final int portNo = 1111;

	public static void main(String args[]){
		ByteStreamTransformer transformer = new ServerTransformer(new NamingServiceMessageServer());
		Address myAddr = new Entry(address,portNo);
		Replyer r = new Replyer("NamingService", myAddr);
		while (true) {
			try {
				r.receive_transform_and_send_feedback(transformer);
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
	}

	//returns the port number the server was registered at
	public static void registerMethod(String name, Object obj) {
		String data = name + ":" + obj.getClass().getTypeName();
		Message msg= new Message("Server",data);
		Requestor req = new Requestor("Server");
		Marshaller m = new Marshaller();
		byte[] bytes = m.marshal(msg);
		Address dest = new Entry(address,portNo);
		bytes = req.deliver_and_wait_feedback(dest, bytes);
		Message answer = m.unmarshal(bytes);
		System.out.println("Registered object " + obj.getClass().getTypeName()+" with name "+name+" at "+answer.data);
		//return Integer.parseInt(answer.data);
		//se preia rezultatul
		int portNumber = Integer.parseInt(answer.data);
		//InfoServerProxy proxy = new InfoServerProxy(portNumber);
		//si se invoca metoda de start a acestuia
		//proxy.dispatch();
		//Se creaza instanta lui serverside proxy prin reflexie
		String objType = obj.getClass().getTypeName();
		int aux = objType.indexOf("Impl");
		String className =objType.substring(0,aux)+"ServerProxy";
		try {
			System.out.println("Class name = " + className);
			Class<?> clazz = Class.forName(className);
			Constructor<?> constructor = clazz.getDeclaredConstructor(int.class);
			//getConstructor(String.class);
			if (constructor==null) {
				System.out.println("Null");
			}
			//System.out.println(constructor.toString());
			ServerProxy proxy = (ServerProxy) constructor.newInstance(portNumber);
			//System.out.println(proxy.toString());
			proxy.dispatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//returns the port number of the object the client searched for
	public static Object getObjectReference(String name){
		Message msg = new Message("Client",name);
		Requestor req = new Requestor("Client");
		Marshaller m = new Marshaller();
		byte[] bytes = m.marshal(msg);
		Address dest = new Entry(address,portNo);
		bytes = req.deliver_and_wait_feedback(dest, bytes);
		Message answer = m.unmarshal(bytes);
		System.out.println("Object reference with name: " + name+" was found at port number: "+answer.data);
		int portNumber = Integer.parseInt(answer.data);
		//se returneaza o referinta (o instanta a lui client side proxy de fapt)
		//la obiectul server care a fost inregistrat ca “MyInfoImpl”
		InfoClientProxy proxy = new InfoClientProxy(portNumber);
		return proxy;
	}

}