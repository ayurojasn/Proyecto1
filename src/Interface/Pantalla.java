package Interface;

import Control.ICMP;
import Entities.ARP;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;


import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;


public class Pantalla{

    //Elementos a utilizar en la interfaz
    private JPanel panel1;
    private JButton ICMPbutton;
    private JButton ARPbutton;
    private JLabel ICMPlegend;
    private JLabel ARPlegend;
    private JLabel bLabel;
    private JTextField DestinoField;
    private JTextField origenField;
    private JLabel OrigenLabel;
    private JLabel DestinoLabel;
    private JButton EnviarICMP;
    private JLabel TamanoBytes;
    private JTextField bytesEnviar;
    private JLabel MacOrigenLabel;
    private JTextField MacOrigenField;
    private JButton Siguiente;
    private JButton EnviarARP;
    private JButton Volver;
    private JLabel dirRed;
    private JComboBox dirRedes;

    //Inicializar elemntos de la clase ICMP y ARP
    private ICMP icm = new ICMP();
    private ARP ar = new ARP();
    //Device hace referencia al indice que identifica los dispositivos de red que se encuentran
    private int device = 0;



    public JPanel getPanel1() {
        return panel1;
    }

    public Pantalla() {
        panel1.setSize(1080, 720);
        ICMPbutton.setVisible(true);
        ICMPlegend.setVisible(true);
        ARPbutton.setVisible(true);
        ARPlegend.setVisible(true);
        bLabel.setVisible(true);

        TamanoBytes.setVisible(false);
        bytesEnviar.setVisible(false);
        DestinoField.setVisible(false);
        OrigenLabel.setVisible(false);
        DestinoLabel.setVisible(false);
        origenField.setVisible(false);
        EnviarICMP.setVisible(false);
        MacOrigenField.setVisible(false);
        MacOrigenLabel.setVisible(false);
        Siguiente.setVisible(false);
        EnviarARP.setVisible(false);
        Volver.setVisible(false);
        dirRed.setVisible(false);
        dirRedes.setVisible(false);


        //Inicializar los valores de la combobox donde se encuentran los equipos de red disponibles
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                NetworkInterface[] devices = JpcapCaptor.getDeviceList();
                for (int i = 0; i< devices.length; i++){
                    String palabras = devices[i].name;
                    dirRedes.addItem(palabras);

                }
            }
        });


        //Cuando el usuario elige la opcion de ICMP en el menu pricipal se configura la interfaz
        ICMPbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ARPbutton.setVisible(false);
                ICMPbutton.setVisible(false);
                ICMPlegend.setVisible(false);
                ARPlegend.setVisible(false);
                MacOrigenField.setVisible(false);
                MacOrigenLabel.setVisible(false);
                Siguiente.setVisible(false);
                Volver.setVisible(true);
                dirRed.setVisible(true);
                dirRedes.setVisible(true);
                bLabel.setText("Introduzca datos para ICMP");

                DestinoField.setVisible(true);
                OrigenLabel.setVisible(true);
                DestinoLabel.setVisible(true);
                origenField.setVisible(true);
                EnviarICMP.setVisible(true);
                TamanoBytes.setVisible(true);
                bytesEnviar.setVisible(true);
                EnviarARP.setVisible(false);
            }
        });

        //Esto ocurre cuando el usuario quiere configuarar y enviar el mensaje ICMP
        EnviarICMP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se capturan de pantalla las direcciones ip y el numero de bytes de datos que ingresa el usuario
                String origen = String.valueOf(origenField.getText());
                String destino = String.valueOf(DestinoField.getText());
                int numBytes = Integer.parseInt(bytesEnviar.getText());

                //Para enviar paquetes con muchos datos, que se pasan del máximo en permitido en ethernet MTU = 1500
                int res = (int) Math.ceil(numBytes/1472);
                int y = 1472;
                if (numBytes <= 1472) {
                    icm.icmp(origen, destino, numBytes, device);
                }
                else{
                    for (int i = res+1; i>0; i--) {
                        if(i == 1){
                            y = numBytes-(res*y);
                        }
                        icm.icmp(origen, destino, y, device);
                    }
                }
            }
        });


        ARPbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ARPbutton.setVisible(false);
                ICMPbutton.setVisible(false);
                ICMPlegend.setVisible(false);
                ARPlegend.setVisible(false);
                dirRed.setVisible(true);
                dirRedes.setVisible(true);
                bLabel.setText("Introduzca datos para ARP");

                Volver.setVisible(true);
                DestinoField.setVisible(true);
                OrigenLabel.setVisible(true);
                DestinoLabel.setVisible(true);
                origenField.setVisible(true);
                EnviarICMP.setVisible(false);
                TamanoBytes.setVisible(false);
                bytesEnviar.setVisible(false);
                MacOrigenField.setVisible(false);
                MacOrigenLabel.setVisible(false);
                Siguiente.setVisible(true);
                EnviarARP.setVisible(false);

            }
        });

        //Esto ocurre cuando el usario quiere configurar y enviar un mensaje ARP
        Siguiente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ARPbutton.setVisible(false);
                Volver.setVisible(true);
                ICMPbutton.setVisible(false);
                ICMPlegend.setVisible(false);
                ARPlegend.setVisible(false);
                dirRed.setVisible(false);
                dirRedes.setVisible(false);
                bLabel.setText("Introduzca datos para ARP");

                DestinoField.setVisible(false);
                OrigenLabel.setVisible(false);
                DestinoLabel.setVisible(false);
                origenField.setVisible(false);
                EnviarICMP.setVisible(false);
                TamanoBytes.setVisible(false);
                bytesEnviar.setVisible(false);
                MacOrigenField.setVisible(true);
                MacOrigenLabel.setVisible(true);
                Siguiente.setVisible(false);
                EnviarARP.setVisible(true);

                //Se configura broadcast para que pueda preguntar a que máquina pertenece la ip solicitada y pueda proveer la MAC de destino
                byte[] broadcast = new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255};

                //Se capturan por pantalla la ip origen y destino
                byte [] origen = convertir(String.valueOf(origenField.getText()));
                byte [] destino = convertir(String.valueOf(DestinoField.getText()));


                //Se le dan los atributos al objeto arp de la clase manual ARP
                ar.ip_origen = origen;
                ar.ip_destino = destino;
                ar.mac_destino = broadcast;

                //Se capturan los dispositivos de red para poder sugeriri la dirección MAC
                //El usuario está en su derecho de cambiar la mac destino si gusta
                jpcap.NetworkInterface[] devices = JpcapCaptor.getDeviceList();
                //Se pone en la interfaz la dirección MAC
                ar.mac_origen = devices[device].mac_address;
                MacOrigenField.setText(String.format("%02x",devices[device].mac_address[0]) + ":" + String.format("%02x",devices[device].mac_address[1])+
                        ":" + String.format("%02x", devices[device].mac_address[2]) +":"+ String.format("%02x", devices[device].mac_address[3])+ ":" +
                        String.format("%02x", devices[device].mac_address[4])+ ":" +
                        String.format("%02x", devices[device].mac_address[5]));


            }
        });

        //Cuando se oprime el botón enviar en arp
        EnviarARP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se captura la mac, independiemtemente que el usuario la haya modificado o no y se le asigna al objeto de l aclase arp
                ar.mac_origen = dehexa(String.valueOf(MacOrigenField.getText()));

                //Se envian los parámetros necesarios para costruir un arreglo de bytes donde este la trama arp
                byte[] trama = ar.tramaARP (ar.mac_destino, ar.mac_origen, ar.ip_origen, ar.ip_destino);
                ar.encapsular(trama, device);
            }
        });
        Volver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ICMPbutton.setVisible(true);
                ICMPlegend.setVisible(true);
                ARPbutton.setVisible(true);
                ARPlegend.setVisible(true);
                bLabel.setVisible(true);
                dirRed.setVisible(false);
                dirRedes.setVisible(false);

                TamanoBytes.setVisible(false);
                bytesEnviar.setVisible(false);
                DestinoField.setVisible(false);
                OrigenLabel.setVisible(false);
                DestinoLabel.setVisible(false);
                origenField.setVisible(false);
                EnviarICMP.setVisible(false);
                MacOrigenField.setVisible(false);
                MacOrigenLabel.setVisible(false);
                Siguiente.setVisible(false);
                EnviarARP.setVisible(false);
                Volver.setVisible(false);
            }
        });

        //Selecciona el dispositivo de red para tranbajar
        dirRedes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                device = dirRedes.getSelectedIndex();
            }
        });
    }

    //Función que convierte de string a bytes
    public byte[] convertir (String palabra){
        //Separa la ip por puntos
        StringTokenizer tokens=new StringTokenizer(palabra, ".");
        //se cuenta el numero de divisiones que se hace
        int nDatos=tokens.countTokens();
        //se crea el arreglo de bytes de tamaño de ndatos
        byte[] pal = new byte[nDatos];
        int i=0;
        //Se comienza la conversicón
        while(tokens.hasMoreTokens()) {
            String str = tokens.nextToken();
            //el string se castea a entero
            int o = Integer.parseInt(str);
            //el entero se castea a byte y se añade al arreglo
            pal[i] = (byte)o;
            i++;
        }
        return pal;
    }

    //Función que convierte de hexadecimal a bytes
    public byte[] dehexa (String palabra){
        StringTokenizer tokens=new StringTokenizer(palabra, ":");
        int nDatos=tokens.countTokens();
        byte[] pal = new byte[nDatos];
        int i=0;
        while(tokens.hasMoreTokens()) {
            String str = tokens.nextToken();
            //conversión a un arreglo de bytes
            byte[] y = DatatypeConverter.parseHexBinary(str);
            //se añade al arreglo
            pal[i] = y[0];
            i++;
        }
        return pal;
    }
}
