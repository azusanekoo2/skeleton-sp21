package gitlet;

import java.io.File;
import java.io.Serializable;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File STAGE_ADD_FILE = join(GITLET_DIR, "stage_add");
    public static final File STAGE_REMOVE_FILE = join(GITLET_DIR, "stage_remove");
    /* TODO: fill in the rest of this class. */
    public static String getHeadCommitId() {
        String branchName = readContentsAsString(HEAD_FILE);
        File branchFile = join(BRANCHES_DIR, branchName);
        String commitId = readContentsAsString(branchFile);
        return commitId;
    }
    public static Commit getHeadCommit() {
        String commitId = getHeadCommitId();
        File commitFile = join(COMMITS_DIR, commitId);
        return readObject(commitFile, Commit.class);
    }
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        writeObject(STAGE_ADD_FILE, new HashMap<String, String>());
        writeObject(STAGE_REMOVE_FILE, new HashSet<String>());
        Commit initial_commit = new Commit("initial commit", new Date(0), null, new HashMap<>());
        String commitId = sha1(serialize(initial_commit));
        File commitFile = join(COMMITS_DIR, commitId);
        writeObject(commitFile, initial_commit);
        File masterBranch = join(BRANCHES_DIR, "master");
        writeContents(masterBranch, commitId);
        writeContents(HEAD_FILE, "master");
    }

    public static void add(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        byte[] content = readContents(file);
        String blobId = sha1(content);
        Commit headCommit = getHeadCommit();
        String blobId2 = headCommit.getBlobId(fileName);
        HashMap<String, String> stageAdd = readObject(STAGE_ADD_FILE, HashMap.class);
        HashSet<String> stageRemove = readObject(STAGE_REMOVE_FILE, HashSet.class);
        if (blobId.equals(blobId2)) {
            stageAdd.remove(fileName);
            stageRemove.remove(fileName);
            writeObject(STAGE_ADD_FILE, stageAdd);
            writeObject(STAGE_REMOVE_FILE, stageRemove);
            return;
        }
        File blobFile = join(BLOBS_DIR, blobId);
        writeContents(blobFile, content);
        stageAdd.put(fileName, blobId);
        stageRemove.remove(fileName);
        writeObject(STAGE_ADD_FILE, stageAdd);
        writeObject(STAGE_REMOVE_FILE, stageRemove);
    }

    public static void rm(String fileName) {
        File file = join(CWD, fileName);
        HashMap<String, String> stageAdd = readObject(STAGE_ADD_FILE, HashMap.class);
        HashSet<String> stageRemove = readObject(STAGE_REMOVE_FILE, HashSet.class);
        boolean bool = false;
        if (stageAdd.containsKey(fileName)) {
            stageAdd.remove(fileName);
            bool = true;
        }
        Commit headCommit = getHeadCommit();
        if (headCommit.getBlobId(fileName) != null) {
            stageRemove.add(fileName);
            restrictedDelete(file);
            bool = true;
        }
        if (bool) {
            writeObject(STAGE_ADD_FILE, stageAdd);
            writeObject(STAGE_REMOVE_FILE, stageRemove);
            return;
        }
        System.out.println("No reason to remove the file.");
    }

    public static void commit(String message) {
        if (message.trim().isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        HashMap<String, String> stageAdd = readObject(STAGE_ADD_FILE, HashMap.class);
        HashSet<String> stageRemove = readObject(STAGE_REMOVE_FILE, HashSet.class);
        if (stageAdd.isEmpty() && stageRemove.isEmpty()) {
             System.out.println("No changes added to the commit.");
             return;
        }
        Commit headCommit = getHeadCommit();
        HashMap<String, String> newTrackedFiles = new HashMap<>(headCommit.getTrackedFiles());
        for (String fileName : stageAdd.keySet()) {
            String blobId = stageAdd.get(fileName);
            newTrackedFiles.put(fileName, blobId);
        }
        for (String fileName : stageRemove) {
            newTrackedFiles.remove(fileName);
        }
        Commit newCommit = new Commit(message, new Date(), getHeadCommitId(), newTrackedFiles);
        String newCommitId = sha1(serialize(newCommit));
        File commitFile = join(COMMITS_DIR, newCommitId);
        writeObject(commitFile, newCommit);
        String branchName = readContentsAsString(HEAD_FILE);
        File branchFile = join(BRANCHES_DIR, branchName);
        writeContents(branchFile, newCommitId);
        stageAdd.clear();
        stageRemove.clear();
        writeObject(STAGE_REMOVE_FILE, stageRemove);
        writeObject(STAGE_ADD_FILE, stageAdd);
    }

    public static void log() {
        printLogChain(getHeadCommitId());
    }

    private static void printOneCommit(String commitId) {
        File commitFile = join(COMMITS_DIR, commitId);
        Commit currentCommit = readObject(commitFile, Commit.class);
        Date timeStamp = currentCommit.getTimestamp();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        String formattedDate = formatter.format(timeStamp);
        System.out.println("===");
        System.out.println("commit " + commitId);
        System.out.println("Date: " + formattedDate);
        System.out.println(currentCommit.getMessage());
        System.out.println();
    }

    private static void printLogChain(String commitId) {
        Commit currentCommit = readObject(join(COMMITS_DIR, commitId), Commit.class);
        String parentId = currentCommit.getParent();
        if (parentId == null) {
            return;
        }
        printLogChain(parentId);
    }

    public static void globalLog() {
        List<String> fileList = plainFilenamesIn(COMMITS_DIR);
        for (String commitId : fileList) {
            printOneCommit(commitId);
        }
    }

    public static void find(String targetMessage) {
        List<String> fileList = plainFilenamesIn(COMMITS_DIR);
        boolean found = false;
        for (String commitId : fileList) {
            Commit currentCommit = readObject(join(COMMITS_DIR, commitId), Commit.class);
            if (currentCommit.getMessage().equals(targetMessage)) {
                System.out.println(commitId);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        String currentBranch = readContentsAsString(HEAD_FILE);
        List<String> branchList = plainFilenamesIn(BRANCHES_DIR);
        HashMap<String, String> stageAdd = readObject(STAGE_ADD_FILE, HashMap.class);
        HashSet<String> stageRemove = readObject(STAGE_REMOVE_FILE, HashSet.class);
        System.out.println("=== Branches ===");
        for (String branch : branchList) {
            if (branch.equals(currentBranch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        List<String> stagedFiles = new ArrayList<>(stageAdd.keySet());
        Collections.sort(stagedFiles);
        for (String fileName : stagedFiles) {
            System.out.println(fileName);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        List<String> removedFiles = new ArrayList<>(stageRemove);
        Collections.sort(removedFiles);
        for (String fileName : removedFiles) {
            System.out.println(fileName);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void checkoutFile(String fileName) {
        File file = join(CWD, fileName);
        Commit headCommit = getHeadCommit();
        Map<String, String> trackedFiles = headCommit.getTrackedFiles();
        String blobId = trackedFiles.get(fileName);
        if (blobId == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File blobFile = join(BLOBS_DIR, blobId);
        byte[] content = readContents(blobFile);
        writeContents(file, content);
    }

    public static void checkoutCommitFile(String commitId, String fileName) {
        File file = join(CWD, fileName);
        File commitFile = join(COMMITS_DIR, commitId);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit commit = readObject(commitFile, Commit.class);
        Map<String, String> trackedFiles = commit.getTrackedFiles();
        String blobId = trackedFiles.get(fileName);
        if (blobId == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File blobFile = join(BLOBS_DIR, blobId);
        byte[] content = readContents(blobFile);
        writeContents(file, content);
    }

    public static void checkoutBranch(String branchName) {
        File branchFile = join(BRANCHES_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            return;
        }
        String currentBranchName = readContentsAsString(HEAD_FILE);
        if (currentBranchName.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        String branchCommitId = readContentsAsString(branchFile);
        Commit branchCommit = readObject(join(COMMITS_DIR, branchCommitId), Commit.class);
        Commit currentCommit = getHeadCommit();
        Map<String, String> targetTrackedFiles = branchCommit.getTrackedFiles();
        Map<String, String> currentTrackedFiles = currentCommit.getTrackedFiles();
        HashMap<String, String> stageAdd = readObject(STAGE_ADD_FILE, HashMap.class);
        HashSet<String> stageRemove = readObject(STAGE_REMOVE_FILE, HashSet.class);
        for (String file : targetTrackedFiles.keySet()) {
            if ((join(CWD, file).exists())
                    && !currentTrackedFiles.containsKey(file)
                    && !stageAdd.containsKey(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        for (String file : targetTrackedFiles.keySet()) {
            checkoutCommitFile(branchCommitId, file);
        }
        for (String fileName : currentTrackedFiles.keySet()) {
            if (!targetTrackedFiles.containsKey(fileName)) {
                restrictedDelete(join(CWD, fileName));
            }
        }
        stageRemove.clear();
        stageAdd.clear();
        writeObject(STAGE_ADD_FILE, stageAdd);
        writeObject(STAGE_REMOVE_FILE, stageRemove);
        writeContents(HEAD_FILE, branchName);
    }
}
