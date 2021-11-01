import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Receiver extends TransportLayer {


    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    Receiver receiver;
    int previousSeqNum;
    TransportLayerPacket packet;

    @Override
    public void init() {
        receiver = new Receiver("Receiver", simulator);
        System.out.println("The Receiver has been initialised!" + getName() + "\n");
        packet = null;
        previousSeqNum = -1;
    }


    public TransportLayerPacket mk_packet(byte[] data, int seqnum) {
        //checksum for the packet we send to Sender
        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        String checksumString = Long.toBinaryString(checksum.getValue());

        return new TransportLayerPacket(seqnum,packet.getAcknum(),checksumString ,data);
    }


    @Override
    public void rdt_send(byte[] data) {
        System.out.println("RECEIVER send method");
        System.out.println("The receiver is sending an ACKNOWLEDGMENT packet" + packet.getSeqnum());
        TransportLayerPacket ack_pkt = mk_packet(data, packet.getSeqnum());
        simulator.sendToNetworkLayer(this, ack_pkt);
        System.out.println("ACKNOWLEDGMENT packet has been sent.");
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        System.out.println("RECEIVE receive method");
        packet = new TransportLayerPacket(pkt);

        if(corrupt()) {
            System.out.println("The packet has been corrupted");
            System.out.println("Waiting for a new packet to be sent");
        } else if(duplicate()) {
            System.out.println("Duplicate packet! Discard!!!");
            packet = null;
        } else {
            System.out.println("Receiver has received the packet");
            simulator.sendToApplicationLayer(this,packet.getData());
            System.out.println("Packet has been send to application layer");
            System.out.println("Sending ACK to the sender");

            this.rdt_send(packet.getData());
        }

    }

    @Override
    public void timerInterrupt() {
    }

    public boolean corrupt() {
        return packet == null || !verifyChecksum();
    }

    public boolean duplicate() {
        return previousSeqNum == packet.getSeqnum();
    }


    public boolean verifyChecksum(){

        String checksumFromSender = packet.getChecksum();

        Checksum checksum = new CRC32();
        checksum.update(packet.getData(), 0, packet.getData().length);
        String checksumString = Long.toBinaryString(checksum.getValue());

        String result = addBits(checksumFromSender, checksumString);
        System.out.println("Adding the checksum: " + result);

        
        //checking if the checksum is valid
        for(int i=0; i<result.length();i++ ){
            if(result.charAt(i)=='0') return false;
        }

        return true;
    }

    public String addBits(String a, String b){
        StringBuilder result = new StringBuilder();
        int carry = 0;
        int sum;

        for (int i = a.length() - 1; i >= 0; i--){
            int first = a.charAt(i)  - '0';
            int second = b.charAt(i)  - '0';

            sum = (first ^ second ^ carry) + '0';
            result.insert(0, (char) sum);

            carry = (first & second) | (second & carry) | (first & carry);
        }

        if (carry == 1) result.insert(0, "1");
        //System.out.println("Testing result:" + result);
        return result.toString();
    }

}