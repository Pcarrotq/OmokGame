package omok.chat;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.*;

public class ChatInventory {
    public ChatInventory() {
        // 메인 프레임 생성
        JFrame frame = new JFrame("대화");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);

        // 레이아웃 설정
        frame.setLayout(new BorderLayout());

        // 테이블 모델 생성
        DefaultTableModel model = new DefaultTableModel(new Object[]{"닉네임", "내용", ""}, 0);
        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 모든 셀은 수정 불가능
            }
        };

        // 테이블 설정: 그리드 숨기기, 행 높이 조정, 배경색 설정
        table.setShowGrid(false);
        table.setRowHeight(40);
        table.setBackground(Color.WHITE);
        table.setOpaque(true);

        // 셀 간 경계선 제거
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder());
        table.setDefaultRenderer(Object.class, cellRenderer);

        // 헤더 설정
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBackground(Color.LIGHT_GRAY);
                label.setForeground(Color.BLACK);
                label.setBorder(BorderFactory.createEmptyBorder());
                return label;
            }
        });

        // 열 너비 설정
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);

        // 알림 열에 커스텀 렌더러 추가
        table.getColumnModel().getColumn(2).setCellRenderer(new CircleRenderer());

        // 데이터 추가
        model.addRow(new Object[]{"닉네임", "내용", 1});
        model.addRow(new Object[]{"닉네임", "내용", 3});
        model.addRow(new Object[]{"닉네임", "내용", 2});
        model.addRow(new Object[]{"닉네임", "내용", 0});

        // 스크롤 패널 설정
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // 스크롤 가능한 테이블 추가
        frame.add(scrollPane, BorderLayout.CENTER);

        // 닫기 버튼 추가
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // 아래 패널 생성
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> frame.dispose()); // 닫기 버튼 클릭 시 창 닫기
        bottomPanel.add(closeButton);

        // 닫기 버튼 패널 추가
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // 프레임 표시
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new ChatInventory();
    }
}

// 동그라미 알림 렌더러 클래스
class CircleRenderer extends JPanel implements TableCellRenderer {

    private int notificationCount;

    public CircleRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Integer) {
            notificationCount = (Integer) value;
        } else {
            notificationCount = 0;
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(Color.WHITE);
        }

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (notificationCount > 0) {
            g2d.setColor(Color.BLACK);
            int diameter = Math.min(getWidth(), getHeight()) - 15;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;
            g2d.drawOval(x, y, diameter, diameter);

            g2d.setColor(Color.BLACK);
            String text = String.valueOf(notificationCount);
            FontMetrics metrics = g2d.getFontMetrics();
            int textX = getWidth() / 2 - metrics.stringWidth(text) / 2;
            int textY = getHeight() / 2 + metrics.getAscent() / 2 - 2;
            g2d.drawString(text, textX, textY);
        }
    }
}
