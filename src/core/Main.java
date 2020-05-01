package core;

import logic.ImageDecoder;
import logic.ImageEncoder;

import java.io.IOException;

public class Main {

    // Картинка, которую я использую для хранения данных выделяет на каждый пиксель по 48 бит
    public static void main(String[] args) {
        ImageEncoder imageEncoder = new ImageEncoder();
        ImageDecoder imageDecoder = new ImageDecoder();
        try {
            imageEncoder.imageName = "thickCat1.bmp";
            imageEncoder.encodeMessage("media/pictures/thickCat.bmp", "media/encodedText/text1");
            imageDecoder.fileName = "text1";
            imageDecoder.decodeMessage("media/processedPictures/thickCat1.bmp");
            System.out.printf("%d%n", imageDecoder.getNumOfReplacedBits("media/processedPictures/thickCat1.bmp"));

            imageEncoder.imageName = "thickCat2.bmp";
            imageEncoder.encodeMessage("media/pictures/thickCat.bmp", "media/encodedText/text2");
            imageDecoder.fileName = "text2";
            imageDecoder.decodeMessage("media/processedPictures/thickCat2.bmp");
            System.out.printf("%d%n", imageDecoder.getNumOfReplacedBits("media/processedPictures/thickCat2.bmp"));

            imageEncoder.imageName = "thickCat3.bmp";
            imageEncoder.encodeMessage("media/pictures/thickCat.bmp", "media/encodedText/text3");
            imageDecoder.fileName = "text3";
            imageDecoder.decodeMessage("media/processedPictures/thickCat3.bmp");
            System.out.printf("%d%n", imageDecoder.getNumOfReplacedBits("media/processedPictures/thickCat3.bmp"));

            imageEncoder.imageName = "thickCat4.bmp";
            imageEncoder.encodeMessage("media/pictures/thickCat.bmp", "media/encodedText/text4");
            imageDecoder.fileName = "text4";
            imageDecoder.decodeMessage("media/processedPictures/thickCat4.bmp");
            System.out.printf("%d%n", imageDecoder.getNumOfReplacedBits("media/processedPictures/thickCat4.bmp"));

            imageEncoder.imageName = "thickCat5.bmp";
            imageEncoder.encodeMessage("media/pictures/thickCat.bmp", "media/encodedText/text5");
            imageDecoder.fileName = "text5";
            imageDecoder.decodeMessage("media/processedPictures/thickCat5.bmp");
            System.out.printf("%d%n", imageDecoder.getNumOfReplacedBits("media/processedPictures/thickCat5.bmp"));

            imageEncoder.imageName = "thickCat6.bmp";
            imageEncoder.encodeMessage("media/pictures/thickCat.bmp", "media/encodedText/text6");
            imageDecoder.fileName = "text6";
            imageDecoder.decodeMessage("media/processedPictures/thickCat6.bmp");
            System.out.printf("%d%n", imageDecoder.getNumOfReplacedBits("media/processedPictures/thickCat6.bmp"));

            imageEncoder.imageName = "thickCat7.bmp";
            imageEncoder.encodeMessage("media/pictures/thickCat.bmp", "media/encodedText/text7");
            imageDecoder.fileName = "text7";
            imageDecoder.decodeMessage("media/processedPictures/thickCat7.bmp");
            System.out.printf("%d%n", imageDecoder.getNumOfReplacedBits("media/processedPictures/thickCat7.bmp"));

            imageEncoder.imageName = "thickCat8.bmp";
            imageEncoder.encodeMessage("media/pictures/thickCat.bmp", "media/encodedText/text8");
            imageDecoder.fileName = "text8";
            imageDecoder.decodeMessage("media/processedPictures/thickCat8.bmp");
            System.out.printf("%d%n", imageDecoder.getNumOfReplacedBits("media/processedPictures/thickCat8.bmp"));
        } catch (IOException cantReadThisFileException) {
            cantReadThisFileException.printStackTrace();
        }
    }
}
