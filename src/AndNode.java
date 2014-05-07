
public class AndNode extends TreeNode {
	int value = -1;
	
	public AndNode(int value) {
		this.value = value;
		super.isOrNode = false;
	}
}
