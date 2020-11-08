package com.antowka.service;

import jssc.SerialPortException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface SerialPortService {

    /**
     * Получаем список доступных com-портов
     *
     * @return
     */
    List<String> portList();

    /**
     * Подключение к com-порту
     *
     * @throws SerialPortException
     * @throws UnsupportedEncodingException
     */
    void connectToPort(String portName, int baudRate) throws SerialPortException, UnsupportedEncodingException;

    /**
     * Отправка текста в com-порт
     *
     * @param text
     */
    boolean sendText(String callsignSrc, String callsignDistination, String text);

    /**
     * Закрыть com-порт
     *
     * @return
     */
    boolean closePort();

    /**
     * Проверка открыт ли порт
     *
     * @return
     */
    boolean isOpenPort();
}
