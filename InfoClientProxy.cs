using CevaTema.Commons;
using CevaTema.Entries;
using CevaTema.MessageMarshaller;
using CevaTema.RequestReply;

namespace CevaTema {
    
    public class InfoClientProxy : IInfo,IClientProxy {
        private readonly Requestor _req = new Requestor("InfoClientProxy");
        private readonly int _portNumber;
        
        public InfoClientProxy(int portNumber) {
            _portNumber = portNumber;
        }
        public int get_temp(string param0) {
            var msgData = param0+":get_temp";
            //scrie in mesaj toti parametri
            var msg = new Message("InfoClientProxy",msgData);
            var bytes = Marshaller.Marshal(msg);
            IAddress dest = new Entry("127.0.0.1",_portNumber);
            //asteapta rezutatul
            bytes = _req.deliver_and_wait_feedback(dest,bytes);
            //despacheteaza rezultatul
            var answer = Marshaller.Unmarshal(bytes);
            //rezultatul este convertiti la tipul care este returnat de metoda curenta
            var result = int.Parse(answer.Data);
            return result;
        }
        public string get_road_info(int param0) {
            var msgData = param0 +":get_road_info";
            //scrie in mesaj toti parametri
            var msg = new Message("InfoClientProxy",msgData);
            var bytes = Marshaller.Marshal(msg);
            IAddress dest = new Entry("127.0.0.1",_portNumber);
            //asteapta rezutatul
            bytes = _req.deliver_and_wait_feedback(dest,bytes);
            //despacheteaza rezultatul
            var answer = Marshaller.Unmarshal(bytes);
            //rezultatul este convertiti la tipul care este returnat de metoda curenta
            var result = answer.Data;
            return result;
        }
    }
}