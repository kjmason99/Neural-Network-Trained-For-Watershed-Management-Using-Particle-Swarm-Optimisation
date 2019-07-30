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

public class Network {
	int inputs;
	int outputs;
	int hiddenLayers;
	int neuronsPHL;
	ArrayList <Integer> neuronsPerLayer = new ArrayList <Integer>();
	ArrayList <ArrayList <Neuron>> network = new ArrayList <ArrayList <Neuron>>();
	

	
	ArrayList <Double> Error = new ArrayList <Double>();
	ArrayList <Double> PreviousOutputs = new ArrayList <Double>();
	ArrayList <Double> CurrentOutputs = new ArrayList <Double>();
	
	int w;
	
	
	public Network(int inputs, int outputs, int hiddenLayers, int neuronsPHL){
		this.inputs = inputs;
		this.outputs = outputs;
		this.hiddenLayers = hiddenLayers; // number of hidden layers, excluding input and output layers
		this.neuronsPHL = neuronsPHL;
		this.neuronsPerLayer.add(this.inputs);
		for(int i=0;i<this.hiddenLayers;i++){
			this.neuronsPerLayer.add(this.neuronsPHL);
		}
		this.neuronsPerLayer.add(this.outputs);
		
		for(int o=0;o<outputs;o++){
			Error.add(0.0);
		}
		
		createNeurons();
	//	countWeights();
		
	}
	
	
	public ArrayList <Double> getNetworkOutput(ArrayList <Double> input){ // reads in an input and returns an array of outputs
		// method to calculate the output of the network for a given input
		PreviousOutputs = new ArrayList<Double>();
		PreviousOutputs = CurrentOutputs;
		
	//	printWeights();
		
		ArrayList <Double> Outputs = new ArrayList <Double>();
		ArrayList <Double> neuronOutputs = new ArrayList <Double> ();
		
//		System.out.println("Neuron Outputs: ");
		for(int i=0;i<network.size();i++){ // for each layer of neurons 
//			System.out.println("Layer "+i);
			ArrayList <Double> unWeightedInputs = new ArrayList <Double>(); // outputs of previous layer before they are weighted
			
			if(i==0){ // if its the first hidden layer
				unWeightedInputs = input; 
			}
			
			else{
	//			unWeightedInputs = toBinary(neuronOutputs);
				unWeightedInputs = neuronOutputs;
			}

			neuronOutputs = new ArrayList <Double> (); // outputs from previous layer are removed 
			
			for(int j=0;j<network.get(i).size();j++){ // for each neuron in current layer
	//			System.out.println("Neuron "+j);
				network.get(i).get(j).setNetworkInputs(input);
				network.get(i).get(j).setInput(unWeightedInputs); // feeds unweighted input to current neuron
				// the inputs are weighted within the neurons getOutput() method
				double neuronOutput = network.get(i).get(j).getOutput();
				neuronOutputs.add(neuronOutput); // calculates output and adds it to array
//				System.out.print(neuronOutput+" ");
			}
	//		System.out.println();
		}
		
		Outputs = neuronOutputs; // the final output is converted to binary signal, eg 0.9 0.5 0.4 -> 1 0 0
//		System.out.println("Outputs");
//		for(int i=0; i<Outputs.size();i++){
//			System.out.println(Outputs.get(i));
//		}
		
		this.CurrentOutputs =  new ArrayList <Double>();
		this.CurrentOutputs = Outputs;
		return Outputs;
	}
	
	
	
	
	
	
	
	public void printWeights(){
		System.out.println("Network Weights");
		for(int i=0;i<network.size();i++){
			System.out.println("Layer "+i);
			for(int j=0;j<network.get(i).size();j++){
				System.out.println("Neuron "+j);
				for(int k=0;k<network.get(i).get(j).numWeights;k++){
					System.out.print(network.get(i).get(j).getW(k)+" ");
				}
				System.out.println();
			}
		}
	}
	
	
	
	
	
	public void countWeights(){
		this.w=0;
		for(int i=0;i<network.size();i++){
			for(int j=0;j<network.get(i).size();j++){
				if(network.get(i).get(j).firstHiddelLayer){
					for(int k=0;k<network.get(i).get(j).inputLayerWeights.size();k++){
						this.w++;
					}
				}
				else{
					for(int k=0;k<network.get(i).get(j).inputWeights.size();k++){
						this.w++;
					}
				}
			}
		}
	}
	
	
	
