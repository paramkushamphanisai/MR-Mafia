package com;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.HashSet; 
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.table.DefaultTableModel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
public class Vector {
	ArrayList<VectorModel> vector = new ArrayList<VectorModel>();
	ArrayList<String> unique_att; 
	HashSet<String> attributes = new HashSet(); 
	int row_len;
	int col_len;
	float matrix[][]; 
	float tfidf[][]; 
	int currentrow;
	DefaultTableModel dtm;
	DecimalFormat format = new DecimalFormat("#.###");
public void readFiles(File file) {
	File[] list = file.listFiles();
    for (File subfile : list) {
        if (subfile.isFile()) {
            readFile(subfile);
        } else if (subfile.isDirectory()) {
            readFiles(subfile);
        }
    }
}
public void readFile(File file){
	try{
		BufferedReader file_reader = new BufferedReader(new FileReader(file));
		String reader = null;
		StringBuilder tokens = new StringBuilder();
		while((reader=file_reader.readLine())!=null){
			reader = reader.toLowerCase().replaceAll("[^a-zA-Z\\s+]", " ");
			tokens.append(reader.trim()+" ");
		}
		String array[] = tokens.toString().trim().toLowerCase().split("\\s+");
		vector.add(new VectorModel(file.getName(),array,attributes));
	}catch(FileNotFoundException fnfe){
		System.out.println("Exception in readFile");
	}catch(IOException io){
		System.out.println("Exception in readFile");
	}
}
public void buildVector(DefaultTableModel dtm,File file) {
	attributes.clear();
	vector.clear();
	this.dtm = dtm;
	StopWordList.wordList();
	readFiles(file);
	vector();
}
public void vector(){
	unique_att = new ArrayList<String>(attributes);
	attributes.clear();
	row_len = vector.size();
	col_len = unique_att.size();
	matrix = new float[row_len][col_len];
	for(int i=0;i<row_len;i++){
		for(int j=0;j<col_len;j++){
			String value = unique_att.get(j).trim();
			matrix[i][j] = isValueExists(i,value);
		}
	}
	tfidf = matrix;
	float att_len = (float)col_len;
	for(int i=0;i<col_len;i++){
		for(int j=0;j<row_len;j++){
			float columns = matrix[j][i];
			if(columns != 0){
				float val = addTerm(i);
				float log = log(att_len/val);
				if(Float.isNaN(log))
					log = 0;
				matrix[j][i] = Float.parseFloat(format.format(log));
			}
		}
	}
}
public float log(float value) {
	return (float)Math.log(value)/(float)Math.log(2.0d);  
}

public float addTerm(int termindex){
	float sum = 0;
	for(int i=0;i<row_len;i++){
		sum+=tfidf[i][termindex];
		if(tfidf[i][termindex] != 0)
			currentrow = i;
	}
	return sum;
}

public float isValueExists(int pos,String word){
	float exist = 0f;
	VectorModel vm = vector.get(pos);
	if(vm.vector.containsKey(word))
		exist = vm.vector.get(word);
	return exist;
}
}
