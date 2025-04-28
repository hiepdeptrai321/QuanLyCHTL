package gui;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private ImageIcon backgroundGif;

    public BackgroundPanel(String path) {
        backgroundGif = new ImageIcon(getClass().getResource(path));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        backgroundGif.paintIcon(this, g, 0, 0);
    }
}
