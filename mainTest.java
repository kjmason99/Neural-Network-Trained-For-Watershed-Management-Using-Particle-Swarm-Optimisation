// Author: Karl Mason

// This code trains a neural network using the particle swarm optimisation algorithm and is applied
// to the problem of watershed management.

// Please use the following bib files to cite the relevant papers describing this work:

/* 
 
@article{mason2018meta,
  title={A meta optimisation analysis of particle swarm optimisation velocity update equations for watershed management learning},
  author={Mason, Karl and Duggan, Jim and Howley, Enda},
  journal={Applied Soft Computing},
  volume={62},
  pages={148--161},
  year={2018},
  publisher={Elsevier}
}

@article{mason2018watershed,
  title={Watershed management using neuroevolution},
  author={Mason, Karl and Duggan, Jim and Howley, Enda},
  journal={Modeling Earth Systems and Environment},
  volume={4},
  number={4},
  pages={1445--1448},
  year={2018},
  publisher={Springer}
}
 
@inproceedings{mason2016applying,
  title={Applying multi-agent reinforcement learning to watershed management},
  author={Mason, Karl and Mannion, Patrick and Duggan, Jim and Howley, Enda},
  booktitle={Proceedings of the Adaptive and Learning Agents workshop (at AAMAS 2016)},
  year={2016}
}

 */

