using System;
using System.Net.Sockets;
using CevaTema.Commons;
using CevaTema.Entries;
using CevaTema.MessageMarshaller;
using CevaTema.RequestReply;

namespace CevaTema
{
	public class NamingService
	{
		public const string address = "127.0.0.1";
		public const int portNo = 1111;

		public static object getObjectReference(string name)
		{
			Message msg = new Message("Client", name);
			Requestor req = new Requestor("Client");
			Marshaller m = new Marshaller();
			byte[] bytes = Marshaller.Marshal(msg);
			IAddress dest = new Entry(address, portNo);
			bytes = req.deliver_and_wait_feedback(dest, bytes);
			Message answer = Marshaller.Unmarshal(bytes);
			Console.Write("Object reference with name: ");
			Console.Write(name);
			Console.Write(" was found at port number: ");
			Console.WriteLine(answer.Data);
			int portNumber = Int32.Parse(answer.Data);
			//se returneaza o referinta (o instanta a lui client side proxy de fapt)
			//la obiectul server care a fost inregistrat
			/*string[] arrOfStr = msg.data.Split(':');
			string theDestObjectName = arrOfStr[1];
			int aux = theDestObjectName.IndexOf("Impl");
			string interfaceName = theDestObjectName.Substring(0, aux);
			string className = interfaceName + "ClientProxy";*/
			InfoClientProxy proxy = new InfoClientProxy(portNumber);
			return proxy;
		}
	}
}