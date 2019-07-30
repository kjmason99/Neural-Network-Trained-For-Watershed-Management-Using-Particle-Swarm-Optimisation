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
import java.util.Collections;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;

import org.ejml.data.DenseMatrix64F;

public class waterShed {
	public int D;
	public int f;
	ArrayList<Double> UpperBoundaries = new ArrayList<Double>(); // boundaries
	ArrayList<Double> LowerBoundaries = new ArrayList<Double>();

	ArrayList<Double> a = new ArrayList<Double>(); // coefficients
	ArrayList<Double> b = new ArrayList<Double>();
	ArrayList<Double> c = new ArrayList<Double>();
	ArrayList<Double> alpha = new ArrayList<Double>(); 
//	ArrayList<Double> Q1 = new ArrayList<Double>();
//	ArrayList<Double> Q2 = new ArrayList<Double>();
//	ArrayList<Double> S = new ArrayList<Double>();
	
	ArrayList<Double> Q1_training = new ArrayList<Double>();
	ArrayList<Double> Q2_training = new ArrayList<Double>();
	ArrayList<Double> S_training = new ArrayList<Double>();
	
	ArrayList<Double> Q1_test = new ArrayList<Double>();
	ArrayList<Double> Q2_test = new ArrayList<Double>();
	ArrayList<Double> S_test = new ArrayList<Double>();
	
	ArrayList<Double> Q1_total = new ArrayList<Double>();
	ArrayList<Double> Q2_total = new ArrayList<Double>();
	ArrayList<Double> S_total = new ArrayList<Double>();
	
	double training_fraction=2.0/3.0;
	
	// boundaries for x1,2,4,6 for each state -> 3 x 4 matrix
//	ArrayList <ArrayList <Double>> Max = new ArrayList <ArrayList <Double>> (); 
//	ArrayList <ArrayList <Double>> Min = new ArrayList <ArrayList <Double>> ();
	
	ArrayList <ArrayList <Double>> trainingMax = new ArrayList <ArrayList <Double>> (); 
	ArrayList <ArrayList <Double>> trainingMin = new ArrayList <ArrayList <Double>> ();
	ArrayList <ArrayList <Double>> testMax = new ArrayList <ArrayList <Double>> (); 
	ArrayList <ArrayList <Double>> testMin = new ArrayList <ArrayList <Double>> ();
	
	ArrayList<Double> F = new ArrayList<Double>(); // each objective function
	
	double x1 ;
	double x2 ;
	double x3 ; 
	double x4 ;
	double x5 ; 
	double x6 ;
	
	double f1 ;
	double f2 ;
	double f3 ; 
	double f4 ;
	double f5 ; 
	double f6 ;
	
	double v1;
	double v2;
	double v3;
	double v4;
	double v5;
	double v6;
	double v7;
	double v8;
	double v9;
	double v10;
	double v11;
	
	int curState;
	double curViolations;
	int curViolationCount;
	double violationMult=100.0;
	double violationsAllStates=0.0;
	
	int directVariables=4;
	
	public waterShed(){
		setCoefficients();
		setBoundaries();
		this.D = UpperBoundaries.size();

	}
	
	
	
