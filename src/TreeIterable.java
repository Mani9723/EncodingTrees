import java.util.Iterator;

/**
 * Interface with the three main traversal methods.
 * Level Order.
 * Pre Order.
 * Post Order.
 * @param <T> AnyType
 */
interface TreeIterable<T> {
	public Iterator<T> getLevelOrderIterator();
	public Iterator<T> getPreOrderIterator();
	public Iterator<T> getPostOrderIterator();
}