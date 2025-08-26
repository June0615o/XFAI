package com.junevi.lc4jtrue.guardrail;

import java.util.Set;

public class SafeInputGuardrail {

    private static final Set<String> sensitiveWords = Set.of(
            "kill",
            "evil",
            "暴力",
            "色情"
    ); //敏感词集合

    public static boolean containsSensitive(String input) {
        return sensitiveWords.stream().anyMatch(input::contains);
    }

    public static String sanitize(String input){
        if(containsSensitive(input)){
            return "[请勿输入敏感词,已被拦截]";
        }
        return input;
    }

}
