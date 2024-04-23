public class PrintName_Invalid {
    public String printMyName(String name) {
    	//exception, "forma" isn'te a known method:
    	return String.forma("Your name is %s", name);
    }
}