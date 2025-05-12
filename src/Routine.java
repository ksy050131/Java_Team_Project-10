public class Routine {
    private boolean isCompleted;
    private String content;

    public Routine() {
        isCompleted = false;
        content = "";
    }

    public Routine(String content) {
        this();
        this.content = content;
    }

    public void complete() {
        isCompleted = true;
    }

    public void uncomplete() {
        isCompleted = false;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public String readContent() {
        return this.content;
    }
}
