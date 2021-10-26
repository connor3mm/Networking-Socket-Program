import java.util.Arrays;

public class Sender extends TransportLayer {


    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    Sender sender;
    TransportLayerPacket packet;

    @Override
    public void init() {
        sender = new Sender("sender", simulator);
        packet = null;
        System.out.println("The sender has been initialised!" + getName());

    }

    public TransportLayerPacket mk_packet(byte[] data, int seqnum) {
        TransportLayerPacket packet = new TransportLayerPacket(seqnum,0,null ,data);
        return packet;
    }

    @Override
    public void rdt_send(byte[] data) {
        packet = mk_packet(data,0);
        // TODO calculate checksum
        simulator.sendToNetworkLayer(sender,packet);
        System.out.println("Packet with data: " + Arrays.toString(data) + " has been sent to network layer");
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

    }

    @Override
    public void timerInterrupt() {

    }
}
