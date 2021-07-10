package com;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.FileWriter;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleInputs;
public class MRPartition{
public static void runParallel()throws Exception{
	JobConf conf = new JobConf(MRPartition.class);
    conf.setJobName("MRMafia");
    conf.setOutputKeyClass(Text.class); 
    conf.setOutputValueClass(Text.class);
    conf.setInputFormat(TextInputFormat.class);
    conf.setOutputFormat(TextOutputFormat.class);
	conf.setMapperClass(ParallelMRMafia.class);
	FileInputFormat.setInputPaths(conf, new Path("input/vector.txt"));
	FileOutputFormat.setOutputPath(conf, new Path("output"));
    JobClient.runJob(conf);
	//JobClient.waitForCompletion(true);
}
}