package NN_Control_Watershed;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class mainTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int iterations = 3000;
		int particles=50;
		int runs = 1;
		int hiddenLayers=4;
		int npl = 2;
		optimise o;
		
		String PSOType = "SPSO";
		String PSOTopology = "Global";
		
		String OUTPUT_FILE_VN_AVERAGE;
		String OUTPUT_FILE_VN_OPTIMUM;
	
		
		String FunctionName= "Watershed";
		String txt = ".txt";
		
		String FILE_LOCATION = "C:\\Users\\KarlJ\\Results\\Test\\";
			
		
		String OUTPUT_FILE_VN_ANALYSIS = FILE_LOCATION+PSOType+"_"+PSOTopology+"_analysis_"+FunctionName+txt;
		String OUTPUT_FILE_VN_SOLUTION = FILE_LOCATION+PSOType+"_"+PSOTopology+"_solution_"+FunctionName+txt;
		
		
		String OUTPUT_FILE_TEST_SOLUTION = FILE_LOCATION+"TestResults"+txt;
		
		String OUTPUT_FILE_RESULTS_SUMMARY = FILE_LOCATION+PSOType+"_Results_Summary.txt";
		
		PrintWriter pwSummary = null;
		try {
			pwSummary = new PrintWriter(new FileOutputStream(OUTPUT_FILE_RESULTS_SUMMARY));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pwSummary.println("Con=0.7298 Benchmark c1,c2=2.05 ");
		pwSummary.println("Watershed "+PSOType);
		pwSummary.println("Neural Network trained by PSO");
		pwSummary.println(hiddenLayers+" hidden layers, "+npl+" neurons per layer");
		pwSummary.println("100 training examples");
		pwSummary.println(iterations+"_iterations, "+particles+"_particles");
		pwSummary.println("Global_avg global_std");

		
		for(int function=1;function<2;function++){
			ArrayList<Double> vnMeanGBestValues = new ArrayList<Double>();
			FunctionName = "Watershed";
			OUTPUT_FILE_VN_AVERAGE = FILE_LOCATION+PSOType+"_"+PSOTopology+"_average_"+FunctionName+txt;

			String content;
			PrintWriter pwvnA = null;
			try {
				pwvnA = new PrintWriter(new FileOutputStream(OUTPUT_FILE_VN_AVERAGE));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String VIOLATIONS = FILE_LOCATION+PSOType+"_"+PSOTopology+"_avg_violations_"+FunctionName+txt;

			PrintWriter pwvnV = null;
			try {
				pwvnV = new PrintWriter(new FileOutputStream(VIOLATIONS));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			OUTPUT_FILE_VN_OPTIMUM = FILE_LOCATION+PSOType+"_"+PSOTopology+"_optimum_"+FunctionName+txt;
			PrintWriter pwvnO = null;
			try {
				pwvnO = new PrintWriter(new FileOutputStream(OUTPUT_FILE_VN_OPTIMUM));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			OUTPUT_FILE_VN_SOLUTION = FILE_LOCATION+PSOType+"_"+PSOTopology+"_solution_"+FunctionName+txt;
		
			PrintWriter pwvnS = null;
			try {
				pwvnS = new PrintWriter(new FileOutputStream(OUTPUT_FILE_VN_SOLUTION));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// list of list of gbest values 
			ArrayList <ArrayList<Double>> vnGBest = new ArrayList<ArrayList<Double>>();  
			ArrayList <ArrayList<Integer>> vnViolations = new ArrayList<ArrayList<Integer>>();  
			ArrayList <Double> vnResults = new ArrayList<Double>(); 
			ArrayList <Double> vnAvgViolations = new ArrayList<Double>(); 
			ArrayList <ArrayList<Double>> vnPostions = new ArrayList<ArrayList<Double>>(); 
			
			for(int i=0;i<runs;i++){
				System.out.println("run = "+i);
				o = new optimise(iterations,particles,hiddenLayers,npl);
				double ans = o.bestFitness;
				vnGBest.add(o.Fitness);
				vnViolations.add(o.Violations);
				content = String.valueOf(ans);
				vnResults.add(ans);
				pwvnO.println(content);
				
				for(int num=0;num<o.bsolutions.size();num++){
					for(int n=0;n<o.bsolutions.get(num).size();n++){
						pwvnS.print(o.bsolutions.get(num).get(n)+" ");
					}
					pwvnS.println();
				}
				
				
				vnPostions.add(o.optimumNetworkWeights);
				pwvnS.println();
				pwvnS.println();
			}
			
			for(int i=0;i<iterations;i++){ 
				double tempVNGbestSum = 0;
				double tempVNVSUM=0.0;
			
				for(int j=0;j<runs;j++){
					tempVNGbestSum=tempVNGbestSum+vnGBest.get(j).get(i);
					tempVNVSUM+= (double) vnViolations.get(j).get(i);
					
				}
				
				double tempVNMeanValue = (tempVNGbestSum)/runs;
				vnMeanGBestValues.add(tempVNMeanValue);
				content = String.valueOf(tempVNMeanValue);
				pwvnA.println(content);
				
				double tempVNMeanViolation = tempVNVSUM/runs;
				vnAvgViolations.add(tempVNMeanViolation);
				pwvnV.println(String.valueOf(tempVNMeanViolation));
			}
		
			pwvnV.close();
			pwvnA.close();
			pwvnO.close();
			pwvnS.close();
		
			for(int i=0;i<iterations;i++){
				if( i%20==0){
					System.out.println("i : "+i+"  > | Fitness : "+vnMeanGBestValues.get(i));
				}
			}
			System.out.println("Average gbest value after 25 runs over 10,000 iterations for a global topology = "+vnMeanGBestValues.get(iterations-1));
			
			pwSummary.println(getMean(vnResults) +" "+getStdDev(vnResults));
		}

		pwSummary.close();
		
		

	
	}
	public static double getStdDev(ArrayList <Double> num){
		double mean = getMean(num);
		double std = 0.0;
		double sum = 0.0;
		for(int i=0;i<num.size();i++){
			sum = sum + Math.pow((num.get(i) - mean), 2);
		}
		std = Math.sqrt(sum/num.size()); 
		return std;
	}
	
	public static double getMean(ArrayList <Double> num){
		double mean;
		double sum = 0.0;
		for(int i=0;i<num.size();i++){
			sum = sum + num.get(i);
		}
		mean = sum / num.size();
		return mean;
	}
	
}











