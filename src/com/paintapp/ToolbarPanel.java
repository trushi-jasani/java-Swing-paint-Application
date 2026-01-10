package com.paintapp;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ToolbarPanel extends JPanel implements PaintCanvas.ToolChangeListener {
    private final PaintCanvas canvas;
    private JPanel textPanel; 
    private JTextField textField;
    private static final Color TOOLBAR_BG = new Color(229, 228, 226); 

    public ToolbarPanel(PaintCanvas canvas) {
        this.canvas = canvas;

        setLayout(new BorderLayout());
        setBackground(TOOLBAR_BG); 
        setPreferredSize(new Dimension(Constants.CANVAS_WIDTH, 80)); 

        // LEFT CONTROLS PANEL
        JPanel leftControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        leftControlsPanel.setBackground(TOOLBAR_BG); 

        // Action buttons
        leftControlsPanel.add(createStyledButton("New", e -> canvas.newFile()));
        leftControlsPanel.add(createStyledButton("Save", e -> handleSave()));
        leftControlsPanel.add(createStyledButton("Undo", e -> canvas.undo()));
        leftControlsPanel.add(createStyledButton("Redo", e -> canvas.redo()));
        leftControlsPanel.add(createStyledButton("Clear", e -> canvas.clearCanvas()));

        // Stroke size dropdown
        leftControlsPanel.add(createLabeledComboPanel("Stroke:", Arrays.asList(1, 2, 3, 5, 8, 12, 20, 26, 30, 40), "3", size -> canvas.setStrokeSize(Integer.parseInt(size))));

        // Font family dropdown
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        List<String> fontList = Arrays.asList(Arrays.copyOf(fontNames, Math.min(fontNames.length, 10)));
        leftControlsPanel.add(createLabeledComboPanel("Font:", fontList, canvas.getFontName(), canvas::setFontName));

        // Font size dropdown
        java.util.List<String> fSizeList = new java.util.ArrayList<>();
        for (int i = 8; i <= 48; i += 2) fSizeList.add(String.valueOf(i));
        leftControlsPanel.add(createLabeledComboPanel("Size:", fSizeList, "24", size -> canvas.setFontSize(Integer.parseInt(size))));

        // Text input
        textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        textPanel.add(new JLabel("Text:"));
        textField = new JTextField("Enter text", 8);
        canvas.setText(textField.getText());
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { canvas.setText(textField.getText()); }
            public void removeUpdate(DocumentEvent e) { canvas.setText(textField.getText()); }
            public void changedUpdate(DocumentEvent e) { canvas.setText(textField.getText()); }
        });
        textPanel.add(textField);
        leftControlsPanel.add(textPanel); 
        
        add(leftControlsPanel, BorderLayout.WEST);

        // RIGHT CONTROLS PANEL (Color swatches)
        JPanel rightControlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        rightControlsPanel.setBackground(TOOLBAR_BG); 

        JPanel colorGroupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        colorGroupPanel.add(new JLabel("Color:")); 
        JPanel colorGrid = createColorGrid();
        colorGroupPanel.add(colorGrid);
        
        rightControlsPanel.add(colorGroupPanel); 
        add(rightControlsPanel, BorderLayout.EAST);
    }

    @Override
    public void toolChanged(Tool newTool) {}

    // Buttons
    private JButton createStyledButton(String label, java.awt.event.ActionListener listener) {
        JButton b = new JButton(label);
        b.setForeground(Color.BLACK);
        b.setBackground(new Color(245, 245, 245));
        b.setFocusPainted(false);
        b.setBorderPainted(true);

        b.addActionListener(e -> {
            Color original = b.getBackground();
            b.setBackground(new Color(255, 245, 160)); // flash
            listener.actionPerformed(e);

            Timer timer = new Timer(150, evt -> b.setBackground(original));
            timer.setRepeats(false);
            timer.start();
        });

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (b.getBackground().equals(new Color(245, 245, 245))) {
                    b.setBackground(new Color(230, 230, 230));
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(245, 245, 245));
            }
        });

        return b;
    }

    // Labeled ComboBox Panel
    private JPanel createLabeledComboPanel(String labelText, List<?> items, String defaultItem, java.util.function.Consumer<String> action) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(lbl);

        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        for (Object item : items) combo.addItem(String.valueOf(item));
        try { combo.setSelectedItem(defaultItem); } catch (Exception ignored) {}
        combo.addActionListener(e -> action.accept((String) combo.getSelectedItem()));
        p.add(combo);

        return p;
    }

    // Save handler
    private void handleSave() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save image");
        fileChooser.setSelectedFile(new File("image.png"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                canvas.saveToFile(fileChooser.getSelectedFile());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Color grid
    private JPanel createColorGrid() {
        JPanel colorGrid = new JPanel(new GridLayout(2, 25, 2, 2));
        Color[] swatches = {
            Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, new Color(220,220,220), Color.WHITE,
            Color.RED, new Color(255,102,102), Color.PINK, new Color(255,0,127), new Color(153,0,0), new Color(255,204,204), new Color(178,34,34), new Color(139,0,0),
            Color.ORANGE, new Color(255,153,51), new Color(255,178,102), new Color(255,128,0), new Color(210,105,30), new Color(128,64,0), new Color(153,102,51), new Color(244,164,96),
            Color.YELLOW, new Color(255,255,153), new Color(255,255,102), new Color(204,204,0), new Color(255,255,204), new Color(218,165,32),
            new Color(0,153,0), Color.GREEN, new Color(0,255,0), new Color(102,255,178), new Color(0,128,64), new Color(50,205,50), new Color(34,139,34), new Color(144,238,144),
            Color.BLUE, new Color(102,178,255), new Color(0,204,204), new Color(70,130,180), new Color(0,0,139), new Color(135,206,250), new Color(25,25,112), new Color(173,216,230),
            Color.MAGENTA, new Color(204,0,204), new Color(148,0,211), new Color(186,85,211), new Color(221,160,221), new Color(75,0,130)
        };
        for (Color c : swatches) {
            JButton cb = new JButton();
            cb.setBackground(c);
            cb.setPreferredSize(new Dimension(16, 16));
            cb.setFocusable(false);
            cb.setBorderPainted(true);
            cb.addActionListener(e -> canvas.setColor(c));
            colorGrid.add(cb);
        }
        return colorGrid;
    }
}