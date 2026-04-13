package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Map;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private String parent;
    private Map<String, String> trackedFiles;
    /* TODO: fill in the rest of this class. */
    public Commit(String message, Date timestamp, String parent, Map<String, String> trackedFiles) {
        this.message = message;
        this.timestamp = timestamp;
        this.parent = parent;
        this.trackedFiles = trackedFiles;
    }
    public String getBlobId(String fileName) {
        return trackedFiles.get(fileName);
    }
    public Map<String, String> getTrackedFiles() {
        return trackedFiles;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public String getMessage() {
        return message;
    }
    public String getParent() {
        return parent;
    }
}
