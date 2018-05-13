namespace CevaTema.MessageMarshaller {
    public class Message {
        public readonly string Sender;
        public readonly string Data;
        public Message(string theSender, string rawData) {
            Sender = theSender;
            Data = rawData;
        }
    }
}