import Commons.Address;
import MessageMarshaller.Message;
import Registry.*;
import RequestReply.*;
import java.lang.reflect.Constructor;
import java.util.HashMap;

class ActivatorMessageServer extends MessageServer{
    HashMap<String,Boolean> activatorTable;

    public ActivatorMessageServer(){
        activatorTable = new HashMap<>();
    }

    public Message get_answer(Message msg) throws Exception {
        Message answer;
        answer = new Message("Activator", "Error");
        if(msg.sender.equals("NamingService")) {
            System.out.println("Activator verifying activatorTable for " + msg.data + " data from " + msg.sender);
            if(activatorTable.containsKey(msg.data)) {
                System.out.println("Activator REACHED data: "+msg.data);
                answer = new Message("Activator", "true"); // Was it there?
            } else {
                activatorTable.put(msg.data,false);
                System.out.println("Activator  CREATED the new entry.");
                answer = new Message("Activator", "false");
            }
        } else if (msg.sender.equals("Client")) {
            String [] arrOfStr = msg.data.split("!", 5);
            String question = arrOfStr[1];
            String data = arrOfStr[0];
            String ans="";
            //System.out.println("Activator received a Client");
            if(question.equals("Verify")){
                System.out.println("Client Verifyies if it contains: "+data);
                if(activatorTable.containsKey(data)) {
                    System.out.println("ActivatorTable contains the "+data+" key");
                    ans=String.valueOf(activatorTable.get(data));
                    System.out.println("Activator answers the client with message: "+ans);
                } else {
                    System.out.println("The activator doesn't contain this data: "+data);
                    throw new Exception("The activator doesn't contain this data: "+data);
                }
            } else if (question.equals("TurnOn")){
                System.out.println("Client TurnsOn");
                arrOfStr = data.split(":", 5);
                data=arrOfStr[0];
                String theDestObject = arrOfStr[1];
                String dataToCheck = data+":"+theDestObject;
                if(activatorTable.containsKey(dataToCheck)) {
                    int portNumber = Integer.valueOf(arrOfStr[2]);
                    int aux = theDestObject.indexOf("Impl");
                    String interfaceName = theDestObject.substring(0,aux);
                    boolean bold = activatorTable.get(dataToCheck);
                    activatorTable.replace(dataToCheck,true);
                    boolean bnew = activatorTable.get(dataToCheck);
                    ans=String.valueOf(bnew);
                    String className = interfaceName+"ServerProxy";
                    System.out.println("Old state: "+bold+" New state: "+bnew+"\n");
                    ServerProxyGenerator generator = new ServerProxyGenerator(className,interfaceName,dataToCheck);
                    generator.generateAndCompile();
                    try {
                        Class<?> clazz = Class.forName(className);
                        Constructor<?> constructor = clazz.getDeclaredConstructor(int.class);
                        if (constructor==null) {
                            System.out.println("Constructor for ServerProxy is not okay.");
                        }
                        ServerProxy proxy = (ServerProxy) constructor.newInstance(portNumber);
                        System.out.println("Executing ServerProxy.dispatch()");
                        new Thread( new Runnable() {
                            public void run() {
                                proxy.dispatch();
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("The activator doesn't contain this data: "+data);
                    throw new Exception("The activator doesn't contain this data: "+data);
                }
            } else if (question.equals("TurnOff")) {
                System.out.println("Client TurnsOff");
                if (activatorTable.containsKey(data)) {
                    boolean bold = activatorTable.get(data);
                    activatorTable.replace(data, false);
                    boolean bnew = activatorTable.get(data);
                    ans = String.valueOf(bnew);
                    System.out.println("Old state: " + bold + " New state: " + bnew);
                } else {
                    System.out.println("The activator doesn't contain this data: " + data);
                    throw new Exception("The activator doesn't contain this data: "+data);
                }
            } else if(question.equals("Check")) {
                ans = String.valueOf(activatorTable.get(data));
            }
            answer = new Message("Activator",ans);
        }
        return answer;
    }
}
/*
 *TODO Activator
 *TODO va fi inca un proces in plus care va rula pe fiecare host care poate sa gazduiasca servere
 *TODO discuta cu NamingService
 *TODO cand un client vine la Broker si ii zice "da-mi un StockMarket"
 *TODO se duce la Activator: esti deja activat? daca nu ii zice .activeaza-te()
 *TODO activatorul atunci, aflandu-se pe host-ul unde se afla resursele face instanta via reflection
 *TODO face si instanta de la ServerProxy si ii da drumul sa listen-uie
 *TODO apoi zice ca e ready si ca clientul poate veni sa il contacteze pe ServerProxy
 */
public class Activator {
    public static final String address = "127.0.0.1";
    public static final int portNo = 1110;

    public static void main(String args[]){
        ByteStreamTransformer transformer = new ServerTransformer(new ActivatorMessageServer());
        Address myAddr = new Entry(address,portNo);
        Replyer r = new Replyer("Activator", myAddr);
        while (true) {
            try {
                r.receive_transform_and_send_feedback(transformer);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
