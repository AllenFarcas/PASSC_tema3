using System;
using CevaTema.Commons;
using CevaTema.Entries;
using CevaTema.MessageMarshaller;
using CevaTema.RequestReply;

namespace CevaTema
{
	public static class NamingService
	{
		private const string Address = "127.0.0.1";
		private const int PortNo = 1111;
		private const int ActivatorPort = 1110;

		public static object GetObjectReference(string name)
		{
			var msg = new Message("Client", name);
			var req = new Requestor("Client");
			var bytes = Marshaller.Marshal(msg);
			IAddress dest = new Entry(Address, PortNo);
			bytes = req.deliver_and_wait_feedback(dest, bytes);
			var answer = Marshaller.Unmarshal(bytes);
			Console.Write("Object reference with name: ");
			Console.Write(name);
			Console.Write(" was found at port number: ");
			Console.WriteLine(answer.Data);
			var portNumber = int.Parse(answer.Data);
			//se returneaza o referinta (o instanta a lui client side proxy de fapt)
			//la obiectul server care a fost inregistrat
			var arrOfStr = msg.Data.Split(':');
			var theDestObjectName = arrOfStr[1];
			var aux = theDestObjectName.IndexOf("Impl");
			var interfaceName = theDestObjectName.Substring(0,aux);
			var className = interfaceName+"ClientProxy";
			
			//Activating the server
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
				Console.WriteLine(ans);
				Console.WriteLine(ret ? "The server is turned on" : "The server could not be turned on");
			} else {
				ans = "The server is on";
				Console.WriteLine(ans);
			}
			
			//Creating the proxy
			try
			{
				object proxy = (IClientProxy) ClientProxyGenerator.GenerateAndCompile(className, interfaceName, name, portNumber);
				return proxy;
			}
			catch(Exception e) 
			{
				Console.WriteLine(e.ToString());
			}
			return null;
		}
		
		public static void SendStopMessage() {
			var msg = new Message("Client", "MyInfoImpl:InfoImpl!TurnOff");
			var req = new Requestor("Client");
			var bytes = Marshaller.Marshal(msg);
			IAddress dest = new Entry(Address, ActivatorPort);
			bytes = req.deliver_and_wait_feedback(dest, bytes);
			var answer = Marshaller.Unmarshal(bytes);
			Console.WriteLine(bool.Parse(answer.Data) ? "The server is still on!!!" : "The server is off");
		}
	}
}