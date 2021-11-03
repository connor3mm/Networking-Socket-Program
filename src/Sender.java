import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/*
 * Sender Class - handles all the functionalities on the sender side of RDT 3.0 Protocol
 */

public class Sender extends TransportLayer {


    /**
     * A constructor for the Sender class
     * @param name
     * @param simulator
     */
    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    Sender sender;
    TransportLayerPacket sent_packet;
    TransportLayerPacket received_packet;
    String senderStatus;
    int akNum;
    int prevSeqNum;
    int packetSeqNum;

    /**
     * This method is used to initialize all variables that we use in the Sender class
     */
    @Override
    public void init() {
        sender = new Sender("Sender", simulator);
        sent_packet = null;
        received_packet = null;
        senderStatus = "Primed";
        prevSeqNum = 1;
        packetSeqNum = 0;

        System.out.println("The sender has been initialised!" + getName());
    }


    /**
     * This method is used to make a new packet to be sent to the receiver
     * @param data
     * @param seqnum
     * @return newPacket
     */
    public TransportLayerPacket mk_packet(byte[] data, int seqnum) {
        //checksum for the packet we send to Receiver
        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        String checksumString = Long.toBinaryString(checksum.getValue());
        checksumString = oneComp(checksumString);

        TransportLayerPacket newPacket = new TransportLayerPacket(seqnum, akNum, checksumString, data);
        if (akNum == 0) {
            akNum++;
        } else {
            akNum--;
        }
        return newPacket;
    }

    /**
     * Ths method sends the newly created packet to the receiver and starts the timer when the packet is sent
     * It changes state from waiting from call above to waiting for ACK when it sends the packet
     * @param data
     */
    @Override
    public void rdt_send(byte[] data) {
        System.out.println("SENDER send method");

        if (!this.senderStatus.equals("Primed")) {
            System.out.println("Checking if another packet can be sent... (timer still on)");
        } else {

            //    Testing the network simulator for different cases
            //    2) Here we are changing this method to test for NULL PACKET
            //    data = null;
            sent_packet = mk_packet(data, packetSeqNum);

            System.out.println("The sender has created the packet");
            prevSeqNum = packetSeqNum;
            if (packetSeqNum == 0) {
                packetSeqNum++;
            } else {
                packetSeqNum--;
            }

            senderStatus = "Waiting for ACK";
            System.out.println("Packet with data: " + Arrays.toString(data) + " has been sent to network layer");
            simulator.sendToNetworkLayer(this, sent_packet);

            System.out.println("The timer has started");
            simulator.startTimer(this, 2000);

        }
    }

    /**
     * This method is used from the sender to receive an ACK packet from the receiver
     * It handles scenarios such as:
     *  - if an ACK has been received successfully
     *  - if the packet that has been sent is corrupted
     *  - if no ACK has been received
     *  - receiving duplicate ACK packet
     * @param pkt
     */
    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        System.out.println("SENDER receive method");

        System.out.println("The sender receiving an ACKNOWLEDGMENT packet");
        received_packet = new TransportLayerPacket(pkt);
        //received_packet.setData(new byte[0]);//used to test duplicate ACK packets
        if (corrupt() || !checkAcknowledgmentNum()) {

            //  Testing the network simulator for different cases
            // 3) Testing for missing ack packet
            // received_packet.setAcknum(99);

            System.out.println("The packet has been corrupted or has not been acknowledged");

            System.out.println("The timer has stopped!");
            simulator.stopTimer(this);

            System.out.println("The packet has been resend");
            timerInterrupt();


        } else if (received_packet.getData().length == 0) {
            System.out.println("Detected duplicate ACK packet! ");
            simulator.stopTimer(this);
            senderStatus = "Primed";
        } else {
            System.out.println("ACK Received");

            senderStatus = "Primed";
            System.out.println("Seq Num: " + received_packet.getSeqnum());
            System.out.println("AckNum: " + received_packet.getAcknum());
            System.out.println("--------------");
            simulator.stopTimer(this);
        }

    }

    /**
     * This method is used to resend the packet if the timer has timed out and no ACK has been received from the Receiver in the given amount of time
     */
    @Override
    public void timerInterrupt() {
        if (corrupt()) {
            senderStatus = "Primed";
            rdt_send(sent_packet.getData());
        }

    }


    /**
     * This method verifies if the ACK packet is received for the correct packet according to its seqNum
     * @return boolean
     */
    public boolean checkAcknowledgmentNum() {
        if (received_packet.getSeqnum() == prevSeqNum) {
            return received_packet.getAcknum() == sent_packet.getAcknum();
        }
        return false;
    }

    /**
     * This method checks if the packet has been corrupted
     * @return null
     */
    public boolean corrupt() {
        return received_packet == null;
    }

    /**
     * This method takes the checksum for the packet and adds ones' complement
     * @param check
     * @return the value of the compArrayOrigin
     */
    public String oneComp(String check) {

        char[] compArrayOrigin = new char[check.length()];
        for (int i = 0; i < check.length(); i++) {
            if (check.toCharArray()[i] == '0') compArrayOrigin[i] = '1';
            else compArrayOrigin[i] = '0';
        }

        return String.valueOf(compArrayOrigin);
    }
}