package com.se421.paths.algorithms.enumeration;

import java.util.ArrayList;
import java.util.List;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.se421.paths.algorithms.PathEnumerator;
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
	@Override
	public EnumerationResult enumeratePaths(Q cfg) {
		// the total number of paths discovered
		List<List<Long>> paths = new ArrayList<List<Long>>();
		long additions = 0L;
		
		// create a directed acyclic graph (DAG)
		DAGTransform transformer = new DAGTransform();
		Q dag = transformer.transform(cfg);
		
		// the roots and leaves of the DAG
		AtlasSet<Node> dagLeaves = dag.leaves().eval().nodes();
		Node dagRoot = dag.roots().eval().nodes().one();

		// TODO: implement

		// note that the size of paths is practically restricted to integer range, 
		// but this algorithm will exhaust memory long before it reaches the max range
		// since an enumeration result enumerates one path at a time, the number of 
		// additions will be the same as the number of paths in the counting result
		return new EnumerationResult(new CountingResult(paths.size(), paths.size()), paths);
	}
	
}
