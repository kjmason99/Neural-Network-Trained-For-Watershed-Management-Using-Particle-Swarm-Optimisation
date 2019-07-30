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
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class optimise {
	
	//4 variables x1,x2,x4,x6
	waterShed watershed = new waterShed();
	int trainingStatesNo = watershed.Q1_training.size();
	
	int maxI;
	ArrayList<Integer> Violations = new ArrayList<Integer>();
	ArrayList<Double> Fitness = new ArrayList<Double>();
	ArrayList<ArrayList<Double>> fsolutions = new ArrayList<ArrayList<Double>> ();
	ArrayList<ArrayList<Double>> bsolutions = new ArrayList<ArrayList<Double>> ();
	Network network;
	public double bestFitness;
	double bestViolationSum;
	int bestViolationCount;
	ArrayList <Double> optimumNetworkWeights;
	ArrayList <Particle> Particles;
	
	double currentViolationSum;
	int currentViolationCount;
	double currentOverallFitness;
	
	
	
	
	public optimise (int iterations,int particles,int hiddenLayers, int neuronsPHL){
		this.currentOverallFitness=0.0;
		this.currentViolationCount=0;
		this.currentViolationSum=0.0;
		this.optimumNetworkWeights = new ArrayList <Double> ();
		this.Particles = new ArrayList <Particle> ();
		this.maxI=iterations;
		this.network= new Network(3,4,hiddenLayers,neuronsPHL); // inputs, outputs, hidden layers, neurons per layer
		this.watershed = new waterShed();
		
		for(int i=0;i<particles;i++){
			this.Particles.add(new Particle (network.getWeights().size()));
		}
		network.countWeights();
		
		trainNetwork(maxI);
	}
	
	
	public void trainNetwork(int iterations){
		this.bestFitness = Double.NEGATIVE_INFINITY;
		this.bestViolationCount = Integer.MAX_VALUE;
		this.bestViolationSum = Double.POSITIVE_INFINITY;

		for(int k=0;k<iterations;k++){ // trains for predetermined num of iterations
			for(int i=0;i<Particles.size(); i++){	
				network.setWeights(Particles.get(i).Position);
				evaluateTrainingStates();
				if(currentOverallFitness>bestFitness){ // see if current set of weights is better than the best known set
					bsolutions=fsolutions;
					bestFitness = currentOverallFitness;
					bestViolationCount = currentViolationCount;
					bestViolationSum = currentViolationSum;
					optimumNetworkWeights = network.getWeights();
				}
				
		//		System.out.println("particle = "+i+", Fitness = "+currentOverallFitness+", "
		//				+ "violations = "+currentViolationCount+", amount = "+currentViolationSum);
				
				Particles.get(i).setCurFitness(currentOverallFitness); // updates the particles current and best fitness
			}
			
			for(int i=0;i<Particles.size(); i++){ // particles update position and velocity using best known solution
				Particles.get(i).update(optimumNetworkWeights);
			}
			
			if(k%1000==0){
				System.out.println("i = "+k+", Fitness = "+bestFitness+", "
						+ "violations = "+bestViolationCount+", amount = "+bestViolationSum);
			}
			
			Violations.add(bestViolationCount);
			Fitness.add(bestFitness);

		}
		
		
//		System.out.println("Best fitness after training = "+bestFitness);
		network.setWeights(optimumNetworkWeights); // the network weights are set to the best found set
		evaluateTrainingStates();
	}
	
	
	
	public void evaluateTrainingStates(){
		ArrayList<ArrayList<Double>> solutions = new ArrayList<ArrayList<Double>> ();
		ArrayList<Double> solutionFitness = new ArrayList<Double> ();
		this.currentOverallFitness=0.0;
		this.currentViolationCount=0;
		this.currentViolationSum=0.0;
		
		for(int i=0;i<trainingStatesNo;i++){
			ArrayList<Double> input = watershed.getNormalisedRiverFlowRates(i,true);
			ArrayList<Double> percentSolution = network.getNetworkOutput(input);
			
			ArrayList<Double> solution = new ArrayList<Double> ();
			solution = watershed.fractionToLitres(i, percentSolution,true);
			solutions.add(solution);
			
			double curFitness = watershed.FitnessCheck(i, solutions.get(i),true);
			currentViolationSum+=watershed.curViolations;
			currentViolationCount+=watershed.curViolationCount;
			currentOverallFitness+=curFitness;
			solutionFitness.add(curFitness);	
		}
		fsolutions=solutions;
	}
	
	
	
	public ArrayList<Double> readFile(String path){
		ArrayList<Double> contents = new ArrayList<Double>();
		
		try {
			Scanner s = null; 
			s = new Scanner(new BufferedReader(new FileReader(path))); 
			while (s.hasNext()) { 
				contents.add(s.nextDouble()); // next integer is added to randomList
				}
			s.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		return contents;
		
	}
	

}














