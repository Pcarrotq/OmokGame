package omok.chat.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientGuiMain {
    public static void main(String[] args) {
        try {
            // 로컬 IP 주소를 가져옴
            InetAddress ia = InetAddress.getLocalHost();
            String ip_str = ia.toString();
            String ip = ip_str.substring(ip_str.indexOf("/") + 1);

            // 사용자 이름 입력
            Scanner scanner = new Scanner(System.in);
            System.out.print("사용자 이름을 입력하세요: ");
            String username = scanner.nextLine();

            // 클라이언트 GUI 실행
            // new ClientGui(ip, 5420, username);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}