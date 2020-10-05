package Entities;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;

import java.io.IOException;


//Clase ARP manual
public class ARP implements java.io.Serializable{

    //Definicion de los campos de la trama
    //Ethernet 00 01 2B
    public final byte[] hard_type = new byte[]{(byte)0, (byte)1};

    //Protocol type 2B ipv4
    public final byte[] ip_type = new byte[]{(byte)8, (byte)0};

    //Size, cada uno 1B
    public final byte hard_size = (byte)6;
    public final byte ip_size = (byte)4;

    //Codigo 2B - Request = 1 o Reply = 2
    public final byte [] cod_operacion = new byte[]{(byte)0, (byte)1};

    //6B
    public byte[] mac_origen;
    //4B
    public byte[] ip_origen;

    //6B
    public byte[] mac_destino;
    //4B
    public byte[] ip_destino;

    public java.io.Serializable link;

    //Contructor
    public ARP() {
    }

    //Funcion que construye la trama en un arreglo de bytes
    public byte [] tramaARP (byte[] macDestino, byte[] macOrigen,byte[] ipOrigen, byte[] ipDestino){
        byte [] a = new byte[28];
        a[0] = hard_type[0];
        a[1] = hard_type[1];
        a[2] = ip_type[0];
        a[3] = ip_type[1];
        a[4] = hard_size;
        a[5] = ip_size;
        a[6] = cod_operacion[0];
        a[7] = cod_operacion[1];
        a[8] = macOrigen[0];
        a[9] = macOrigen[1];
        a[10] = macOrigen[2];
        a[11] = macOrigen[3];
        a[12] = macOrigen[4];
        a[13] = macOrigen[5];
        a[14] = ipOrigen[0];
        a[15] = ipOrigen[1];
        a[16] = ipOrigen[2];
        a[17] = ipOrigen[3];
        a[18] = macDestino[0];
        a[19] = macDestino[1];
        a[20] = macDestino[2];
        a[21] = macDestino[3];
        a[22] = macDestino[4];
        a[23] = macDestino[5];
        a[24] = ipDestino[0];
        a[25] = ipDestino[1];
        a[26] = ipDestino[2];
        a[27] = ipDestino[3];

        return a;
    }

    //Se añade a ethernet
    public void encapsular(byte [] trama, int dev){
        //Solo se usa jpcap para el paquete de ethernet
        EthernetPacket ether = new EthernetPacket();
        //Se crea un paquete vacío
        Packet paquete = new Packet();

        //Se inica el sender para enviar la trama arp en el paquete ethernet
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        JpcapSender sender= null;
        try {
            sender = JpcapSender.openDevice(devices[dev]);
        } catch (
                IOException ioException) {
            ioException.printStackTrace();
        }

        // se añade el arreglo de bytes al paquete
        paquete.data = trama;
        //Definición de variables de la trama ethernet con mensaje ARP
        ether.frametype = EthernetPacket.ETHERTYPE_ARP;
        ether.src_mac = mac_origen;
        ether.dst_mac = mac_destino;
        //Se añade el paquete con el arreglo de bytes a la trama ethernet
        paquete.datalink = ether;

        //Se envía el paquete
        sender.sendPacket(paquete);
        sender.close();
    }

}
