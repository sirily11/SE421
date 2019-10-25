package com.se421.paths.algorithms.counting;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.log.Log;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.se421.paths.algorithms.PathCounter;
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

		// create a directed acyclic graph (DAG)
		DAGTransform transformer = new DAGTransform();
		Q dag = transformer.transform(cfg);
		
		// the roots and leaves of the DAG
		AtlasSet<Node> dagLeaves = dag.leaves().eval().nodes();
		Node dagRoot = dag.roots().eval().nodes().one();

		// TODO: implement a O(n) solution to count paths
		
		// at the end, we have traversed all paths once, so return the count
		return new CountingResult(additions, numPaths);
	}
	
}
