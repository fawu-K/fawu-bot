package com.kang.commons.util;

import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: service
 * @description: 处理html文件的类
 * @author: K.faWu
 * @create: 2022-07-20 10:15
 **/

public class HtmlUtil {
    public Html2Java toPlainText(final String html) {
        if (CommonsUtils.isEmpty(html)){
            return null;
        }

        final Document document = Jsoup.parse(html);
        final Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
        document.outputSettings(outputSettings);
        document.select("br").append("\\n");
        document.select("p").prepend("\\n");
        document.select("p").append("\\n");
        document.select("img").append("【/img】");
        document.select("at").append("【/at】");
        //获取所有的img标签
        Elements img = document.select("img");
        List<String> imgs = new ArrayList<>();
        img.forEach(element -> {
            imgs.add(element.attributes().get("src"));
        });
        //获取所有的at标签
        Elements at = document.select("at");
        List<String> ats = new ArrayList<>();
        at.forEach(element -> {
            ats.add(element.attributes().get("code"));
        });
        final String newHtml = document.html().replaceAll("\\\\n", "\n");
        final String plainText = Jsoup.clean(newHtml, "", Whitelist.none(), outputSettings);
        Html2Java html2Java = new Html2Java();
        html2Java.setText(plainText);
        html2Java.setImgs(imgs);
        html2Java.setAts(ats);
        return html2Java;
    }

    @Data
    public static class Html2Java{
        private String text;
        private List<String> imgs;
        private List<String> ats;
    }
}
