package test.api;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherApp {
    private static final String API_KEY = "0d8f6d74e32882c838851fbd3ecffaf6";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static JTextArea textArea;

    public static void main(String[] args) {
        // 스윙 GUI 생성
        JFrame frame = new JFrame("Weather Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);

        // 날씨 정보를 가져오는 작업을 별도 스레드에서 수행
        SwingUtilities.invokeLater(() -> getWeatherInfo());
    }

    public static void getWeatherInfo() {
        try {
            // 1. 사용자의 IP 기반 위치 정보 가져오기
            String locationUrl = "https://ipinfo.io/json";
            URL locationApiUrl = new URL(locationUrl);
            HttpURLConnection locationConnection = (HttpURLConnection) locationApiUrl.openConnection();
            locationConnection.setRequestMethod("GET");
            locationConnection.setConnectTimeout(5000);
            locationConnection.setReadTimeout(5000);

            int locationStatus = locationConnection.getResponseCode();
            if (locationStatus == 200) {
                BufferedReader locationReader = new BufferedReader(new InputStreamReader(locationConnection.getInputStream()));
                StringBuilder locationResponse = new StringBuilder();
                String locationLine;
                while ((locationLine = locationReader.readLine()) != null) {
                    locationResponse.append(locationLine);
                }
                locationReader.close();

                // JSON 응답에서 위치 정보 파싱 (도시명 추출)
                JsonObject locationObject = JsonParser.parseString(locationResponse.toString()).getAsJsonObject();
                String city = locationObject.get("city").getAsString(); // 사용자의 도시 정보
                appendText("사용자의 위치: " + city);

                // 2. 해당 위치의 날씨 정보 가져오기
                String urlString = BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric";  // 섭씨 사용
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int status = connection.getResponseCode();
                if (status == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // JSON 응답 파싱
                    JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                    double temperature = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
                    String weatherDescription = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

                    appendText("현재 온도: " + temperature + "°C");
                    appendText("날씨 설명: " + weatherDescription);
                } else {
                    appendText("Error: " + status);
                }
                connection.disconnect();
            } else {
                appendText("Error: " + locationStatus);
            }
            locationConnection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            appendText("Exception: " + e.getMessage());
        }
    }

    // 텍스트를 JTextArea에 추가하는 메서드
    private static void appendText(String text) {
        textArea.append(text + "\n");
    }
}
