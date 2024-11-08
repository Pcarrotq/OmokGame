package test.gameServer;

import javax.swing.JTextArea;

public class Logger {
    private StringBuilder log; // 로그를 저장할 StringBuilder

    public Logger() {
        log = new StringBuilder();
    }

    // 로그에 메시지를 추가하는 메소드
    public void log(String message) {
        log.append(message).append("\n");
    }

    // 현재 로그 내용을 가져오는 메소드
    public String getLog() {
        return log.toString();
    }

    // GUI 요소에 로그를 표시하는 메소드
    public void displayLog(JTextArea textArea) {
        textArea.setText(getLog()); // JTextArea에 로그 표시
    }
}