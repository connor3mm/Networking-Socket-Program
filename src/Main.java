import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Main {

    public static void main(String[] args) {
        NetworkSimulator sim = new NetworkSimulator(10, 0.0, 0.0, 10.0, false, 1);

        // TODO: Set the sender   (sim.setSender)
        Sender sender = new Sender("Sender",sim);
        sim.setSender(sender);

        // TODO: Set the receiver (sim.setReceiver)
        Receiver receiver = new Receiver("Receiver",sim);
        sim.setReceiver(receiver);
        sim.runSimulation();

    }

}
