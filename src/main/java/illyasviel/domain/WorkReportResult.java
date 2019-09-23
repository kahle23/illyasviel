package illyasviel.domain;

/**
 * Work report result.
 * @author Kahle
 */
public class WorkReportResult {
    private String sourceName;
    private String sourceAddress;
    private String occurredTime;
    private String targetUser;
    private String content;

    public String getSourceName() {

        return sourceName;
    }

    public void setSourceName(String sourceName) {

        this.sourceName = sourceName;
    }

    public String getSourceAddress() {

        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {

        this.sourceAddress = sourceAddress;
    }

    public String getOccurredTime() {

        return occurredTime;
    }

    public void setOccurredTime(String occurredTime) {

        this.occurredTime = occurredTime;
    }

    public String getTargetUser() {

        return targetUser;
    }

    public void setTargetUser(String targetUser) {

        this.targetUser = targetUser;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

}
