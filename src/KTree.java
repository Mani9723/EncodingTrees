import java.util.Iterator;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Stack;

/**
 * This class creates a generic K-ary tree that implements the {@link TreeIterable} class.
 * It uses an array based representation of the tree rather than a linked
 * based version. Since this class implements TreeIterable interface,
 * methods that return an Iterator for the three different kinds of tree
 * traversals have to be implemented. PreOrder, PostOrder and LevelOrder.
 *
 *
 * @param <E> Any Type
 */
public class KTree<E> implements TreeIterable<E>
{
	/**
	 * Holds the location of rightmost element on the last level.
	 * of the tree
	 */
	private int lastElemIndex;

	/**
	 * Represents the branching factor of the tree.
	 */
	private int k;

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
	 * Holds a deep copy of the given tree.
	 */
	private E[] internalStorage;

	/**
	 * Initializes the class with an array of nodes and a branching
	 * factor.
	 * It will throw an {@link InvalidKException} if the value of {@code k < 2}.
	 * since the smallest tree can be a binary tree.
	 *
	 * @param arrayTree Array representation of the tree
	 * @param k Branching Factor
	 */
	@SuppressWarnings("unchecked")
	public KTree(E[] arrayTree, int k)
	{
		if (k < 2)
			throw new InvalidKException();
		internalStorage = (E[]) new Object[arrayTree.length];
		this.k = k;
		deepCopy(internalStorage, arrayTree);
		capacity = internalStorage.length;
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
		if (i < 0)
			throw new IllegalArgumentException("Index < 0");
		E value = internalStorage[i];
		if (value == null)
			throw new IllegalArgumentException("Null Value");
		return value;
	}

	/**
	 * Sets/Removes nodes based on the provided location with
	 * the provided value.
	 *  If value is null then the node is removed otherwise it
	 *  simply replaces an exisiting node.
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
		if (i < 0) {
			return false;
		} else if (value == null) { // Asked to remove node
			if (hasChildren(i) || i > capacity){
				return false;
			} else if(internalStorage[i] == null){
				return false;
			} else {
				internalStorage[i] = null;
				--numElem;
				findLastElem();
				return true;
			}
		} else if (i < capacity) {      // replace or create node
			if (internalStorage[i] == null && hasParent(i)) {
				internalStorage[i] = value;
				++numElem;
				return true;
			} else {
				internalStorage[i] = value;
				return true;
			}
		} else {
			if (i >= capacity && hasParent(i)) {     // adding a new level
				internalStorage = growArray();       // expanding by only adding another level
				internalStorage[i] = value;
				numElem++;
				lastElemIndex = i;
				updateNewHeight();      // Since a level a created the height needs to be updated.
				return true;
			} else if (!hasParent(i)) {
				throw new InvalidTreeException();
			}
		}
		return false;
	}

	/**
	 * Helper method that calculates the height
	 * based on the known location of a node at the lowest level
	 * of the tree.
	 */
	private void updateNewHeight()
	{
		int index = lastElemIndex, currH = 0;

		while (index != 0) {
			if (internalStorage[index] != null) {
				index = (index - 1) / k;        // Go to its parent until root is reached
				currH++;
			}
		}
		setH(currH);
	}

	/**
	 * Creates a private copy of the tree array so that the source
	 * is independent of the destination.
	 * @param dest  Array being copied to
	 * @param src   Array to copy
	 */
	private void deepCopy(E[] dest, E[] src)
	{
		for (int i = 0; i < src.length; i++) {
			dest[i] = src[i];
			if (src[i] != null) {
				numElem++;          // simultaneously calculate # of elems and find lastIndex
				lastElemIndex = i;
			}
		}
		updateNewHeight();
	}

	/**
	 * Helper method that finds the index of the rightmost
	 * node in the tree. mainly to aid in calculating the height
	 * of the tree.
	 */
	private void findLastElem()
	{
		for(int i = 0; i < capacity; i++){
			if(internalStorage[i] != null){
				lastElemIndex = i;
			}
		}
		updateNewHeight();
	}

	/**
	 * Helper method that calculates the number of spaces needed for
	 * the new array being created in the {@link #toArray()} method.
	 * Primarily to avoid storing a null level.
	 * @return  # of spaces
	 */
	private int calcNumSpacesNeeded()
	{
		int len = 0;
		int nodesInLvl = 1, nodesPrinted = 0;
		for (int i = 0; i < capacity; i++) {
			if (nodesPrinted == nodesInLvl) {
				if (i >= lastElemIndex) break;
				nodesPrinted = 0;
				nodesInLvl *= k;        // calc # of nodes in a particular level based on branching factor
			}
			nodesPrinted++;
			len++;
		}
		return len;
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
		if (numElem == 0)
			return null;
		int len = calcNumSpacesNeeded();
		Object[] array = new Object[len];
		System.arraycopy(internalStorage,0,array,0,len);
		return array;
	}

