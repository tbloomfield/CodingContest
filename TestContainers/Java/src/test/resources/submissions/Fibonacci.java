public class Fibonacci {
	// Function to print N Fibonacci Number
    public int findFib(int position) {
        int num1 = 0, num2 = 1, sum = 1; 
        for (int i = 0; i < position; i++) {            
        	sum = num2 + num1;
            num1 = num2;
            num2 = sum;
        }
        
        return sum;
    }
}