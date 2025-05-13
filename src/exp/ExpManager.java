package exp;

public class ExpManager {
    private int level;
    private int exp;

    public ExpManager() {
        this.level = 1;
        this.exp = 0;
    }

    public void addExp(int amount) {
        exp += amount;
        System.out.println(amount + " 경험치를 획득했습니다!");

        while (exp >= getExpForNextLevel(level)) {
            exp -= getExpForNextLevel(level);
            level++;
            System.out.println("레벨 업! 현재 레벨: " + level);
        }
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getNextLevelExp() {
        return getExpForNextLevel(level) - exp;
    }

    private int getExpForNextLevel(int level) {
        if (level <= 1) return 100;
        int a = 100, b = 100;
        for (int i = 2; i < level; i++) {
            int temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    public void printStatus() {
        System.out.println("현재 레벨: " + level);
        System.out.println("현재 경험치: " + exp + " / " + getExpForNextLevel(level));
        System.out.println("다음 레벨까지 남은 경험치: " + getNextLevelExp());
    }
}

