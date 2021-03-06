import java.util.*;
public class LinkedList {
	Node head;
	
	
	void insert(int d) {
		Node tail = new Node(d);
		Node curr = head;
		
		while(curr.next != null) {
			curr = curr.next;
		}
		curr.next = tail;
	}
	
	Node delete(Node head,int d) {
		Node n = head;
		
		while(n.next != null ) {
			if(n.next.value == d) {
				n.next = n.next.next;
				return head;
			}
		}
		return head;
	}
	
	Node deleteDuplicates() {
		HashSet<Integer> set = new HashSet<>();
		Node curr = head;
		while(curr.next != null) {
			if(set.contains(curr.value)) {
				curr.next =  curr.next.next;
			}else {
				set.add(curr.value);
			}
			curr = curr.next;
		}
		
		return head;
	}
	
	Node removeNth(int n) {
		Node curr = head;
		int size = 0;
		while(curr.next != null) {
			curr = curr.next;
			size++;
		}
		int i = 0;
		curr = head;
		while(i < size - (n + 1)) {
			curr = curr.next;
		}
		curr.next = curr.next.next;
		return head;
	}
}
