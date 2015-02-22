import java.util.AbstractMap;
import java.util.Map.Entry;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 */
//Cem-Changed BPlusTreeEmpty to BPlusTree
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	/**
	 * Searchs the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {
      
      int index = 0;
      Node<K,T> current = root;
      
      while(!current.isLeafNode)
      {
         index = 0;
         
         //Traversing through the keys of the current node
         for(K curkey : current.keys)
         {
            // curkey > key
            if(curkey.compareTo(key) == 1)
               break;
            else
               ++index;
         }
         
         if(current.keys.size() > 0)
            current = ((IndexNode<K,T>)current).children.get(index);         
      }

      //If given node is valid, and no error occured
      if(current != root || current!=null)
      {
         //will be used for accessing the value
         index = 0;
         
         for(K curkey : current.keys)
         {
            // curkey == key
            if(curkey.compareTo(key) == 0)
               return ((LeafNode<K,T>)curkey).values.get(index);
            index++;
         }        
      }
      
		return null;
	}

   /**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {

	}

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {

		return null;
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {

		return null;
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {

	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
		return -1;

	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex,
			IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
		return -1;
	}

}
