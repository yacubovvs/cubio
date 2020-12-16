package ru.cubos.examples.Transporter_GUI;

import jssc.SerialPortException;
import jssc.SerialPortList;
import ru.cubos.connectors.SerialConnector;
import ru.cubos.customViews.ImagePanel;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static ru.cubos.Protocol.PinModes.INPUT_PULLUP;

public class Transporter extends JFrame{
    private JLabel sensor1Result;
    private JLabel sensor2Result;
    private JPanel mainpanel;
    private JComboBox comboBoxComPorts;
    private JButton connectButton;
    private JButton updateButton;
    private JPanel sensor1Image;
    private JPanel sensor2Image;

    private boolean isConnected = false;

    public static void main(String[] args) {
        new Transporter();
    }

    public Transporter(){
        SerialConnector serialConnector = new SerialConnector() {
            @Override
            protected void decode_unknownOperation(String s) {
                super.decode_unknownOperation(s);
            }

            @Override
            protected void onErrorUnknownCommandOnBoard(String value) {
                super.onErrorUnknownCommandOnBoard(value);
            }

            @Override
            protected void onBoardStart() {
                //super.onBoardStart();

                pinMode(8, INPUT_PULLUP);
                pinMode(9, INPUT_PULLUP);

                setPinInterrupt(8);
                setPinInterrupt(9);
            }

            @Override
            protected void digitalInterruptReply(int pin, int value) {
                //super.digitalInterruptReply(pin, value);

                if(pin == 8){
                    if(value==1){
                        sensor1Result.setText("OFF");
                        ((ImagePanel) sensor1Image).setImage(bi_red);
                        Transporter.this.repaint();
                    } else {
                        sensor1Result.setText("ON");
                        ((ImagePanel) sensor1Image).setImage(bi_green);
                        Transporter.this.repaint();
                    }
                }

                if(pin == 9){
                    if(value==1){
                        sensor2Result.setText("OFF");
                        ((ImagePanel) sensor2Image).setImage(bi_red);
                        Transporter.this.repaint();
                    } else {
                        sensor2Result.setText("ON");
                        ((ImagePanel) sensor2Image).setImage(bi_green);
                        Transporter.this.repaint();
                    }
                }
            }
        };

        //lightSwitch.setPort("COM11");
        //lightSwitch.setPort("//dev//cu.usbserial-1420");
        //lightSwitch.setPort("//dev//cu.usbserial-1420");

        setContentPane(mainpanel);


        setSize(600,300);
        setVisible(true);

        updateComboBoxCom_Ports();
        updateConnectStatus();

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(isConnected){
                    try {
                        serialConnector.disconnect();
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }

                    onSerialPortDisonnect();
                }else{

                    serialConnector.setPort(comboBoxComPorts.getSelectedItem().toString());
                    try {
                        serialConnector.connect();
                        onSerialPortConnect();
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                        onSerialPortDisonnect();
                    }

                }

                isConnected =! isConnected;
                updateConnectStatus();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateComboBoxCom_Ports();
            }
        });

        onSerialPortDisonnect();
    }

    void updateComboBoxCom_Ports(){
        String[] portNames = SerialPortList.getPortNames();
        comboBoxComPorts.removeAllItems();
        for(String portName: portNames){
            comboBoxComPorts.addItem(portName);
        }
    }

    void updateConnectStatus(){
        if(isConnected) connectButton.setText("Disconnect");
        else connectButton.setText("Connect");
    }

    void onSerialPortConnect(){
        sensor1Result.setText("OFF");
        sensor2Result.setText("OFF");

        ((ImagePanel) sensor1Image).setImage(bi_red);
        ((ImagePanel) sensor2Image).setImage(bi_red);
    }

    void onSerialPortDisonnect(){
        sensor1Result.setText("-");
        sensor2Result.setText("-");

        ((ImagePanel) sensor1Image).setImage(bi_gray);
        ((ImagePanel) sensor2Image).setImage(bi_gray);
    }


    BufferedImage bi_gray;
    BufferedImage bi_red;
    BufferedImage bi_green;

    private void createUIComponents() {
        File img_gray   = new File("images/Circles/gray.png");
        File img_red    = new File("images/Circles/red.png");
        File img_green  = new File("images/Circles/green.png");


        // TODO: place custom component creation code here
        sensor1Image = new ImagePanel();
        sensor2Image = new ImagePanel();

        try {
            bi_gray   = ImageIO.read(img_gray);
            bi_red    = ImageIO.read(img_red);
            bi_green  = ImageIO.read(img_green);

            ((ImagePanel) sensor1Image).setImage(bi_gray);
            ((ImagePanel) sensor2Image).setImage(bi_gray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
