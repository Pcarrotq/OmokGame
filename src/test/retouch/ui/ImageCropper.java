package test.retouch.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageCropper extends JPanel {
    private BufferedImage originalImage;
    private Image scaledImage;  // 창 크기에 맞게 리사이징된 이미지
    private BufferedImage croppedImage;
    private Rectangle cropArea;
    private Point startPoint;
    private Point endPoint;
    private double scale;  // 원본 이미지와 스케일링된 이미지의 비율
    private JLabel croppedImageLabel;  // 잘린 이미지를 표시할 라벨

    public ImageCropper(BufferedImage image) {
        this.originalImage = image;
        this.scaledImage = image;
        this.croppedImage = null;
        this.cropArea = new Rectangle();
        this.startPoint = null;
        this.endPoint = null;
        this.scale = 1.0;  // 기본 스케일 값

        this.croppedImageLabel = new JLabel();  // 잘린 이미지를 표시할 라벨 초기화
        this.croppedImageLabel.setPreferredSize(new Dimension(200, 200));  // 잘린 이미지 영역 크기 고정

        setLayout(new GridBagLayout());  // GridBagLayout으로 고정된 위치 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // 패널 간 간격 설정

        // 원본 이미지가 들어갈 패널
        JPanel originalImagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (scaledImage != null) {
                    // 리사이징된 이미지를 중앙에 표시
                    int x = (getWidth() - scaledImage.getWidth(this)) / 2;
                    int y = (getHeight() - scaledImage.getHeight(this)) / 2;
                    g.drawImage(scaledImage, x, y, this);

                    // 선택된 자를 영역을 시각적으로 보여줌
                    if (cropArea != null) {
                        g.setColor(Color.RED);
                        ((Graphics2D) g).setStroke(new BasicStroke(2));
                        g.drawRect(cropArea.x, cropArea.y, cropArea.width, cropArea.height);
                    }
                }
            }
        };
        originalImagePanel.setPreferredSize(new Dimension(500, 500));  // 원본 이미지 패널 크기 고정
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(originalImagePanel, gbc);  // 원본 이미지 패널을 왼쪽에 고정

        // 잘린 이미지를 오른쪽에 고정
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(croppedImageLabel, gbc);

        // 창 크기에 맞게 이미지 리사이징
        resizeImageToFit();

        // 창 크기 변경 시 이미지를 다시 리사이징
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeImageToFit();
                originalImagePanel.repaint();
            }
        });

        // 마우스 이벤트 처리
        originalImagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endPoint = e.getPoint();
                cropImage();
                originalImagePanel.repaint();
            }
        });

        originalImagePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endPoint = e.getPoint();
                updateCropArea();
                originalImagePanel.repaint();
            }
        });
    }

    // 창 크기에 맞게 이미지를 리사이징하는 메서드
    private void resizeImageToFit() {
        int panelWidth = 500;  // 고정된 크기
        int panelHeight = 500;  // 고정된 크기
        if (originalImage != null && panelWidth > 0 && panelHeight > 0) {
            // 창 크기에 맞게 비율을 유지하며 이미지 크기 조정
            double widthScale = (double) panelWidth / originalImage.getWidth();
            double heightScale = (double) panelHeight / originalImage.getHeight();
            scale = Math.min(widthScale, heightScale); // 더 작은 비율로 크기 조정

            int newWidth = (int) (originalImage.getWidth() * scale);
            int newHeight = (int) (originalImage.getHeight() * scale);

            scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        }
    }

    // 자르기 영역 업데이트
    private void updateCropArea() {
        if (startPoint != null && endPoint != null) {
            cropArea.setBounds(
                Math.min(startPoint.x, endPoint.x),
                Math.min(startPoint.y, endPoint.y),
                Math.abs(startPoint.x - endPoint.x),
                Math.abs(startPoint.y - endPoint.y)
            );
        }
    }

    // 이미지를 선택된 영역으로 자르기
    private void cropImage() {
        if (cropArea.width > 0 && cropArea.height > 0) {
            // 실제 자르기 위한 원본 이미지의 좌표로 변환
            int x = (int) (cropArea.x / scale);
            int y = (int) (cropArea.y / scale);
            int width = (int) (cropArea.width / scale);
            int height = (int) (cropArea.height / scale);

            // 이미지 경계를 넘어가지 않도록 자를 영역의 크기 제한
            x = Math.max(0, x);
            y = Math.max(0, y);
            width = Math.min(width, originalImage.getWidth() - x);
            height = Math.min(height, originalImage.getHeight() - y);

            try {
                // 잘라낼 영역이 이미지 경계 내에 있는지 확인
                croppedImage = originalImage.getSubimage(x, y, width, height);

                // 잘린 이미지를 라벨에 아이콘으로 설정하여 표시
                ImageIcon croppedIcon = new ImageIcon(croppedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH));
                croppedImageLabel.setIcon(croppedIcon);
            } catch (RasterFormatException e) {
                System.out.println("잘라낼 영역이 유효하지 않습니다: " + e.getMessage());
            }
        }
    }
    
    public BufferedImage getCroppedImage() {
        return croppedImage;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Image Cropper Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // 파일 선택기 사용하여 이미지 로드
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("이미지 파일 선택");

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File imageFile = fileChooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(imageFile);
                ImageCropper imageCropper = new ImageCropper(image);

                frame.add(imageCropper);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