	/**
	 * Creates an expanded version of tree with accurate representation of
	 * the levels. It reflects the height as well as the number of nodes, including
	 * null nodes, in the tree. It returns a tree that perfect.
	 * @return Expanded tree
	 */
	public String toString()
	{
		if(numElem == 0)
			return null;
		StringBuilder treeString = new StringBuilder();
		int numNodesInLevel = 1, nodesPrinted = 0;
		for (int i = 0; i < capacity; i++) {
			if (nodesPrinted == numNodesInLevel) { // move to next level
				if(i >= lastElemIndex) break;
				nodesPrinted = 0;
				numNodesInLevel *= k;
				treeString.append("\n");
			}
			treeString.append(internalStorage[i]).append(" ");
			nodesPrinted++;
		}
		return treeString.toString();
	}

	/**
	 * Setter method that sets the height of the tree.
	 * @param val Height
	 */
	private void setH(int val)
	{
		h = val;
	}

	/**
	 * Returns an Iterator that traverses the tree in level order.
	 * The iterator only goes through the nodes that exist.
	 * @return Level order iterator
	 */
	@Override
	public Iterator<E> getLevelOrderIterator()
	{
		return new Iterator<E>()
		{
			int elemsIterated = 0, totalElements = size();
			int currIndex = 0;

			@Override
			public boolean hasNext()
			{
				return elemsIterated < totalElements;
			}
			@Override
			public E next()
			{
				E value = internalStorage[currIndex];
				while(value == null && ++currIndex <= capacity-1){ // skip null nodes
					value = internalStorage[currIndex];
				}
				currIndex++; elemsIterated++;
				return value;
			}
		};
	}

	/**
	 * Uses the LevelOrderIterator to provide a string
	 * that contains the tree in level order. This excludes the null nodes
	 * as well.
	 *
	 * @return Level Order Tree
	 */
	public String toStringLevelOrder()
	{
		StringBuilder levelOrder = new StringBuilder();
		Iterator<E> iterator = getLevelOrderIterator();
		while (iterator.hasNext()) {
			levelOrder.append(iterator.next()).append(" ");
		}
		return levelOrder.toString();
	}

	/**
	 * Returns a PostOrder Iterator
	 * @return Iterator
	 */
	@Override
	public Iterator<E> getPostOrderIterator()
	{
		return new Iterator<E>()
		{
			int elemsIterated = 0, totalElements = size();
			int currIndex = 0;
			@Override
			public boolean hasNext()
			{
				return elemsIterated < totalElements;
			}

			@Override
			public E next()
			{

				return null;
			}
		};
	}

	/**
	 * Returns a string representation of post order traversal of tree
	 * @return String
	 */
	public String toStringPostOrder()
	{
		return null;
	}

	/**
	 * Return a Preorder iterator
	 * @return Iterator
	 */
	@Override
	public Iterator<E> getPreOrderIterator()
	{
		return new Iterator<E>()
		{
			int elemsIterated = 0;
			final int totalElements = size();
			int i = 0;
			Stack<E> stack = new Stack<>();

			@Override
			public boolean hasNext()
			{
				return elemsIterated < totalElements;
			}
			@Override
			public E next()
			{
				E value = internalStorage[i];
				while(value == null && ++i <= capacity-1){
					value = internalStorage[i];
				}
				i++; elemsIterated++;
				return value;
			}
		};
	}

	/**
	 * Returns string representation of PreOrder traversal
	 * @return String
	 */
	public String toStringPreOrder()
	{
		return null;
	}

