import java.util.*;

public class test {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Map<String, String> map = new HashMap<>();
        map.put("1","666");
        map.put("2","666");
        map.put("3","666");
        map.put("4","666");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
        }
    }
}
