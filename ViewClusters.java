package com;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
public class ViewClusters extends JFrame{
	
	JTextArea area;
	JScrollPane jsp;
	Font f1;
	JPanel p1;
	
public ViewClusters(){
	super("View Clusters");
	
	f1 = new Font("Courier New",Font.BOLD,14);
	p1 = new JPanel();
	p1.setLayout(new BorderLayout());
	p1.setBackground(Color.white);
	
	area = new JTextArea();
	area.setLineWrap(true);
	area.setEditable(false);
	jsp = new JScrollPane(area);
	area.setFont(f1);
	p1.add(jsp,BorderLayout.CENTER);

	add(p1,BorderLayout.CENTER);
}
}