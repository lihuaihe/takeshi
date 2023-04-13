package com.takeshi.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.takeshi.constants.SysCode;
import com.takeshi.exception.TakeshiException;
import lombok.SneakyThrows;

/**
 * 全球手机号码工具类
 *
 * @author 七濑武【Nanase Takeshi】
 */
public final class GlobalPhoneNumberUtil {

    /**
     * PhoneNumberUtil instance
     */
    public static final PhoneNumberUtil instance = PhoneNumberUtil.getInstance();

    /**
     * 构造函数
     */
    private GlobalPhoneNumberUtil() {
    }

    /**
     * 测试电话号码是否与有效模式匹配
     *
     * @param countryCode 区号
     * @param number      电话号码
     * @return boolean
     */
    @SneakyThrows
    public static boolean isValidNumber(String countryCode, String number) {
        return instance.isValidNumber(parse(countryCode, number));
    }

    /**
     * 测试电话号码是否与有效模式匹配，不匹配抛出异常
     *
     * @param countryCode 区号
     * @param number      电话号码
     */
    public static void checkNumber(String countryCode, String number) {
        boolean validNumber;
        try {
            validNumber = instance.isValidNumber(parse(countryCode, number));
        } catch (NumberParseException ignored) {
            validNumber = false;
        }
        if (!validNumber) {
            throw new TakeshiException(SysCode.MOBILE_VALIDATION);
        }
    }

    /**
     * 解析字符串并将其作为原始缓冲区格式的电话号码返回
     *
     * @param countryCode 区号
     * @param number      电话号码
     * @return PhoneNumber
     * @throws NumberParseException 数字解析异常
     */
    public static Phonenumber.PhoneNumber parse(String countryCode, String number) throws NumberParseException {
        return instance.parse(countryCode.concat(number), null);
    }

}
