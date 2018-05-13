namespace CevaTema.Entries {
    using IAddress = Commons.IAddress;

    public sealed class Entry : IAddress {
        private readonly string destinationId;
        private readonly int portNr;
        public Entry(string theDest, int thePort) {
            destinationId = theDest;
            portNr = thePort;
        }
        public string dest() {
            return destinationId;
        }
        public int port() {
            return portNr;
        }

    }
}
