package com.se421.dataflow.queries;

import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.se421.dataflow.dependence.DataDependenceGraph;
import com.se421.dataflow.support.HelperQueries;

/**
 * This program computes all UD chains at a given program point for the specified variable.
 * 
 * @author STUDENT NAME HERE
 */

public class VariableSpecificDataDependence {

	public static Q variableSpecificDataDependence(Q selectedStatement, String variableName) {
		// TODO: 1) Ensure that your selection corresponds to a control flow statement.
		// If not handled properly, your may end up selecting a variable within the statement.
		// Use an appropriate query instead of the empty initialization. Hint: Look in HelperQueries
		Q selectedControlFlow = selectedStatement.containers().nodes(XCSG.ControlFlow_Node);
		
		// DO NOT MODIFY this section
		Q containingFunction = HelperQueries.getContainingFunction(selectedControlFlow);
		Q dfg = HelperQueries.dfg(containingFunction);
		DataDependenceGraph ddg = new DataDependenceGraph(dfg.eval());
		Q ddgQ = ddg.getGraph();
		Q dataDependenceEdges = ddgQ.edges("data-dependence");
		
		// An empty graph to store the UD chains
		Q variableSpecificDataDependenceGraph = Common.empty();
		
		// TODO: 2) You need to get the variable-specific edges using variableName
		// You will need to use the attribute "dependent-variable"
		Q variableDependenceEdges = dataDependenceEdges.selectEdge("dependent-variable", variableName);
		Q variableDependenceEdges2 = dataDependenceEdges.selectEdge("dependent-variable", variableName);
		variableDependenceEdges = variableDependenceEdges.union(variableDependenceEdges2);
		// TODO: 3) You need to use these edges to get the variable specific UD chains
		// Store them in the variable variableSpecificDataDependenceGraph
		// You may need to create intermediate variables here.
		
		Q temp =  variableDependenceEdges.reverseStep(selectedControlFlow);
		Q tDefinitions = temp.roots();
		Q temp2 = dataDependenceEdges.reverse(tDefinitions);
		variableSpecificDataDependenceGraph = temp.union(temp2);
		return variableSpecificDataDependenceGraph;
	}

}
