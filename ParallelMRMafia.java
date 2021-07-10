package com;
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
public class ParallelMRMafia extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text> {
	IntWritable one = new IntWritable(1);
    Text locText = new Text();
	Text empty = new Text();

public static double getSimilarity(double[] vector1, double[] vector2){
	double dot = 0, magnitude1 = 0, magnitude2=0;
	for(int i=0;i<vector1.length;i++){
		dot+=vector1[i]*vector2[i];
		magnitude1+=Math.pow(vector1[i],2);
    	magnitude2+=Math.pow(vector2[i],2);
	}
	magnitude1 = Math.sqrt(magnitude1);
    magnitude2 = Math.sqrt(magnitude2);
    double d = dot / (magnitude1 * magnitude2);
    return d == Double.NaN ? 0 : d;
}

public void map(LongWritable key,Text value,OutputCollector<Text,Text> output,Reporter reporter) throws IOException {
	String line = value.toString();
	String arr[] = line.split(",");
	double distance=0;
	double mindistance=Double.MAX_VALUE;
	int interval = -1;
	String file = "";
	double point[] = new double[arr.length-1];
	for(int j=1;j<arr.length;j++){
		point[j-1] = Double.parseDouble(arr[j]);
	}
	for(int i=0;i<MRMafia.dimension.length;i++){
		double dimension[] = MRMafia.dimension[i];
		if(dimension.length == point.length){
			distance = getSimilarity(point,dimension);
			if(distance < mindistance) {
				mindistance = distance;
				interval=i;
			}
		}
	}
	if(MRMafia.cluster.containsKey(interval)){
		MRMafia.cluster.get(interval).add(arr[0]);
	}else{
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(arr[0]);
		MRMafia.cluster.put(interval,temp);
	}
}
}