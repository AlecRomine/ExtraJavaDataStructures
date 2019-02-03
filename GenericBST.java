// Sean Szumlanski
// COP 3503, Spring 2017

// ====================
// GenericBST: BST.java
// ====================
// Basic binary search tree (BST) implementation that supports insert() and
// delete() operations. This framework is provide for you to modify as part of
// Programming Assignment #2.
//
//Modified by: Alec Romine
//      al411896

import java.io.*;
import java.util.*;

//defining our node object
class Node<T>
{
	T data;
	Node<T> left, right;
        //constructor for data
	Node(T data)
	{
		this.data = data;
	}
}
//A BST class that can handle generic data types
public class GenericBST <T extends Comparable <T>>
{
	private Node<T> root;
        
        //call function for insertion 
	public void insert(T data)
	{
		root = insert(root, data);
	}
        //adds new node in tree holding <T>data unless identical one is present 
	private Node<T> insert(Node<T> root, T data)
	{       
                Node<T> newNode = new Node<>(data);
            
		if (root == null)
		{
			return newNode;
		}
                
                int val = data.compareTo(root.data);
                //traverses left is data is smaller
		if (val < 0)
		{
			root.left = insert(root.left, data);
		}
                //right if data is bigger
		else if (val > 0)
		{
			root.right = insert(root.right, data);
		}
		else
		{
			// Stylistically, I have this here to explicitly state that we are
			// disallowing insertion of duplicate values. This is unconventional
			// and a bit cheeky.
			;
		}

		return root;
	}
        //call function to delete node containing "data" from tree
	public void delete(T data)
	{
		root = delete(root, data);
	}
        //searches for node containing data and deletes it
	private Node<T> delete(Node<T> root, T data)
	{
		if (root == null)
		{
			return null;
		}
                
                int val = data.compareTo(root.data);
                //traverse left if data is smaller
		if (val < 0)
		{
			root.left = delete(root.left, data);
		}
                //traverse right if data is larger
		else if (val > 0)
		{
			root.right = delete(root.right, data);
		}
                //found the node to delete
		else
		{       //just return null for no children
			if (root.left == null && root.right == null)
			{
				return null;
			}
                        //no right child? return left child
			else if (root.right == null)
			{
				return root.left;
			}
                        //no left child? return right
			else if (root.left == null)
			{
				return root.right;
			}
                        //finds next highest node if there are two children
			else
			{
				root.data = findMax(root.left);
				root.left = delete(root.left, root.data);
			}
		}

		return root;
	}

	// This method assumes root is non-null, since this is only called by
	// delete() on the left subtree, and only when that subtree is non-empty.
	private T findMax(Node<T> root)
	{
		while (root.right != null)
		{
			root = root.right;
		}

		return root.data;
	}

	// Returns true if the value is contained in the BST, false otherwise.
	public boolean contains(T data)
	{
		return contains(root, data);
	}

	private boolean contains(Node<T> root, T data)
	{
		if (root == null)
		{
			return false;
		}
                
                int val = data.compareTo(root.data);
                        
		if (val < 0)
		{
			return contains(root.left, data);
		}
		else if (val > 0)
		{
			return contains(root.right, data);
		}
		else
		{
			return true;
		}
	}

	public void inorder()
	{
		System.out.print("In-order Traversal:");
		inorder(root);
		System.out.println();
	}

	private void inorder(Node<T> root)
	{
		if (root == null)
			return;

		inorder(root.left);
		System.out.print(" " + root.data);
		inorder(root.right);
	}

	public void preorder()
	{
		System.out.print("Pre-order Traversal:");
		preorder(root);
		System.out.println();
	}

	private void preorder(Node<T> root)
	{
		if (root == null)
			return;

		System.out.print(" " + root.data);
		preorder(root.left);
		preorder(root.right);
	}

	public void postorder()
	{
		System.out.print("Post-order Traversal:");
		postorder(root);
		System.out.println();
	}

	private void postorder(Node<T> root)
	{
		if (root == null)
			return;

		postorder(root.left);
		postorder(root.right);
		System.out.print(" " + root.data);
	}
        
        public static double difficultyRating()
        {
            //all around good time
            return 1.5;
        }
        
        public static double hoursSpent()
        {
            return 2;
        }
        /*
	public static void main(String [] args)
	{
		GenericBST<Integer> myTree = new GenericBST<Integer>();

		for (int i = 0; i < 5; i++)
		{
			int r = (int)(Math.random() * 100) + 1;
			System.out.println("Inserting " + r + "...");
			myTree.insert(r);
		}

		myTree.inorder();
		myTree.preorder();
		myTree.postorder();
	}
        */
}