	// reads in as L^3 not %, convert from % to L3 is done in optimise class
	public double FitnessCheck(int state, ArrayList <Double> x, boolean training){ // size 4 (4 direct variables) x1,x2,x4,x6___ state = 0,1,2 
		ArrayList<Double> Q1 = new ArrayList<Double>();
		ArrayList<Double> Q2 = new ArrayList<Double>();
		ArrayList<Double> S = new ArrayList<Double>();
		if (training){
			Q1=Q1_training;
			Q2=Q2_training;
			S=S_training;
		}
		else{
			Q1=Q1_test;
			Q2=Q2_test;
			S=S_test;
		}
		
		this.curState = state;
		this.x1 = x.get(0);
		this.x2 = x.get(1);
		this.x3 = Q2.get(state) - x.get(2); //x3 = Q2-x4
		this.x4 = x.get(2);
		this.x5 = x.get(1)+x3-x.get(3); // x5 = x2+x3 -x6
		this.x6 = x.get(3);
		
//		System.out.println("x1 = "+x1);
//		System.out.println("x2 = "+x2);
//		System.out.println("x3 = "+x3);
//		System.out.println("x4 = "+x4);
//		System.out.println("x5 = "+x5);
//		System.out.println("x6 = "+x6);
		
		double fitness=0;
		
		this.f1 = (a.get(0)*x1*x1) + (b.get(0)*x1)+c.get(0);
		this.f2 = (a.get(1)*x2*x2) + (b.get(1)*x2)+c.get(1);
		this.f3 = (a.get(2)*x3*x3) + (b.get(2)*x3)+c.get(2);
		this.f4 = (a.get(3)*x4*x4) + (b.get(3)*x4)+c.get(3);
		this.f5 = (a.get(4)*x5*x5) + (b.get(4)*x5)+c.get(4);
		this.f6 = (a.get(5)*x6*x6) + (b.get(5)*x6)+c.get(5);
		
		while(F.size()>0){
			F.remove(0);
		}
		F.add(f1);
		F.add(f2);
		F.add(f3);
		F.add(f4);
		F.add(f5);
		F.add(f6);
		
		for(int i=0;i<F.size();i++){
//			System.out.println("f"+i+" = "+F.get(i));
		}
		
		double v = getConstraintViolations(training);
//		System.out.println("v  = "+(v));
	//	double v = 0.0;
	//	fitness = (f1 + f2+f3+f4+f5+f6) ;
		fitness = (f1 + f2+f3+f4+f5+f6)-v ; 
//		System.out.println("Watershed fitness = "+(f1 + f2+f3+f4+f5+f6));
//		System.out.println("Watershed violations = "+(-v));
		
		return fitness;
	}
	
	
	public double getConstraintViolations(boolean training){
		ArrayList<Double> Q1 = new ArrayList<Double>();
		ArrayList<Double> Q2 = new ArrayList<Double>();
		ArrayList<Double> S = new ArrayList<Double>();
		if (training){
			Q1=Q1_training;
			Q2=Q2_training;
			S=S_training;
		}
		else{
			Q1=Q1_test;
			Q2=Q2_test;
			S=S_test;
		}
		int count = 0;
		double violation=0.0;
		
		this.v1=0.0;
		this.v2=0.0;
		this.v3=0.0;
		this.v4=0.0;
		this.v5=0.0;
		this.v6=0.0;
		this.v7=0.0;
		this.v8=0.0;
		this.v9=0.0;
		this.v10=0.0;
		this.v11=0.0;
		
		
		if(alpha.get(0)-x1>0){
			v1=alpha.get(0)-x1;
			count++;
//			System.out.println("v1");
		}
		if(alpha.get(1)-Q1.get(curState)+x1>0){
			v2=alpha.get(1)-Q1.get(curState)+x1;
			count++;
//			System.out.println("v2");
		}
		if(x2-S.get(curState)-Q1.get(curState)+x1>0){
			v3=x2-S.get(curState)-Q1.get(curState)+x1;
			count++;
//			System.out.println("v3");
		}
		if(alpha.get(3)-x3>0){
			v5=alpha.get(3)-x3;
			count++;
//			System.out.println("v5");
		}
		if(alpha.get(2)-x4>0){
			v6=alpha.get(2)-x4;
			count++;
//			System.out.println("v6");
		}
		if(alpha.get(3)-Q2.get(curState)+x4>0){
			v7=alpha.get(3)-Q2.get(curState)+x4;
			count++;
//			System.out.println("v7");
		}
		if(alpha.get(5)-x5>0){
			v9=alpha.get(5)-x5;
			count++;
	//		System.out.println("v9");
	//		System.out.println("alpha6 = "+alpha.get(5));
	//		System.out.println("x5 = "+x5);
	//		System.out.println("v9 violated by "+v9);
		}
		if(alpha.get(4)-x6>0){
			v10=alpha.get(4)-x6;
			count++;
//			System.out.println("v10");
		}
		if(alpha.get(5)-x2-x3+x6>0){
			v11=alpha.get(5)-x2-x3+x6;
			count++;
	//		System.out.println("v11");
		}
//		System.out.println("violations "+count);
		violation = (v1+v2+v3+v4+v5+v6+v7+v8+v9+v10+v11);
		
		this.curViolationCount = count;
		
		if(violation>0){
			this.curViolations = (violation+1)*violationMult;
			return (violation+1)*violationMult;
			
		}
		else{
			this.curViolations = violation;
			return violation;
		}
		
		
		
	}
	
