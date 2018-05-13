using System;
using CevaTema.Commons;
using CevaTema.Entries;
using CevaTema.MessageMarshaller;
using CevaTema.RequestReply;

namespace CevaTema
{
	public class NamingService
	{
		private const string Address = "127.0.0.1";
		private const int PortNo = 1111;

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
			var proxy = new InfoClientProxy(portNumber);
			return proxy;
		}
	}
}