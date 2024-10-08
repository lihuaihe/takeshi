package com.takeshi.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.exception.TakeshiException;
import lombok.SneakyThrows;

/**
 * 全球手机号码工具类
 * <pre>{@code
 * implementation 'com.googlecode.libphonenumber:libphonenumber:+'
 * }</pre>
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
     * 测试电话号码是否与有效模式匹配
     *
     * @param phoneNumber 电话号码
     * @return boolean
     */
    @SneakyThrows
    public static boolean isValidNumber(Phonenumber.PhoneNumber phoneNumber) {
        return instance.isValidNumber(phoneNumber);
    }

    /**
     * 测试电话号码是否与有效模式匹配，不匹配抛出异常
     *
     * @param countryCode 区号
     * @param number      电话号码
     */
    public static void verifyNumber(String countryCode, String number) {
        boolean validNumber;
        try {
            validNumber = instance.isValidNumber(parse(countryCode, number));
        } catch (NumberParseException ignored) {
            validNumber = false;
        }
        if (!validNumber) {
            throw new TakeshiException(TakeshiCode.MOBILE_VALIDATION);
        }
    }

    /**
     * 测试电话号码是否与有效模式匹配，不匹配抛出异常
     *
     * @param phoneNumber 电话号码
     */
    public static void verifyNumber(Phonenumber.PhoneNumber phoneNumber) {
        if (!instance.isValidNumber(phoneNumber)) {
            throw new TakeshiException(TakeshiCode.MOBILE_VALIDATION);
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

    /**
     * 解析字符串并将其作为原始缓冲区格式的电话号码返回
     *
     * @param phoneNumber   电话号码
     * @param defaultRegion 默认国家区域
     * @return PhoneNumber
     * @throws NumberParseException 数字解析异常
     */
    public static Phonenumber.PhoneNumber parseWithRegion(String phoneNumber, String defaultRegion) throws NumberParseException {
        return instance.parse(phoneNumber, defaultRegion);
    }

}