	/**
	 * Static method used in conjunction with the {@link #decode(KTree, String)} method.
	 *
	 * Calculates the ith child of node based on the branching factor.
	 *
	 * @param k Branching factor
	 * @param currIndex current node
	 * @param childIndex ith child
	 * @return index of the ith child of current node
	 */
	private static int goToChild(int k, int currIndex, int childIndex)
	{
		return (k*currIndex) + (childIndex+1);
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
		Object[] objArr = tree.toArray();
		StringBuilder message = new StringBuilder();
		int codedMessLen = codedMessage.length();
		int currMessIndex = 0, currTreeIndex = 0,currInstruction;
		boolean foundLetter = false;

		while(currMessIndex < codedMessLen){
			currInstruction = Integer.parseInt(codedMessage.substring(currMessIndex,currMessIndex+1));
			if(foundLetter){
				currTreeIndex = 0;  // go back to root
				foundLetter = false;
			}
			currTreeIndex = goToChild(tree.k,currTreeIndex,currInstruction);
			if(!tree.hasChildren(currTreeIndex)){
				message.append(objArr[currTreeIndex]);  // message found, add to stringbuilder
				foundLetter = true;
			}
			currMessIndex++;
		}
		return message.toString();
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

	/**
	 * Returns the mirror of the tree
	 * All the values are flipped
	 * @return Mirrored Tree
	 */
	@SuppressWarnings("unchecked")
	public E[] mirror()
	{
		E[] mirrorArray = (E[]) new Object[capacity];
		int nodesInLvl = 1, nodesStored = 1;
		int mirrIndex = 0, currIndex = 0;
		mirrorArray[0] = internalStorage[0];

		for(int i = 1; i < capacity; i++){
			if(nodesStored == nodesInLvl){
				mirrIndex += nodesInLvl *=k;// holds the index of last node at a level
				currIndex = mirrIndex;      // modify only the index
				nodesStored = 0;
			}
			mirrorArray[i] = internalStorage[currIndex];
			nodesStored++; currIndex--;
		}
		return mirrorArray;
	}

	/**
	 * Helper method that checks if the node at the provided index has children.
	 * @param index Location of potential parent
	 * @return True if it has children
	 */
	private boolean hasChildren(int index)
	{
		for (int i = 0; i <= k; i++)
			if ((index * k) + (i + 1) >= capacity) {    //if i > available space for  nodes; false
				return false;
			} else if (internalStorage[(index * k) + (i + 1)] != null)
				return true;
		return false;
	}

	/**
	 * Checks if node at provided location has a parent.
	 * If root index is provided, then false is returned.
	 * @param index Location
	 * @return True if node has parent
	 */
	private boolean hasParent(int index)
	{
		if (index == 0) return false;
		else {
			if (index > (capacity * k + 1)) return false;   // index is greater than the last level
			else return internalStorage[(index - 1) / k] != null;
		}
	}

	/**
	 * Grows the array.
	 * Every tre provided has the right number of nodes to make it perfect.
	 * In order to keep that structure, this method only add another level to
	 * the tree based on the branching factor of the tree.
	 * @return Array with a new level.
	 */
	@SuppressWarnings("unchecked")
	private E[] growArray()
	{
		int numValsToCopy = capacity;
		capacity = (capacity * k) + 1;
		E[] temp = (E[]) new Object[capacity];
		System.arraycopy(internalStorage, 0, temp, 0, numValsToCopy);
		return temp;
	}


	/* EDIT THIS MAIN METHOD FOR TESTS. PUT */
	/* HELPER TEST METHODS IN THIS SECTION  */
	/* AS WELL. TESTS REQUIRED FOR FULL     */
	/* CREDIT.                              */


	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		//change this method around to test!
		methodSigCheck();
//
//		/*
//		* **************** T E S T I N G**************
//		 * **/
		System.out.println("\n\nPrivate Testing\n\n");

		System.out.println("Testing a Binary Tree");

		String[] arr = {"_","A","_","N","B",null,null};
		String[] nullLevelarr = {"_","A","_",null,null,null,null};

		KTree<String> lastLevel = new KTree<>(nullLevelarr,2);
		/*
		* **** toString() with null level****/

		String w = lastLevel.toString();
		System.out.println(w);      // Should not include null level

		/*
		* ********* toArray() with null level*********/
		Object[] array = lastLevel.toArray();   // should not print null level
		for(Object object: array){
			System.out.print(object + " ");
		}
		System.out.println();

		/*
		* ***********Constructor if it throws exception ******/
		try{
			KTree<String> faultyTree = new KTree<>(new String[] {"_",null,"A"},1);
		}catch (InvalidKException e){
			System.out.println("Requirement K > 2");
		}

		/*
		* ***** capacity, size height and get methods***/
		KTree<String> tree1 = new KTree<>(arr,2);
		System.out.println(tree1.capacity);     // 7
		System.out.println(tree1.size());       // 5
		System.out.println(tree1.height());     // 2

		System.out.println(tree1.getK());       // 2

		/*
		* mirror() method
		 */
		Object[] mirrored = lastLevel.mirror();     // "_","_","A",null,null,null,null
		for(int i = 0; i < mirrored.length;i++){
			System.out.print(mirrored[i] + " ");
		}
		System.out.println();
		/*
		 * Private hasChildren, hasParent
		 */
		System.out.println(tree1.hasChildren(0));   // true
		System.out.println(tree1.hasChildren(1));   // true
		System.out.println(tree1.hasChildren(2));   // false
		System.out.println(tree1.hasParent(1));     // true
		System.out.println(tree1.hasParent(0));     // false
		/*
		* ******General print of the the tree**********/
		for(int i = 0; i < tree1.capacity; i++){    // _ A _ N B null null
			try {
				System.out.print(tree1.get(i) + " ");
			}catch (IllegalArgumentException e){
				System.out.print(" null ");
			}
		}
		System.out.println();

		/*
		* *******Set() toString() **********/
		System.out.println(tree1.set(0,"Root"));    // "Root" "A"....null
		System.out.println(tree1);              // Root\n A _\n N B null null
		System.out.println(tree1.set(14, null));   // False
		try {
			System.out.println(tree1.set(12, "Crazy"));   // throws InvalidTreeException
		}catch (InvalidTreeException e){
			System.out.println("can't add node with no parents");
		}
		System.out.println(tree1.set(6,"R"));   // True

		/*
		remove node
		 */
		System.out.println(tree1.set(0,null));  // False can't remove root

		System.out.println(tree1.set(8,"C"));   // True
		System.out.println(tree1.height());     // 3
		System.out.println(tree1.toString());   // Root\n A _\n N B null R\n null C null null null null null null
		System.out.println(tree1.size());       // 7

		/*
		* ***********toArray() *********/
		Object[] tree1Arr = tree1.toArray();    // Root A _ N B null R null C null null null null null null
		for(Object object : tree1Arr){
			System.out.print(object + " ");
		}
		System.out.println();

		/*
		* ****set, height, size toString() goToChild setH**********/
		System.out.println(tree1.set(8,null));  // Removes C
		System.out.println(tree1.height());     // 2
		System.out.println(tree1.size());       // 6
		System.out.println(tree1.toString());   // Root\n A _\n N B R C
		System.out.println(goToChild(tree1.getK(),0,1)); // _

		int org = tree1.height(); //2
		tree1.setH(2134);
		System.out.println(tree1.height()); // 2134
		tree1.setH(org);
		System.out.println(tree1.height()); // 2

		/*
		* toArray()
		 */
		tree1Arr = tree1.toArray();             // toArray essentially level order
		for(Object object : tree1Arr){
			System.out.print(object + " ");
		}
		System.out.println();

		/*
		  * Level order iterators
		 */
		Iterator<String> itr1 = tree1.getLevelOrderIterator();      // get iterator
		while(itr1.hasNext()){
			System.out.print(itr1.next() + " ");
		} // Print level order w/out null values
		System.out.println();
		/*
			Tostringlevelorder()
		 */
		String levelOrderStr = tree1.toStringLevelOrder(); // same as last test
		System.out.println(levelOrderStr);

		/*
			3-ary tree
		 */
		System.out.println("\nTesting a 3-ary Tree\n");

		Integer[] numArray = {0,1,2,null,4,5,6,null,null,9,null,null,null};

		KTree<Integer> integerKTree = new KTree<>(numArray,3);
		/*
		*   Level order iterator() and printing
		*
		 */
		Iterator<Integer> itr2 = integerKTree.getLevelOrderIterator();
		while(itr2.hasNext()){      // print all nodes excluding null
			System.out.print(itr2.next() + " ");
		}
		System.out.println();

		/*
		* mirror()
		 */
		Object[] mirr = integerKTree.mirror();  // 0, null, 2, 1, null,null,null,9,null,null,6,5,4
		for(Object o : mirr){
			System.out.print(o + " ");
		}
		System.out.println();

		/*
		* height, size capacity, getK()
		 */
		System.out.println(integerKTree.height());  //2
		System.out.println(integerKTree.size());    //7
		System.out.println(integerKTree.capacity);  //13
		System.out.println(integerKTree.getK());    // 3

		String treeRep = integerKTree.toString();   // expanding tree version
		/*
		* indirect toString()
		 */
		System.out.println(treeRep);                // print
		/*
		* toStringlevelOrder
		 */
		levelOrderStr = integerKTree.toStringLevelOrder();  // print all nodes w/out null
		System.out.println(levelOrderStr);

		System.out.println("Will make a perfect 3-ary tree now\nby filling all null nodes");

		/*
		* Make a perfect tree for k = 3 using a nearly complete tree
		* Testing set and get
		 */
		for(int i = 0; i<integerKTree.capacity;i++){    // create a perfect tree, create nodes wherever null
			try {
				integerKTree.get(i);
			}catch (IllegalArgumentException e){
				integerKTree.set(i,(int)(Math.random()*10));    // make a new node with random int
			}
		}
		System.out.println(integerKTree.height());  // still be 2
		System.out.println(integerKTree.size());    // 13

		/*
		* LevelOrder Iterator
		 */
		Iterator<Integer> perfectItr = integerKTree.getLevelOrderIterator();
		while(perfectItr.hasNext()){                // get an iterator and print nodes w/out null
			System.out.print(perfectItr.next() + " ");
		}
		System.out.println();

		/*
		* toString()
		 */
		String perfTree = integerKTree.toString();
		System.out.println(perfTree);           // print expanded version

		/*
		* set, height size and corresponding toString()(levelorder) calls
		 */
		System.out.println(integerKTree.set(13,45));    // True new level added
		System.out.println(integerKTree.height());      // 3
		System.out.println(integerKTree.size());        //14

		System.out.println(integerKTree.toString());
		System.out.println(integerKTree.toStringLevelOrder());

		/*
		* Null level with toString and toArray
		 */
		System.out.println(integerKTree.set(13,null));         // set last level to null
		System.out.println(integerKTree.toString());// should not include last null level
		System.out.println(integerKTree.lastElemIndex);
		Object[] another = integerKTree.toArray();      // Should exclude null level
		for(Object object : another){
			System.out.print(object + " ");
		}
		System.out.println();

		/*
		* Decode method with 3-ary tree
		 */
		//Holds my G# 00974705
		String[] codedTree = {"_","_",null,"_","_",null,"_",null,null,null,null,"_"
				,"_","0",null,"7",null,null,null,"9",null,null,null,null,null,null,null,null,null
				,null,null,null,null,null,null,"5",null,null,"4",null};

		KTree<String> secretTree = new KTree<>(codedTree,3);

		/*
		* tostring(), height, size,k
		 */
		System.out.println(secretTree.toString());   // _\n _ null _\n_ null _ null null null null _ _\n
													// 0 null 7 null null null 9 null null null null null null null null
													//null null null null null null null 5 null null 4 null
		System.out.println(secretTree.height());        // 3
		System.out.println(secretTree.size());          // 12
		System.out.println(secretTree.getK());          // 2
		System.out.println(secretTree.get(38));          //4

		/*
		* actually decode
		* should print 00974705
		 */
		String decodedGNumber = decode(secretTree,"000000020002221002000211");
		System.out.println(decodedGNumber);     //Should Print 00974705

		/*
		* Testing tree with only one node
		 */
		String[] empty = {"Hello",null,null,null,null,null};
		KTree<String> emptytree = new KTree<>(empty,5);

		/*
		* toString, levelorder as well
		 */
		System.out.println(emptytree.toString()); // only print root
		System.out.println(emptytree.toStringLevelOrder()); // only print root

		/*
		* toArray
		 */
		Object[] arr1 = emptytree.toArray();
		for(Object o : arr1){
			System.out.print(o + " "); //should only print root
		}
		System.out.println();

		/*
		* size,height,getK
		 */
		System.out.println(emptytree.size());
		System.out.println(emptytree.height());
		System.out.println(emptytree.getK());

		/*
		* Set methods
		 */
		System.out.println(emptytree.set(1,"s"));       //True
		System.out.println(emptytree.set(8,"s"));       //True make new level
		System.out.println(emptytree.set(4,"f"));       // true add node

		/*
		* print resulting tree in tostring and levelorder
		 */
		System.out.println(emptytree.toString());       // hello s null null null f null..s..null very long
		System.out.println(emptytree.toStringLevelOrder()); // hello s f s

		/*
		* toArray
		 */
		Object[] arr2 = emptytree.toArray();        //hello s null null null f null ..s.. null
		for(Object o : arr2){
			System.out.print(o + " ");
		}
		System.out.println();
		/*
			height size methods
		 */
		System.out.println(emptytree.height());     //2
		System.out.println(emptytree.size());       // 4

		/*
		last level null
		 */
		System.out.println(emptytree.set(8,null));  //make a null level
		System.out.println(emptytree.size());       //2

		/*
		* resulting toarray
		 */
		Object[] arr3 = emptytree.toArray();        // Hello s null null f null
		for(Object o : arr3){
			System.out.print(o + " ");
		}
		System.out.println();
		/*
		* toString both levelorder
		 */
		System.out.println(emptytree.toString());   // hello\ns null null f null
		System.out.println(emptytree.toStringLevelOrder());//hello s f


		Integer[] lastOne = {0,1,2,null,4,5,null};
		KTree<Integer> lastTree = new KTree<>(lastOne,2);
		/*
		* deepcopy, updateNewHeight(), findLastElem,goToChild, toString, getk, levelorder string and itr
		 */
	//	Integer[] deep = new Integer[lastOne.length];
		//lastTree.deepCopy(deep,lastOne);       // deep = 0,1,2,null,4,5,null

		lastTree.findLastElem();        // find the rightmost node
		System.out.println(lastTree.lastElemIndex); // 5

		System.out.println(lastTree.set(9,9));  //true

		lastTree.updateNewHeight(); // 3
		System.out.println(lastTree.height());  // 3

		System.out.println(goToChild(lastTree.getK(),0,1)); // index 2 value 2

		System.out.println(goToChild(lastTree.getK(),4,1)); // index 9, value 9
		System.out.println(lastTree.toString());    // 0\n 1 2\n null 4 5 null\n null null 9 null null null null null

		System.out.println(lastTree.set(9,null)); // remove last level

		System.out.println(lastTree.toString()); // 0\n 1 2\n null 4 5 null

		System.out.println(lastTree.toStringLevelOrder()); //0 1 2 4 5
		//System.out.println(lastTree.toString());
		Iterator<Integer> itr = lastTree.getLevelOrderIterator();
		System.out.println(itr.next()); //0
		System.out.println(itr.hasNext()); // true
		System.out.println(itr.next()); // 1
		System.out.println(itr.next()); //2
		System.out.println(itr.hasNext());  //true

		/*
		* Another mirror
		 */
		Object[] mrr = lastTree.mirror();  // 0 2 1 null 5 4 null null null null null null null null null
		for(Object o: mrr){
			System.out.print(o + " ");
		}
		System.out.println();

		/*
		* Another decode test
		 */
		//Holds GEORGE MASON
		// Branching factor 4
		String[] decodeAnother = {"_","_","_","N","S","E","R","O"
				,null,"M","A","G"," ",null,null,null,null,null,null,null,null};

		KTree<String> secretTree1 = new KTree<>(decodeAnother,4);
		String message = decode(secretTree1,"1200020112001310113022");
		System.out.println(message);  // GEORGE MASON


	}

