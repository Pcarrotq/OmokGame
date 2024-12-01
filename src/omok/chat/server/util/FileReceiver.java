package omok.chat.server.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class FileReceiver extends Thread {
    private ServerSocket fileServerSocket;

    public FileReceiver(int port) throws IOException {
        fileServerSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (true) {
            try (Socket clientSocket = fileServerSocket.accept();
                 InputStream is = clientSocket.getInputStream();
                 DataInputStream dis = new DataInputStream(is)) {

                // 파일 메타데이터 읽기
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();

                // 파일 저장
                File file = new File("received_" + fileName);
                try (FileOutputStream fos = new FileOutputStream(file);
                     BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalRead = 0;

                    while ((bytesRead = dis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                        if (totalRead >= fileSize) break;
                    }
                }

                // 수신한 파일 브로드캐스트 (예: 모든 클라이언트에게 알림)
                System.out.println("파일 수신 완료: " + file.getAbsolutePath());
                // 파일 전송 알림 메시지를 클라이언트들에게 전송 (필요 시 구현)
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}