package com.antowka;

import com.antowka.service.SerialPortService;
import com.antowka.service.SerialPortServiceImpl;

public class Resources {

    private static SerialPortService serialPort = null;

    /**
     * Сервис работы с com-портами
     *
     * @return
     */
    public static SerialPortService getSerialPortService() {

        if (serialPort == null) {
            serialPort = new SerialPortServiceImpl();
        }
        return serialPort;
    }
}
