namespace CevaTema.Entries {
    using IAddress = Commons.IAddress;

    public class Entry : IAddress {
        private string destinationId;
        private int portNr;
        public Entry(string theDest, int thePort) {
            destinationId = theDest;
            portNr = thePort;
        }

        /*public Entry()
        {
        }*/

        public virtual string dest() {
            return destinationId;
        }
        public virtual int port() {
            return portNr;
        }

        /*public override string ToString()
        {
            return "Entry{destinationId='" + destinationId + '\'' + ", portNr=" + portNr + '}';
        }*/
    }
}
/*private String destinationId;
	private int portNr;
	public Entry(String theDest, int thePort)
	{
		destinationId = theDest;
		portNr = thePort;
	}
	public String dest() {
		return destinationId;
	}
	public int port() {
		return portNr;
	}*/