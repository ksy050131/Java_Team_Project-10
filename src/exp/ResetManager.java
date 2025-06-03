package exp;

import data.UserData;
import title.TitleManager;

public class ResetManager {
    public void resetProgress(UserData user) {
        // 초기화 횟수 증가
        user.setCycle(user.getCycle() + 1);

        // 레벨, 경험치, 필요 경험치 초기화
        user.setLevel(1);
        user.setExp(0);
        user.setNeedExp(100);

        // 루틴 목록 초기화
        user.getRoutines().clear();

        // 회차 칭호 업데이트
        TitleManager.checkCycleTitle(user);

        System.out.printf("\n🔥 진행 현황 초기화 완료 (%d회차)\n", user.getCycle());
    }
}