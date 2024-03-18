package Leecode;


import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class StringTest {

    public static void main(String[] args) {
        System.out.println(getMaxValue("aaabvvv"));

        System.out.println(ValidParentheses("((("));
    }


    /**
     * 找出字符串中出现次数最多的字符及次数
     * @param str
     * @return
     */
    public static String getMaxValue(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        char[] arr = str.toCharArray();
        for (char ch : arr) {
            if (map.get(ch) == null) {
                map.put(ch, 1);
            } else {
                map.put(ch, map.get(ch) + 1);
            }
        }
        List<Integer> list = map.values().stream().sorted().collect(Collectors.toList());
        int maxCount = list.get(list.size() - 1);

        for (char ch : map.keySet()) {
            if (maxCount == map.get(ch)) {
                sb.append("出现此时最多的字符：").append(ch).append("出现次数为：").append(maxCount).append("\n");
            }
        }
        return sb.toString();
    }

    private static String ValidParentheses(String s){
        Stack<Integer> stack = new Stack<>();
        StringBuffer sb = new StringBuffer(s);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '('){
                //将开括号的索引压入栈
                stack.push(i);
            }else if (c==')' && !stack.isEmpty()){
                //找到有效对
                stack.pop(); //弹出与之匹配的开括号
            }else if (c==')'){
                // 无效闭括号
                sb.setCharAt(i, '*');
            }else {
                sb.setCharAt(i, '*');
            }
        }
        while (!stack.isEmpty()){
            sb.setCharAt(stack.pop(),'*');
        }
        return sb.toString().replaceAll("\\*","");
    }
}
