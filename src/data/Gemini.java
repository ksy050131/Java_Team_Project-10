package data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Gemini {
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String MODEL_NAME = "gemini-1.5-flash-latest";

    public static int getDif(String routine) {
        String prompt = routine + "이 루틴에 대해서 난이도는 1~5의 범위중에서 골라서 응답으로 숫자 하나만 보내줘";

        try {
            String response = callGeminiApi(prompt).trim();
            int difficulty = Integer.parseInt(response);
            System.out.println("난이도 (1~5): " + difficulty);
            return difficulty;
        } catch (NumberFormatException e) {
            System.err.println("숫자로 파싱할 수 없습니다. 응답: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return 3; // 기본값
    }

    private static String callGeminiApi(String prompt) throws Exception {
        String urlString = "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL_NAME + ":generateContent?key=" + API_KEY;

        // 수정: URI 사용
        URL url = new URI(urlString).toURL();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setDoOutput(true);

        String jsonInput = String.format("{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}", prompt);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        (responseCode == HttpURLConnection.HTTP_OK
                                ? conn.getInputStream()
                                : conn.getErrorStream()), StandardCharsets.UTF_8))) {

            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String raw = response.toString();
            Pattern p = Pattern.compile("[1-5]");
            java.util.regex.Matcher m = p.matcher(raw);

            if (m.find()) {
                return m.group();
            }
            return raw;
        } else {
            throw new RuntimeException("HTTP 오류 코드 : " + responseCode + ", 응답: " + response.toString());
        }
    }
}