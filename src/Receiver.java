public class Receiver extends TransportLayer {


    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    Receiver receiver;

    @Override
    public void init() {
        receiver = new Receiver("Receiver", simulator);
        System.out.println("The Receiver has been initialised!" + getName());
    }

    @Override
    public void rdt_send(byte[] data) {

    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

    }

    @Override
    public void timerInterrupt() {
        simulator.stopTimer(receiver);
    }
}