package gh2;

import java.io.File;

public class PlayMySong {
    public static void main(String[] args) {
        GuitarPlayer player = new GuitarPlayer(new File("C:/Users/azusa/Downloads/相遇天使（轻音少女）_爱给网_aigei_com.mid"));
        player.play();
    }
}
