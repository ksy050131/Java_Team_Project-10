package data;

import com.mongodb.client.*;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

import java.util.*;

/**
 * 자바와 database.json을 연결하는 클래스
 * Database클래스가 database의 데이터를 UserData로 로드
 * 사용자 인증과 같은 특수한 경우는 Database에서 관리
 *
 * MongoDB Atlas 연결 완료
 * 기존 Gson.jar 제거 (database.json 파일이 필요없어짐)
 * mongodb-driver-sync-5.5.0.jar 추가
 *  MongoClient, MongoDatabase, MongoCollection클래스 포함, 데이터베이스 상호작용
 * mongodb-driver-core-5.5.0.jar 추가
 *  MongoDB 드라이버의 핵심 로직 및 네트워크 처리
 * bson-5.5.0.jar 추가
 *  MongoDB에서 사용하는 BSON 포맷을 인코딩/디코딩하는 데 필요한 클래스 제공
 *  Gson처럼 객체 -> Binary JSON으로 변환
 *
 */
//데이터베이스 직접 확인하고 싶으시면 MongoDB가입 후
public class Database {
    private static final String URI = "mongodb+srv://javaproject:hl46K54WXdLo0SMC@java-team-project-datab.rsqz05z.mongodb.net/?retryWrites=true&w=majority&appName=Java-team-project-database";
    private static final String DB_NAME = "myAppDB";
    private static final String COLLECTION_NAME = "users";

    private static MongoCollection<Document> getCollection() {
        MongoClient client = MongoClients.create(URI);
        MongoDatabase database = client.getDatabase(DB_NAME);
        return database.getCollection(COLLECTION_NAME);
    }

    // 모든 사용자 데이터 저장 (전체 갱신)
    public static void saveUserData(List<UserData> users) {
        MongoCollection<Document> col = getCollection();
        col.drop(); // 기존 데이터 삭제
        List<Document> docs = new ArrayList<>();
        for (UserData user : users) {
            docs.add(user.toDocument());
        }
        col.insertMany(docs);
    }

    // 모든 사용자 데이터 로드
    public static List<UserData> loadUserData() {
        MongoCollection<Document> col = getCollection();
        List<UserData> users = new ArrayList<>();
        for (Document doc : col.find()) {
            users.add(UserData.fromDocument(doc));
        }
        return users;
    }

    // 개별 사용자 등록
    public static boolean uploadUserData(UserData userData) {
        MongoCollection<Document> col = getCollection();
        if (col.find(eq("userId", userData.getUserId())).first() != null) {
            System.out.println("이미 존재하는 아이디입니다.");
            return false;
        }
        col.insertOne(userData.toDocument());
        return true;
    }

    // 개별 사용자 업데이트
    public static boolean updateUserData(UserData updatedUser) {
        MongoCollection<Document> col = getCollection();
        Document existing = col.find(eq("userId", updatedUser.getUserId())).first();

        if (existing != null) {
            col.replaceOne(eq("userId", updatedUser.getUserId()), updatedUser.toDocument());
        } else {
            System.out.println("해당 아이디의 사용자를 찾을 수 없습니다.");
            col.insertOne(updatedUser.toDocument());
            System.out.println("사용자 추가 완료");
        }
        return true;
    }

    // userId로 사용자 찾기
    public static UserData findUserDataById(String userId) {
        MongoCollection<Document> col = getCollection();
        Document doc = col.find(eq("userId", userId)).first();
        return doc != null ? UserData.fromDocument(doc) : null;
    }

    // 이름, 전화번호, 생년월일로 아이디 찾기
    public static String findId(String phoneNumber, String birthDate, String name) {
        MongoCollection<Document> col = getCollection();
        Document doc = col.find(
                        eq("phoneNumber", phoneNumber))
                .filter(eq("birthDate", birthDate))
                .filter(eq("username", name))
                .first();
        return doc != null ? doc.getString("userId") : null;
    }

    // 사용자 존재 여부
    public static boolean isUserExists(String userId) {
        return findUserDataById(userId) != null;
    }
}
