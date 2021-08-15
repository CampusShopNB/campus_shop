package com.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 敏感词过滤
 */
public class SensitivewordFilter {
    @SuppressWarnings("rawtypes")
    private static Map sensitiveWordMap = null;
    public static int minMatchTYpe = 1;      //最小匹配规则，只要包含一个字，就匹配
    public static int maxMatchType = 2;      //最大匹配规则，至少包含一个词

    /**
     * 构造函数，初始化敏感词库
     */
    public SensitivewordFilter(){
        sensitiveWordMap = new SensitiveWordInit().initKeyWord();
    }

    /**
     * 获取文字中的敏感词
     *
     * @param txt 文字
     * @param matchType 匹配规则&nbsp;1：最小匹配规则，2：最大匹配规则
     * @return
     *
     */
    public Set<String> getSensitiveWord(String txt, int matchType){
        Set<String> sensitiveWordList = new HashSet<String>();//存放包含的敏感词

        for(int i = 0 ; i < txt.length() ; i++){
            int length = CheckSensitiveWord(txt, i, matchType);    //判断是否包含敏感字符
            if(length > 0){    //存在,加入list中
                sensitiveWordList.add(txt.substring(i, i+length));
                i = i + length - 1;    //减1的原因，是因为for会自增
            }
        }
        return sensitiveWordList;
    }


    /**
     * 检查文字中是否包含敏感字符，检查规则如下：<br>
     *
     * @param txt
     * @param beginIndex
     * @param matchType
     *
     */
    @SuppressWarnings({ "rawtypes"})
    public static int CheckSensitiveWord(String txt, int beginIndex, int matchType){
        boolean  flag = false;    //敏感词结束标识位：用于敏感词只有1位的情况
        int matchFlag = 0;     //匹配标识数默认为0
        char word = 0;
        Map nowMap = sensitiveWordMap;
        for(int i = beginIndex; i < txt.length() ; i++){
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);     //获取指定key对应的敏感词
            if(nowMap != null){     //存在，则判断是否为最后一个
                matchFlag++;     //找到相应key，匹配标识+1
                if("1".equals(nowMap.get("isEnd"))){       //如果为最后一个匹配规则,结束循环，返回匹配标识数
                    flag = true;       //结束标志位为true
                    if(SensitivewordFilter.minMatchTYpe == matchType){    //最小规则，直接返回,最大规则还需继续查找
                        break;
                    }
                }
            }
            else{     //不存在，直接返回
                break;
            }
        }
        if(!flag){
            matchFlag = 0;
        }
        return matchFlag;
    }

    /**
     * 获取敏感词的个数
     * @param txt
     * @return 语句中含有的敏感词的数量
     */
    public static int getSensitiveNum(String txt){
        SensitivewordFilter filter = new SensitivewordFilter();
        Set<String> set = filter.getSensitiveWord(txt, 1);
        return set.size();
    }
}

