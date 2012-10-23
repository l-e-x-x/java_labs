import java.util.Scanner;
import java.util.Vector;
public class DelimParser
{
	public static void main(String[] args) 
	{
		Scanner scan=new Scanner(System.in);
		boolean successFlag=false; // флаг корректности ввода символа-разделителя
		String delim=""; // символ-разделитель
		while (!successFlag)
		{
			System.out.print("Enter the delimiter symbol (may be a word): ");	
			delim = scan.nextLine();
			if (delim.isEmpty())
				System.out.println("Error! Delimiter can't be empty! Try again!");
			else
				successFlag=true; // выставляем флаг корректности
		}
		int spaceSymbInd=spaceSymbInd=delim.indexOf(' '); // находим первый пробел в строке
		if (spaceSymbInd >= 0) // если были в строке пробелы
			if (spaceSymbInd != 0)
				delim = delim.substring(0, spaceSymbInd); // если несколько символов до пробела (слово)
			else
				delim = delim.substring(0, 1); // если только один пробел
		
		System.out.print("New delimiter: ");
		System.out.println(delim);			
		System.out.print("Enter string to parse: ");		
		String parsingLine=scan.nextLine(); // вводим строку для парсинга
		scan.close();

		scan=new Scanner(parsingLine).useDelimiter(delim); // создаем сканнер, который парсит строку (исп. новый разделитель)
		Vector tokensList = new Vector();		
		while (scan.hasNext())
			tokensList.add(scan.next()); // добавляем в массив распарсенную строку (по токенам) 
		int i=tokensList.size()-1;
		System.out.print("New string: ");		
		while(i >= 0)
		{	// выводим токены в обратном порядке
			System.out.print(tokensList.get(i)); 
			if (i == 0)
				{System.out.println(); break;}
			System.out.print(" "); // выводим пробел вместо нашего разделителя
			i--;
		}
	}
}


