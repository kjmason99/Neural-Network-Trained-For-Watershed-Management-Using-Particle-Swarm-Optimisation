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

import java.util.ArrayList;

public class Neuron {
	
	ArrayList <Double> neuronInput;
	
	boolean firstHiddelLayer;
	
	boolean outputLayer;
	
	ArrayList <Double> inputLayerWeights = new ArrayList <Double>(); // only used if neuron is in first hidden layer
	ArrayList <Double> networkInputs = new ArrayList <Double>(); // only used if neuron is in first hidden layer
	
	ArrayList <Double> inputWeights = new ArrayList <Double>(); // array of weights for inputs to neuron
	
	ArrayList <Double> delta = new ArrayList <Double>();
	ArrayList <Double> grad = new ArrayList <Double>();
	
	double DELTA;
	int numWeights;
	double output;
	int numNetworkOutputs;
	
	public Neuron(boolean firstHiddelLayer, int numNetworkInputs, int neuronsPerLayer, int numNetworkOut){
		this. firstHiddelLayer = firstHiddelLayer; // Informs neuron if it is in first layer 
		this.numNetworkOutputs=numNetworkOut;
		for(int i=0;i<numNetworkInputs;i++){
			this.networkInputs.add(Math.random()); // initialised at random
		}
		for(int i=0;i<numNetworkInputs;i++){
			this.inputLayerWeights.add(Math.random());
		}
		for(int i=0;i<neuronsPerLayer;i++){
			this.inputWeights.add(Math.random());
		}
		
		for(int i=0;i<numNetworkOutputs;i++){
			delta.add(Math.random());
			grad.add(Math.random());
		}
		
		if(firstHiddelLayer){
			numWeights=inputLayerWeights.size();
		}
		else{
			numWeights=inputWeights.size();
		}
		
		
		
	}
	
	public void setOutputLayer(boolean outLayer){
		this.outputLayer = outLayer;
	}
	
	
	
	public void setInput(ArrayList <Double> input){
		this.neuronInput = input; // neuron reads in the unweighted inputs
	}
	
	public double getW(int i){
		if(firstHiddelLayer){
			return inputLayerWeights.get(i);
		}
		else{
			return inputWeights.get(i);
		}
	}
	
	public void setW(int i, double v){
		if(firstHiddelLayer){
			inputLayerWeights.set(i,v);
		}
		else{
			inputWeights.set(i,v);
		}
	}
	
	
	
	public void setNetworkInputs(ArrayList <Double> i){ // set input to network (for first hidden layer neurons use)
		this.networkInputs= i;
	}
	
	public double getOutput(){
	//	double output;
		double v;
//		System.out.println("----------------------");
//		System.out.println("My inputs are");
//		for(int i =0;i<neuronInput.size();i++){
//			System.out.print(neuronInput.get(i)+" ");
//		}
//		System.out.println("\nMy weights are");
//		if(firstHiddelLayer){
//			for(int i =0;i<inputLayerWeights.size();i++){
//				System.out.print(inputLayerWeights.get(i)+" ");
//			}
//		}
//		else{
//			for(int i =0;i<inputWeights.size();i++){
//				System.out.print(inputWeights.get(i)+" ");
//			}
//		}
		
		if(firstHiddelLayer){
			output = 0.0; // output of neuron
			v = 0.0; // v will be the sum of the weighted inputs 
			for(int i=0;i<neuronInput.size();i++){
	//			System.out.print(neuronInput.get(i)*inputLayerWeights.get(i)+" ");
				v+=(neuronInput.get(i)*inputLayerWeights.get(i)); // summing up the weighted inputs

			}

	//		output = v/((double)inputLayerWeights.size()); // normalise the output so that it is between 0 and 1
			output = sigmoid(v);
			
		}

		else{
			output = 0.0; // output of neuron
			v = 0.0; // v will be the sum of the weighted inputs 
			for(int i=0;i<neuronInput.size();i++){
	//			System.out.print(neuronInput.get(i)*inputWeights.get(i)+" ");
				v+=neuronInput.get(i)*inputWeights.get(i); // summing up the weighted inputs 

			}
			output = sigmoid(v);
	//		output = v/(double)neuronInput.size(); // normalise the output so that it is between 0 and 1
		}
		
//		System.out.println("\nTotal weighted input is "+v);
//		System.out.println("Neuron output = "+output);
//		System.out.println("----------------------");
		
		
		return output;
	}
	
	public double sigmoid(double v){
		return 1.0/(1.0+(Math.exp(-v))) ;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
