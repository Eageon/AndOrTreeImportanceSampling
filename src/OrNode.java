import java.util.ArrayList;


public class OrNode extends TreeNode {
	ArrayList<Double> arcWeight;
	ArrayList<Integer> count;
	
	Variable nodeVariable;
	boolean alreadySplitted = false;
	
	public OrNode(Variable var) {
		nodeVariable = var;
		super.isOrNode = true;
		super.V = 0;
	}
}
