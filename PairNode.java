
public class PairNode {
	Node key;
	PairNode leftChild;
	PairNode prev;
	PairNode nextSibling;
	
	public PairNode(Node key){
		this.key=key;
		this.prev=null;
		this.leftChild=null;
		this.nextSibling=null;		
	}
}
