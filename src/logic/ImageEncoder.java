package logic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

public class ImageEncoder extends ImageHandler {

    public String imageName = "image";

    protected String lineSeparator;

    /**
     * Конструктор заносит в переменную lineSeparator строку, отвечающую за резделение строк
     * в определенной системе. Это будет необходимо при считывании текста из текстового файла
     */
    public ImageEncoder() {
        lineSeparator = System.getProperty("line.separator");
    }

    /**
     * Метод записывает текстовый документ textPath в изображение imagePath
     * Работает только с изображениями формата bmp
     * Осторожно, при слишком большом объеме текстовой информации или
     * слишком маленьком изображении возможны помехи и шумы на обработанном изображении
     *
     * @param imagePath путь к изображению
     * @param textPath  путь к текстовому файлу
     * @throws IOException смотреть внутренние методы
     */
    public void encodeMessage(String imagePath, String textPath) throws IOException {
        byte[] imageBytes = getImageBytes(imagePath);
        byte[] textBytes = getTextBytes(textPath);

        /*
         * Количество байт в изображении, которые программа сможет использовать для
         * записи в них информации. Данные изображения начинаются с 1024 байта, помимо
         * этого программа будет использовать следующие 16 байт для записи длины текста
         * и еще 2 байта для записи количество бит, которые будут зарезервированы в
         * каждом байте изображения для записи информации. 1024 + 16 + 2 = 1042
         */
        int writeableBytes = imageBytes.length - 1042;

        /*
         * Количество младших бит, которые будут перезаписаны в байтах изображения,
         * на разбитые на части байты текстового документа
         */
        int numOfReplaceableBits = getNumOfBitsToReplace(textBytes.length, writeableBytes);
        if (numOfReplaceableBits == -1) {
            System.out.println("This text is too big for such an image!");
            return;
        }

        // Запись информации о длине текста в картинку
        embedTextSizeInfo(imageBytes, textBytes.length, numOfReplaceableBits);
        // Запись информации о количестве заменяемых бит
        embedNumOfBitsToReplaceInfo(imageBytes, numOfReplaceableBits);
        // Запись самого текста в изображение
        embedTextFileToImage(imageBytes, textBytes, numOfReplaceableBits);

        // Запись изображения на жесткий диск
        writeImageToHardDrive(imageBytes);
    }

    /**
     * @param textPath путь к текстовому файлу
     * @return массив байтов, из которых состоит текстовый документ
     * @throws IOException если программе не удастся закрыть fileReader
     *                     Также выбрасывается подкласс IOException - FileNotFoundException.
     *                     Такое исключение выбрасывается в случае, если fileReader не найдет
     *                     указанный файл
     */
    protected byte[] getTextBytes(String textPath) throws IOException {
        FileReader fileReader = new FileReader(textPath);
        Scanner scanner = new Scanner(fileReader);

        StringBuilder fileText = new StringBuilder();
        while (scanner.hasNextLine()) {
            fileText.append(scanner.nextLine());
            fileText.append(lineSeparator);
        }

        fileReader.close();
        return fileText.toString().getBytes();
    }

