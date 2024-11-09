package test.additional;

import javax.swing.*;

import test.game.GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class CharacterSelectionScreen extends JFrame {
    private JPanel characterPanel;
    private JButton[] characterButtons;
    private Consumer<String> selectionListener;
    private Consumer<String> characterSelectionCallback;

    public CharacterSelectionScreen(Consumer<String> selectionListener) {
        this.selectionListener = selectionListener;
        setTitle("Character Selection");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 중앙에 배치할 패널 설정
        characterPanel = new JPanel();
        characterPanel.setLayout(new GridBagLayout());
        characterPanel.setBackground(Color.LIGHT_GRAY);

        // 캐릭터 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 20, 0)); // 버튼을 가로로 정렬
        buttonPanel.setBackground(Color.LIGHT_GRAY); // 버튼 패널 배경을 동일하게 설정

        characterButtons = new JButton[4];
        for (int i = 0; i < characterButtons.length; i++) {
            characterButtons[i] = createCharacterButton(i);
            buttonPanel.add(characterButtons[i]);
        }

        // 버튼 패널을 중앙에 배치
        characterPanel.add(buttonPanel, new GridBagConstraints());
        add(characterPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createCharacterButton(int index) {
        JButton button = new JButton("Character " + (index + 1));
        button.setPreferredSize(new Dimension(150, 150)); // 버튼을 정사각형으로 설정
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false); // 포커스 보더 제거

        button.setIcon(createCharacterIcon(index));

        button.addActionListener(e -> {
            String selectedCharacter = "Character " + (index + 1);
            if (selectionListener != null) {
                selectionListener.accept(selectedCharacter); // 선택된 캐릭터 정보를 전달
            }

            // 게임 GUI를 열고 캐릭터 정보를 설정
            SwingUtilities.invokeLater(() -> {
                GUI gameGui = new GUI("오목 게임");
                gameGui.setPlayer1Profile(selectedCharacter);
                gameGui.setVisible(true);
            });

            dispose(); // 캐릭터 선택 창 닫기
        });

        return button;
    }

    private Icon createCharacterIcon(int index) {
        int size = 100;
        Image image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (index) {
            case 0:
                g2d.setColor(Color.RED);
                g2d.fillOval(10, 10, size - 20, size - 20);
                break;
            case 1:
                g2d.setColor(Color.BLUE);
                g2d.fillRect(10, 10, size - 20, size - 20);
                break;
            case 2:
                g2d.setColor(Color.GREEN);
                g2d.fillRoundRect(10, 10, size - 20, size - 20, 30, 30);
                break;
            case 3:
                g2d.setColor(Color.ORANGE);
                g2d.fillPolygon(new int[]{size / 2, 10, size - 10}, new int[]{10, size - 10, size - 10}, 3);
                break;
        }

        g2d.dispose();
        return new ImageIcon(image);
    }
    
    private void openCharacterSelection() {
        new CharacterSelectionScreen(selectedCharacter -> {
            SwingUtilities.invokeLater(() -> {
                GUI gameGui = new GUI("오목 게임");
                gameGui.setPlayer1Profile(selectedCharacter);
                gameGui.setVisible(true);
                dispose(); // GameLobbyScreen 닫기
            });
        }).setVisible(true);
    }
    
    private void onCharacterSelected(String character) {
        if (selectionListener != null) {
            selectionListener.accept(character);
            dispose(); // 창 닫기
        }
    }
}