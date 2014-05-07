import java.util.ArrayList;


public class PseudoTree {
	TreeNode root;
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
}
