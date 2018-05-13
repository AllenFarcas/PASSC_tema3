namespace CevaTema.RequestReply
{
    public interface IByteStreamTransformer
    {
        byte[] Transform(byte[] @in);
    }
}