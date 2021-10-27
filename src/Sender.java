import java.util.Arrays;

public class Sender extends TransportLayer {


    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    Sender sender;
    TransportLayerPacket packet;

    @Override
    public void init() {
        sender = new Sender("Sender", simulator);
        packet = null;
        System.out.println("The sender has been initialised!" + getName());

    }

    public TransportLayerPacket mk_packet(byte[] data, int seqnum) {
        TransportLayerPacket newPacket = new TransportLayerPacket(seqnum,0,null ,data);
        return newPacket;
    }

    @Override
    public void rdt_send(byte[] data) {
        packet = mk_packet(data,0);
        System.out.println("The sender has created the packet");

        // TODO calculate checksum
        simulator.sendToNetworkLayer(sender,packet);
        System.out.println("Packet with data: " + Arrays.toString(data) + " has been sent to network layer");
        System.out.println("The timer has started");
        simulator.startTimer(sender,1);

    }


    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        System.out.println("The sender receiving an ACKNOWLEDGMENT packet");
        TransportLayerPacket receivePacket = new TransportLayerPacket(pkt);

        if(corrupt(receivePacket) || !checkAcknowledgmentNum(receivePacket)) {
            System.out.println("The packet has been corrupted or has not been acknowledged");
            timerInterrupt();
            System.out.println("The timer has stopped!");
            rdt_send(packet.getData());
            System.out.println("The packet has been resend");

        } else {
            System.out.println("ACK Received");
            timerInterrupt();
            receivePacket.setAcknum(0);
        }

    }

    @Override
    public void timerInterrupt() {
        simulator.stopTimer(sender);
    }

    public boolean checkAcknowledgmentNum(TransportLayerPacket receivePacket) {
        if(receivePacket.getAcknum() == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean corrupt(TransportLayerPacket receivePacket) {
        if(receivePacket == null) {
            return true;
        }
        else {
            return false;
        }
    }
}
