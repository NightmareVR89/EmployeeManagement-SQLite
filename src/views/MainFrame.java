package views;

import java.awt.Dimension;

import javax.swing.JFrame;

import views.mainwindow.MainWindow;

public class MainFrame extends JFrame {
    public MainFrame() {
        super.setSize(new Dimension(600, 460));
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLocationRelativeTo(null);

        MainWindow mw = new MainWindow();
        super.getContentPane().add(mw);
        super.setTitle("Mitarbeiterverwaltung");

        super.setVisible(true);
    }
}