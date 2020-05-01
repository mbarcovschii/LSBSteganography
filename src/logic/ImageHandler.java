package logic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public abstract class ImageHandler {

    /**
     * Принимает текстовое двоичное представление байта и возвращает его
     * нормальный вид - 8 символов и знак минуса, если требуется
     * @param unFormattedByte строковое двоичное представление байта
     * @return форматированный байт
     */
    public StringBuilder getFormattedByte(StringBuilder unFormattedByte) {
        StringBuilder formattedByte = new StringBuilder(unFormattedByte);

        if (formattedByte.charAt(0) == '-') {
            while (formattedByte.length() < 9) {
                formattedByte.insert(1, "0");
            }
        } else {
            while (formattedByte.length() < 8) {
                formattedByte.insert(0, "0");
            }
        }

        return formattedByte;
    }

    public String getFormattedByte(String unFormattedByte) {
        return getFormattedByte(new StringBuilder(unFormattedByte)).toString();
    }

    /**
     * Метод получает путь к изображению и возвращает изображение в виде массива байт
     * @param imagePath путь к изображению
     * @return массив байтов, из которых состоит изображение
     * @throws IOException если ImageIO не сможет прочитать указанный файл
     */
    protected byte[] getImageBytes(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "bmp", byteArrayOutputStream);
        byteArrayOutputStream.flush();

        String base64String = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        return Base64.getDecoder().decode(base64String);
    }
}
