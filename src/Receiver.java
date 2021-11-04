import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/*
 * Receiver Class - handles all the functionalities on the receiver side of RDT 3.0 Protocol
 */

public class Receiver extends TransportLayer {


    /**
     * A constructor for the Receiver class
     * @param name
     * @param simulator
     */
    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    Receiver receiver;
    int previousSeqNum;
    TransportLayerPacket packet;

    /**
     * This method is used to initialize all variables that we use in the Receiver class
     */
    @Override
    public void init() {
        receiver = new Receiver("Receiver", simulator);
        System.out.println("The Receiver has been initialised!" + getName() + "\n");
        packet = null;
        previousSeqNum = -1;
    }


    /**
     * This method is used to make a new ACK packet to be sent to the sender
     * @param data
     * @param seqnum
     * @return newPacket
     */
    public TransportLayerPacket mk_packet(byte[] data, int seqnum) {

        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        String checksumString = Long.toBinaryString(checksum.getValue());

       TransportLayerPacket newPacket = new TransportLayerPacket(seqnum, packet.getAcknum(), checksumString, data);

        return newPacket;
    }


    /**
     * This method sends the ACK packet to the sender for the packet that it has received
     * @param data
     */
    @Override
    public void rdt_send(byte[] data) {
        System.out.println("RECEIVER send method");
        System.out.println("The receiver is sending an ACKNOWLEDGMENT packet" + packet.getSeqnum());
        TransportLayerPacket ack_pkt = mk_packet(data, packet.getSeqnum());

        simulator.sendToNetworkLayer(this, ack_pkt);
        System.out.println("ACKNOWLEDGMENT packet has been sent.");
    }

    /**
     * This method receives the packet that has been sent by the sender
     * It handles scenarios such as:
     *  - receiving the correct packet
     *  - receiving a duplicate packet
     *  - receiving a corrupt packet
     * @param pkt
     */
    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        System.out.println("RECEIVER receive method");
        packet = new TransportLayerPacket(pkt);

        //previousSeqNum = 0; -> test - setting the previous seq num to 0 to simulate receiving duplicate packets
        //packet = null; -> test - to check for packet corruption

        if (corrupt()) {
            System.out.println("The packet has been corrupted");
            System.out.println("Waiting for a new packet to be sent");
        } else if (duplicate()) {
            System.out.println("Duplicate packet! ");
            packet.setData(new byte[0]);
            this.rdt_send(packet.getData());
        } else {
            System.out.println("Receiver has received the packet");
            simulator.sendToApplicationLayer(this, packet.getData());
            System.out.println("Packet has been send to application layer");
            System.out.println("Sending ACK to the sender");

            this.rdt_send(packet.getData());
        }

    }

    @Override
    public void timerInterrupt() {
    }

    /**
     * This method checks if the packet is corrupted
     * @return null or no verification for the checksum
     */
    public boolean corrupt() {
        return packet == null || !verifyChecksum();
    }

    /**
     * This method checks if the packet that has been received is a duplicate
     * @return duplicate packet
     */
    public boolean duplicate() {
        return previousSeqNum == packet.getSeqnum();
    }


    /**
     * This method converts the checksum to a binary string, takes the checksum of the received packet, creates a new checksum of the receiver data and adds them together
     * If the calculated checksum includes all 1, no corruption has occurred during the transmission of the bits
     * If the calculated checksum includes 0, a corruption has occurred during the transmission of the bits
     * @return boolean
     */
    public boolean verifyChecksum() {

        String checksumFromSender = packet.getChecksum();

        Checksum checksum = new CRC32();
        checksum.update(packet.getData(), 0, packet.getData().length);
        String checksumString = Long.toBinaryString(checksum.getValue());

        String result = addBits(checksumFromSender, checksumString);
        System.out.println("Adding the checksum: " + result);

        //checking if the checksum is valid
        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '0') return false;
        }

        return true;
    }

    /**
     * This method calculates the sum of two binary strings
     * @param a
     * @param b
     * @return result
     */
    public String addBits(String a, String b) {
        StringBuilder result = new StringBuilder();
        int carry = 0;
        int sum;

        for (int i = a.length() - 1; i >= 0; i--) {
            int first = a.charAt(i) - '0';
            int second = b.charAt(i) - '0';

            sum = (first ^ second ^ carry) + '0';
            result.insert(0, (char) sum);

            carry = (first & second) | (second & carry) | (first & carry);
        }

        if (carry == 1) result.insert(0, "1");

        return result.toString();
    }

}