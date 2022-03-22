import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * A general tree with K children. Uses Linked List as
 * the data structure.
 */
public class KTreeLinkedList<E>
{

	/**
	 * Holds root of the given tree.
	 */
	private Node<E> root;
	/**
	 * Holds the location of rightmost element on the last level.
	 * of the tree
	 */
	private int lastElemIndex;
	/**
	 * Represents the branching factor of the tree.
	 */
	private int k; // branching factor
	/**
	 * Holds the value for the number of nodes in the tree.
	 */
	private int numElem;
	/**
	 * Holds the height of the tree. Starts at 0..N.
	 */
	private int h;
	/**
	 * Holds the total available space in the tree that includes null
	 * nodes.
	 */
	private int capacity;


	/**
	 * Initializes the class with an array of nodes and a branching
	 * factor.
	 * It will throw an {@link InvalidKException} if the value of {@code k < 2}.
	 * since the smallest tree can be a binary tree.
	 *
	 * @param arrayTree Array representation of the tree
	 * @param k Branching Factor
	 */
	public KTreeLinkedList(E[] arrayTree, int k)
	{
		h = 0;
		if(k < 2){
			throw new InvalidKException();
		}if(arrayTree == null){
		throw new InvalidTreeException();
	}
		this.k = k;
		capacity = arrayTree.length;
		storeToTree(arrayTree);
	}

	/**
	 * Converts the array representation of k-ary tree to a Linked List version
	 * @param arrayTree K-ary Tree
	 */
	private void storeToTree(E[] arrayTree)
	{
		for (E e : arrayTree) {
			root = insert(root, e);
			if (e != null) numElem++;
		}
	}

	/**
	 * Insert a given node in the tree
	 * @param root Root of the tree
	 * @param data Data to be added
	 * @return Root Node
	 */
	private Node<E> insert(Node<E> root, E data)
	{
		if(root == null){
			root = new Node<>(data);
			return root;
		}else {
			Queue<Node<E>> queue = new LinkedList<>();
			queue.add(root);
			while (!queue.isEmpty()) {
				Node<E> parentNode = queue.remove();
				if (parentNode.children.size() < k) {
					if(parentNode.data == null && data != null){
						throw new InvalidTreeException();
					}
					parentNode.addChildren(new Node<>(data), k);
					return root;
				} else {
					queue.addAll(parentNode.children);
				}
			}
			return root;
		}
	}

	private int findHeight(Node<E> root)
	{
		if(root == null){
			return 0;
		}
		if(root.children != null){
			for(Node<E> node : root.children){
				int tempH = findHeight(node);
				h = Math.max(h,tempH);
			}
			h++;
		}
		return h;
	}


	/**
	 * Returns the K value.
	 * @return Branching factor.
	 */
	public int getK()
	{
		return k;
	}

	/**
	 * Returns the number of nodes in the tree.
	 * @return number of nodes.
	 */
	public int size()
	{
		return numElem;
	}

	/**
	 * Returns the height of the tree.
	 * @return height.
	 */
	public int height()
	{
		return h;
	}

	/**
	 * Returns the node at the provided index.
	 *
	 * Throws an IllegalArgumentException if {@code index < 0} or node
	 * does not exist.
	 *
	 * @param i Location in tree
	 * @return  Value[i]
	 */
	public E get(int i)
	{
		if(i >= capacity) throw new IndexOutOfBoundsException("Index >= "+capacity);
		if(i == 0) return root.data;
		int index = 0;
		Queue<Node<E>> queue = new LinkedList<>();
		queue.add(root);
		while(!queue.isEmpty() && index++ != i){
			queue.addAll(queue.remove().children);
		}
		return queue.remove().data;
	}

	/**
	 * Sets/Removes nodes based on the provided location with
	 * the provided value.
	 *  If value is null then the node is removed otherwise it
	 *  simply replaces an existing node.
	 *  New nodes can also be added if the location given has a parent.
	 *
	 * {@link InvalidTreeException} is thrown if attempting to remove node
	 * with children or create a node without any parents
	 *
	 * @param i Location
	 * @param value Value
	 * @return True if success.
	 */
	public boolean set(int i, E value)
	{
		return false;
	}

	/**
	 * Creates an object array of the tree.
	 * It contains the same values as the original tree.
	 * However, if it is found that there is a null level, that level
	 * will not be in the resulting array.
	 * @return Object[]
	 */
	public Object[] toArray()
	{
		return null;
	}

	/**
	 * Creates an expanded version of tree with accurate representation of
	 * the levels. It reflects the height as well as the number of nodes, including
	 * null nodes, in the tree. It returns a tree that perfect.
	 * @return Expanded tree
	 */
	@Override
	public String toString()
	{
		return "KTreeLinkedList{" +
				"root=" + root +
				", k=" + k +
				", numElem=" + numElem +
				", h=" + h +
				", capacity=" + capacity +
				'}';
	}

	/**
	 * Static method of the class that accepts a K-ary encoding tree and a coded message.
	 * Both the tree and the message are assumed to be error free.
	 *
	 * This method evaluates each value of the coded message as an instruction to go to a
	 * certain child in the tree.
	 * Only the leaf nodes contain information
	 *
	 * @param tree Encoding tree
	 * @param codedMessage  Secret Message
	 * @return  Decoded message
	 */
	public static String decode(KTree<String> tree, String codedMessage)
	{
		return "";
	}

	/**
	 * Returns the subtree from the given node location
	 * @param i Location
	 * @return null
	 */
	public E[] subtree(int i)
	{
		return null;
	}

	public E[] mirror()
	{
		return null;
	}

	public static void main(String[] args)
	{
		System.out.println("\nTesting a 3-ary Tree\n");

		Integer[] numArray = {0,1,2,3,4,5,6,null,null,9,null,11,null};

		KTreeLinkedList<Integer> integerKTree = new KTreeLinkedList<>(numArray,3);
		System.out.println(integerKTree.get(11));
	}

}
