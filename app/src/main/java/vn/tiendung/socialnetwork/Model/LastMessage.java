package vn.tiendung.socialnetwork.Model;

public class LastMessage {
    private String sender_id;
    private String content;
    private String timestamp;
    private String message_type;
    private String Time;

    public LastMessage(String sender_id, String content, String timestamp, String message_type, String time) {
        this.sender_id = sender_id;
        this.content = content;
        this.timestamp = timestamp;
        this.message_type = message_type;
        Time = time;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessage_type() {
        return message_type;
    }

    public String getTime() {
        return Time;
    }
}
