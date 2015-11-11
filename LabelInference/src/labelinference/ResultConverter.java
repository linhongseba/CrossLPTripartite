/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package labelinference;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author linhong
 */
public class ResultConverter {
    double accuracy;
    double runtimetotal;
    double runtimeupdate;
    double error;
    ArrayList<String> []objs;
    double confidence;
    double newdatapercentage;
    int beta;
    int maxiter;
    public static final String[] Graphs = {
         "graph-30", "graph_imdb", "graph-37", "graph_paper"
     };
     public static final String[] Algorithms = {
         "MRG", "ARG", "GRF", "MRO", "ARO" 
     };
     public static final String[] labelpercent = {
         "05"
     };
    public ResultConverter(){
        objs=new ArrayList[Algorithms.length];
        for(int i=0;i<Algorithms.length;i++){
            objs[i]=new ArrayList<>(20);
        }
    }
    public void ReadResult(String filename, ArrayList<String> objvalue, boolean isfirst){
        try {
            objvalue.clear();
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int numiter;
            maxiter=0;
            while((strline=br.readLine())!=null){
                if(strline.contains("Iter")==true){
                    String iter=strline.substring(7,strline.length());
                    do{
                        strline=br.readLine();
                    }while(strline.contains("ObjValue")==false);
                    String obj=strline.substring(10, strline.length());
                    //System.out.println(iter+"\t"+obj);
                    if(isfirst==true)
                        objvalue.add(iter+"\t"+obj);
                    else
                        objvalue.add(obj);
                    numiter=Integer.parseInt(iter);
                    if(numiter>maxiter)
                        maxiter=numiter;
                }
                if(strline.contains("Processed")==true){
                    if(strline.contains("update")){
                        runtimeupdate=Double.parseDouble(strline.substring(12, strline.length()-16));
                        //System.out.println(runtimeupdate);
                    }else{
                        runtimetotal=Double.parseDouble(strline.substring(12, strline.length()-10));
                        //System.out.println(runtimetotal);
                    }
                }
                if(strline.contains("Accuracy = ")==true){
                    int index1=strline.lastIndexOf(",");
                    accuracy=Double.parseDouble(strline.substring(index1+1, strline.length()-5));
                    //System.out.println(accuracy);
                }
                if(strline.contains("BER")==true){
                    error=Double.parseDouble(strline.substring(5));
                    //System.out.println(error);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ResultConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ResultConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Run(String folddir, String outputdir){
        try {
            beta=5;
            FileWriter fstream = null;
            fstream = new FileWriter(outputdir+"/accuracy.txt",false);
            BufferedWriter outacc=new BufferedWriter(fstream);
            FileWriter fstream1 = new FileWriter(outputdir+"/BER.txt",false);
            BufferedWriter outber=new BufferedWriter(fstream1);
            FileWriter fstream2 = new FileWriter(outputdir+"/timeall.txt",false);
            BufferedWriter outtime=new BufferedWriter(fstream2);
            FileWriter fstream3 = new FileWriter(outputdir+"/timeiter.txt",false);
            BufferedWriter outtime2=new BufferedWriter(fstream3);
            FileWriter fstream5 = new FileWriter(outputdir+"/iter.txt",false);
            BufferedWriter outiter=new BufferedWriter(fstream5);
            outacc.write("Accuracy");
            outber.write("BER");
            outtime2.write("Time_iter(ms)");
            outtime.write("Time(s)");
            outiter.write("iter");
            for(int j=0;j<Algorithms.length;j++){
                outtime.write("\t"+Algorithms[j]);
                outtime2.write("\t"+Algorithms[j]);
                outiter.write("\t"+Algorithms[j]);
                outacc.write("\t"+Algorithms[j]+"_avg");
                outacc.write("\t"+Algorithms[j]+"_min");
                outacc.write("\t"+Algorithms[j]+"_max");
                outber.write("\t"+Algorithms[j]+"_avg");
                outber.write("\t"+Algorithms[j]+"_min");
                outber.write("\t"+Algorithms[j]+"_max");
            }
            outiter.write("\n");
            outacc.write("\n");
            outber.write("\n");
            outtime.write("\n");
            outtime2.write("\n");
            for(int i=0;i<Graphs.length;i++){
                outacc.write(Graphs[i]);
                outber.write(Graphs[i]);
                outtime.write(Graphs[i]);
                outtime2.write(Graphs[i]);
                outiter.write(Graphs[i]);
                FileWriter fstream4 = new FileWriter(outputdir+"/Converge-"+Graphs[i],false);
                BufferedWriter outobj=new BufferedWriter(fstream4);
                outobj.write("#iter");
                for(int j=0;j<Algorithms.length;j++){
                    outobj.write("\t"+Algorithms[j]);
                }
                outobj.write("\n");
                for(int j=0;j<Algorithms.length;j++){
                    double time1=0;
                    double time2=0;
                    double iter=0;
                    double accmin=1.0;
                    double accmax=0;
                    double accavg=0;
                    double bermin=1.0;
                    double bermax=0.0;
                    double beravg=0.0;
                    for(int k=0;k<labelpercent.length;k++){
                        //graph_imdb_05_GRF_0_0_0.000001_5.txt
                        String filename=folddir+"/"+Graphs[i]+"_"+labelpercent[k]+"_"+Algorithms[j]+"_0_0_-1_"+beta+".txt";
                        if(j==0)
                            this.ReadResult(filename,objs[j],true);
                        else
                            this.ReadResult(filename, objs[j], false);
                        accmin=Math.min(accmin, this.accuracy);
                        accmax=Math.max(accmax, this.accuracy);
                        accavg+=this.accuracy;
                        bermin=Math.min(bermin,this.error);
                        bermax=Math.max(bermax, this.error);
                        beravg+=this.error;
                        //outacc.write("\t"+this.accuracy);
                        //outber.write("\t"+this.error);
                        time1+=this.runtimetotal;
                        time2+=this.runtimeupdate;
                        iter+=maxiter;
                    }
                    iter/=labelpercent.length;
                    beravg/=labelpercent.length;
                    accavg/=labelpercent.length;
                    time1/=labelpercent.length;
                    time2/=labelpercent.length;
                    outiter.write("\t"+iter);
                    outacc.write("\t"+accavg);
                    outacc.write("\t"+accmin);
                    outacc.write("\t"+accmax);
                    outber.write("\t"+beravg);
                    outber.write("\t"+bermin);
                    outber.write("\t"+bermax);
                    outtime.write("\t"+time1/1000);
                    outtime2.write("\t"+time2/iter);
                }
                outiter.write("\n");
                outtime.write("\n");
                outtime2.write("\n");
                outacc.write("\n");
                outber.write("\n");
                double maxobj=0;
                for(int j=1;j<Algorithms.length;j++){
                    maxobj=Math.max(maxobj, Double.parseDouble(objs[j].get(0)));
                 }
                 for(int j=0;j<Algorithms.length;j++){
                     if(j==0)
                        outobj.write("0\t"+maxobj);
                    else
                        outobj.write("\t"+maxobj);
                 }
                 outobj.write("\n");
                 
                for(int k=1;k<objs[0].size();k++){
                    for(int j=0;j<Algorithms.length;j++){
                        if(j==0){
                            if(k<objs[j].size())
                                outobj.write(objs[j].get(k));
                            else
                                outobj.write(objs[j].get(objs[j].size()-1));
                        }
                        else
                            if(k<objs[j].size())
                                outobj.write("\t"+objs[j].get(k));
                        else
                                outobj.write("\t"+objs[j].get(objs[j].size()-1));
                    }
                    outobj.write("\n");
                }
                outobj.close();
            }
            outacc.close();
            outber.close();
            outtime.close();
            outtime2.close();
            outiter.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ResultConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String []args){
        ResultConverter myresult=new ResultConverter();
        myresult.Run("C:\\Users\\linhong\\Documents\\GitHub\\CrossLPTripartite\\LabelInference\\experiment\\experimental_scripts\\results", "./plot");
        
    }
    
}
