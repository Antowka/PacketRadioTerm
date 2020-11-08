package com.antowka.controller;

import com.antowka.Resources;
import com.antowka.service.SerialPortService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    public ComboBox<String> portList;

    @FXML
    public Button connectBtn;

    @FXML
    public TextField callsignTxt;

    @FXML
    public TextArea sendTxt;

    @FXML
    public TextArea dialogTxt;

    @FXML
    public Button sendBtn;

    private String selectedPortName = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializePortList();
        initializeConnectBtn();
        initializeSendBtn();
    }

    private void initializePortList() {
        List<String> portNames = Resources.getSerialPortService().portList();
        portList.setItems(FXCollections.observableList(portNames));
        portList.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println(selectedPortName + " -> " + newVal);
            selectedPortName = newVal;
        });
    }

    private void initializeConnectBtn() {
        connectBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            SerialPortService serialPortService = Resources.getSerialPortService();
            if (selectedPortName != null && !serialPortService.isOpenPort()) {
                try {
                    serialPortService.connectToPort(selectedPortName, SerialPort.BAUDRATE_19200);
                    connectBtn.setText("Connected");
                } catch (SerialPortException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    connectBtn.setText("Connect");
                }
            } else {
                serialPortService.closePort();
                connectBtn.setText("Connect");
            }
        });
    }

    private void initializeSendBtn() {
        sendBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            SerialPortService serialPortService = Resources.getSerialPortService();
            if (!serialPortService.isOpenPort()) return;
            String sendText = callsignTxt.getText() + " > " + sendTxt.getText();
            if (serialPortService.sendText(callsignTxt.getText(), "ALL",
                    sendTxt.getText())) {
                dialogTxt.appendText(sendText + "\n\r");
                sendTxt.clear();
            }
        });
    }
}
