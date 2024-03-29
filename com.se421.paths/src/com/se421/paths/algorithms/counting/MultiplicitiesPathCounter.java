package com.se421.paths.algorithms.counting;

import java.util.HashMap;
import java.util.Stack;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.log.Log;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.se421.paths.algorithms.PathCounter;
import com.se421.paths.algorithms.PathCounter.CountingResult;
import com.se421.paths.transforms.DAGTransform;

/**
 * This program counts all paths in a CFG by counting path multiplicities.
 * This implementation runs in O(n) time.
 * 
 * @author STUDENT NAME HERE
 */
public class MultiplicitiesPathCounter extends PathCounter {
	
	public MultiplicitiesPathCounter() {}

	/**
	 * Counts the number of paths in a given CFG
	 * 
	 * Example Atlas Shell Usage:
	 * var dskqopt = functions("dskqopt")
	 * var dskqoptCFG = cfg(dskqopt)
	 * var mCounter = new MultiplicitiesPathCounter
	 * mCounter.countPaths(dskqoptCFG)
	 * 
	 * @param cfg
	 * @return
	 */
	public CountingResult countPaths(Q cfg) {
		// the total number of paths discovered
		// and the number of additions required to count the path
		long numPaths = 0;
		long additions = 0;
		
		HashMap<Node, Long> multiplicities = new HashMap<Node, Long>();
		HashMap<Node, Long> triggers = new HashMap<Node, Long>();
		
		// create a directed acyclic graph (DAG)
		DAGTransform transformer = new DAGTransform();
		Q dag = transformer.transform(cfg);
		// the roots and leaves of the DAG
		AtlasSet<Node> dagLeaves = dag.leaves().eval().nodes();
		Node dagRoot = dag.roots().eval().nodes().one();
		// handle some trivial edge cases
		if(dagRoot == null) {
			// function is empty, there are no paths
			return new CountingResult(0L,0L);
		} else if(dagLeaves.contains(dagRoot)) {
			// function contains a single node there must be 1 path
			return new CountingResult(0L,1L);
		}


		Stack<Node> stack = new Stack<Node>();

		// start searching from the root
		stack.push(dagRoot);
		// depth first search on directed acyclic graph
		while (!stack.isEmpty()) {
			// next node to process
			Node currentNode = stack.pop();
		
			// get the children of the current node
			// note: we iterate by edge in case there are multiple edges from a predecessor to a successor
			for (Edge outgoingEdge : dag.forwardStep(Common.toQ(currentNode)).eval().edges()) {
				Node successor = outgoingEdge.to();
				Long ms;
				Long ts;
				Long mv;
				// Number of edges from v to s
				Long numEdges = dag.betweenStep(Common.toQ(currentNode), Common.toQ(successor)).eval().edges().size();
				
				if(multiplicities.containsKey(successor)) {
					ms = multiplicities.get(successor);
				} else {
					ms = (long) 0;
				}
				
				if(multiplicities.containsKey(currentNode)) {
					mv = multiplicities.get(currentNode);
					
				} else {
					if(currentNode.equals(dagRoot)) {
						mv = (long) 1;
					} else {
						mv = (long) 0;
					}
				}
				
				if(triggers.containsKey(successor)) {
					ts = triggers.get(successor);
				} else {
					ts = dag.reverseStep(Common.toQ(successor)).eval().edges().size();
				}
				
				ms = ms + numEdges * mv;
				ts = ts - numEdges;
				triggers.put(successor, ts);
				multiplicities.put(successor, ms);
				Log.info("MS: " + ms + " NumEdges: " + numEdges + " MV: " + mv + " TS: " + ts);
				if(ts == 0) {
					if(dagLeaves.contains(successor)) {
						// if we reached a leaf increment the counter by 1
						numPaths = numPaths + ms;
						additions++;
					} else {
						// push the child node on the stack to be processed
						stack.push(successor);
					}
				}			
			}
		}
		
		// at the end, we have traversed all paths once, so return the count
		return new CountingResult(additions, numPaths);
	}
	
}
