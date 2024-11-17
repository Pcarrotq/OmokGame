package test.chat.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import test.chat.client.frame.ChatWindowPanel;
import test.chat.server.datacommunication.Message;

public class ClientSocket {
	Socket socket;
	private boolean isConnected = false; // 연결 상태 플래그

	public void startClient() {
	    Thread thread = new Thread(() -> {
	        try {
	            socket = new Socket();
	            socket.connect(new InetSocketAddress("localhost", 5000));
	            System.out.println("연결 요청 성공");
	            System.out.println("Socket 상태: " + socket); // 디버깅용 출력
	        } catch (IOException e) {
	            System.out.println("서버 연결 실패: " + e.getMessage());
	            socket = null;
	            System.out.println("Socket 상태: " + socket); // 연결 실패 시 상태 확인
	        }
	    });
	    thread.start();
	}

	public void stopClient() {
	    try {
	    	if (socket != null && !socket.isClosed()) {
	    		socket.close();
	    	}
	    } catch (IOException e) {
	    }
	}

	// 서버에서 보낸 데이터를 받는 역할
	public void receive() {
	    while (true) {
	    	// 버퍼 생성
	    	byte[] recvBuffer = new byte[1024];
	      
		    // 서버로부터 받기 위한 입력 스트림 뚫음
		    InputStream inputStream;
	      
		    try {
		    	inputStream = socket.getInputStream();
		    	int readByteCount = inputStream.read(recvBuffer);
		    	if (readByteCount == -1) {
		    		throw new IOException();
		    	}
		    	Message message = toMessage(recvBuffer, Message.class);
		    	ChatWindowPanel.displayComment(message);
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
	    }
	}

	private Message toMessage(byte[] recvBuffer, Class<Message> class1) {
		Object obj = null;
	    
	    try {
	    	ByteArrayInputStream bis = new ByteArrayInputStream(recvBuffer);
	    	ObjectInputStream ois = new ObjectInputStream(bis);
	    	obj = ois.readObject();
	    } catch (Exception e) {
	    }
	    
	    return class1.cast(obj);
	}

	  // 서버로 메시지를 보내는 역할
	public void send(Message messageInfo) {
		Thread thread = new Thread(()->{
	    	// 객체를 byte array로 변환
			byte[] bytes = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream(); // 바이트 배열에 데이터를 입출력하는데 사용되는 스트림.
	                                                               // 데이터를 임시로 바이트 배열에 담아서 변환 등 작업 사용

			try {
				ObjectOutputStream oos = new ObjectOutputStream(bos); // 객체를 직렬화.
		        oos.writeObject(messageInfo); // 객체를 직렬화하기 위해 메소드 사용
		        oos.flush(); // 버퍼에 잔류하는 모든 바이트 출력
		        oos.close();
		        bos.close();
		        bytes = bos.toByteArray(); // byteArray로 변환
			} catch (IOException e) {
			}
	      
			// message객체를 byte로 변환 후 소켓을 통해 보냄
			try {
		      	byte[] data = bytes;
		        OutputStream outputStream = socket.getOutputStream(); // 출력 스트림 얻기.
		        outputStream.write(data);
		        outputStream.flush();
		        System.out.println("서버로 보내기 완료!");
			} catch (IOException e) {
		      	System.out.println("서버로 통신 안됨");
		        e.printStackTrace();
			}
	    });
	    
	    thread.start();
	}
	
	public synchronized boolean isConnected() {
	    return socket != null && socket.isConnected();
	}
	
	public boolean getConnectionStatus() {
	    return isConnected;
	}
	
	public void logSocketStatus() {
	    System.out.println("Socket 상태: " + socket);
	}

	
	public void sendMessage(Message message) {
	    if (socket == null || socket.isClosed()) {
	        System.out.println("소켓이 초기화되지 않았거나 연결이 닫혔습니다.");
	        return;
	    }
	    try {
	        OutputStream out = socket.getOutputStream();
	        ObjectOutputStream oos = new ObjectOutputStream(out);
	        oos.writeObject(message);
	        System.out.println("메시지 전송 성공: " + message);
	    } catch (IOException e) {
	        System.out.println("메시지 전송 실패: " + e.getMessage());
	    }
	}
}