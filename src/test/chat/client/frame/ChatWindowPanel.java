package test.chat.client.frame;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;

import test.chat.client.util.ColorSet;
import test.chat.client.util.FileChooser;
import test.chat.controller.Controller;
import test.chat.enums.AlignEnum;
import test.server.connect.Message;

@SuppressWarnings("serial")
public class ChatWindowPanel extends JPanel {
	private String panelName;
	private JTextArea textArea;
	private JButton sendButton;
	private JButton fileButton;
	private JTextPane jtp;
	private StyledDocument document;
	Controller controller;
	private static String userName;

	public ChatWindowPanel(String friendName) {
	    controller = Controller.getInstance();
	    userName = controller.username;
	    if (userName == null || userName.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "사용자 이름이 설정되지 않았습니다. 먼저 로그인하세요.", "경고", JOptionPane.ERROR_MESSAGE);
	        throw new IllegalStateException("사용자 이름이 설정되지 않았습니다.");
	    }
	
	    panelName = friendName;
	    setBackground(ColorSet.talkBackgroundColor);
	    setLayout(null);
	    showFriendInfo(friendName);
	    writeMessageArea();
	    showContentArea();
	
	    fileButton = showFileButton();
	    add(fileButton);
	    fileButton.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent arg0) {
	    		File file = FileChooser.showFile();
	    		textArea.setText(file.toString());
	    	}
	    });
	
	    sendButton = showSendButton();
	    add(sendButton);
	    sendButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            Controller controller = Controller.getInstance();

	            // 메시지 내용 가져오기
	            String messageContent = textArea.getText().trim();
	            if (messageContent.isEmpty()) {
	                JOptionPane.showMessageDialog(null, "메시지를 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
	                return; // 메시지가 비어있으면 전송하지 않음
	            }

	            // 메시지 유형 결정
	            String messageType = messageContent.matches(".*\\.(jpg|png|JPG|PNG)$") ? "file" : "text";

	            // 메시지 객체 생성
	            Message message = new Message(
	                controller.username, 
	                messageContent, 
	                LocalTime.now(), 
	                messageType, 
	                friendName
	            );

	            // 서버로 메시지 전송
	            controller.clientSocket.sendMessage(messageContent); // 수정: sendMessage 사용
	            textArea.setText(""); // 입력창 초기화

	            // 메시지 출력 (자기 화면에 표시)
	            ChatWindowPanel.displayComment(message); // 메시지를 화면에 즉시 반영
	        }
	    });
	}

	public void paint(Graphics g) {
	    super.paint(g);
	    Graphics2D g2 = (Graphics2D) g;
	    g2.draw(new Line2D.Float(0, 81, 400, 81));
	    g2.draw(new Line2D.Float(0, 458, 400, 458));
	}

	private void showFriendInfo(String friendName) {
	    JLabel friendInfoLabel = new JLabel(friendName); // 이미지 대신 이름만 표시
	    friendInfoLabel.setOpaque(true);
	    friendInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
	    friendInfoLabel.setBounds(0, 0, 400, 80);
	    friendInfoLabel.setBackground(Color.WHITE);
	    add(friendInfoLabel);
	}

	private JButton showFileButton() {
	    JButton fileButton = new JButton("파일");
	    fileButton.setBackground(ColorSet.talkBackgroundColor);
	    Border emptyBorder = BorderFactory.createEmptyBorder();
	    fileButton.setBorder(emptyBorder);
	    fileButton.setFocusPainted(false);
	    fileButton.setBounds(0, 460, 60, 40);
	    return fileButton;
	}

	private JButton showSendButton() {
	    JButton sendButton = new JButton("전송");
	    sendButton.setBackground(ColorSet.messageSendButtonColor);
	    sendButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
	    sendButton.setFocusPainted(false);
	    sendButton.setBounds(320, 500, 68, 65);
	    return sendButton;
	}

	private void writeMessageArea() {
	    textArea = new JTextArea(20, 20);
	    JScrollPane scroller = new JScrollPane(textArea);
	    scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    scroller.setBounds(0, 500, 321, 65);
	    add(scroller);
	}

	private void showContentArea() {
	    StyleContext context = new StyleContext();
	    document = new DefaultStyledDocument(context);
	    jtp = new JTextPane(document);
	    jtp.setBackground(ColorSet.talkBackgroundColor);
	    jtp.setEditable(false);
	    JScrollPane scroller = new JScrollPane(jtp);
	    scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    scroller.setBounds(0, 80, 389, 380);
	    add(scroller);
	}

	public static void displayComment(Message message) {
	    if (userName == null) {
	        System.err.println("userName이 초기화되지 않았습니다.");
	        return;
	    }

	    for (ChatWindowPanel chatName : IndexPanel.chatPanelName) {
	        // 오른쪽 출력
	        if (userName.equals(message.getSendUserName()) && chatName.panelName.equals(message.getReceiveFriendName())) {
	            chatName.textPrint(message.getSendTime().format(DateTimeFormatter.ofPattern("aHH:mm")) + "  <" + message.getSendUserName() + ">", AlignEnum.RIGHT);
	            chatName.textPrint(message.getSendComment(), AlignEnum.RIGHT);
	        }

	        // 왼쪽 출력
	        if (chatName.panelName.equals(message.getSendUserName()) && !chatName.panelName.equals(message.getReceiveFriendName())) {
	            chatName.textPrint(message.getSendTime().format(DateTimeFormatter.ofPattern("aHH:mm")) + "  <" + message.getSendUserName() + ">", AlignEnum.LEFT);
	            chatName.textPrint(message.getSendComment(), AlignEnum.LEFT);
	        }
	    }
	}

	private void textPrint(String string, AlignEnum alignEnum) {
	    try {
	    	document = jtp.getStyledDocument();
	    	SimpleAttributeSet alignAttr = new SimpleAttributeSet();
	    	StyleConstants.setAlignment(alignAttr, alignEnum == AlignEnum.RIGHT ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
	    	document.setParagraphAttributes(document.getLength(), document.getLength() + 1, alignAttr, true);
	    	document.insertString(document.getLength(), string + "\n", alignAttr);
	    } catch (BadLocationException e) {
	    	e.printStackTrace();
	    }
	}
	
	public void updateChatWindow(String message) {
	    SwingUtilities.invokeLater(() -> {
	    	textArea.append(message + "\n"); // chatArea는 JTextArea 또는 유사한 컴포넌트
	    });
	}
}