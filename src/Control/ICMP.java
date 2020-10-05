package Control;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.EthernetPacket;
import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

//ICMP trabajado con la librería JPCAP
public class    ICMP {

    //constructor
    public ICMP() {
    }

    //Función icmp que completa el mensaje icmp y lo envía en un paquete IP, dentro de la trama Ethernet
    public void icmp (String ipOrigen, String ipDestino, int numBytes, int dev){

        //Busca los dispositivos de red
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        //Inicializa el sender para enviar el mensaje ICMP
        JpcapSender sender= null;
        try {
            sender = JpcapSender.openDevice(devices[dev]);
        } catch (
                IOException ioException) {
            ioException.printStackTrace();
        }

        //Se crea un paquete icmp de la libreria
        ICMPPacket icmp = new ICMPPacket();

        //Definicion de campos de ICMP
        //Broadcast para preguntar por la dirección Mac
        byte[] broadcast = new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255};
        //Arreglo de tamaño introducido por el usuario
        byte [] datos = new byte[numBytes];

        //Tipo de mensaje ICMP
        icmp.type = ICMPPacket.ICMP_ECHO;
        //Numero de secuencia del mensaje ICMP
        icmp.seq += 1;
        //Identificador del mensaje ICMP
        icmp.id += 1;

        //Inicializaciñon del checksum, siempre comienza en 0
        icmp.checksum = 0;

        //Setear parámetros IP, donde val el tipo de paquete, en este caso un ICMP IPPacket.IPPROTO_ICMP, las direcciones ip y su encabezado
        try {
            icmp.setIPv4Parameter(0,false,false,true,0,false,false,false,0,1010101,100, IPPacket.IPPROTO_ICMP,
                    InetAddress.getByName(ipOrigen),InetAddress.getByName(ipDestino));
        } catch (
                UnknownHostException unknownHostException) {
            unknownHostException.printStackTrace();
        }

        //Mandar datos aleatorios, de acuerdo al numero de bytes especificado por el usuario
        for (int i = 0; i< numBytes; i++){
            char d = (char) (Math.random() * 26 + 65);
            datos[i] = (byte)d;
        }
        //se añaden los caracteres aleatorios a capo de datos de icmp
        icmp.data = datos;

        //Definicion de campos Ethernet
        EthernetPacket ethernet =new EthernetPacket();
        //Tipo de mensaje a enviar en ethernet en este caso es un IP, ya que el icmp viene dentro
        ethernet.frametype=EthernetPacket.ETHERTYPE_IP;
        //Definicion de direcciones mac de origen y destino
        ethernet.src_mac = devices[dev].mac_address;
        ethernet.dst_mac=broadcast;
        //Se vinculan los paquetes
        icmp.datalink=ethernet;

        //Se envia el paquete
        sender.sendPacket(icmp);
        sender.close();

    }
}
