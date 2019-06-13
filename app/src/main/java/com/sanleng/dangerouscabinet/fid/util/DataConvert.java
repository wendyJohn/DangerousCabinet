package com.sanleng.dangerouscabinet.fid.util;

public class DataConvert {
    /**
     * Convert byte[] to hex
     * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            char[] str = hv.toCharArray();
            for (int j = 0; j < str.length; j++) {
                if ((int) str[j] >= 97 && (int) str[j] <= 122) {
                    str[j] -= 32;
                }
            }
            hv = new String(str);
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 把字节数组转换成10进制字符串
     *
     * @param bArray
     *            需要被转换的byte数组
     * @return 转换成10进制字符串
     */
    public static final String bytesToDecimalismString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        for (int i = 0; i < bArray.length; i++) {
            String temp = null;
            // 因为unsigned byte = byte & 0xFF，0~255的颜色用byte-128~127表示
            // 其中0~127对应0~127 ，-128~-1 对应128~255。计算机中负值是通过补位的方式进行换算。
            // 超过127就为负数,就要转换下
            if (bArray[i] < 0) {
                temp = String.valueOf(bArray[i] & 0xFF);
            } else {
                temp = String.valueOf(bArray[i]);
            }
            if (temp.length() < 2) {
                sb.append(0);
            }
            sb.append(temp.toUpperCase() + " ");
        }
        return sb.toString();
    }

    /**
     * Convert byte to hex
     * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src
     *            字符串
     * @return hex string
     */
    public static String bytesToHexString(byte src) {
        StringBuilder stringBuilder = new StringBuilder("");
        int v = src & 0xFF;
        String hv = Integer.toHexString(v);
        char[] str = hv.toCharArray();
        for (int i = 0; i < str.length; i++) {
            if ((int) str[i] >= 97 && (int) str[i] <= 122) {
                str[i] -= 32;
            }
        }
        hv = new String(str);
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }

    /**
     * 把16进制字符串转换成字节数组（自动把字符串转换为大写）
     *
     * @param hex
     *            字符串
     * @return 字符串转换后的byte数组
     */
    public static byte[] hexStringToByte(String hex) {
        hex = hex.toUpperCase();
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = ((byte) (toByte(achar[pos]) << 4 | toByte(achar[(pos + 1)])));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * 字符串转换为16进制
     *
     * @param str
     * @return
     */
    public static String strHexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString
     *            16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] hexStr2ByteArray(String hexString) {
        if (hexString.isEmpty()) {
            throw new IllegalArgumentException(
                    "this hexString must not be empty");
        }
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            // 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            // 将hex 转换成byte "&" 操作为了防止负数的自动扩展
            // hex转换成byte 其实只占用了4位，然后把高位进行右移四位
            // 然后“|”操作 低四位 就能得到 两个 16进制数转换成一个byte.
            //
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    /**
     * 16进制字符串转换成byte数组
     *
     * @param 16进制字符串
     * @return 转换后的byte数组
     */
    public static byte[] hex2Byte(String hex) {
        String digital = "0123456789ABCDEF";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            // 其实和上面的函数是一样的 multiple 16 就是右移4位 这样就成了高4位了
            // 然后和低四位相加， 相当于 位操作"|"
            // 相加后的数字 进行 位 "&" 操作 防止负数的自动扩展. {0xff byte最大表示数}
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return bytes;
    }

    /**
     * 字符串转Ascll
     *
     * @param src
     *            字符串
     * @return ascll码
     */
    public static byte[] stringToByte(String src) {
        byte[] result = new byte[src.length()];
        char[] charStr = src.toCharArray();
        for (int i = 0; i < charStr.length; i++) {
            result[i] = (byte) charStr[i];
        }
        return result;
    }

    /**
     * Convert byte to int 这里我们可以将byte转换成int，
     *
     * @param src
     *            字节超过127的值
     * @return hex int
     */
    public static final int byteToInt(byte str) {
        int result = 0;
        // 因为unsigned byte = byte & 0xFF，0~255的颜色用byte-128~127表示
        // 其中0~127对应0~127 ，-128~-1 对应128~255。计算机中负值是通过补位的方式进行换算。
        // 超过127就为负数,就要转换下
        if (str < 0) {
            result = str & 0xFF;
        } else {
            return str;
        }
        return result;
    }

    /**
     * 卡号加空格
     *
     * @param cardNo
     * @return
     */
    public static String getCardNo(String cardNo) {
        char[] ch = cardNo.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ch.length; i++) {
            sb.append(ch[i]);
            if ((i + 1) % 2 == 0 && i < cardNo.length() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    // The size port of the convert
    public static int ReverseByte(int value) {
        return ((value & 0xFF) << 8 | (value & 0xFF00) >> 8);
    }

    public static String cardAddEmpity(String cardNo) {
        char[] ch = cardNo.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ch.length; i++) {
            sb.append(ch[i]);
            if ((i + 1) % 2 == 0 && i < 24) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * 检查IP格式是否正
     *
     * @param strIP
     * @return
     */
    public static boolean IsValidIP(String strIP) {
        // 先检查有无非数字字符
        if (!Regex.IsMatch(strIP)) {
            return false;
        }
        // 再检查数据范围是否合理
        String[] strNumArray = strIP.split("\\.");
        if (strNumArray.length != 4) {
            return false;
        }
        int n = 0;
        for (int i = 0; i < 4; ++i) {
            n = Integer.parseInt((strNumArray[i]));
            if (n > 255) {
                return false;
            }
        }
        return true;
    }

    public static boolean arrayCompareEquals(byte[] start, byte[] end) {
        if (start.length != end.length) {
            return false;
        }
        for (int i = 0; i < start.length; i++) {
            if (start[i] != end[i]) {
                return false;
            }
        }
        return true;
    }

    public static String bytesToString(byte[] wifibody, int startIndex, int len) {
        byte[] data = new byte[len];
        System.arraycopy(wifibody, startIndex, data, 0, len);
        return DataConvert.bytesToHexString(data);
    }

    public static String convertToDecimalString(byte[] bytes, int startIndex,
                                                int length, char signal) {
        StringBuffer result = new StringBuffer();
        for (int index = 0; index < length && index < bytes.length; index++) {
            int total = DataConvert.byteToInt(bytes[index + startIndex]);
            result.append(String.valueOf(total));
            if (index < length - 1) {
                result.append(signal);
            }
        }
        return result.toString();
    }

    public static String convertToHexString(byte[] bytes, int startIndex,
                                            int length, char signal) {
        StringBuffer result = new StringBuffer();
        for (int index = 0; index < length && index < bytes.length; index++) {
            result.append(DataConvert
                    .bytesToHexString(bytes[index + startIndex]));
            if (index < length - 1) {
                result.append(signal);
            }
        }
        return result.toString();
    }

    public static String convertToString(byte[] bytes, int startIndex,
                                         int length) {
        StringBuffer result = new StringBuffer();
        for (int index = 0; index < length && index < bytes.length; index++) {
            result.append(DataConvert
                    .bytesToHexString(bytes[index + startIndex]));
        }
        return result.toString();
    }

    public static int toInt(String hex) {
        int ss = 0;
        if ((hex.charAt(0) - 'A') >= 0) {
            ss += (hex.charAt(0) - 'A' + 10) * 16;
        } else {
            ss += (hex.charAt(0) - '0') * 16;
        }
        if ((hex.charAt(1) - 'A') >= 0) {
            ss += hex.charAt(1) - 'A' + 10;
        } else {
            ss += hex.charAt(1) - '0';
        }
        return ss;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] { (byte) (a & 0xFF), (byte) ((a >> 8) & 0xFF),
                (byte) ((a >> 16) & 0xFF), (byte) ((a >> 24) & 0xFF) };
    }
}
