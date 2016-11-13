import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

/*
 *
 * If use standard mode, the output file is named output.txt.
 *
*/

class Operation {
    	
	private Map<String, Integer> nameValue;
	private Map<Integer, Integer> valueCount;

	public Operation() {
		nameValue = new HashMap<>();
		valueCount = new HashMap<>();
	}

	public Operation(Operation op) {
		nameValue = new HashMap<>(op.nameValue);
		valueCount = new HashMap<>(op.valueCount);
	}

	public void set(String name, int value) {
		if (nameValue.containsKey(name)) {
			int valueBefore = nameValue.get(name);
			int countBefore = valueCount.get(valueBefore);
			valueCount.put(valueBefore, countBefore - 1);
			valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
			nameValue.put(name, value);
		}
		else {
			nameValue.put(name, value);
			valueCount.put(value, valueCount.getOrDefault(value, 0) + 1);
		}
	}
	
	public String getValue(String name) {
		if (nameValue.containsKey(name)) {
			return nameValue.get(name) + "";
		}
		else {
			return "NULL";
		}
	}
	
	public void unSet(String name) {
		if (nameValue.containsKey(name)) {
			int value = nameValue.get(name);
			valueCount.put(value, valueCount.get(value) - 1);
			nameValue.remove(name);
		}
	}
	
	public String numEqualTo(int count) {
		return valueCount.getOrDefault(count, 0) + "";
	}

	public boolean equals(Operation operation) {
		Map<String, Integer> nameValue2 = operation.nameValue;
		Map<Integer, Integer> valueCount2 = operation.valueCount;
		if (nameValue.size() != nameValue2.size() || valueCount.size() != valueCount2.size()) {
			return false;
		}
		for (String name : nameValue.keySet()) {
			int value = nameValue.get(name);
			int count = valueCount.get(value);
			if (nameValue2.containsKey(name) && value == nameValue2.get(name) && valueCount2.containsKey(value) && count == valueCount2.get(value)) {
				continue;
			}
			else {
				return false;
			}
		}
		return true;
	}
}

class Transaction {
	
	private Stack<Operation> stack;
	
	public Transaction() {
		stack = new Stack<>();
	}
	
	public Operation begin(Operation op) {
		stack.push(op);
		return new Operation(op);
	}
	
	public Operation commit(Operation op) {
		if (!stack.empty()) {
			stack.clear();
			stack.push(op);
		}
		return new Operation(op);
	}
	
	public Operation rollback(Operation op) {
		Operation now = null;
		if (!stack.empty()) {
			now = stack.pop();
		}
		return now;
	}

	public boolean empty() {
		return stack.empty();
	}
}
 
public class Solution {

    public static void process(String input, String mode) {
    	ans += input + "\n";
    	String[] array = input.split("\\s");
    	String op = array[0];
		if (op.equals("SET")) {
			if (array.length == 3) {
				String name = array[1];
				int value = 0;
				try {
					value = Integer.parseInt(array[2]);
					operation.set(name, value);
				} 
				catch (NumberFormatException e) {
					System.out.println("NumberFormatException!");
				}
			}
			else {
				System.out.println("Parameters Error!");
			}
		}
		else if (op.equals("UNSET")) {
			if (array.length == 2) {
				String name = array[1];
				operation.unSet(name);
			}
			else {
				System.out.println("Parameters Error!");
			}
		}
		else if (op.equals("GET")) {
			if (array.length == 2) {
				String name = array[1];
				String value = operation.getValue(name);
				if (mode.equals("cmdMode")) {
					System.out.println("> " + value);
				}
				ans += "> " + value + "\n";
			}
			else {
				System.out.println("Parameters Error!");
			}
		}		
		else if (op.equals("NUMEQUALTO")) {
			if (array.length == 2) {
				int value = 0;
				try {
					value = Integer.parseInt(array[1]);
					String count = operation.numEqualTo(value);
					if (mode.equals("cmdMode")) {
					System.out.println("> " + count);
				}
					ans += "> " + count + "\n";
				} 
				catch (NumberFormatException e) {
					System.out.println("NumberFormatException!");
				}
			}
			else {
				System.out.println("Parameters Error!");
			}
		}
		else if (op.equals("BEGIN")) {
			operation = transaction.begin(operation);
		}
		else if (op.equals("ROLLBACK")) {
			Operation now = transaction.rollback(operation);
			if (now == null || now.equals(operation)) {
				if (mode.equals("cmdMode")) {
					System.out.println("> " + "NO TRANSACTION");
				}
				ans += "> NO TRANSACTION" + "\n";
			}
			operation = now;
		} 
		else if (op.equals("COMMIT")) {
			Operation now = transaction.commit(operation);
			if (now == null || transaction.empty()) {
				if (mode.equals("cmdMode")) {
					System.out.println("> " + "NO TRANSACTION");
				}
				ans += "> NO TRANSACTION" + "\n";
			}
		}
		else if (op.equals("END")) {
			return;
		}
		else {
			System.out.println("Error: please enter legal input, like 'SET x 10'");
		}
    }

    public static void cmdMode() {
    	Scanner scanner = new Scanner(System.in);
		String line = "";
		while (scanner.hasNext()) {
		    line = scanner.nextLine();
		    if (line.equals("END")) {
		    	ans += "END";
		    	break;
		    }
		    process(line, "cmdMode");
		}
    }

    public static void stdMode(String fileName) throws NumberFormatException, IOException{
    	FileInputStream fis = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		int lineIndex = 0;
		while ((line = br.readLine()) != null) {
			process(line, "stdMode");
		}
		br.close();
    }

    public static void writeFile(String fileName, String ans) throws IOException {
		File file = new File(fileName);
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(ans);
		bw.close();
	}

    static Operation operation;
    static Transaction transaction;
    static String ans;

    public static void main(String args[] ) throws Exception {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT */
        operation = new Operation();
        transaction = new Transaction();
        ans = "";
        if (args.length == 0) {
        	cmdMode();
        }
        if (args.length == 1) {
        	stdMode(args[0]);
        	writeFile("output.txt", ans);
        }
    }
}
