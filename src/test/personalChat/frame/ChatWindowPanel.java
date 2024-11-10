package test.personalChat.frame;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;

import test.personalChatEnum.AlignEnum;

@SuppressWarnings("serial")
public class ChatWindowPanel extends JPanel {
	private String panelName;
	private JTextArea textArea;
	private JButton sendButton;
	private JButton imgFileButton;
	private JTextPane jtp;
	private StyledDocument document;
	private static String userName;

	public ChatWindowPanel(Icon icon, String friendName) {
		userName = "User";
		
		panelName = friendName;
		// setBackground(Color.LIGHT_GRAY);
		setLayout(null);
		showFriendInfo(icon, friendName);  // Icon으로 변경
		writeMessageArea();
		showContentArea();
		
		imgFileButton = showImgFileButton();
		add(imgFileButton);
		
		sendButton = showSendButton();
		add(sendButton);
	}
	  
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		Line2D lin = new Line2D.Float(0, 81, 400, 81);
		g2.draw(lin);
		
		Graphics2D g3 = (Graphics2D) g;
		Line2D lin2 = new Line2D.Float(0, 458, 400, 458);
		g3.draw(lin2);
	}

	private void showFriendInfo(Icon icon, String friendName) {
		JLabel friendInfolabel = new JLabel(icon);
	    friendInfolabel.setOpaque(true);
	    friendInfolabel.setText(friendName);
	    friendInfolabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
	    friendInfolabel.setBounds(0, 0, 400, 80);
	    friendInfolabel.setBackground(Color.WHITE);
	    add(friendInfolabel);
	}

	private JButton showImgFileButton() {
	    JButton imgFileButton = new JButton();
	    imgFileButton.setBackground(Color.LIGHT_GRAY); // 예시 배경색
	    Border emptyBorder2 = BorderFactory.createEmptyBorder();
	    imgFileButton.setBorder(emptyBorder2);
	    imgFileButton.setFocusPainted(false);
	    imgFileButton.setBounds(0, 460, 60, 40);
	    return imgFileButton;
	}

	private JButton showSendButton() {
	    JButton sendButton = new JButton("전송");
	    sendButton.setBackground(Color.GRAY); // 예시 배경색
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
	    jtp.setBackground(Color.LIGHT_GRAY); // 예시 배경색
	    jtp.setEditable(false);
	    JScrollPane scroller2 = new JScrollPane(jtp);
	    scroller2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scroller2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    scroller2.setBounds(0, 80, 389, 380);
	    add(scroller2);
	}

	public static void displayComment(String message) {
	    for (ChatWindowPanel chatName : IndexPanel.chatPanelName) {
	        // 메시지가 파일 전송인지 텍스트인지 구분
	        boolean isFileMessage = message.endsWith(".jpg") || message.endsWith(".png") || message.endsWith(".JPG") || message.endsWith(".PNG");

	        // 사용자 이름과 타임스탬프가 없으므로 임시 값으로 표시
	        String userDisplayName = "User";  // 예시 사용자 이름
	        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("aHH:mm"));

	        // 오른쪽으로 출력.
	        if (userName.equals(userDisplayName) && chatName.panelName.equals("FriendName")) {
	            chatName.textPrint(timestamp + "  <" + userDisplayName + ">", AlignEnum.RIGHT);
	            if (isFileMessage) {
	                chatName.imgPrint(message);
	            } else {
	                chatName.textPrint(message, AlignEnum.RIGHT);
	            }
	        }

	        // 왼쪽으로 출력.
	        if (chatName.panelName.equals(userDisplayName) && !chatName.panelName.equals("FriendName")) {
	            chatName.textPrint(timestamp + "  <" + userDisplayName + ">", AlignEnum.LEFT);
	            if (isFileMessage) {
	                chatName.imgPrint(message);
	            } else {
	                chatName.textPrint(message, AlignEnum.LEFT);
	            }
	        }
	    }
	}

	private void imgPrint(String sendComment) {
	    // 이미지 경로에서 ImageIcon을 생성하고 크기를 조정
	    ImageIcon imageIcon = new ImageIcon(sendComment);
	    Image imgResize = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);

	    StyledDocument doc = jtp.getStyledDocument();
	    Style style = doc.addStyle("ImageStyle", null);
	    StyleConstants.setIcon(style, new ImageIcon(imgResize));

	    try {
	        doc.insertString(doc.getLength(), "invisible text\n", style);
	    } catch (BadLocationException e) {
	        e.printStackTrace();
	    }
	}

	private void textPrint(String string, AlignEnum alignEnum) {
	    try {
	    	document = jtp.getStyledDocument();
	    	SimpleAttributeSet sortMethod = new SimpleAttributeSet();

	    	if(alignEnum == AlignEnum.RIGHT) {
	    	  StyleConstants.setAlignment(sortMethod, StyleConstants.ALIGN_RIGHT);   
	    	} else if (alignEnum == AlignEnum.LEFT) {
	    	  StyleConstants.setAlignment(sortMethod, StyleConstants.ALIGN_LEFT);  
	    	}
	    	document.setParagraphAttributes(document.getLength(), document.getLength() + 1, sortMethod, true);
	    	document.insertString(document.getLength(), string + "\n", sortMethod);
	    } catch (BadLocationException e) {
	    	e.printStackTrace();
	    }
	}
}