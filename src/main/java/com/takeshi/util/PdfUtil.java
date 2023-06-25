package com.takeshi.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.template.Template;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * PdfUtil
 * <p>css中还需要添加字体样式才会显示中文 body{font-family: SimSun;}</p>
 * <pre>{@code
 * <dependency>
 * <groupId>org.springframework.boot</groupId>
 * <artifactId>spring-boot-starter-thymeleaf</artifactId>
 * </dependency>
 *
 * <dependency>
 * <groupId>org.xhtmlrenderer</groupId>
 * <artifactId>flying-saucer-pdf</artifactId>
 * <version>9.1.22</version>
 * </dependency>
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public final class PdfUtil {

    /**
     * 默认的字体文件夹
     */
    public static final String FONT_FILES = "fonts";

    private PdfUtil() {
    }

    /**
     * PDF预览（通过浏览器GET请求直接访问可直接预览显示PDF）
     *
     * @param templateName 模板名称（不需要文件后缀名）
     * @param map          模板参数集
     * @param response     HttpServletResponse
     */
    public static void preview(String templateName, Map<?, ?> map, HttpServletResponse response) {
        try {
            generatePdf(templateName, map, response.getOutputStream());
        } catch (DocumentException | IOException e) {
            log.error("PdfUtil.preview --> ", e);
        }
    }

    /**
     * 以文件流形式下载到浏览器
     *
     * @param templateName 模板名称（不需要文件后缀名）
     * @param map          模板参数集
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
        } catch (DocumentException | IOException e) {
            log.error("PdfUtil.download --> ", e);
        }
    }

    /**
     * PDF下载到特定位置
     *
     * @param templateName 模板名称（不需要文件后缀名）
     * @param map          模板参数集
     * @param file         下载文件存放位置
     */
    public static void save(String templateName, Map<?, ?> map, File file) {
        try {
            generatePdf(templateName, map, FileUtil.getOutputStream(file));
        } catch (DocumentException | IOException e) {
            log.error("PdfUtil.save --> ", e);
        }
    }

    /**
     * 核心: 根据Thymeleaf 模板生成PDF文档
     *
     * @param templateName 模板名称（不需要文件后缀名）
     * @param bindingMap   绑定的参数，此Map中的参数会替换模板中的变量
     * @param out          输出流
     * @throws DocumentException 文件异常
     * @throws IOException       IO异常
     */
    private static void generatePdf(String templateName, Map<?, ?> bindingMap,
                                    OutputStream out) throws DocumentException, IOException {
        Template template = TakeshiUtil.getTemplateEngine().getTemplate(templateName + ".html");
        String render = template.render(bindingMap);
        ITextRenderer renderer = new ITextRenderer();
        // 添加字体
        addFontDirectory(renderer);
        renderer.setDocumentFromString(render);
        renderer.layout();
        renderer.createPDF(out, true);
        out.close();
    }

    /**
     * 添加字体，将 resources/fonts 目录下的字体文件全部引入
     *
     * @param renderer ITextRenderer
     */
    private static void addFontDirectory(ITextRenderer renderer) {
        // 解决中文支持问题，css中还需要添加字体样式才会显示中文 body{font-family: SimSun;}
        TakeshiUtil.listFileNames(FONT_FILES).forEach(item -> {
            try {
                renderer.getFontResolver().addFont(FONT_FILES + StrUtil.SLASH + item, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            } catch (DocumentException | IOException e) {
                log.error("PdfUtil.addFontDirectory --> pdf add [" + item + "] font file exception", e);
            }
        });
    }

}
