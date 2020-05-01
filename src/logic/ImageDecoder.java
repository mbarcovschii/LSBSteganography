package logic;

import java.io.FileWriter;
import java.io.IOException;

public class ImageDecoder extends ImageHandler {

    public String fileName = "text";

    /**
     * Метод считывает скрытый в изображении текст и записывает
     * результат чтения в текстовый файл с выбранным названием
     * Скрытый текст заносится в изображение с помощью класса ImageEncoder
     *
     * @param imagePath путь к изображению
     * @throws IOException смотреть внутренние методы
     */
    public void decodeMessage(String imagePath) throws IOException {
        byte[] imageBytes = getImageBytes(imagePath);
        int textLength = getTextLength(imageBytes);
        int numOfReplacedBits = getNumOfReplacedBits(imageBytes);
        String[] buffer = new String[textLength];
        buffer[0] = "";

        for (int i = 1042, bufferIndex = 0; i < 1042 + textLength; i++, bufferIndex++) {
            StringBuilder binaryText = new StringBuilder(Integer.toString(imageBytes[i], 2));
            binaryText = getFormattedByte(binaryText);

            buffer[bufferIndex] = binaryText.substring(binaryText.length() - numOfReplacedBits);
        }

        char[] text = new char[buffer.length / (int) Math.ceil(8.0 / numOfReplacedBits)];
        for (int i = 0, bufferIndex = 0, symbolNumber = 0; i < text.length; i++) {
            StringBuilder binaryText = new StringBuilder();

            for (int j = 0; j < 8; j++) {
                if (symbolNumber == numOfReplacedBits) {
                    symbolNumber = 0;
                    bufferIndex++;
                }

                binaryText.append(buffer[bufferIndex].charAt(symbolNumber));
                symbolNumber++;
            }

            text[i] = (char) Integer.parseInt(binaryText.toString(), 2);
        }

        // Запись текста в новый файл
        FileWriter fileWriter = new FileWriter("media/decodedText/" + fileName);
        fileWriter.write(text);
        fileWriter.close();
    }

    /**
     * (Смотреть ImageEncoder)
     *
     * @param imageBytes изображение в виде массива байт
     * @return длину встроенного в изображение текста
     */
    public int getTextLength(byte[] imageBytes) {
        StringBuilder binaryTextSize = new StringBuilder();

        for (int i = 1024; i < 1040; i++) {
            StringBuilder byteString =
                    getFormattedByte(new StringBuilder(Integer.toString(imageBytes[i], 2)));

            binaryTextSize.
                    append(byteString.charAt(byteString.length() - 2)).
                    append(byteString.charAt(byteString.length() - 1));
        }

        return Integer.parseInt(binaryTextSize.toString(), 2);
    }

    public int getTextLength(String imagePath) throws IOException {
        return getTextLength(getImageBytes(imagePath));
    }

    /**
     * (Смотреть ImageEncoder)
     *
     * @param imageBytes изображение в виде массива байт
     * @return количество младших бит, которые были использованы
     * для записи текстовой информации
     */
    public int getNumOfReplacedBits(byte[] imageBytes) {
        StringBuilder binaryNumOfReplaceableBits = new StringBuilder();

        for (int i = 1040; i < 1042; i++) {
            StringBuilder byteString = getFormattedByte(new StringBuilder(Integer.toString(imageBytes[i], 2)));

            binaryNumOfReplaceableBits.
                    append(byteString.charAt(byteString.length() - 2)).
                    append(byteString.charAt(byteString.length() - 1));
        }

        return Integer.parseInt(binaryNumOfReplaceableBits.toString(), 2);
    }

    public int getNumOfReplacedBits(String imagePath) throws IOException {
        return getNumOfReplacedBits(getImageBytes(imagePath));
    }
}
