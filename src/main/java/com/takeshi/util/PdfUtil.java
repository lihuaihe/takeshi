package com.takeshi.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

/**
 * PdfUtil
 * <p>默认使用thymeleaf作为html模版引擎</p>
 * <p>如需其他字体支持，自行设置字体，参考{@link ConverterProperties}, 且css中还需要添加字体样式才会显示中文 BODY{font-family: SimSun;}</p>
 *
 * <pre>{@code
 * implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
 * implementation 'com.itextpdf:html2pdf:5.0.0'
 *
 * HTML标头中建议设置 <meta charset="UTF-8">
 *
 * // 如需按照页面分页生成， 则需在CSS中定义分页，如下所示
 * @page {
 *   @bottom-right {
 *      content: "Page " counter(page) " of " counter(pages);
 *   }
 * }
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public final class PdfUtil {

    /**
     * 配置一个默认的HtmlConverter将使用的属性，及默认使用的字体
     */
    private static final ConverterProperties DEFAULT_CONVERTER_PROPERTIES = new ConverterProperties().setCharset("UTF_8").setFontProvider(new DefaultFontProvider());

    private PdfUtil() {
    }

    /**
     * PDF预览（通过浏览器GET请求直接访问可直接预览显示PDF）
     *
     * @param templateName 模板文件名称
     * @param map          绑定的参数，此Map中的参数会替换模板中的变量
     * @param response     HttpServletResponse
     */
    @SneakyThrows
    public static void preview(String templateName, Map<?, ?> map, HttpServletResponse response) {
        generatePdf(templateName, map, response.getOutputStream());
    }

    /**
     * PDF预览（通过浏览器GET请求直接访问可直接预览显示PDF）
     *
     * @param templateName        模板文件名称
     * @param map                 绑定的参数，此Map中的参数会替换模板中的变量
     * @param response            HttpServletResponse
     * @param converterProperties HtmlConverter将使用的属性
     */
    @SneakyThrows
    public static void preview(String templateName, Map<?, ?> map, HttpServletResponse response, ConverterProperties converterProperties) {
        generatePdf(templateName, map, response.getOutputStream(), converterProperties);
    }

    /**
     * 以文件流形式下载到浏览器
     *
     * @param templateName 模板文件名称
     * @param map          绑定的参数，此Map中的参数会替换模板中的变量
     * @param response     HttpServletResponse
     */
    public static void download(String templateName, Map<?, ?> map, HttpServletResponse response) {
        download(templateName, map, response, IdUtil.fastSimpleUUID());
    }

    /**
     * 以文件流形式下载到浏览器
     *
     * @param templateName 模板文件名称
     * @param map          绑定的参数，此Map中的参数会替换模板中的变量
     * @param response     HttpServletResponse
     * @param fileName     文件名，不需要带扩展名，会自动添加扩展名(.pdf)
     */
    public static void download(String templateName, Map<?, ?> map, HttpServletResponse response, String fileName) {
        download(templateName, map, response, fileName, DEFAULT_CONVERTER_PROPERTIES);
    }

    /**
     * 以文件流形式下载到浏览器
     *
     * @param templateName        模板文件名称
     * @param map                 绑定的参数，此Map中的参数会替换模板中的变量
     * @param response            HttpServletResponse
     * @param fileName            文件名，不需要带扩展名，会自动添加扩展名(.pdf)
     * @param converterProperties HtmlConverter将使用的属性
     */
    @SneakyThrows
    public static void download(String templateName, Map<?, ?> map, HttpServletResponse response, String fileName, ConverterProperties converterProperties) {
        // 配置文件下载
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/pdf;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLUtil.encode(fileName) + ".pdf");
        generatePdf(templateName, map, response.getOutputStream(), converterProperties);
    }

    /**
     * PDF下载到特定位置
     *
     * @param templateName 模板文件名称
     * @param map          绑定的参数，此Map中的参数会替换模板中的变量
     * @param file         下载文件存放位置
     */
    public static void save(String templateName, Map<?, ?> map, File file) {
        generatePdf(templateName, map, FileUtil.getOutputStream(file));
    }

    /**
     * PDF下载到特定位置
     *
     * @param templateName        模板文件名称
     * @param map                 绑定的参数，此Map中的参数会替换模板中的变量
     * @param file                下载文件存放位置
     * @param converterProperties HtmlConverter将使用的属性
     */
    public static void save(String templateName, Map<?, ?> map, File file, ConverterProperties converterProperties) {
        generatePdf(templateName, map, FileUtil.getOutputStream(file), converterProperties);
    }

    /**
     * 根据模版引擎将模板与绑定参数融合后返回为字符串
     *
     * @param templateName 模板文件名称
     * @param bindingMap   绑定的参数，此Map中的参数会替换模板中的变量
     * @return 融合后的内容
     */
    public static String getTemplateContent(String templateName, Map<?, ?> bindingMap) {
        String resource = StrUtil.isBlank(FileNameUtil.extName(templateName)) ? templateName + ".html" : templateName;
        return TakeshiUtil.getTemplateEngine().getTemplate(resource).render(bindingMap);
    }

    /**
     * 根据模版引擎融合后的内容生成PDF文档
     *
     * @param templateName 模板文件名称
     * @param bindingMap   绑定的参数，此Map中的参数会替换模板中的变量
     * @param out          输出流
     */
    public static void generatePdf(String templateName, Map<?, ?> bindingMap, OutputStream out) {
        generatePdf(templateName, bindingMap, out, DEFAULT_CONVERTER_PROPERTIES);
    }

    /**
     * 根据模版引擎融合后的内容生成PDF文档
     *
     * @param templateName        模板文件名称
     * @param bindingMap          绑定的参数，此Map中的参数会替换模板中的变量
     * @param out                 输出流
     * @param converterProperties HtmlConverter将使用的属性
     */
    @SneakyThrows
    public static void generatePdf(String templateName, Map<?, ?> bindingMap, OutputStream out,
                                   ConverterProperties converterProperties) {
        try (out) {
            String templateContent = getTemplateContent(templateName, bindingMap);
            HtmlConverter.convertToPdf(templateContent, out, ObjUtil.defaultIfNull(converterProperties, DEFAULT_CONVERTER_PROPERTIES));
        }
    }

}
