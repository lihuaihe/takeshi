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
     * @param countryCode 区号，例如：+86
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
     * @param phoneNumber 带区号的电话号码，例如：+8618888888888
     * @return boolean
     */
    @SneakyThrows
    public static boolean isValidNumber(String phoneNumber) {
        return instance.isValidNumber(parse(phoneNumber));
    }

    /**
     * 测试电话号码是否与有效模式匹配
     *
     * @param phoneNumber   带区号的电话号码，例如：+8618888888888
     * @param defaultRegion 默认国家区域，例如：CN
     * @return boolean
     */
    @SneakyThrows
    public static boolean isValidNumberWithRegion(String phoneNumber, String defaultRegion) {
        return instance.isValidNumber(parseWithRegion(phoneNumber, defaultRegion));
    }

    /**
     * 测试电话号码是否与有效模式匹配
     *
     * @param phoneNumber 电话号码
     * @return boolean
     */
    public static boolean isValidNumber(Phonenumber.PhoneNumber phoneNumber) {
        return instance.isValidNumber(phoneNumber);
    }

    /**
     * 测试电话号码是否与有效模式匹配，不匹配抛出异常
     *
     * @param countryCode 区号，例如：+86
     * @param number      电话号码
     */
    public static void verifyNumber(String countryCode, String number) {
        try {
            if (!instance.isValidNumber(parse(countryCode, number))) {
                throw new TakeshiException(TakeshiCode.MOBILE_VALIDATION);
            }
        } catch (NumberParseException e) {
            throw new TakeshiException(TakeshiCode.MOBILE_VALIDATION);
        }
    }

    /**
     * 测试电话号码是否与有效模式匹配，不匹配抛出异常
     *
     * @param phoneNumber 带区号的电话号码，例如：+8618888888888
     */
    public static void verifyNumber(String phoneNumber) {
        try {
            if (!instance.isValidNumber(parse(phoneNumber))) {
                throw new TakeshiException(TakeshiCode.MOBILE_VALIDATION);
            }
        } catch (NumberParseException e) {
            throw new TakeshiException(TakeshiCode.MOBILE_VALIDATION);
        }
    }

    /**
     * 测试电话号码是否与有效模式匹配，不匹配抛出异常
     *
     * @param phoneNumber   带区号的电话号码，例如：+8618888888888
     * @param defaultRegion 默认国家区域，例如：CN
     */
    public static void verifyNumberWithRegion(String phoneNumber, String defaultRegion) {
        try {
            if (!instance.isValidNumber(parseWithRegion(phoneNumber, defaultRegion))) {
                throw new TakeshiException(TakeshiCode.MOBILE_VALIDATION);
            }
        } catch (NumberParseException e) {
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
     * @param countryCode 区号，例如：+86
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
     * @param phoneNumber 带区号的电话号码
     * @return PhoneNumber
     * @throws NumberParseException 数字解析异常
     */
    public static Phonenumber.PhoneNumber parse(String phoneNumber) throws NumberParseException {
        return instance.parse(phoneNumber, null);
    }

    /**
     * 解析字符串并将其作为原始缓冲区格式的电话号码返回
     *
     * @param phoneNumber   电话号码，例如：+8618888888888
     * @param defaultRegion 默认国家区域，例如：CN
     * @return PhoneNumber
     * @throws NumberParseException 数字解析异常
     */
    public static Phonenumber.PhoneNumber parseWithRegion(String phoneNumber, String defaultRegion) throws NumberParseException {
        return instance.parse(phoneNumber, defaultRegion);
    }

    /**
     * 使用E164规则将电话号码格式化为指定格式
     *
     * @param phoneNumber 电话号码
     * @return 格式化的电话号码，例如：+8618888888888
     */
    public static String formatE164(Phonenumber.PhoneNumber phoneNumber) {
        return instance.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    /**
     * 使用INTERNATIONAL规则将电话号码格式化为指定格式
     *
     * @param phoneNumber 电话号码
     * @return 格式化的电话号码，例如：+86 188 8888 8888
     */
    public static String formatInternational(Phonenumber.PhoneNumber phoneNumber) {
        return instance.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
    }

    /**
     * 使用NATIONAL规则将电话号码格式化为指定格式
     *
     * @param phoneNumber 电话号码
     * @return 格式化的电话号码，例如：188 8888 8888
     */
    public static String formatNational(Phonenumber.PhoneNumber phoneNumber) {
        return instance.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
    }

}
