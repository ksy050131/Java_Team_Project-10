package data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Gemini {

    // api가 필요합니다 api발급받는 곳: https://aistudio.google.com/app/apikey
    private static final String API_KEY = "API";

    // 모델명
    private static final String MODEL_NAME = "gemini-1.5-flash-latest";

    /**
     * 루틴 설명을 받아 난이도(1~5)를 출력하는 메서드
     */
    public static int getDif(String routine) {
        String prompt = routine + "이 루틴에 대해서 난이도는 1~5의 범위중에서 골라서 응답으로 숫자 하나만 보내줘"
                + "예시:  " +
                "- 걷기 10분 -> 1  " +
                "- 매일 30분 조깅 -> 2  " +
                "- 하루 2시간 강도 높은 운동 -> 4"+
                "하루 10분 공부 -> 1"+
                "하루 1시간 집중 공부 -> 3"+
                "매일 4시간 복잡한 수학 공부 -> 5"+
                "1분간 암기 -> 1"+
                "1000시간 독서 -> 5";

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
        return 0;
    }

    /**
     * Gemini API 호출 함수
     */
    private static String callGeminiApi(String prompt) throws Exception {
        String urlString = "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL_NAME + ":generateContent?key=" + API_KEY;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setDoOutput(true);

        // 요청 JSON 수동 작성
        String jsonInput = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ]
                }
                """.formatted(prompt);

        // 요청 전송
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 응답 처리
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
            //System.out.println(raw);  //확인용도

            // 정규식으로 1~5 중 첫번째 숫자 찾기
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("[1-5]");
            java.util.regex.Matcher m = p.matcher(raw);

            if (m.find()) {
                return m.group(); // 첫 번째 매칭 숫자 리턴
            } else {
                // 찾지 못하면 원본 반환하거나 에러 처리
                return raw;
            }
        } else {
            throw new RuntimeException("HTTP 오류 코드 : " + responseCode + ", 응답: " + response.toString());
        }
    }
}
