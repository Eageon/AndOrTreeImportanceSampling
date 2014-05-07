import java.util.ArrayList;

public class TreeNode {
	boolean isOrNode = false;

	TreeNode parent;
	ArrayList<TreeNode> children = new ArrayList<>();
	double V;

	public void addChildren(TreeNode child) {
		if (!children.contains(child)) {
			children.add(child);
			child.parent = this;
		}
	}
}