	public ArrayList <Double> percentageToLitres(int state, ArrayList <Double> p, boolean training){ // read in state and array of percentages for x1,x2,x4,x6
		this.curState=state;
		ArrayList <Double> x = new ArrayList <Double>();
		
		if(training){
			for(int i=0;i<p.size();i++){
				double max = this.trainingMax.get(curState).get(i);
				double min = this.trainingMin.get(curState).get(i);
				double range = max-min;
				x.add((range*(p.get(i)/100.0))+min);
			}
		}
		else{
			for(int i=0;i<p.size();i++){
				double max = this.testMax.get(curState).get(i);
				double min = this.testMin.get(curState).get(i);
				double range = max-min;
				x.add((range*(p.get(i)/100.0))+min);
			}
		}
		return x;
	}
	

	
	public ArrayList <Double> fractionToLitres(int state, ArrayList <Double> p, boolean training){ // read in state and array of percentages for x1,x2,x4,x6
		this.curState=state;
		ArrayList <Double> x = new ArrayList <Double>();
		
		if(training){
			for(int i=0;i<p.size();i++){
				double max = this.trainingMax.get(curState).get(i);
				double min = this.trainingMin.get(curState).get(i);
				double range = max-min;
				x.add((range*(p.get(i)))+min);
			}
		}
		else{
			for(int i=0;i<p.size();i++){
				double max = this.testMax.get(curState).get(i);
				double min = this.testMin.get(curState).get(i);
				double range = max-min;
				x.add((range*(p.get(i)))+min);
			}
		}
		return x;
	}
	

	
	public ArrayList <Double> litresToFraction(int state, ArrayList <Double> p,boolean training){ // read in state and array of percentages for x1,x2,x4,x6
		this.curState=state;
		ArrayList <Double> x = new ArrayList <Double>();
		
		if(training){
			for(int i=0;i<p.size();i++){
				double max = this.trainingMax.get(curState).get(i);
				double min = this.trainingMin.get(curState).get(i);
				double range = max-min;
				x.add((p.get(i)-min)/range);
			}
		}
		else{
			for(int i=0;i<p.size();i++){
				double max = this.testMax.get(curState).get(i);
				double min = this.testMin.get(curState).get(i);
				double range = max-min;
				x.add((p.get(i)-min)/range);
			}
		}
		return x;
	}
	
	

	
	public ArrayList <Double> getRiverFlowRates(int state,boolean training){
		ArrayList <Double> riverFlow = new ArrayList <Double>();
		if(training){ // if on the training dataset
			riverFlow.add(Q1_training.get(state));
			riverFlow.add(Q2_training.get(state));
			riverFlow.add(S_training.get(state));
		}
		else{// test data set
			riverFlow.add(Q1_test.get(state));
			riverFlow.add(Q2_test.get(state));
			riverFlow.add(S_test.get(state));
		}
		return riverFlow;
	}
	

	
	
