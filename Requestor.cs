using System;
using System.Net.Sockets;
using System.Text;
using CevaTema.Commons;

namespace CevaTema.RequestReply {
    public sealed class Requestor {
        private TcpClient _s;
        private NetworkStream _stream;
        private string _myName;

        public Requestor(string theName) { _myName = theName; }

        public byte[] deliver_and_wait_feedback(IAddress theDest, byte[] data){
            byte[] buffer = null;
	        try {
                _s = new TcpClient(theDest.dest(), theDest.port());
                Console.WriteLine("Requestor: Socket "+ _s);
	            _stream = _s.GetStream();
                //_stream.WriteByte((byte)(data.Length >> 8));
                //_stream.WriteByte((byte)(data.Length & 0xFF));
	            Console.WriteLine("Message to send is :"+Encoding.ASCII.GetString(data));
	            Console.WriteLine("Message length in bytes is : "+data.Length);
                _stream.Write(data, 0, data.Length);
                _stream.Flush();
	            Console.WriteLine("Message sent succesfully");
                var val = _stream.ReadByte();
		        Console.WriteLine("\nValue read: "+val);
	            //val |= _stream.ReadByte();
                buffer = new byte[val];
		        Console.WriteLine("Buffer length is: "+buffer.Length);
                _stream.Read(buffer, 0, buffer.Length);
	            _stream.Close();
	            _s.Close();
		        Console.WriteLine("Message read succesfully");
		        Console.WriteLine("Message contains the next message:"+Encoding.ASCII.GetString(buffer));
            }
            catch (SocketException) {
                Console.WriteLine("IOException in deliver_and_wait_feedback");
            }
            return buffer;
        }

    }
}