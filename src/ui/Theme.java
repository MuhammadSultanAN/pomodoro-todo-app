package ui;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public final class Theme {
    private Theme() {}

    public static final Color CREAM = new Color(243, 237, 225);
    public static final Color PAPER = new Color(234, 225, 208);
    public static final Color LIGHT = new Color(248, 244, 235);
    public static final Color SAGE = new Color(135, 148, 122);
    public static final Color DARK_SAGE = new Color(101, 116, 92);
    public static final Color BROWN = new Color(81, 72, 61);
    public static final Color DARK_BROWN = new Color(57, 51, 45);
    public static final Color ROSE = new Color(173, 125, 115);
    public static final Color GOLD = new Color(182, 154, 104);
    public static final Color MUTED = new Color(140, 129, 114);
    public static final Color BORDER = new Color(216, 204, 185);

    public static Font serif(float size, int style) { return new Font("Serif", style, Math.round(size)); }
    public static Font sans(float size, int style) { return new Font("SansSerif", style, Math.round(size)); }

    public static JPanel panel(Color color) {
        JPanel p = new JPanel();
        p.setBackground(color);
        return p;
    }

    public static JLabel label(String text, int size, Color color, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(serif(size, bold ? Font.BOLD : Font.PLAIN));
        l.setForeground(color);
        return l;
    }

    public static JButton button(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setFont(sans(13, Font.BOLD));
        b.setForeground(fg);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(new LineBorder(bg.darker(), 1), new EmptyBorder(10, 18, 10, 18)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        return b;
    }

    public static void styleInput(JTextField f) {
        f.setFont(sans(14, Font.PLAIN));
        f.setForeground(BROWN);
        f.setBackground(LIGHT);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER), new EmptyBorder(9, 10, 9, 10)));
    }

    public static JPanel cardPanel() {
        JPanel p = panel(LIGHT);
        p.setBorder(new CompoundBorder(new LineBorder(BORDER), new EmptyBorder(22, 22, 22, 22)));
        return p;
    }

    public static void pageHeader(JPanel p, String title, String subtitle) {
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel h = label(title, 38, DARK_BROWN, true);
        JLabel s = new JLabel(subtitle);
        s.setFont(sans(13, Font.PLAIN));
        s.setForeground(MUTED);
        p.add(h);
        p.add(Box.createVerticalStrut(4));
        p.add(s);
        p.add(Box.createVerticalStrut(24));
    }

    public static <T extends JPanel> T align(T p, float ax, boolean stretch) {
        for (Component c : p.getComponents()) {
            if (!(c instanceof JComponent)) continue;
            JComponent jc = (JComponent) c;
            jc.setAlignmentX(ax);
            if (c instanceof Box.Filler) continue;
            if (c instanceof JButton) {
                jc.setMaximumSize(jc.getPreferredSize());
            } else if (stretch && !(c instanceof JLabel)) {
                Dimension d = jc.getPreferredSize();
                jc.setMaximumSize(new Dimension(Integer.MAX_VALUE, d.height));
            }
        }
        return p;
    }

    public static JLabel addStat(JPanel parent, String value, String caption) {
        JPanel c = cardPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        JLabel v = label(value, 36, DARK_SAGE, true);
        JLabel t = new JLabel(caption);
        t.setFont(sans(12, Font.PLAIN));
        t.setForeground(MUTED);
        c.add(v);
        c.add(Box.createVerticalStrut(5));
        c.add(t);
        align(c, Component.CENTER_ALIGNMENT, false);
        parent.add(c);
        return v;
    }
}