	public ArrayList <Double> getNormalisedRiverFlowRates(int state, boolean training){
		ArrayList <Double> normalRiverFlow = new ArrayList <Double>();
		
		double maxQ1 = Collections.max(Q1_total);
		double maxQ2 = Collections.max(Q2_total);
		double maxS = Collections.max(S_total);

		if(training){ // if on the training dataset
			normalRiverFlow.add((Q1_training.get(state))/(maxQ1));
			normalRiverFlow.add((Q2_training.get(state))/(maxQ2));
			normalRiverFlow.add((S_training.get(state))/(maxS));
		}
		else{ // test data set
			normalRiverFlow.add((Q1_test.get(state))/(maxQ1));
			normalRiverFlow.add((Q2_test.get(state))/(maxQ2));
			normalRiverFlow.add((S_test.get(state))/(maxS));
		}
		
		return normalRiverFlow;
	}
	
	
//	public ArrayList <Double> getNormalisedRiverFlowRates(int state){
//		ArrayList <Double> normalRiverFlow = new ArrayList <Double>();
//		
//		double maxQ1 = Collections.max(Q1);
//		double maxQ2 = Collections.max(Q2);
//		double maxS = Collections.max(S);
//		
//		
//		normalRiverFlow.add((Q1.get(state))/(maxQ1));
//		normalRiverFlow.add((Q2.get(state))/(maxQ2));
//		normalRiverFlow.add((S.get(state))/(maxS));
//				
//		return normalRiverFlow;
//	}
	
	
	
	
	
