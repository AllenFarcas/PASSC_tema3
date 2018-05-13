using System;
using System.Text;
namespace CevaTema.MessageMarshaller {
    public class Marshaller {
        public static byte[] Marshal(Message theMsg) {
            var m = "  " + theMsg.Sender + ":" + theMsg.Data;
            var b = Encoding.ASCII.GetBytes(m);
            b[0] = (byte)m.Length;
            return b;
        }
        public static Message Unmarshal(byte[] byteArray) {
            var msg = Encoding.ASCII.GetString(byteArray);
            Console.WriteLine("Umnarshalling the message:"+Encoding.ASCII.GetString(byteArray));
            var sender = msg.Substring(1, msg.IndexOf(':'));
            Console.WriteLine("The sender is "+sender);
            Console.WriteLine("The index of the first digit is: "+(msg.IndexOf(':')+1));
            var m = msg.Substring(msg.IndexOf(':')+1);
            Console.WriteLine("The port number is: "+m);
            return new Message(sender, m);
        }
    }
}