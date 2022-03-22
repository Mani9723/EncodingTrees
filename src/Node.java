import java.util.ArrayList;

public class Node<E>
{
	E data;
	ArrayList<Node<E>> children;

	public Node(E val)
	{
		data = val;
		children = new ArrayList<>();
	}

	public void addChildren(Node<E> child, int k)
	{
		if(children.size() < k) {
			children.add(child);
		}
	}


	@Override
	public String toString()
	{
		return "Node{" + data + ","+ children + '}';
	}

	public static void main(String[] args)
	{
		Node<Integer> node = new Node<>(null);
		node.children.add(node);
		System.out.println(node);
	}
}
