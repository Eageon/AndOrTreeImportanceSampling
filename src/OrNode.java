import java.util.ArrayList;


public class OrNode extends TreeNode {
	ArrayList<Double> arcWeight;
	ArrayList<Integer> count;
	ArrayList<Factor> cluster;
	
	Variable nodeVariable;
	boolean alreadySplitted = false;
	double value;  // for virtual use
	
	public OrNode(Variable var) {
		nodeVariable = var;
		super.isOrNode = true;
		super.V = 0;
	}
}
