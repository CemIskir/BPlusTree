import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 */
//Cem-Changed BPlusTreeEmpty to BPlusTree
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	
	/*Returns the leaf Node that the given key existed on*/
	private Node<K,T> searchLeaf(K key)
	{
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
      return current;
	}
	
	
	
	
	
	/**
	 * Searchs the value for a specific key
	 * 
	 * @author Cem
	 * @param key
	 * @return value
	 */
	public T search(K key) {

	   //Getting the leaf that key should be on (key may not be existed)
      Node<K,T> current = searchLeaf(key);

      //If given node is valid, and no error occured
      if(current!=null)
      {
         //will be used for accessing the value
         int index = 0;
         
         for(K curkey : current.keys)
         {
            
            // curkey == key
            if(curkey.compareTo(key) == 0)
               return ((LeafNode<K,T>)current).values.get(index);
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
	   //If parameters is not valid
      if(key == null | value == null)
      {
         System.err.println("Given key and/or value to insert function is empty");
         return;
      }
   
	   //First entry to be inserted
	   if(root == null)
	      root = new LeafNode<K,T>(key,value);
      //all other cases where there is entry in the tree, and given key-value pair is valid
	   else
	      insertMe(key,value);
	}

	
	public void addSortedToKeyOrValue(Node<K,T> current, K key, T value) {
	   //will be used for accessing the value
      int index = 0;
         
      for(K curkey : current.keys)
      {
         // curkey > key
         if(curkey.compareTo(key) == 1)
            break;
         else
            ++index;
      }
	   current.keys.add(index, key);
	   ((LeafNode<K,T>)current).values.add(index, value);
	}
	
	  public void addSortedToIndexNode(Node<K,T> current, K key, Node<K,T> childNode) {
	      //will be used for accessing the value
	      int index = 0;
	         
	      for(K curkey : current.keys)
	      {
	         // curkey > key
	         if(curkey.compareTo(key) == 1)
	            break;
	         else
	            ++index;
	      }
	      current.keys.add(index, key);
	      ((IndexNode<K,T>)current).children.add(index+1, childNode);
	   }
	
	
   public void insertMe(K key, T value) {
      int index = 0;
      Node<K,T> current = root, parent = root;
      ArrayList<Node<K,T>> parentNodes = new ArrayList<Node<K,T>>();
      
      //Go till the leafNode
      while(!current.isLeafNode)
      {

         //PARENT NODE LOGIC
         //Appending the parent nodes
         if(parentNodes.size() == 0)
            parentNodes.add(current);
         //There is at least on item in the list
         else
         {
            //The variables below holds the number of elements in each 
            int current_key_size = current.keys.size();
            
            //if there is space in the node, we don't need to split it so remove all the previous parents(we don't need to hold them anymore)
            if(current_key_size < 2*D)
               parentNodes.clear();
            parentNodes.add(current);
         }
         //

         
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
         {
            parent = current;
            current = ((IndexNode<K,T>)current).children.get(index);
         }
      }
      
      addSortedToKeyOrValue(current,key,value);
      
      //Overflow occured
      if(current.isOverflowed())
      {

         //current holds left child
         Node<K,T> leftPart = current;

         Map.Entry<K, Node<K,T>> rightPartPair = splitLeafNode((LeafNode<K,T>)leftPart);
         K middlekey = rightPartPair.getKey();
         //right child
         Node<K,T> rightPart = rightPartPair.getValue();
         
         
    
         
         
         
         
         if(parentNodes.size() > 0)
         {
            int number_of_parents = parentNodes.size();

            while(number_of_parents > 0)
            {

               
//!!!!!        
               //Current parent
               leftPart = parentNodes.get(number_of_parents-1);
               
               //add the new key & children to the current parent
               addSortedToIndexNode(leftPart,middlekey,rightPart);
               
               if(leftPart.isOverflowed())
               {
                  //Returning the entry - removing the middle element which is the key
                  rightPartPair = splitIndexNode((IndexNode<K,T>)leftPart);
                  rightPart = rightPartPair.getValue();
                  middlekey = rightPartPair.getKey();
                  
                  
                  //(indexLeaf.keys.remove(D),newNode);
                  //SPLIT index
                  //set newkey and node
               }
               else
               {
                  rightPartPair = null;
                  rightPart = null;
                  middlekey = null;
               }

               //reducing the number of parents one more level
               --number_of_parents;                              
               //leftPart = parentNodes.get(number_of_parents-1);
            }
            
            //if root, update root- get(0) size check size check
            if( middlekey != null)
            {
               Node<K,T> indexRoot = new IndexNode<K, T>(middlekey, leftPart, rightPart);
               root = indexRoot;
            }
            
         }
         //No previous Index Node
         else
         {
            //In case there was no IndexNode before, create a new one, and assign it as a root
            Node<K,T> indexRoot = new IndexNode<K, T>(middlekey, leftPart, rightPart);
            root = indexRoot;
         }
         
//check update root case
         
         
      }
   }

	
	
	
	
	/**
	 * Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @author Cem
	 * @param leaf
	 * @return the key/node pair as an Entry
	 */
	//Cem-Splits the given leaf
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {
	   if(leaf != null)
	   {	
	      /*keys and values that will be inserted to the new LeafNode*/
         ArrayList<K> newKeys = new ArrayList<K>();
         ArrayList<T> newValues = new ArrayList<T>();
         
	      //Insert into the specific position
         int index = D;
         //How many items to copy
         int copyItems = leaf.keys.size();
         
         while(index < copyItems)
         {
            newValues.add(leaf.values.remove(D));
            newKeys.add(leaf.keys.remove(D));
            ++index;
         }
	      
         //Creating the LeafNode
         Node<K,T> newNode = new LeafNode<K,T>(newKeys, newValues);
         newNode.isLeafNode = true;
         
         //Setting the previous and next values
         LeafNode<K,T> next = leaf.nextLeaf;
         
         ((LeafNode<K,T>)newNode).nextLeaf = next;
         ((LeafNode<K,T>)newNode).previousLeaf = leaf;
         
         if(next != null)
            next.previousLeaf = ((LeafNode<K,T>)newNode);
         leaf.nextLeaf = ((LeafNode<K,T>)newNode);
         
         Map.Entry<K, Node<K,T>> entry = new AbstractMap.SimpleEntry<K, Node<K,T>>(newNode.keys.get(0),newNode);
         return entry;
	   }
	   else
	      return null;
	}

	/**
	 * split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @author Cem
	 * @param index
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> indexLeaf) {
      if(indexLeaf != null)
      {  
         //keys and children that will be inserted to the new LeafNode
         ArrayList<K> newKeys = new ArrayList<K>();
         ArrayList<Node<K,T>> newChildren = new ArrayList<Node<K,T>>();
         
         //Insert into the specific position
         int index = D + 1;
         //How many items to copy
         int copyItems = indexLeaf.keys.size();
         
         while(index < copyItems)
         {
            newChildren.add(indexLeaf.children.remove(D+1));
            newKeys.add(indexLeaf.keys.remove(D+1));
            ++index;
         }
         newChildren.add(indexLeaf.children.remove(D+1));
         
         //Creating the LeafNode
         Node<K,T> newNode = new IndexNode<K,T>(newKeys, newChildren);
         newNode.isLeafNode = false;
         
         //Returning the entry - removing the middle element which is the key
         Map.Entry<K, Node<K,T>> entry = new AbstractMap.SimpleEntry<K, Node<K,T>>(indexLeaf.keys.remove(D),newNode);
         return entry;
         
      }
      else
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