	/****************************************/
	/* DO NOT EDIT ANYTHING BELOW THIS LINE */
	/****************************************/

	public static void methodSigCheck() {
		//This ensures that you've written your method signatures correctly
		//and understand how to call the various methods from the assignment
		//description.

		String[] strings = { "_", "_", "A", "B", "N", null, null };

		KTree<String> tree = new KTree<>(strings, 2);
		int x = tree.getK(); //should return 2
		int y = tree.size(); //should return 5
		int z = tree.height(); //should return 2
		System.out.println(x);
		System.out.println(y);
		System.out.println(z);

		String v = tree.get(0); //should be "_"
		boolean b = tree.set(0, "x"); //should set the root to "x"
		Object[] o = tree.toArray(); //should return [ "x", "_", "A", "B", "N", null, null ]
		System.out.println(tree.toStringLevelOrder());

		String s = tree.toString(); //should be "x\n_ A\nB N null null"
		String s2 = "" + tree; //should also be "x\n_ A\nB N null null"
		System.out.println(s +"\n");
		System.out.println(s2 +"\n");

		Iterator<String> it1 = tree.getLevelOrderIterator(); //gets an iterator
		Iterator<String> it2 = tree.getPreOrderIterator(); //gets an iterator
		Iterator<String> it3 = tree.getPostOrderIterator(); //gets an iterator

		String s3 = tree.toStringLevelOrder(); //should be "x _ A B N"
		System.out.println(s3+"\n");
		String s4 = tree.toStringPreOrder(); //should be "_ _ B N _ N"
		String s5 = tree.toStringPostOrder(); //should be "B N _ A _"

		String s6 = decode(tree, "001011011"); //should be "BANANA"
		System.out.println(s6);

		Object[] o2 = tree.mirror(); //should return [ "x", "A", "_", null, null, "N", "B" ]
		Object[] o3 = tree.subtree(1); //should return [ "_", "B", "N" ]
	}
}