// 과학기술정보통신부 우정사업본부_우편번호 정보조회

package test.member;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class ApiExplorer extends JFrame {

    private JTextField searchField, postalCodeField, addressField;
    private JList<String> resultList;
    private DefaultListModel<String> listModel;
    private JButton searchButton, confirmButton, closeButton;
    private SignUp signUp;
    private JFrame parentFrame;

    public ApiExplorer(JFrame parentFrame) {
    	this.parentFrame = parentFrame;
        setTitle("우편번호 찾기");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 패널 (검색창 + 버튼)
        JPanel panel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("검색");
        panel.add(new JLabel("주소 검색어: "));
        panel.add(searchField);
        panel.add(searchButton);
        add(panel, BorderLayout.NORTH);

        // 검색 결과 표시 영역 (JList)
        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(resultList);
        add(scrollPane, BorderLayout.CENTER);

        // 하단 패널 (우편번호, 주소 입력란)
        JPanel bottomPanel = new JPanel(new GridLayout(3, 2));
        postalCodeField = new JTextField();
        addressField = new JTextField();
        postalCodeField.setEditable(false);
        addressField.setEditable(false);

        bottomPanel.add(new JLabel("우편번호: "));
        bottomPanel.add(postalCodeField);
        bottomPanel.add(new JLabel("주소: "));
        bottomPanel.add(addressField);

        confirmButton = new JButton("확인");
        closeButton = new JButton("닫기");
        bottomPanel.add(confirmButton);
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // 검색 버튼 액션 리스너
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText();
                if (!query.isEmpty()) {
                    searchPostalCode(query);
                } else {
                    listModel.clear();
                    JOptionPane.showMessageDialog(null, "검색어를 입력하세요.");
                }
            }
        });

        // 리스트에서 항목 선택 시 우편번호와 주소 입력란에 기록
        resultList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedText = resultList.getSelectedValue();
                if (selectedText != null) {
                    String[] parts = selectedText.split(" - ");
                    String postalCode = parts[0]; // 우편번호
                    String address = parts[1]; // 주소

                    // Update the fields in the form
                    postalCodeField.setText(postalCode);
                    addressField.setText(address);
                }
            }
        });

        // 확인 버튼 클릭 시 창을 닫음
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (resultList.getSelectedValue() != null) {
                    // 선택된 우편번호와 주소를 SignUp 폼에 업데이트
                    String postalCode = postalCodeField.getText();
                    String address = addressField.getText();

                    // 부모가 SignUp인지 확인하고 주소 업데이트
                    if (parentFrame instanceof SignUp) {
                        SignUp signUp = (SignUp) parentFrame;
                        signUp.updateAddressFields(postalCode, address);  // 부모창의 메서드를 호출
                    }
                    dispose(); // 창 닫기
                } else {
                    JOptionPane.showMessageDialog(null, "주소를 선택해 주세요.");
                }
            }
        });

        // 닫기 버튼 클릭 시 창을 닫음
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 창 닫기
            }
        });
    }

    // 우편번호 API 호출 및 결과 출력
    private void searchPostalCode(String query) {
        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll");
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=Z9jz5i%2B0QLoeuz2QsQpNoCY48Q2AZr18BtLQP6i4hblHKtfsmNcZVR0n1faoLRNB4RKJVbWkgDD%2Bl8gqbIJEvA%3D%3D");
            urlBuilder.append("&" + URLEncoder.encode("srchwrd", "UTF-8") + "=" + URLEncoder.encode(query, "UTF-8")); // 검색어
            urlBuilder.append("&" + URLEncoder.encode("countPerPage", "UTF-8") + "=10"); // 페이지당 출력 개수
            urlBuilder.append("&" + URLEncoder.encode("currentPage", "UTF-8") + "=1"); // 현재 페이지

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            parseAndDisplayResults(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
            listModel.clear();
            JOptionPane.showMessageDialog(null, "API 호출 중 오류가 발생했습니다.");
        }
    }

    // XML 응답을 파싱하여 결과를 JList에 출력
    private void parseAndDisplayResults(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes("UTF-8")));

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("newAddressListAreaCdSearchAll");

            listModel.clear(); // 이전 결과 지우기

            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                String zipNo = element.getElementsByTagName("zipNo").item(0).getTextContent(); // 우편번호
                String roadAddress = element.getElementsByTagName("lnmAdres").item(0).getTextContent(); // 도로명 주소
                listModel.addElement(zipNo + " - " + roadAddress);
            }

            if (nList.getLength() == 0) {
                listModel.addElement("검색 결과가 없습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            listModel.clear();
            JOptionPane.showMessageDialog(null, "결과를 처리하는 중 오류가 발생했습니다.");
        }
    }
    
    // 검색 결과에서 확인 버튼을 눌렀을 때 호출되는 메서드에서 SignUp 인스턴스에 값 전달
    private void onConfirmButtonPressed(String selectedPostalCode, String selectedAddress) {
        // SignUp 클래스의 정적 메서드를 통해 현재 열려있는 인스턴스에 접근
        SignUp signUpInstance = SignUp.getCurrentInstance();
        if (signUpInstance != null) {
            signUpInstance.updateAddressFields(selectedPostalCode, selectedAddress);
        }
        dispose();  // 현재 우편번호 검색창 닫기
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SignUp signUpInstance = new SignUp(); // SignUp 인스턴스 생성
                new ApiExplorer(signUpInstance).setVisible(true); // ApiExplorer에 SignUp 인스턴스 전달
            }
        });
    }
}
