import java.util.ArrayList;

public class TreeNode {
	boolean isOrNode = false;

	ArrayList<TreeNode> children = new ArrayList<>();
	double V;

	public void addChildren(TreeNode child) {
		if (!children.contains(child)) {
			children.add(child);
		}
	}
}
