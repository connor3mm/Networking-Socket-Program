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
        System.out.println("The Receiver has been initialised!" + getName());
        packet = null;
        previousSeqNum = -1;
    }


    public TransportLayerPacket mk_packet(byte[] data, int seqnum) {
        //checksum for the packet we send to Sender
        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        String checksumString = Long.toBinaryString(checksum.getValue());

        System.out.println("dfvgbhnjm:" );

        TransportLayerPacket newPacket = new TransportLayerPacket(seqnum,1,checksumString ,data);
        return newPacket;
    }


    @Override
    public void rdt_send(byte[] data) {
        System.out.println("The receiver is sending an ACKNOWLEDGMENT packet" + packet.getSeqnum());
        packet = mk_packet(data, packet.getSeqnum());
        simulator.sendToNetworkLayer(receiver, packet);
        System.out.println("ACKNOWLEDGMENT packet has been sent.");
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        packet = new TransportLayerPacket(pkt);

        if(corrupt()) {
            System.out.println("The packet has been corrupted");
            System.out.println("Waiting for a new packet to be sent");
        } else if(duplicate()) {
            System.out.println("Duplicate packet! Discard!!!");
            packet = null;
        } else {
            System.out.println("Receiver has received the packet");
            simulator.sendToApplicationLayer(receiver,packet.getData());
            System.out.println("Packet has been send to application layer");
            System.out.println("Sending ACK to the sender");
            rdt_send(packet.getData());
        }

    }

    @Override
    public void timerInterrupt() {
        simulator.stopTimer(receiver);
    }

    public boolean corrupt() {
        if(packet == null) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean duplicate() {
        if(previousSeqNum == packet.getSeqnum()) {
            return true;
        }
        else {
            return false;
        }
    }

}