	public void setCoefficients(){
		
		// origional values from MAS paper
//		this.Q1.add(80.0);
//		this.Q1.add(40.0);
//	//	this.Q1.add(15.0);
//		
//		this.Q2.add(35.0);
//		this.Q2.add(20.0);
//	//	this.Q2.add(8.0);
//		
//		this.S.add(10.0);
//		this.S.add(8.0);
//	//	this.S.add(3.0);
		
		// values from ALA 2016 MARL PSO paper
//		this.Q1.add(160.0);
//		this.Q1.add(115.0);
//		this.Q1.add(80.0);
//		
//		this.Q2.add(65.0);
//		this.Q2.add(50.0);
//		this.Q2.add(35.0);
//		
//		this.S.add(15.0);
//		this.S.add(12.0);
//		this.S.add(10.0);

		String cFile = ("C:\\Users\\KarlJ\\Research\\Watershed\\Q1_proportional.txt") ;
		this.Q1_total = readFile(cFile);
		cFile = ("C:\\Users\\KarlJ\\Research\\Watershed\\Q2_proportional.txt") ;
		this.Q2_total = readFile(cFile);
		cFile = ("C:\\Users\\KarlJ\\Research\\Watershed\\S_proportional.txt") ;
		this.S_total = readFile(cFile);
		
		double trainSize=Q1_total.size()*training_fraction;
		for(int i=0;i<Q1_total.size();i++){
			if(i<trainSize){
				Q1_training.add(Q1_total.get(i));// training set is first 100 of total set 
				Q2_training.add(Q2_total.get(i));
				S_training.add(S_total.get(i));
			}
			else{
				Q1_test.add(Q1_total.get(i)); // final 50 is test set
				Q2_test.add(Q2_total.get(i));
				S_test.add(S_total.get(i));
			}
		}
		
		this.a.add(-0.20);
		this.a.add(-0.06);
		this.a.add(-0.29);
		this.a.add(-0.13);
		this.a.add(-0.056);
		this.a.add(-0.15);
		
		this.b.add(6.0);
		this.b.add(2.5);
		this.b.add(6.28);
		this.b.add(6.0);
		this.b.add(3.74);
		this.b.add(7.6);
		
		this.c.add(-5.0);
		this.c.add(0.0);
		this.c.add(-3.0);
		this.c.add(-6.0);
		this.c.add(-23.0);
		this.c.add(-15.0);
		
		this.alpha.add(12.0);
		this.alpha.add(10.0);
		this.alpha.add(8.0);
		this.alpha.add(6.0);
		this.alpha.add(15.0);
		this.alpha.add(10.0);
		
		for(int i=0;i<Q1_training.size();i++){
	//		System.out.println("State "+i);
			ArrayList <Double> tempMax = new ArrayList <Double> ();
			ArrayList <Double> tempMin = new ArrayList <Double> ();
			tempMax.add(Q1_training.get(i)-alpha.get(1)); // max for x1
			tempMin.add(alpha.get(0)); //min for x1
//			System.out.println("x1 max = "+(Q1_training.get(i)-alpha.get(1)));
//			System.out.println("x1 min = "+(alpha.get(0)));
			
			
			tempMax.add(Q1_training.get(i)+S_training.get(i)-alpha.get(0)); // max for x2
			tempMin.add(0.0); // min for x2
//			System.out.println("x2 max = "+(Q1_training.get(i)+S_training.get(i)-alpha.get(0)));
//			System.out.println("x2 min = "+0.0);
			
			tempMax.add(Q2_training.get(i)-alpha.get(3)); // max for x4
			tempMin.add(alpha.get(2)); // min for x4
//			System.out.println("x4 max = "+(Q2_training.get(i)-alpha.get(3)));
//			System.out.println("x4 min = "+(alpha.get(2)));
			
			tempMax.add(Q1_training.get(i)+Q2_training.get(i)+S_training.get(i)-alpha.get(0)-alpha.get(2)-alpha.get(5)); // max for x6
			tempMin.add(alpha.get(4)); // min for x6
//			System.out.println("x6 max = "+(Q1_training.get(i)+Q2_training.get(i)+S_training.get(i)-alpha.get(0)-alpha.get(2)-alpha.get(5)));
//			System.out.println("x6 min = "+(alpha.get(4)));
			
//			this.Max.add(tempMax);
//			this.Min.add(tempMin);
			this.trainingMax.add(tempMax);
			this.trainingMin.add(tempMin);
			
			for(int j=0;j<tempMax.size();j++){
				UpperBoundaries.add(tempMax.get(j));
				LowerBoundaries.add(tempMin.get(j));
			}
		}
		
		for(int i=0;i<Q1_test.size();i++){
			ArrayList <Double> tempMax = new ArrayList <Double> ();
			ArrayList <Double> tempMin = new ArrayList <Double> ();
			tempMax.add(Q1_test.get(i)-alpha.get(1)); // max for x1
			tempMin.add(alpha.get(0)); //min for x1
			
			tempMax.add(Q1_test.get(i)+S_test.get(i)-alpha.get(0)); // max for x2
			tempMin.add(0.0); // min for x2
			
			tempMax.add(Q2_test.get(i)-alpha.get(3)); // max for x4
			tempMin.add(alpha.get(2)); // min for x4
			
			tempMax.add(Q1_test.get(i)+Q2_test.get(i)+S_test.get(i)-alpha.get(0)-alpha.get(2)-alpha.get(5)); // max for x6
			tempMin.add(alpha.get(4)); // min for x6
			
			this.testMax.add(tempMax);
			this.testMin.add(tempMin);
		}

	}


	public void setBoundaries(){ // boundaries meaning generator operating limits
		
		
		
	}

	public ArrayList <Double> getUpperBoundaries(){
		return UpperBoundaries;
	}

	public ArrayList <Double> getLowerBoundaries(){
		return LowerBoundaries;
	}
	
	
	
	public static ArrayList<Double> readFile(String path){
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
	
	public  double quadraticEquationRoot1(double a, double b, double c) {    
	    double root1, root2; //This is now a double, too.
	    root1 = (-b + Math.sqrt(Math.pow(b, 2) - 4*a*c)) / (2*a);
	    root2 = (-b - Math.sqrt(Math.pow(b, 2) - 4*a*c)) / (2*a);
	//    System.out.println("Root 1 = "+root1+" _ Root 2 = "+root2);
	    return Math.min(root1, root2);  
	}

	

}










