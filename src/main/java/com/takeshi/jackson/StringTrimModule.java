package com.takeshi.jackson;// package com.takeshi.jackson;
//
// import cn.hutool.core.util.StrUtil;
// import org.springframework.beans.propertyeditors.StringTrimmerEditor;
// import org.springframework.web.bind.WebDataBinder;
//
///**
// * 字符串去掉首尾空格，会和其他String类型的格式化冲突
// *
// * @author 七濑武【Nanase Takeshi】
// */
//
//@JsonComponent
//@ControllerAdvice
// public class StringTrimModule extends SimpleModule {
//
//    /**
//     * url和form表单中的参数trim
//     *
//     * @param binder
//     */
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        //如果要将空字符串转换为null，则为true
//        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
//    }
//
//    /**
//     * Request Body中JSON或XML对象参数trim
//     */
//    public StringTrimModule() {
//        addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
//            @Override
//            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//                return StrUtil.trimToNull(p.getText());
//            }
//        });
//    }
//
//}
