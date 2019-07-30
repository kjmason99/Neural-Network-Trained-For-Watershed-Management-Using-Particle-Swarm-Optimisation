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


public class Particle {
	double MaxPos = 1.0; // boundaries of search space for particle, ie max min values for weights
	double MinPos = -1.0;
//	double MinPos = 0.0;
	int D; // number of weights
	
	ArrayList<Double> Position = new ArrayList<Double>(); // position in each dimension
	ArrayList<Double> Velocity = new ArrayList<Double>(); // velocity in each dimension
	ArrayList<Double> bestPosition = new ArrayList<Double>(); // best position in each dimension
	public double bFitness; // best personal fitness evaluation
	public double cFitness; // current personal fitness evaluation
	
	
	public double vmax; // max velocity
	public double c2 = 2.05; // acceleration coefficients
	public double c1 = 2.05;
	public double w = 1; // inertia
	public double con = 0.7298437881; // constriction value
	
	
	public Particle(int dimensions) { // initialize particle
		this.D = dimensions;
		
		vmax = (MaxPos - MinPos) ; // max velocity
		for (int i = 0; i < D; i++) {	// defining start position and velocity
			double startPos = (double) (Math.random() *( MaxPos - MinPos) ) + MinPos;
			Position.add(startPos);
			
			double startVel = (double) (Math.random() *(MaxPos - MinPos) ) +MinPos;
			startVel = (startVel-startPos)/2;
			
			Velocity.add(startVel);
			bestPosition.add(Position.get(i));
			
		}
	
		cFitness = Double.NEGATIVE_INFINITY; // current/best fitness initialised 
		bFitness = Double.NEGATIVE_INFINITY;
	}
	
	public void setCurFitness(double f){ // updates best/current fitness
		cFitness = f;
		if(cFitness>bFitness){ 
			bFitness=cFitness;
			bestPosition = Position;
		}
	}
	
	
	public void update(ArrayList<Double> gBestPositions) { // updates velocity and position of particle

//		ArrayList<Double> gBestPos = gBestPositions;
//		if (gBestPos.size() == 0) {
//			gBestPos = Position;
//		}

		
		for (int i = 0; i < D; i++) { // updates velocities
			double vPrev = Velocity.get(i);
			double t1=(Math.random() * c1 * (bestPosition.get(i) - Position.get(i)));
			double t2=(Math.random() * c2 * (gBestPositions.get(i) - Position.get(i)));
			double newVel = con* (w*vPrev+ t1 + t2);
			
			Velocity.set(i, newVel);
		}
		
		

		for (int i = 0; i < D; i++) { // ensures velocities aren't bigger than vmax
			if (Velocity.get(i) > vmax) {
				Velocity.set(i, vmax);
			}

			else if (Velocity.get(i) < 0 - vmax) {
				Velocity.set(i, 0 - vmax);
			}
		}

		for (int i = 0; i < D; i++) { // updates position
			if ((Position.get(i) + Velocity.get(i) > 1.0)) { // checks within upper boundary
				double newPos = MaxPos- (Position.get(i) + Velocity.get(i) - MaxPos);
				Position.set(i, newPos);
				double newVel = 0 - Velocity.get(i);
				Velocity.set(i, newVel);
			}

			else if ((Position.get(i) + Velocity.get(i) < 0.0)){ // checks within lower boundary
				double newPos = MinPos+ (MinPos - (Position.get(i) + Velocity.get(i)));
				Position.set(i, newPos);
				double newVel = 0 - Velocity.get(i);
				Velocity.set(i, newVel);
			} 
			else {
				double newPos = Position.get(i) + Velocity.get(i); // particle is within acceptable region											
				Position.set(i, newPos);
			}
		}
		
	}
}
