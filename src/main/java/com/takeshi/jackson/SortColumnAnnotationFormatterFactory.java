package com.takeshi.jackson;

import com.takeshi.constraints.VerifySortColumn;
import com.takeshi.pojo.basic.TakeshiPage;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Collections;
import java.util.Set;

/**
 * 对排序字段进行转下划线处理，只适用于非json参数
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class SortColumnAnnotationFormatterFactory implements AnnotationFormatterFactory<VerifySortColumn> {

    @Override
    public Set<Class<?>> getFieldTypes() {
        return Collections.singleton(String.class);
    }

    @Override
    public Printer<?> getPrinter(VerifySortColumn annotation, Class<?> fieldType) {
        return (Printer<String>) (object, locale) -> object;
    }

    @Override
    public Parser<?> getParser(VerifySortColumn annotation, Class<?> fieldType) {
        return (Parser<String>) (text, locale) -> TakeshiPage.sortColumnToUnderline(text);
    }

}
