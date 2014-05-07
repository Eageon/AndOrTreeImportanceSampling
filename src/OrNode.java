import java.util.ArrayList;


public class OrNode extends TreeNode {
	ArrayList<Double> arcWeight;
	ArrayList<Integer> count;
	
	Variable nodeVariable;
	
	public OrNode(Variable var) {
		nodeVariable = var;
	}
}
