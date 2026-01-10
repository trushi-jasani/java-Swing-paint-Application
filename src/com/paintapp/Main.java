package com.paintapp;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Paint Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            PaintCanvas canvas = new PaintCanvas();
            ToolSidebar toolSidebar = new ToolSidebar(canvas);
            ToolbarPanel toolbar = new ToolbarPanel(canvas);
            
            // Link canvas to toolbar for updates
            canvas.setToolChangeListener(toolbar);

            frame.setLayout(new BorderLayout());
            frame.add(toolbar, BorderLayout.NORTH);
            frame.add(toolSidebar, BorderLayout.WEST);
            frame.add(canvas, BorderLayout.CENTER);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}