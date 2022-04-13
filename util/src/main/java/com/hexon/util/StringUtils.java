package com.hexon.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class StringUtils {
    public static String bytesToMd5String(byte[] bytes) {
        String resultString;
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            resultString = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            resultString = String.valueOf(bytes.hashCode());
        }

        return  resultString;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static Date stringToDate(String pattern, String in) {
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern(pattern);

        Date retDate = null;
        try {
            retDate = simpleDateFormat.parse(in);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return retDate;
    }

     /**
      * 将Date转换成formatStr格式的字符串
      *
      * @param date
      * @param pattern
      * @return
      */
    public static String dateToDateString(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
     }

    public static String getFriendlyDateString(Date date, boolean showDayOfWeek) {
        if (date == null) {
            if (showDayOfWeek)
                return "--.--. 周--";
            else
                return "--.--.";
        }

        GregorianCalendar nowCalender = new GregorianCalendar();
        GregorianCalendar dstCalender = new GregorianCalendar();
        dstCalender.setTime(date);

        int now = nowCalender.get(GregorianCalendar.DAY_OF_YEAR);
        int dst = dstCalender.get(GregorianCalendar.DAY_OF_YEAR);
        int dstDayOfWeek = dstCalender.get(GregorianCalendar.DAY_OF_WEEK);
        String dstDayOfWeekString = "周--";
        if (showDayOfWeek) {
            switch (dstDayOfWeek) {
                case 1: {
                    dstDayOfWeekString = "周日";
                } break;
                case 2: {
                    dstDayOfWeekString = "周一";
                } break;
                case 3: {
                    dstDayOfWeekString = "周二";
                } break;
                case 4: {
                    dstDayOfWeekString = "周三";
                } break;
                case 5: {
                    dstDayOfWeekString = "周四";
                } break;
                case 6: {
                    dstDayOfWeekString = "周五";
                } break;
                case 7: {
                    dstDayOfWeekString = "周六";
                } break;
            }
        }

        if (dst - now == 0) {
            if (showDayOfWeek)
                return "今天" + " " + dstDayOfWeekString;
            else
                return "今天";
        } else if (dst - now == 1) {
            if (showDayOfWeek)
                return "明天" + " " + dstDayOfWeekString;
            else
                return "明天";
        } else if (dst - now == 2) {
            if (showDayOfWeek)
                return "后天" + " " + dstDayOfWeekString;
            else
                return "后天";
        } else if (dst - now == -1) {
            if (showDayOfWeek)
                return "昨天" + " " + dstDayOfWeekString;
            else
                return "昨天";
        } else if (dst - now == -2) {
            if (showDayOfWeek)
                return "前天" + " " + dstDayOfWeekString;
            else
                return "前天";
        } else {
            SimpleDateFormat simpleDateFormat = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
            if (showDayOfWeek)
                simpleDateFormat.applyPattern("M.d EE");
            else
                simpleDateFormat.applyPattern("M.d");
            return simpleDateFormat.format(date);
        }
    }

    public static String getCurrentDateTime(String pattern) {
        GregorianCalendar currentCalendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat)SimpleDateFormat.getDateTimeInstance();
        simpleDateFormat.applyPattern(pattern);

        return simpleDateFormat.format(currentCalendar.getTime());
    }

    public static byte[] IntToByteArray(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    //将字节在前转为int，高字节在后的byte数组(与IntToByteArray1想对应)
    public static int ByteArrayToInt(byte[] bArr) {
        if(bArr.length > 4){
            return -1;
        }
        if (bArr.length == 4) {
            return (int) ((((bArr[0] & 0xff) << 24)
                    | ((bArr[1] & 0xff) << 16)
                    | ((bArr[2] & 0xff) << 8)
                    | ((bArr[3] & 0xff) << 0)));
        } else if (bArr.length == 3) {
            return (int) ((((bArr[0] & 0xff) << 16)
                    | ((bArr[1] & 0xff) << 8)
                    | ((bArr[2] & 0xff) << 0)));
        } else if (bArr.length == 2) {
            return (int) ((((bArr[0] & 0xff) << 8)
                    | ((bArr[1] & 0xff) << 0)));
        } else if (bArr.length == 1) {
            return (int) ((bArr[0] & 0xff) << 0);
        }

        return 0;
    }

    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 首字母大写
     *
     * @param s 待转字符串
     * @return 首字母大写字符串
     */
    public static String upperFirstLetter(final String s) {
        if (isEmpty(s) || !Character.isLowerCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param s 待转字符串
     * @return 首字母小写字符串
     */
    public static String lowerFirstLetter(final String s) {
        if (isEmpty(s) || !Character.isUpperCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * 反转字符串
     *
     * @param s 待反转字符串
     * @return 反转字符串
     */
    public static String reverse(final String s) {
        if (isEmpty(s)) {
            return null;
        }
        int len = s.length();
        if (len <= 1) return s;
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * 转化为半角字符
     *
     * @param s 待转字符串
     * @return 半角字符串
     */
    public static String toDBC(final String s) {
        if (isEmpty(s)) return s;
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 转化为全角字符
     *
     * @param s 待转字符串
     * @return 全角字符串
     */
    public static String toSBC(final String s) {
        if (isEmpty(s)) return s;
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }
}
