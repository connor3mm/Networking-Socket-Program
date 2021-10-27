import java.util.Arrays;

public class Receiver extends TransportLayer {


    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    Receiver receiver;
    int seqNum;
    TransportLayerPacket packet;

    @Override
    public void init() {
        receiver = new Receiver("Receiver", simulator);
        System.out.println("The Receiver has been initialised!" + getName());
        packet = null;
        seqNum = -1;
    }


    public TransportLayerPacket mk_packet(byte[] data, int seqnum) {
        TransportLayerPacket newPacket = new TransportLayerPacket(seqnum,1,null ,data);
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




    }

    @Override
    public void timerInterrupt() {
        simulator.stopTimer(receiver);
    }
}