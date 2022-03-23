import java.util.*;


/**
 * A general tree with K children. Uses Linked List as
 * the data structure.
 */
public class KTreeLinkedList<E> implements TreeIterable<E>
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
		System.out.println(findHeight(root));
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
		int height = -1;
		if (root == null ) {
			return height;
		}
		if (root.children == null) {
			return 1;
		}
		for (Node<E> child : root.children) {
			height = Math.max(height, findHeight(child));
		}
		return height + 1;
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
	 * @param index Location
	 * @param value Value
	 * @return True if success.
	 */
	public boolean set(int index, E value)
	{
		if(index < 0){
			return false;
		} if(index == 0){
			root.data = value;
			if(value == null){
				numElem = 0; h = 0;
				capacity = 0;
			}
			return true;
		} else{
			int i = 0;
			Node<E> parentNode = null;
			Queue<Node<E>> queue = new LinkedList<>();
			queue.add(root);
			while(!queue.isEmpty() && i++ != index){
				parentNode = queue.remove();
				queue.addAll(parentNode.children);
			}if(parentNode.data == null && value != null){
				throw new InvalidTreeException();
			}else{
				if(queue.element().data == null && value != null){
					numElem++;
				}else if(queue.element().data != null && value == null){
					numElem--;
				}
				queue.element().data = value;
				return true;
			}
		}
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
		Object[] array = new Object[capacity];
		Queue<Node<E>> queue = new LinkedList<>();
		int i = 0;
		queue.add(root);
		while(!queue.isEmpty()){
			array[i++] = queue.element().data;
			queue.addAll(queue.remove().children);
		}
		return array;
	}


	public void preorder()
	{
		preorder(root);
	}

	public void postorder()
	{
		postorder(root);
	}

	public void levelOrder()
	{
		Queue<Node<E>> queue = new LinkedList<>();
		queue.add(root);
		while(!queue.isEmpty()){
			System.out.print(queue.peek().data + " ");
			queue.addAll(queue.remove().children);
		}
	}

	private void preorder(Node<E> root)
	{
		if(root == null){
			System.out.println();
			return;
		}
		System.out.print(root.data + " ");
		for(Node<E> node : root.children){
			preorder(node);
		}
	}

	private void postorder(Node<E> root)
	{
		if(root == null){
			System.out.println();
			return;
		}
		for(Node<E> node : root.children){
			postorder(node);
		}
		System.out.print(root.data + " ");
	}

	@Override
	public Iterator<E> getLevelOrderIterator()
	{
		Queue<Node<E>> iteratorQueue = new LinkedList<>();
		iteratorQueue.add(root);

		return new Iterator<E>()
		{
			int visited = 0;
			@Override
			public boolean hasNext()
			{
				return !iteratorQueue.isEmpty() && visited < numElem ;
			}

			@Override
			public E next()
			{
				Node<E> node = iteratorQueue.remove();
				if(node.data == null) {
					while (node.data == null) {
						if(node.children.size() > 0) {
							iteratorQueue.addAll(node.children);
						}
						node = iteratorQueue.remove();
					}
				}else {
					iteratorQueue.addAll(node.children);
				}
				visited++;
				return node.data;
			}
		};
	}

	@Override
	public Iterator<E> getPreOrderIterator()
	{
		Stack<Node<E>> stack = new Stack<>();
		stack.push(root);
		return new Iterator<E>()
		{
			@Override
			public boolean hasNext()
			{
				return !stack.isEmpty();
			}

			@Override
			public E next()
			{
				Node<E> node = stack.pop();
				stack.addAll(node.children);
				return node.data;
			}
		};
	}

	@Override
	public Iterator<E> getPostOrderIterator()
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
		int index = 0;
		Queue<Node<E>> queue = new LinkedList<>();
		queue.add(root);
		while(!queue.isEmpty() && index++ != i){
			queue.addAll(queue.remove().children);
		}
		E[] subtree = (E[]) new Object[capacity-i];
		int j = 0;
		Queue<Node<E>> queue1 = new LinkedList<>();
		queue1.add(queue.remove());
		while(!queue1.isEmpty()){
			subtree[j] = queue1.remove().data;
			queue.addAll(queue1.remove().children);
		}

		return subtree;
	}

	public static void main(String[] args)
	{
		System.out.println("\nTesting a 3-ary Tree\n");

		Integer[] numArray = {0,1,2,null,4,5,6,null,null,9,null,null,null};

		KTreeLinkedList<Integer> integerKTree = new KTreeLinkedList<>(numArray,3);
		System.out.println(integerKTree.get(11));
//		integerKTree.set(11,23);

		integerKTree.subtree(1);

		integerKTree.preorder();
		System.out.println();
		integerKTree.postorder();
		System.out.println();
		integerKTree.levelOrder();
		System.out.println();

		Iterator<Integer> iterator = integerKTree.getLevelOrderIterator();;
		while(iterator.hasNext()){
			System.out.print(iterator.next() + " ");
		}
		System.out.println();
		Iterator<Integer> iteratorPreOrder = integerKTree.getPreOrderIterator();;
		while(iteratorPreOrder.hasNext()){
			System.out.print(iteratorPreOrder.next() + " ");
		}
	}

}
