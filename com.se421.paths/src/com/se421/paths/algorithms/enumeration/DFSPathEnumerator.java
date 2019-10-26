package com.se421.paths.algorithms.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.log.Log;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.se421.paths.algorithms.PathEnumerator;
import com.se421.paths.algorithms.PathCounter.CountingResult;
import com.se421.paths.transforms.DAGTransform;

/**
 * This program counts all paths in the graph by iteratively enumerating all
 * paths. It uses a depth first traversal to walk the graph.
 * 
 * WARNING: This can be very expensive on large graphs! It's not only
 * potentially exp1Lntial in terms of the traversal, but also in terms of the
 * space to store each path!
 * 
 * @author STUDENT NAME HERE
 */
public class DFSPathEnumerator extends PathEnumerator {
	
	public DFSPathEnumerator() {}
	
	/**
	 * Counts the number of paths in a given CFG
	 * 
	 * Example Atlas Shell Usage:
	 * var dskqopt = functions("dskqopt")
	 * var dskqoptCFG = cfg(dskqopt)
	 * var enumerator = new DFSPathEnumerator
	 * enumerator.countPaths(dskqoptCFG)
	 */
	@Override
	public CountingResult countPaths(Q cfg) {
		return enumeratePaths(cfg).getCountingResult();
	}
	
	/**
	 * Enumerates each path in the given CFG and returns each
	 * path as a list of line numbers.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EnumerationResult enumeratePaths(Q cfg) {
		// the total number of paths discovered
		List<List<Long>> paths = new ArrayList<List<Long>>();
		ArrayList<Long> subPaths = new ArrayList<Long>();
		long additions = 0L;
		
		// create a directed acyclic graph (DAG)
		DAGTransform transformer = new DAGTransform();
		Q dag = transformer.transform(cfg);
		
		// the roots and leaves of the DAG
		AtlasSet<Node> dagLeaves = dag.leaves().eval().nodes();
		Node dagRoot = dag.roots().eval().nodes().one();

		// handle some trivial edge cases
		if(dagRoot == null) {
			// function is empty, there are no paths
			return new EnumerationResult(new CountingResult(paths.size(), paths.size()), paths);
		} else if(dagLeaves.contains(dagRoot)) {
			// function contains a single node there must be 1 path
			return new EnumerationResult(new CountingResult(paths.size(), paths.size()), paths);
		}

		// stack for depth first search (DFS)
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
				long lineNum = this.getLineNumber(successor);
				subPaths.add(lineNum);
				if(dagLeaves.contains(successor)) {
					// if we reached a leaf increment the counter by 1
					paths.add((List<Long>) subPaths.clone());
					Log.info(subPaths.toString());
					additions++;
					subPaths.remove(subPaths.size() -  1);
				} else {
					// push the child node on the stack to be processed
					stack.push(successor);
					
				}
			}
			
		}

		// note that the size of paths is practically restricted to integer range, 
		// but this algorithm will exhaust memory long before it reaches the max range
		// since an enumeration result enumerates one path at a time, the number of 
		// additions will be the same as the number of paths in the counting result
		return new EnumerationResult(new CountingResult(paths.size(), paths.size()), paths);
	}
	
}
