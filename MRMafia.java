package com;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JFileChooser;
import java.awt.Cursor;
import com.jd.swing.custom.component.panel.HeadingPanel;
import com.jd.swing.util.PanelType;
import com.jd.swing.util.Theme;
import org.jfree.ui.RefineryUtilities;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.UIManager;
import java.util.LinkedHashMap;
import java.util.Map;
public class MRMafia extends JFrame{
	JLabel l1;
	JPanel p1,p2,p3;
	Font f1;
	JScrollPane jsp;
	JButton b1,b2,b3,b4,b5;
	JFileChooser chooser;
	MyTableModel dtm;
	JTable table;
	File file;
	DecimalFormat format = new DecimalFormat("#.###");
	Vector vector = new Vector();
	static int count;
	static double dimension[][];
	static LinkedHashMap<Integer,ArrayList<String>> cluster = new LinkedHashMap<Integer,ArrayList<String>>();

public MRMafia(){
	super("MRMafia");
	
	p1 = new HeadingPanel("Project Title",Theme.GLOSSY_OLIVEGREEN_THEME);
	p1.setPreferredSize(new Dimension(600,50));
	l1 = new JLabel("<html><body><center>MR-Mafia: Parallel Subspace Clustering Algorithm Based on MapReduce<br>For Large Multi-dimensional Datasets</center></body></html>".toUpperCase());
	l1.setFont(new Font("Courier New",Font.BOLD,18));
	l1.setForeground(Color.white);
	p1.add(l1);
	getContentPane().add(p1,BorderLayout.NORTH);

	f1 = new Font("Courier New",Font.BOLD,14);

	p2 = new JPanel();
	p2.setLayout(new BorderLayout());
	dtm = new MyTableModel(){
		public boolean isCellEditable(int r,int c){
			return false;
		}
	};
	table = new JTable(dtm);
	table.setRowHeight(30);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	table.setFont(f1);
	table.getTableHeader().setFont(f1);
	jsp = new JScrollPane(table);
	
	
	p3 = new HeadingPanel("",Theme.GLOSSY_OLIVEGREEN_THEME);
	p3.setPreferredSize(new Dimension(150,100));
	chooser = new JFileChooser(new File("."));
	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	b1 = new JButton("Upload Text Documents");
	b1.setFont(f1);
	p3.add(b1);
	b1.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			int option = chooser.showOpenDialog(MRMafia.this);
			if(option == chooser.APPROVE_OPTION){
				file = chooser.getSelectedFile();
				clearTable();
				JOptionPane.showMessageDialog(MRMafia.this,"Dataset Loaded");
			}
		}
	});

	b2 = new JButton("Generate Vector");
	b2.setFont(f1);
	p3.add(b2);
	b2.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglassCursor);
			vector.buildVector(dtm,file);
			dtm.addColumn("File Name");
			StringBuilder buffer = new StringBuilder();
			for(int i=0;i<vector.unique_att.size();i++){
				dtm.addColumn(vector.unique_att.get(i));
			}
			for(int i=0;i<dtm.getColumnCount();i++){
				table.getColumnModel().getColumn(i).setPreferredWidth(100);
			}
			count = dtm.getColumnCount()-1;
			dimension = new double[3][count];
			for(int i=0;i<vector.vector.size();i++){
				VectorModel vm = vector.vector.get(i);
				String row[] = new String[dtm.getColumnCount()];
				row[0] = vm.filename;
				buffer.append(vm.filename+",");
				for(int j=1;j<dtm.getColumnCount();j++){
					if(vm.vector.get(dtm.getColumnName(j)) != null){
						int value = vm.vector.get(dtm.getColumnName(j));
						row[j] = Integer.toString(value);
						buffer.append(value+",");
					}else{
						row[j] = "0";
						buffer.append("0,");
					}
				}
				buffer.deleteCharAt(buffer.length()-1);
				buffer.append(System.getProperty("line.separator"));
				dtm.addRow(row);
			}
			Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(normalCursor);
			try{
				FileWriter fw = new FileWriter("input/vector.txt");
				fw.write(buffer.toString());
				fw.close();
				BufferedReader br = new BufferedReader(new FileReader("input/vector.txt"));
				String line1[] = br.readLine().split(",");
				String line2[] = br.readLine().split(",");
				String line3[] = br.readLine().split(",");
				for(int j=1;j<line1.length;j++){
					dimension[0][j-1] = Double.parseDouble(line1[j]);
				}
				for(int j=1;j<line2.length;j++){
					dimension[1][j-1] = Double.parseDouble(line2[j]);
				}
				for(int j=1;j<line3.length;j++){
					dimension[2][j-1] = Double.parseDouble(line3[j]);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	});

	b3 = new JButton("Run MR Parallel Clustering");
	b3.setFont(f1);
	p3.add(b3);
	b3.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			cluster.clear();
			try{
				deleteFiles(new File("output"));
				long start = System.currentTimeMillis();
				MRPartition.runParallel();
				long end = System.currentTimeMillis();
				FileWriter fw = new FileWriter("Execution_Time.txt",true);
				fw.write((end-start)+","+count+System.getProperty("line.separator"));
				fw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			ViewClusters vc = new ViewClusters();
			int cluster_id = 0;
			for(Map.Entry<Integer,ArrayList<String>> me : cluster.entrySet()){
				int key = me.getKey();
				ArrayList<String> value = me.getValue();
				vc.area.append("Cluster No : "+cluster_id+" Cluster Size : "+value.size()+"\n\n");
				vc.area.append("Cluster Data : "+value.toString()+"\n\n");
				cluster_id = cluster_id + 1;
			}
			vc.setVisible(true);
			vc.setSize(600,600);
		}
	});

	b4 = new JButton("Dimension Execution Time Graph");
	b4.setFont(f1);
	p3.add(b4);
	b4.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			Chart chart1 = new Chart("Dimension Execution Time Graph");
			chart1.pack();
			RefineryUtilities.centerFrameOnScreen(chart1);
			chart1.setVisible(true);
		}
	});

	b5 = new JButton("Exit");
	b5.setFont(f1);
	p3.add(b5);
	b5.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			System.exit(0);
		}
	});

	getContentPane().add(jsp,BorderLayout.CENTER);
	getContentPane().add(p3,BorderLayout.SOUTH);
}
public void clearTable(){
	for(int i=table.getRowCount()-1;i>=0;i--){
		dtm.removeRow(i);
	}
	for(int i=table.getColumnCount()-1;i>=0;i--){
		dtm.removeColumn(i);
	}
}
public void deleteFiles(File path){
	if(path.exists()){
		File[] dir = path.listFiles();
		for(int d=0;d<dir.length;d++){
			if(dir[d].isFile()){
				dir[d].delete();
			}else if(dir[d].isDirectory()){
				deleteFiles(dir[d]);
			}
		}
		if(path.isDirectory()){
			path.delete();
		}
	}
}
public static void main(String a[])throws Exception{
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	MRMafia ud = new MRMafia();
	ud.setVisible(true);
	ud.setExtendedState(JFrame.MAXIMIZED_BOTH);
}
}
class MyTableModel extends DefaultTableModel {
    public void removeColumn(int column){
		columnIdentifiers.remove(column);
		for(Object row: dataVector){
			((java.util.Vector) row).remove(column);
		}
		fireTableStructureChanged();
    }	
}