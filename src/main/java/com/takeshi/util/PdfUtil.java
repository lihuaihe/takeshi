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
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * PdfUtil
 * <p>默认使用thymeleaf作为html模版引擎</p>
 * <p>如需其他字体支持，自行设置字体，且css中还需要添加字体样式才会显示中文 body{font-family: SimSun;}</p>
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
     * 配置一个默认的HtmlConverter将使用的属性
     */
    private static volatile ConverterProperties defaultConverterProperties;

    private PdfUtil() {
    }

    static {
        if (ObjUtil.isNull(defaultConverterProperties)) {
            synchronized (PdfUtil.class) {
                if (ObjUtil.isNull(defaultConverterProperties)) {
                    try {
                        defaultConverterProperties = new ConverterProperties()
                                .setCharset("UTF_8")
                                .setFontProvider(new DefaultFontProvider());
                        log.info("PdfUtil.static --> defaultConverterProperties Initialization successful");
                    } catch (Exception e) {
                        log.error("PdfUtil.static --> defaultConverterProperties initialization failed, e: ", e);
                    }
                }
            }
        }
    }

    /**
     * PDF预览（通过浏览器GET请求直接访问可直接预览显示PDF）
     *
     * @param templateName 模板文件名称
     * @param map          绑定的参数，此Map中的参数会替换模板中的变量
     * @param response     HttpServletResponse
     */
    public static void preview(String templateName, Map<?, ?> map, HttpServletResponse response) {
        try {
            generatePdf(templateName, map, response.getOutputStream());
        } catch (IOException e) {
            log.error("PdfUtil.preview --> ", e);
        }
    }

    /**
     * 以文件流形式下载到浏览器
     *
     * @param templateName 模板文件名称
     * @param map          绑定的参数，此Map中的参数会替换模板中的变量
     * @param response     HttpServletResponse
     */
    public static void download(String templateName, Map<?, ?> map, HttpServletResponse response) {
        try {
            String fileName = IdUtil.fastSimpleUUID() + ".pdf";
            // 配置文件下载
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/pdf;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLUtil.encode(fileName));
            generatePdf(templateName, map, response.getOutputStream());
        } catch (IOException e) {
            log.error("PdfUtil.download --> ", e);
        }
    }

    /**
     * PDF下载到特定位置
     *
     * @param templateName 模板文件名称
     * @param map          绑定的参数，此Map中的参数会替换模板中的变量
     * @param file         下载文件存放位置
     */
    public static void save(String templateName, Map<?, ?> map, File file) {
        try {
            generatePdf(templateName, map, FileUtil.getOutputStream(file));
        } catch (IOException e) {
            log.error("PdfUtil.save --> e: ", e);
        }
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
     * @throws IOException IO异常
     */
    public static void generatePdf(String templateName, Map<?, ?> bindingMap, OutputStream out) throws IOException {
        String templateContent = getTemplateContent(templateName, bindingMap);
        HtmlConverter.convertToPdf(templateContent, out, defaultConverterProperties);
        out.close();
    }

    /**
     * 根据模版引擎融合后的内容生成PDF文档
     *
     * @param templateName        模板文件名称
     * @param bindingMap          绑定的参数，此Map中的参数会替换模板中的变量
     * @param out                 输出流
     * @param converterProperties HtmlConverter将使用的属性
     * @throws IOException IO异常
     */
    public static void generatePdf(String templateName, Map<?, ?> bindingMap, OutputStream out,
                                   ConverterProperties converterProperties) throws IOException {
        String templateContent = getTemplateContent(templateName, bindingMap);
        HtmlConverter.convertToPdf(templateContent, out, converterProperties);
        out.close();
    }

}
