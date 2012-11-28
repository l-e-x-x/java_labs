package ru.spbstu.telematics.student_Finagin.lab_02_sorted_set;
import java.util.Scanner;

public class Main {
	static Scanner scanner_=new Scanner(System.in);
	static boolean operatingFlag_=true;
	static SortedSet<String> sortedSet_=new SortedSet<String>();
	static void printOperationsList()
	{
		System.out.println("\n.:Operations List:.");
		System.out.println("[add element] -- [1]");
		System.out.println("[del element] -- [2]");
		System.out.println("[find element] - [3]");
		System.out.println("[foreach test] - [4]");
		System.out.println("[quit] --------- [0]");
		System.out.println("    - - -");
	}
	
	public static void main(String[] args)
	{
		while (operatingFlag_)
		{
			printOperationsList();
			System.out.print("Enter opcode: ");
			switch (scanner_.nextInt())
			{
			case 1:
				System.out.print("Enter element to add: ");
				sortedSet_.add(scanner_.next());
				break;
			case 2:
				System.out.print("Enter element to del: ");
				sortedSet_.remove(scanner_.next());
				break;
			case 3:
				System.out.print("Enter element to search for: "); 
				if (sortedSet_.contains(scanner_.next()))
					System.out.println("Success! Element found!");
				else
					System.out.println("Failed! Element not found!");
				break;
			case 4:
				System.out.print("[begin] -> ");
				for (String elem : sortedSet_) {
					System.out.print(elem.toString() + " -> ");
				}
				System.out.println("[end]");
				break;
			case 0:
				operatingFlag_=false;
				break;
			}
		}
	}
}
