package com.zzuli.gaokao.Utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class HeaderUtil {

    /**
     * 绘制字体头像
     * 如果是英文名，只显示首字母大写
     * 如果是中文名，只显示最后两个字
     *
     * @param name
     * @param outputPath 文件路径
     * @param outputName 文件名
     */
    public static void generate(String name,String outputPath,String outputName){
        try {
            generateImg(name,outputPath,outputName,30);
        } catch (IOException e) {
           log.warn("头像生成失败！",e);
        }
    }



    private static void generateImg(String name, String outputPath, String outputName, Integer fillet) throws IOException {
        int width = 100;
        int height = 100;
        int nameLen = name.length();
        String nameWritten;
        // 如果用户输入的姓名少于等于2个字符，不用截取
        if (nameLen <= 2) {
            nameWritten = name;
        } else {
            // 如果用户输入的姓名大于等于3个字符，截取后面两位
            String first = name.substring(0, 1);
            if (isChinese(first)) {
                // 截取倒数两位汉字
                nameWritten = name.substring(nameLen - 2);
            } else {
                // 截取前面的两个英文字母
                nameWritten = name.substring(0, 2).toUpperCase();
            }
        }

        String filename = outputPath + File.separator + outputName + ".jpg";
        File file = new File(filename);
        //Font font = new Font("微软雅黑", Font.PLAIN, 30);

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setBackground(getRandomColor());

        g2.clearRect(0, 0, width, height);

        g2.setPaint(Color.WHITE);


        Font font = null;
        // 两个字及以上
        if (nameWritten.length() >= 2) {
            font = new Font("微软雅黑", Font.PLAIN, 30);
            g2.setFont(font);

            String firstWritten = nameWritten.substring(0, 1);
            String secondWritten = nameWritten.substring(1, 2);

            // 两个中文 如 张三
            if (isChinese(firstWritten) && isChinese(secondWritten)) {
                g2.drawString(nameWritten, 20, 60);
            }
            // 首中次英 如 张S
            else if (isChinese(firstWritten) && !isChinese(secondWritten)) {
                g2.drawString(nameWritten, 27, 60);

            }
            // 首英,如 ZS
            else {
                nameWritten = nameWritten.substring(0, 1);
            }

        }
        // 一个字
        if (nameWritten.length() == 1) {
            // 中文
            if (isChinese(nameWritten)) {
                font = new Font("微软雅黑", Font.PLAIN, 50);
                g2.setFont(font);
                g2.drawString(nameWritten, 25, 70);
            }
            // 英文
            else {
                font = new Font("微软雅黑", Font.PLAIN, 55);
                g2.setFont(font);
                g2.drawString(nameWritten.toUpperCase(), 33, 67);
            }

        }

        BufferedImage rounded = makeRoundedCorner(bi, fillet);
        ImageIO.write(rounded, "png", file);
    }




    public static boolean isChinese(String str) {
        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if (m.find()) return true;
        else return false;
    }



    private static Color getRandomColor() {
        String[] beautifulColors = new String[]{"2,168,250"};
        int len = beautifulColors.length;
        Random random = new Random();
        String[] color = beautifulColors[random.nextInt(len)].split(",");
        return new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2]));
    }


    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return output;
    }




}
