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
    @SuppressWarnings("unchecked")
    private static HashMap<String, String> readStageAdd() {
        return (HashMap<String, String>) readObject(STAGE_ADD_FILE, HashMap.class);
    }

    @SuppressWarnings("unchecked")
    private static HashSet<String> readStageRemove() {
        return (HashSet<String>) readObject(STAGE_REMOVE_FILE, HashSet.class);
    }

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
        Commit initial_commit = new Commit("initial commit", new Date(0), null, null, new HashMap<>());
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
        HashMap<String, String> stageAdd = readStageAdd();
        HashSet<String> stageRemove = readStageRemove();
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
        HashMap<String, String> stageAdd = readStageAdd();
        HashSet<String> stageRemove = readStageRemove();
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
        commitInternal(message, null);
    }

    public static void mergeCommit(String message, String secondParent) {
        commitInternal(message, secondParent);
    }

    private static void commitInternal(String message, String secondParent) {
        if (message.trim().isEmpty()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        HashMap<String, String> stageAdd = readStageAdd();
        HashSet<String> stageRemove = readStageRemove();
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
        Commit newCommit = new Commit(message, new Date(), getHeadCommitId(), secondParent, newTrackedFiles);
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
        if (currentCommit.getSecondParent() != null) {
            System.out.println("Merge: " + currentCommit.getParent().substring(0, 7) + " " + currentCommit.getSecondParent().substring(0, 7));
        }
        System.out.println("Date: " + formattedDate);
        System.out.println(currentCommit.getMessage());
        System.out.println();
    }

    private static void printLogChain(String commitId) {
        printOneCommit(commitId);
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
        HashMap<String, String> stageAdd = readStageAdd();
        HashSet<String> stageRemove = readStageRemove();
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
        String fullCommitId = resolveCommitId(commitId);
        if (fullCommitId == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        File commitFile = join(COMMITS_DIR, fullCommitId);
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

    private static String resolveCommitId(String commitId) {
        File commitFile = join(COMMITS_DIR, commitId);
        if (commitFile.exists()) {
            return commitId;
        }
        List<String> commitIds = plainFilenamesIn(COMMITS_DIR);
        for (String id : commitIds) {
            if (id.startsWith(commitId)) {
                return id;
            }
        }
        return null;
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
        if (!checkoutCommitSnapshot(branchCommitId, branchCommit)) {
            return;
        }
        writeContents(HEAD_FILE, branchName);
    }

    public static void branch(String branchName) {
        File branchFile = join(BRANCHES_DIR, branchName);
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        String commitId = getHeadCommitId();
        writeContents(branchFile, commitId);
    }

    public static void rmBranch(String branchName) {
        File branchFile = join(BRANCHES_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String currBranchName = readContentsAsString(HEAD_FILE);
        if (currBranchName.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branchFile.delete();
    }

    public static void reset(String commitId) {
        String fullCommitId = resolveCommitId(commitId);
        if (fullCommitId == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        File commitFile = join(COMMITS_DIR, fullCommitId);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit targetCommit = readObject(commitFile, Commit.class);
        if (!checkoutCommitSnapshot(commitId, targetCommit)) {
            return;
        }
        String currBranch = readContentsAsString(HEAD_FILE);
        File branchFile = join(BRANCHES_DIR, currBranch);
        writeContents(branchFile, commitId);
    }

    public static boolean checkoutCommitSnapshot(String targetCommitId, Commit targetCommit) {
        Commit currentCommit = getHeadCommit();
        Map<String, String> targetTrackedFiles = targetCommit.getTrackedFiles();
        Map<String, String> currentTrackedFiles = currentCommit.getTrackedFiles();
        HashMap<String, String> stageAdd = readStageAdd();
        HashSet<String> stageRemove = readStageRemove();
        for (String file : targetTrackedFiles.keySet()) {
            if ((join(CWD, file).exists())
                    && !currentTrackedFiles.containsKey(file)
                    && !stageAdd.containsKey(file)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return false;
            }
        }
        for (String file : targetTrackedFiles.keySet()) {
            checkoutCommitFile(targetCommitId, file);
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
        return true;
    }

    public static void merge(String branchName) {
        Map<String, String> addMap = readStageAdd();
        Set<String> removeSet = readStageRemove();
        if (!addMap.isEmpty() || !removeSet.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        File branchFile = join(BRANCHES_DIR, branchName);
        String currBranchName = readContentsAsString(HEAD_FILE);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (currBranchName.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        String currCommitId = readContentsAsString(join(BRANCHES_DIR, currBranchName));
        String givenCommitId = readContentsAsString(join(BRANCHES_DIR, branchName));
        String splitPoint = findSplitPoint(currBranchName, branchName);

        if (splitPoint.equals(givenCommitId)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitPoint.equals(currCommitId)) {
            System.out.println("Current branch fast-forwarded.");
            checkoutBranch(branchName);
            return;
        }

        Commit currCommit = getHeadCommitByBranch(currCommitId);
        Commit givenCommit = getHeadCommitByBranch(givenCommitId);
        Commit splitCommit = getHeadCommitByBranch(splitPoint);

        Map<String, String> currTrackedFiles = currCommit.getTrackedFiles();
        Map<String, String> givenTrackedFiles = givenCommit.getTrackedFiles();
        Map<String, String> splitTrackedFiles = splitCommit.getTrackedFiles();
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(currTrackedFiles.keySet());
        allFiles.addAll(givenTrackedFiles.keySet());
        allFiles.addAll(splitTrackedFiles.keySet());
        boolean hasConflict = false;

        for (String fileName : allFiles) {
            String currBlob = currTrackedFiles.get(fileName);
            String givenBlob = givenTrackedFiles.get(fileName);
            String splitBlob = splitTrackedFiles.get(fileName);
            boolean currChanged = false;
            boolean givenChanged = false;
            boolean willModify = false;
            if (!Objects.equals(currBlob, splitBlob)) currChanged = true;
            if (!Objects.equals(givenBlob, splitBlob)) givenChanged = true;

            if (givenChanged && !currChanged) {
                willModify = true;
            }
            if (givenChanged && currChanged) {
                if (!Objects.equals(givenBlob, currBlob)) {
                    willModify = true;
                }
            }
            if (join(CWD, fileName).exists() && willModify && !currTrackedFiles.containsKey(fileName) && !addMap.containsKey(fileName)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        for (String fileName : allFiles) {
            String currBlob = currTrackedFiles.get(fileName);
            String givenBlob = givenTrackedFiles.get(fileName);
            String splitBlob = splitTrackedFiles.get(fileName);
            boolean currChanged = false;
            boolean givenChanged = false;
            if (!Objects.equals(currBlob, splitBlob)) currChanged = true;
            if (!Objects.equals(givenBlob, splitBlob)) givenChanged = true;

            if (givenChanged && !currChanged) {
                if (givenBlob == null) {
                    rm(fileName);
                }
                else {
                    checkoutCommitFile(givenCommitId, fileName);
                    add(fileName);
                }
            }

            if (givenChanged && currChanged) {
                if (!Objects.equals(givenBlob, currBlob)) {
                    writeConflictFile(fileName, currBlob, givenBlob);
                    hasConflict = true;
                }
            }
        }
        mergeCommit("Merged " + branchName + " into " + currBranchName + ".", givenCommitId);
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public static String findSplitPoint(String currBranch, String givenBranch) {
        String currCommitId = getHeadCommmitIdByBranch(currBranch);
        String givenCommitId = getHeadCommmitIdByBranch(givenBranch);
        Set<String> currSet = getParentsSet(currCommitId);
        Set<String> givenSet = getParentsSet(givenCommitId);
        Set<String> commonAncestors = new HashSet<>(currSet);
        commonAncestors.retainAll(givenSet);

        for (String candidate : commonAncestors) {
            boolean isLatest = true;
            for (String other : commonAncestors) {
                if (candidate.equals(other)) {
                    continue;
                }
                if (getParentsSet(other).contains(candidate)) {
                    isLatest = false;
                    break;
                }
            }
            if (isLatest) {
                return candidate;
            }
        }
        return null;
    }
    private static Set<String> getParentsSet(String commitId) {
        Set<String> visited = new HashSet<>();
        Deque<String> fringe = new ArrayDeque<>();
        fringe.add(commitId);
        while (!fringe.isEmpty()) {
            String id = fringe.removeFirst();
            if (visited.contains(id)) {
                continue;
            }
            visited.add(id);
            Commit c = readObject(join(COMMITS_DIR, id), Commit.class);
            if (c.getParent() != null) {
                fringe.addLast(c.getParent());
            }
            if (c.getSecondParent() != null) {
                fringe.addLast(c.getSecondParent());
            }
        }
        return visited;
    }
    private static Commit getHeadCommitByBranch(String commitId) {
        File commitFile = join(COMMITS_DIR, commitId);
        return readObject(commitFile, Commit.class);
    }

    private static String getHeadCommmitIdByBranch(String branchName) {
        File branchFile = join(BRANCHES_DIR, branchName);
        return readContentsAsString(branchFile);
    }

    private static String getBlobContentAsString(String blobId) {
        if (blobId == null) {
            return "";
        }
        else {
            File file = join(BLOBS_DIR, blobId);
            return readContentsAsString(file);
        }
    }
    private static void writeConflictFile(String fileName, String currBlob, String givenBlob) {
        String conflictContent = "<<<<<<< HEAD\n" + getBlobContentAsString(currBlob) + "=======\n" + getBlobContentAsString(givenBlob) + ">>>>>>>\n";
        writeContents(join(CWD, fileName), conflictContent);
        add(fileName);
    }
}
