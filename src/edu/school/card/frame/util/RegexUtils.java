package edu.school.card.frame.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static boolean isNumberOrEnglish(String value){
        String regex="^[a-zA-Z0-9\u4E00-\u9FA5]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match=pattern.matcher(value);
        return match.matches();
    }
}
