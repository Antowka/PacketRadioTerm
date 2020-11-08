package com.antowka.service;

import com.antowka.Constants;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SerialPortServiceImpl implements SerialPortService {

    private static jssc.SerialPort serialPort;

    private static String portName;

    private static int baudRate;

    /**
     * Получаем список доступных com-портов
     *
     * @return
     */
    public List<String> portList() {
        return Arrays
                .stream(SerialPortList.getPortNames())
                .filter(portName -> portName.contains("COM"))
                .collect(Collectors.toList());

    }

    public void connectToPort(String portName, int baudRate) throws SerialPortException {
        SerialPortServiceImpl.portName = portName;
        SerialPortServiceImpl.baudRate = baudRate;
        connect();

    }

    public boolean closePort() {
        if (serialPort != null && serialPort.isOpened()) {
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }

        return serialPort == null || !serialPort.isOpened();
    }

    public boolean isOpenPort() {
        return serialPort != null && serialPort.isOpened();
    }

    public boolean sendText(String callsignSrc, String callsignDistination, String text){

        try {
            if (!connect()) return false;
            byte[] frame = buildFrame(callsignSrc, callsignDistination, text);
            serialPort.writeInt(Constants.FEND);
            serialPort.writeInt(Constants.CMD_DATA);
            serialPort.writeBytes(frame);
            serialPort.writeInt(Constants.FEND);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean connect() throws SerialPortException {
        if (serialPort != null && serialPort.isOpened()) {
            return true;
        }

        if ((serialPort == null || !serialPort.isOpened()) && portName == null) {
            return false;
        }

        serialPort = new jssc.SerialPort(SerialPortServiceImpl.portName);
        serialPort.openPort();
        serialPort.setParams(SerialPortServiceImpl.baudRate, jssc.SerialPort.DATABITS_8, jssc.SerialPort.STOPBITS_1, jssc.SerialPort.PARITY_NONE);
        return serialPort.isOpened();
    }

    private byte[] buildFrame(String callsignSrc, String callsignDistination, String msg) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //Получатель
        byte[] dest = buildCallsign(callsignDistination + "*");
        baos.write(dest, 0, dest.length);

        //Отправитель
        byte[] src = buildCallsign(callsignSrc);
        baos.write(src, 0, src.length);

        //Сервер
        byte[] d = buildCallsign("TCPIP*");
        d[6] |= 1;
        baos.write(d, 0, 7);

        // control: UI-frame, poll-bit set
        baos.write(0x03);
        // pid: 0xF0 - no layer 3 protocol
        baos.write(0xF0);
        // write content
        byte[] content = (":" + msg).getBytes();
        baos.write(content, 0, content.length);
        return baos.toByteArray();
    }

    private byte[] buildCallsign(String callsign) {
        byte[] callbytes = callsign.getBytes();
        byte[] ax25 = new byte[7];
        // shift " " by one
        java.util.Arrays.fill(ax25, (byte)0x40);
        if (callbytes.length > 6)
            throw new IllegalArgumentException("Callsign " + callsign + " is too long for AX.25!");
        for (int i = 0; i < callbytes.length; i++) {
            ax25[i] = (byte)(callbytes[i] << 1);
        }
        int ssidval = 0;
        ax25[6] = (byte) (0x60 | ((ssidval*2) & 0x1e));
        return ax25;
    }
}
