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

    @Override
    public void rdt_send(byte[] data) {
        
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

    }

    @Override
    public void timerInterrupt() {

    }
}
