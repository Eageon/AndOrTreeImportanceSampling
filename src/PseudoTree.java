import java.util.ArrayList;


public class PseudoTree {
	OrNode root;
	GraphicalModel model;
	ArrayList<OrNode> orNodes;
	
	public PseudoTree(GraphicalModel model) {
		this.model = model;
	}
	
	public void generatePseudoTree() {
		ArrayList<Integer> softOrder = model.computeSoftOrder();
		ArrayList<ArrayList<Factor>> clusters = model.generateSoftClusters(softOrder);
		ArrayList<Variable> order = model.orderIntToOrderVar(softOrder);
		
		orNodes = new ArrayList<>(order.size());
		for (Variable variable : order) {
			orNodes.add(new OrNode(variable));
		}
		
		root = orNodes.get(orNodes.size() - 1);
		for (int i = 0; i < order.size(); i++) {
			Variable thisVariable = order.get(i);
			ArrayList<Factor> thisCluster = clusters.get(i);
			OrNode thisOrNode = orNodes.get(i);
			
			ArrayList<Variable> tmpVars = new ArrayList<>();
			for (Factor factor : thisCluster) {
				for (Variable var : factor.variables) {
					if(var != thisVariable) {
						if(!tmpVars.contains(var)) {
							tmpVars.add(var);
						}
					}
				}
			}
			Factor newFactor = new Factor(tmpVars);
			
			for (int j = i + 1; j < order.size(); j++) {
				Variable thatVariable = order.get(j);
				OrNode thatOrNode = orNodes.get(j);
				ArrayList<Factor> thatCluster = clusters.get(j);
				
				if(newFactor.inScope(thatVariable)) {
					thatOrNode.addChildren(thisOrNode);
					thatCluster.add(newFactor);
					break;
				}
			}
		}
	}
	
	public int treeHeight() {
		return treeHeight(root);
	}
	
	public int treeHeight(TreeNode node) {
		if(null == node) {
			return 0;
		}
		
		int height = 0;
		for (TreeNode child : node.children) {
			int tmp = treeHeight(child);
			if(tmp > height) {
				height = tmp;
			}
		}
		
		return height;
	}
	
	public void splitOrNodes() {
		splitOrNodes(root);
	}
	
	public void splitOrNodes(OrNode orNode) {
		if(null == orNode) {
			return;
		}
		
		if (orNode.alreadySplitted) {
			return;
		}
		
		// backup of Or children
		ArrayList<TreeNode> orChildren = orNode.children;
		ArrayList<Variable> varChildren = new ArrayList<>();
		for (TreeNode treeNode : orChildren) {
			varChildren.add(((OrNode)treeNode).nodeVariable);
		}
		// split to And children
		orNode.children = new ArrayList<>(orNode.nodeVariable.domainSize());
		for (int i = 0; i < orNode.nodeVariable.domainSize(); i++) {
			orChildren.add(new AndNode(i));
		}
		
		for (TreeNode andNode : orNode.children) {
			splitAndNodes(varChildren, (AndNode)andNode);
		}
	}
	
	public void splitAndNodes(ArrayList<Variable> vars, AndNode andNode) {
		for (Variable var : vars) {
			andNode.addChildren(new OrNode(var));
		}
		
		for (TreeNode orNode : andNode.children) {
			splitOrNodes((OrNode)orNode);
		}
		
		if (vars.size() == 0) {
			andNode.V = 1;
		}
	}
}
