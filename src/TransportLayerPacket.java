public class TransportLayerPacket {

    // Maybe remove these
    // You may need extra fields
    private int seqnum;
    private int acknum;
    private String checksum;

    byte[] data;

    // You may need extra methods

    public TransportLayerPacket(TransportLayerPacket pkt) {
        this.seqnum = pkt.seqnum;
        this.acknum = pkt.acknum;
        this.data = pkt.data;
        this.checksum = pkt.checksum;
    }

    public TransportLayerPacket(int seqnum, int acknum, String checksum, byte[] data) {
        this.seqnum = seqnum;
        this.acknum = acknum;
        this.data = data;
        this.checksum = checksum;
    }

    public void setSeqnum(int seqnum) {
        this.seqnum = seqnum;
    }

    public void setAcknum(int acknum) {
        this.acknum = acknum;
    }

    public byte[] getData() {
        return data;
    }

}
