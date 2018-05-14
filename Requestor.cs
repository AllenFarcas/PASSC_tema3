using System;
using System.Net.Sockets;
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
	            _stream = _s.GetStream();
                _stream.Write(data, 0, data.Length);
                _stream.Flush();
                var val = _stream.ReadByte();
                buffer = new byte[val];
                _stream.Read(buffer, 0, buffer.Length);
	            _stream.Close();
	            _s.Close();
            }
            catch (SocketException) {
                Console.WriteLine("IOException in deliver_and_wait_feedback");
            }
            return buffer;
        }

    }
}