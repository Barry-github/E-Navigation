package cn.ehanghai.route.nav.domain;

public class BackupTime {
    private  String time;
    private  String note;

    public BackupTime(String time, String note) {
        this.time = time;
        this.note = note;
    }

    public BackupTime() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