	public void createNeurons(){ // method to create neurons 
		for(int i =0;i<this.hiddenLayers;i++){ // creates hidden neurons
			ArrayList <Neuron> layer = new ArrayList <Neuron>();
			
			if(i==0){
				for(int j =0;j<this.neuronsPHL;j++){ // neurons in the first hidden layer
					layer.add(new Neuron(true, inputs, neuronsPHL, outputs));
				}
			}
			else{
				for(int j =0;j<this.neuronsPHL;j++){
					layer.add(new Neuron(false, inputs,neuronsPHL, outputs));
				}
			}
			network.add(layer); // layer of neurons added to network
		}
		ArrayList <Neuron> layer = new ArrayList <Neuron>();
		for(int i=0;i<outputs;i++){ // creates output layer of neurons
			layer.add(new Neuron(false, inputs,neuronsPHL, outputs));
			layer.get(i).setOutputLayer(true); 
		}
		network.add(layer);
	}
	
	
	

	
	public ArrayList<Double> getWeights(){ // method to get weights from neurons so they may be passed to particles for optimisation
		ArrayList<Double> weights = new ArrayList<Double>();
		for(int i=0;i<network.size();i++){
			for(int j=0;j<network.get(i).size();j++){
				if(network.get(i).get(j).firstHiddelLayer){ // if first hidden layer of neurons, need to get weights from input layer
					for(int k=0;k<network.get(i).get(j).inputLayerWeights.size();k++){
						weights.add(network.get(i).get(j).inputLayerWeights.get(k)); 
					}
				}
				
				else{ // otherwise get regular set of input weights
					for(int k=0;k<network.get(i).get(j).inputWeights.size();k++){ 
						weights.add(network.get(i).get(j).inputWeights.get(k)); 
					}
				}
				
			}
		}
		return weights;
	}
	
	
	
	
	public void setWeights(ArrayList<Double> w){ // method to set network weights 
		// this method is used when evaluating a solution (set of weights) from a particle during optimisation
		int indx = 0;
		// method has similar structure to getWeights()
		for(int i=0;i<network.size();i++){
			for(int j=0;j<network.get(i).size();j++){
				if(network.get(i).get(j).firstHiddelLayer){
					for(int k=0;k<network.get(i).get(j).inputLayerWeights.size();k++){
						network.get(i).get(j).inputLayerWeights.set(k, w.get(indx)); 
						indx++;
					}
				}
				
				else{
					for(int k=0;k<network.get(i).get(j).inputWeights.size();k++){
						network.get(i).get(j).inputWeights.set(k, w.get(indx));
						indx++;
					}
				}
				
			}
		}
	}
	
	
	
	public ArrayList<Double> readFileNum(String path){ // method to read in file of integers or doubles
		
		ArrayList<Double> contents = new ArrayList<Double>();
		
		try {
			Scanner s = null; 
			s = new Scanner(new BufferedReader(new FileReader(path))); 
			while (s.hasNext()) { 
				double n = s.nextDouble();
				contents.add(n); 
				}
			s.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		return contents;
		
	}
	
	public ArrayList<String> readFileText(String path){ 
		ArrayList<String> contents = new ArrayList<String>();
		
		try {
			Scanner s = null; 
			s = new Scanner(new BufferedReader(new FileReader(path))); 
			while (s.hasNext()) { 
				contents.add(s.nextLine()); // next integer is added to randomList
				}
			s.close();
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		return contents;
		
	}
	
	
	public ArrayList <Double> toMaxBinary(ArrayList <Double> n){ 
		// finds max in array of real numbers and converts to binary where max -> 1 and others -> 0... eg 0.9 0.7 0.7 -> 1 0 0
		ArrayList <Double> binary = new ArrayList <Double> () ;
		int maxI=0;
		
		for(int i=0;i<n.size();i++){
			if(n.get(i)>n.get(maxI)){
				maxI=i;
			}
		}
		
		for(int i=0;i<n.size();i++){
			double val;
			if(i==maxI){
				val =1.0;
			}
			else{
				val =0.0;
			}
			
			binary.add(val);
		}
		
		return binary;
	}
	
	public ArrayList <Double> toBinary(ArrayList <Double> n){ // step function
		// converts real numbers between 0 and 1 to binary
		// used for step function
		ArrayList <Double> binary = new ArrayList <Double> () ;
			
		for(int i=0;i<n.size();i++){
			double val;
			if(n.get(i)>=0.5){
				val =1.0;
			}
			else{
				val =0.0;
			}
					
			binary.add(val);
		}
				
		return binary;
		
	}

	
	
	
	
	
}





















