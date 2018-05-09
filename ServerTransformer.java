import MessageMarshaller.Marshaller;
import MessageMarshaller.Message;
import RequestReply.ByteStreamTransformer;

abstract class MessageServer {
    public MessageServer() {}
    public abstract Message get_answer(Message msg) throws Exception;
}
class ServerTransformer implements ByteStreamTransformer {
    private MessageServer originalServer;
    public ServerTransformer(MessageServer s)
    {
        originalServer = s;
    }
    public byte[] transform(byte[] in) throws Exception{
        Message msg;
        Marshaller m = new Marshaller();
        msg = m.unmarshal(in);
        Message answer = originalServer.get_answer(msg);
        byte[] bytes = m.marshal(answer);
        return bytes;
    }
}
