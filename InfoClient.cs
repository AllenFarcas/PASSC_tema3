using System;
using System.Text;
using CevaTema.Commons;
using CevaTema.Entries;
using CevaTema.MessageMarshaller;
using CevaTema.RequestReply;

namespace CevaTema {
    public static class InfoClient {
	    private const string Address = "127.0.0.1";
	    private const int PortNo = 1111;
	    private const int ActivatorPort = 1110;
        public static void Main() {
	        try {
		        const string name = "MyInfoImpl:InfoImpl";
		        Console.WriteLine("Sending request to NamingService");
		        var msg = new Message("Client", name);
		        var req = new Requestor("Client");
		        var bytes = Marshaller.Marshal(msg);
		        Console.WriteLine("Message was marshalled :"+ Encoding.ASCII.GetString(bytes));
		        IAddress dest = new Entry(Address, PortNo);
		        Console.WriteLine("Message sent");
		        bytes = req.deliver_and_wait_feedback(dest, bytes);
		        Console.WriteLine("Message was received succesfully and contains the message:"+Encoding.ASCII.GetString(bytes));
		        var answer = Marshaller.Unmarshal(bytes);
		        Console.WriteLine("Message Received from " + answer.Sender + "and contains " + answer.Data);
		        Console.Write("Object reference with name: ");
		        Console.Write(name);
		        Console.Write(" was found at port number: ");
		        Console.WriteLine(answer.Data);
		        var portNumber = int.Parse(answer.Data);
		        //var myInfo = (IInfo) NamingService.getObjectReference("MyInfoImpl:InfoImpl");
		        IInfo myInfo = new InfoClientProxy(portNumber);
		        
		        var data = name+"!Check";
		        msg = new Message("Client",data);
		        req = new Requestor("Client");
		        bytes = Marshaller.Marshal(msg);
		        dest = new Entry(Address,ActivatorPort);
		        bytes = req.deliver_and_wait_feedback(dest,bytes);
		        answer = Marshaller.Unmarshal(bytes);
		        var serverOn = bool.Parse(answer.Data);
		        string ans;
		        if(!serverOn){
			        ans = "The server is off";
			        data = name+":"+ portNumber +"!TurnOn";
			        msg = new Message("Client",data);
			        req = new Requestor("Client");
			        bytes = Marshaller.Marshal(msg);
			        dest = new Entry(Address,ActivatorPort);
			        bytes = req.deliver_and_wait_feedback(dest,bytes);
			        answer = Marshaller.Unmarshal(bytes);
			        var ret = bool.Parse(answer.Data);
			        Console.WriteLine(ret ? "The server is turned on" : "The server could not be turned on");
		        } else {
			        ans = "The server is on";
		        }
		        Console.WriteLine(ans);
		        
		        var retVal1 = myInfo.get_temp("Timisoara");
		        Console.WriteLine("Returned value is:");
		        Console.WriteLine(retVal1);
		        var retVal2 = myInfo.get_road_info(2);
		        Console.WriteLine("Returned value is:");
		        Console.WriteLine(retVal2);
		        SendStopMessage();
	        }
	        catch (Exception e) {
		        Console.WriteLine(e.ToString());
	        }
        }

	    private static void SendStopMessage() {
		    var msg = new Message("Client", "MyInfoImpl:InfoImpl!TurnOff");
		    var req = new Requestor("Client");
		    var bytes = Marshaller.Marshal(msg);
		    IAddress dest = new Entry(Address, ActivatorPort);
		    bytes = req.deliver_and_wait_feedback(dest, bytes);
		    var answer = Marshaller.Unmarshal(bytes);
		    Console.WriteLine(bool.Parse(answer.Data) ? "Server still on" : "Server is off.");
	    }
    }
}