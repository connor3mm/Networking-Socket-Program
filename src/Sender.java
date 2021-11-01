import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.lang.Math;

public class Sender extends TransportLayer {


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

    public TransportLayerPacket mk_packet(byte[] data, int seqnum) {
        Checksum checksum = new CRC32();
        checksum.update(data, 0, data.length);
        String checksumString = Long.toBinaryString(checksum.getValue());
        checksumString = oneComp(checksumString);


        TransportLayerPacket newPacket = new TransportLayerPacket(seqnum,akNum,checksumString,data);
        if(akNum == 0){
            akNum++;
        }else{
            akNum--;
        }
        return newPacket;
    }

    @Override
    public void rdt_send(byte[] data) {
        System.out.println("SENDER send method");

        if(!this.senderStatus.equals("Primed")){
            System.out.println("Checking if another packet can be sent....");
        } else {
            sent_packet = mk_packet(data,packetSeqNum);
            System.out.println("The sender has created the packet");
            prevSeqNum = packetSeqNum;
            if(packetSeqNum == 0){
                packetSeqNum++;
            }else{
                packetSeqNum--;
            }

            senderStatus = "Waiting for ACK";
            System.out.println("Packet with data: " + Arrays.toString(data) + " has been sent to network layer");
            simulator.sendToNetworkLayer(this,sent_packet);

            System.out.println("The timer has started");
            simulator.startTimer(this,2000);

        }
    }


    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        System.out.println("SENDER receive method");

        System.out.println("The sender receiving an ACKNOWLEDGMENT packet");
        received_packet = new TransportLayerPacket(pkt);

        if(corrupt() || !checkAcknowledgmentNum()) {
            System.out.println("The packet has been corrupted or has not been acknowledged");

            System.out.println("The timer has stopped!");
            simulator.stopTimer(this);
            //this.rdt_send(received_packet.getData());
            System.out.println("The packet has been resend");
            //senderStatus = "ERROR!!!!";
            timerInterrupt();


        } else {
            System.out.println("ACK Received");

            senderStatus = "Primed";
            System.out.println("Seq Num: " + received_packet.getSeqnum());
            System.out.println("AckNum: " + received_packet.getAcknum());
            System.out.println("--------------");
            simulator.stopTimer(this);
        }


    }

    @Override
    public void timerInterrupt() {
        if(corrupt()) {
            senderStatus = "Primed";
            rdt_send(sent_packet.getData());
        }

    }


    public boolean checkAcknowledgmentNum() {
        if (received_packet.getSeqnum() == prevSeqNum){
            return received_packet.getAcknum() == sent_packet.getAcknum();
        }
        return false;
    }

    public boolean corrupt() {
        return received_packet == null;
    }

    public String oneComp(String check){

        char[] compArrayOrigin = new char[check.length()];
        for(int i=0; i<check.length();i++ ){
            if(check.toCharArray()[i] == '0') compArrayOrigin[i] = '1';
            else compArrayOrigin[i] = '0';
        }

        return String.valueOf(compArrayOrigin);
    }
}