    /**
     * Данный метод рассчитывает, сколько младших бит потребуется использовать
     * для записи информации длиной textLength байт в количетсво байт availableLength
     * Если метод вернет 1, это значит, что каждый байт информации для записи
     * будет разделен на 8 частей, потому что в каждый доступный байт будет записано
     * по одному биту информации.
     * Если, например, 8 - байты не будут разделены, а будут просто записываться
     * поверх байтов изображения
     *
     * @param textLength      длина текста в байтах
     * @param availableLength количество байт, доступных для записи
     * @return минимальное число младших бит в доступных для записи байтах,
     * которые потребуется использовать для записи информации длиной
     * textLength байт
     */
    protected int getNumOfBitsToReplace(int textLength, int availableLength) {
        int textLengthInBits = textLength * 8;

        for (int i = 1; i <= 8; i++) {
            if (Math.ceil(textLengthInBits / (float) i) <= availableLength) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Метод вшивает в изображение информацию о размере записываемого в него текста
     * Для этой информации используются 16 байт изображения, начиная с 1078 по 1093
     * Под размером текста имеется ввиду не то, сколько байт занимал оригинальный текст,
     * а то, сколько байт в картинке займут разделенные на части байты текста
     *
     * @param imageBytes           изображение в виде массива байт
     * @param textLength           длина записываемого в изображение текста
     * @param numOfReplaceableBits количество младших бит, которые будут перезаписываться
     *                             в байтах изображения на разбитые на части байты текста
     */
    protected void embedTextSizeInfo(byte[] imageBytes, int textLength, int numOfReplaceableBits) {
        String binaryTextLength =
                Integer.toString((int) Math.ceil(textLength * 8.0 / numOfReplaceableBits), 2);

        if (binaryTextLength.length() % 2 == 1) {
            binaryTextLength = "0" + binaryTextLength;
        }

        // Позиция, на которой начнется запись размера текста
        // Остальные биты, зарезервированные для записи размера,
        // но предшествующие этой позиции, заполнятся нулями
        int startPosition = 16 - binaryTextLength.length() / 2;

        // Заполнение избыточных позиций нулями
        for (int i = 1024; i < 1024 + startPosition; i++) {
            String byteString = Integer.toString(imageBytes[i], 2);
            byteString = byteString.substring(0, byteString.length() - 2) + "00";
            imageBytes[i] = (byte) Integer.parseInt(byteString, 2);
        }

        // Заполнение остальных позиций информацией о размере текста
        // 1024 + 16 = 1040
        for (int i = 1024 + startPosition, j = 0; i < 1040; i++, j += 2) {
            String byteString = Integer.toString(imageBytes[i], 2);
            byteString = byteString.substring(0, byteString.length() - 2) +
                    binaryTextLength.charAt(j) + binaryTextLength.charAt(j + 1);
            imageBytes[i] = (byte) Integer.parseInt(byteString, 2);
        }
    }

    /**
     * Метод вшивает в изображение информацию о количестве заменяемых младших бит в
     * в байтах изображения для записи в них бит текстовой информации
     * Для этой информации используются 2 байта изображения, начиная с 1094 по 1095
     *
     * @param imageBytes           изображение в виде массива байт
     * @param numOfReplaceableBits количество младших бит, которые будут перезаписываться
     *                             в байтах изображения на разбитые на части байты текста
     */
    protected void embedNumOfBitsToReplaceInfo(byte[] imageBytes, int numOfReplaceableBits) {
        StringBuilder binaryNumOfReplaceableBits =
                new StringBuilder(Integer.toString(numOfReplaceableBits, 2));

        while (binaryNumOfReplaceableBits.length() < 4) {
            binaryNumOfReplaceableBits.insert(0, "0");
        }

        String byteString = Integer.toString(imageBytes[1040], 2);
        byteString = byteString.substring(0, byteString.length() - 2) +
                binaryNumOfReplaceableBits.charAt(0) + binaryNumOfReplaceableBits.charAt(1);
        imageBytes[1040] = (byte) Integer.parseInt(byteString, 2);

        byteString = Integer.toString(imageBytes[1041], 2);
        byteString = byteString.substring(0, byteString.length() - 2) +
                binaryNumOfReplaceableBits.charAt(2) + binaryNumOfReplaceableBits.charAt(3);
        imageBytes[1041] = (byte) Integer.parseInt(byteString, 2);
    }

    /**
     * Метод записывает текстовый документ в изображение
     * В методе ошибка, в каком месте указано дальше
     *
     * @param imageBytes           изображение в виде массива байт
     * @param textBytes            текст в виде массива байт
     * @param numOfReplaceableBits количество младших бит для замены
     */
    protected void embedTextFileToImage(byte[] imageBytes, byte[] textBytes, int numOfReplaceableBits) {
        String[] buffer =
                new String[(int) Math.ceil(textBytes.length * 8.0 / numOfReplaceableBits)];
        buffer[0] = "";

        for (int i = 0, bufferIndex = 0, symbolNumber = 0; i < textBytes.length; i++) {
            StringBuilder binaryText = new StringBuilder(Integer.toString(textBytes[i], 2));
            binaryText = getFormattedByte(binaryText);

            for (int j = 0; j < 8; j++) {
                if (symbolNumber == numOfReplaceableBits) {
                    symbolNumber = 0;
                    bufferIndex++;
                    buffer[bufferIndex] = "";
                }

                buffer[bufferIndex] += binaryText.charAt(j);
                symbolNumber++;
            }
        }

        // Скорее всего проблема в этом месте, в буфере все правильно, ошибки при
        // извлечении битов из массива байт изображения
        for (int i = 0, imageIndex = 1042; i < buffer.length; i++, imageIndex++) {
            StringBuilder binaryText = new StringBuilder(Integer.toString(imageBytes[imageIndex], 2));
            binaryText = getFormattedByte(binaryText);

            binaryText.replace(binaryText.length() - numOfReplaceableBits,
                    binaryText.length(), buffer[i]);

            imageBytes[imageIndex] = (byte) Integer.parseInt(binaryText.toString(), 2);
        }
    }

    /**
     * Метод записывает обработанное изображение на жесткий диск
     *
     * @param imageBytes изображение в виде массива байт
     * @throws IOException если не удается считать массив байт
     */
    protected void writeImageToHardDrive(byte[] imageBytes) throws IOException {
        BufferedImage processedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        ImageIO.write(processedImage, "bmp",
                new File("media/processedPictures/" + imageName));
    }
}
