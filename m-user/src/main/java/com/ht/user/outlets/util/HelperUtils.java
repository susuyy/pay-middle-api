package com.ht.user.outlets.util;

/**
 * <p>
 *    帮助工具类
 * </p>
 *
 * @author hy.wang
 * @since 20/8/21
 */
public class HelperUtils {


    /**
     * 分页默认页号
     */
    public static final int PAGE_NO_DEFAULT_VALUE = 1;

    /**
     * 分页默认页大小
     */
    public static final int PAGE_SIZE_DEFAULT_VALUE = 10;


    public static int getDefaultIntValue(Integer value, int defaultVal) {
        if (value == null) {
            return defaultVal;
        } else {
            return value;
        }
    }

    /**
     * 创建指定数量的随机字符串
     *
     * @param numberFlag 是否是数字
     * @param length
     * @return
     */
    public static String createRandom(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "ABCDEFGHIJKMNPQRSTUVWXYZ";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 1) {
                bDone = false;
            }
        } while (bDone);
        return retStr;
    }

    public static String createRandomLetter(int length) {
        String retStr = "";
        String[] strTable = new String[]{"A","B","C","D","E","F","G","H","I","J","K","M","N","P","Q","R","S","T","U","V","W","X","Y","Z"};
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random()* strTable.length);
            String s = strTable[index];
            retStr += s;
        }
        return retStr;
    }


}
