import functions.*;

import functions.basic.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static java.nio.file.Files.newBufferedWriter;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            // Часть 1: Создание объектов Sin и Cos
            System.out.println("1. СОЗДАНИЕ ОБЪЕКТОВ Sin И Cos\n");

            Function sin = new Sin();
            Function cos = new Cos();

            System.out.println("   Значения sin(x) и cos(x) на отрезке [0, π] с шагом 0.1:");
            System.out.println("   x\t\t\tsin(x)\t\t\tcos(x)");
            System.out.println("   -----------------------------------------------------------------");
            for (double x = 0; x <= Math.PI; x += 0.1) {
                double sinValue = sin.getFunctionValue(x);
                double cosValue = cos.getFunctionValue(x);
                System.out.printf("   %.6f\t\t%.10f\t\t%.10f%n", x, sinValue, cosValue);
            }

            // Часть 2: Табулированные аналоги Sin и Cos
            System.out.println("\n\n2. ТАБУЛИРОВАННЫЕ АНАЛОГИ Sin И Cos (10 точек)\n");

            TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
            TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);

            System.out.println("   Сравнение точных и табулированных значений:");
            System.out.println("   x\t\t\tsin(x)\t\t\ttabSin(x)\t\tcos(x)\t\t\ttabCos(x)");
            System.out.println("   -----------------------------------------------------------------------------------------------");

            // Для вычисления погрешностей
            double maxSinError = 0;
            double maxCosError = 0;
            double sumSinError = 0;
            double sumCosError = 0;
            int count = 0;

            for (double x = 0; x <= Math.PI; x += 0.1) {
                double exactSin = sin.getFunctionValue(x);
                double tabSin = tabulatedSin.getFunctionValue(x);
                double exactCos = cos.getFunctionValue(x);
                double tabCos = tabulatedCos.getFunctionValue(x);

                double sinError = Math.abs(exactSin - tabSin);
                double cosError = Math.abs(exactCos - tabCos);

                maxSinError = Math.max(maxSinError, sinError);
                maxCosError = Math.max(maxCosError, cosError);
                sumSinError += sinError;
                sumCosError += cosError;
                count++;

                System.out.printf("   %.6f\t\t%.10f\t%.10f\t\t%.10f\t%.10f%n",
                        x, exactSin, tabSin, exactCos, tabCos);
            }

            double avgSinError = sumSinError / count;
            double avgCosError = sumCosError / count;

            System.out.println("\n   Статистика погрешностей (10 точек):");
            System.out.printf("   sin(x): средняя погрешность = %.10f, максимальная = %.10f%n",
                    avgSinError, maxSinError);
            System.out.printf("   cos(x): средняя погрешность = %.10f, максимальная = %.10f%n",
                    avgCosError, maxCosError);

            // Часть 3: Сумма квадратов табулированных функций
            System.out.println("\n\n3. СУММА КВАДРАТОВ ТАБУЛИРОВАННЫХ ФУНКЦИЙ sin²(x) + cos²(x)\n");

            System.out.println("   Теория: sin²(x) + cos²(x) ≡ 1 для всех x");
            System.out.println("   Проверка точности для разного количества точек табуляции:");
            System.out.println("\n   Кол-во точек\tМакс. отклонение от 1\t\tСред. отклонение от 1");
            System.out.println("   -----------------------------------------------------------------");

            int[] pointCounts = {5, 10, 20, 50, 100};
            for (int points : pointCounts) {
                // Создаем табулированные функции
                TabulatedFunction ts = TabulatedFunctions.tabulate(sin, 0, Math.PI, points);
                TabulatedFunction tc = TabulatedFunctions.tabulate(cos, 0, Math.PI, points);

                // Создаем функцию суммы квадратов
                Function sumOfSquares = Functions.sum(
                        Functions.power(ts, 2),
                        Functions.power(tc, 2)
                );

                double maxDeviation = 0;
                double sumDeviation = 0;
                int deviationCount = 0;

                for (double x = 0; x <= Math.PI; x += 0.1) {
                    double value = sumOfSquares.getFunctionValue(x);
                    if (!Double.isNaN(value)) {
                        double deviation = Math.abs(value - 1.0);
                        maxDeviation = Math.max(maxDeviation, deviation);
                        sumDeviation += deviation;
                        deviationCount++;
                    }
                }

                double avgDeviation = sumDeviation / deviationCount;
                System.out.printf("   %d\t\t%.10f\t\t\t%.10f%n",
                        points, maxDeviation, avgDeviation);
            }

            // Часть 4: Экспонента и работа с текстовыми файлами
            System.out.println("\n\n4. ЭКСПОНЕНТА: ТЕКСТОВЫЙ ФОРМАТ ХРАНЕНИЯ\n");

            // Создаем табулированную экспоненту
            Function exp = new Exp();
            TabulatedFunction tabulatedExp = TabulatedFunctions.tabulate(exp, 0, 10, 11);

            System.out.println("   Создана табулированная экспонента exp(x) на [0, 10] с 11 точками");

            // Сохраняем в текстовый файл
            Path textFile = Paths.get("textFile.txt");
            try (BufferedWriter writer = Files.newBufferedWriter(textFile, StandardCharsets.UTF_8)) {
                TabulatedFunctions.writeTabulatedFunction(tabulatedExp, writer);
                System.out.println("   Функция записана в текстовый файл: " + textFile.toAbsolutePath());
            }

            // Читаем из текстового файла
            TabulatedFunction restoredExp;
            try (Reader reader = Files.newBufferedReader(textFile)) {
                restoredExp = TabulatedFunctions.readTabulatedFunction(reader);
                System.out.println("   Функция прочитана из текстового файла");
            }

            // Сравниваем значения
            System.out.println("\n   Сравнение исходной и восстановленной экспоненты:");
            System.out.println("   x\t\tИсходная exp(x)\t\tВосстановленная\t\tРазность");
            System.out.println("   -----------------------------------------------------------------");

            double maxExpDiff = 0;
            for (double x = 0; x <= 10; x += 1.0) {
                double original = tabulatedExp.getFunctionValue(x);
                double restored = restoredExp.getFunctionValue(x);
                double diff = Math.abs(original - restored);
                maxExpDiff = Math.max(maxExpDiff, diff);

                System.out.printf("   %.0f\t\t%.10f\t%.10f\t\t%.15f%n",
                        x, original, restored, diff);
            }
            System.out.printf("\n   Максимальная разность: %.15f%n", maxExpDiff);

            // Анализ текстового файла
            System.out.println("\n   Анализ текстового файла:");
            String textContent = new String(Files.readAllBytes(textFile));
            System.out.println("   Содержимое файла: \"" + textContent.trim() + "\"");
            System.out.println("   Размер файла: " + Files.size(textFile) + " байт");

            // Часть 5: Логарифм и работа с бинарными файлами
            System.out.println("\n\n5. ЛОГАРИФМ: БИНАРНЫЙ ФОРМАТ ХРАНЕНИЯ\n");

            // Создаем табулированный логарифм (начинаем с 0.1, так как ln(0) = -∞)
            Function ln = new Log(Math.E);
            TabulatedFunction tabulatedLn = TabulatedFunctions.tabulate(ln, 0.1, 10, 11);

            System.out.println("   Создан табулированный натуральный логарифм ln(x) на [0.1, 10] с 11 точками");

            // Сохраняем в бинарный файл
            Path binaryFile = Paths.get("ln_function.bin");
            try (OutputStream out = Files.newOutputStream(binaryFile)) {
                TabulatedFunctions.outputTabulatedFunction(tabulatedLn, out);
                System.out.println("   Функция записана в бинарный файл: " + binaryFile.toAbsolutePath());
            }

            // Читаем из бинарного файла
            TabulatedFunction restoredLn;
            try (InputStream in = Files.newInputStream(binaryFile)) {
                restoredLn = TabulatedFunctions.inputTabulatedFunction(in);
                System.out.println("   Функция прочитана из бинарного файла");
            }

            // Сравниваем значения
            System.out.println("\n   Сравнение исходного и восстановленного логарифма:");
            System.out.println("   x\t\tИсходный ln(x)\t\tВосстановленный\t\tРазность");
            System.out.println("   -----------------------------------------------------------------");

            double maxLnDiff = 0;
            for (double x = 0.1; x <= 10; x += 1.0) {
                double original = tabulatedLn.getFunctionValue(x);
                double restored = restoredLn.getFunctionValue(x);
                double diff = Math.abs(original - restored);
                maxLnDiff = Math.max(maxLnDiff, diff);

                System.out.printf("   %.1f\t\t%.10f\t\t%.10f\t\t%.15f%n",
                        x, original, restored, diff);
            }
            System.out.printf("\n   Максимальная разность: %.15f%n", maxLnDiff);

            // Анализ бинарного файла
            System.out.println("\n   Анализ бинарного файла:");
            System.out.println("   Размер файла: " + Files.size(binaryFile) + " байт");

            // Выводим начало бинарного файла в hex
            System.out.println("   Первые 64 байта файла (hex):");
            try (InputStream in = Files.newInputStream(binaryFile)) {
                byte[] buffer = new byte[64];
                int bytesRead = in.read(buffer);

                for (int i = 0; i < bytesRead; i++) {
                    if (i % 16 == 0) {
                        System.out.printf("%n   %04X: ", i);
                    }
                    System.out.printf("%02X ", buffer[i]);
                    if (i % 8 == 7) {
                        System.out.print(" ");
                    }
                }
                System.out.println();

                // ASCII представление
                System.out.print("   ASCII: ");
                for (int i = 0; i < bytesRead; i++) {
                    char c = (char) buffer[i];
                    System.out.print(Character.isISOControl(c) || c > 127 ? '.' : c);
                }
                System.out.println();
            }

            System.out.println("=== ТЕСТИРОВАНИЕ СЕРИАЛИЗАЦИИ ===\n");

            try {
                // Часть 1: Создание сложной функции
                System.out.println("1. СОЗДАНИЕ ТАБУЛИРОВАННОЙ ФУНКЦИИ\n");

                // Создаем функцию ln(exp(x)) = x (должно быть тождество)
                Function exp1 = new Exp();
                Function ln1 = new Log(Math.E);

                // Создаем композицию: ln(exp(x)) = x
                Function composition = Functions.composition(ln1, exp1);

                // Табулируем на отрезке [0, 10] с 11 точками
                TabulatedFunction tabulatedFunc = TabulatedFunctions.tabulate(composition, 0, 10, 11);

                System.out.println("   Создана функция: ln(exp(x)) = x");
                System.out.println("   Количество точек: " + tabulatedFunc.getPointsCount());
                System.out.println("   Область определения: [" +
                        tabulatedFunc.getLeftDomainBorder() + ", " +
                        tabulatedFunc.getRightDomainBorder() + "]");

                System.out.println("\n   Значения исходной функции:");
                System.out.println("   x\t\tf(x)");
                System.out.println("   -------------------");
                for (double x = 0; x <= 10; x += 1.0) {
                    System.out.printf("   %.1f\t\t%.6f%n", x, tabulatedFunc.getFunctionValue(x));
                }

                // Часть 2: Сериализация с Serializable
                System.out.println("\n\n2. СЕРИАЛИЗАЦИЯ С ИСПОЛЬЗОВАНИЕМ Serializable\n");

                Path serializableFile = Paths.get("function_serializable.dat");

                // Сериализация
                try (ObjectOutputStream out = new ObjectOutputStream(
                        Files.newOutputStream(serializableFile))) {
                    out.writeObject(tabulatedFunc);
                    System.out.println("   Функция сериализована в файл: " +
                            serializableFile.toAbsolutePath());
                }

                // Десериализация
                TabulatedFunction deserializedFunc;
                try (ObjectInputStream in = new ObjectInputStream(
                        Files.newInputStream(serializableFile))) {
                    deserializedFunc = (TabulatedFunction) in.readObject();
                    System.out.println("   Функция десериализована из файла");
                }

                // Сравнение
                System.out.println("\n   Сравнение исходной и десериализованной функции:");
                System.out.println("   x\t\tИсходная\tДесериализ.\tРазность");
                System.out.println("   -----------------------------------------------------");

                double maxDiffSerializable = 0;
                for (double x = 0; x <= 10; x += 1.0) {
                    double original = tabulatedFunc.getFunctionValue(x);
                    double restored = deserializedFunc.getFunctionValue(x);
                    double diff = Math.abs(original - restored);
                    if (diff > maxDiffSerializable) maxDiffSerializable = diff;

                    System.out.printf("   %.1f\t\t%.6f\t%.6f\t%.10f%n",
                            x, original, restored, diff);
                }
                System.out.printf("\n   Максимальная разность: %.10f%n", maxDiffSerializable);

                // Информация о файле
                System.out.println("\n   Информация о файле Serializable:");
                System.out.println("   Размер файла: " + Files.size(serializableFile) + " байт");

                // Часть 3: Создание и сериализация с Externalizable
                System.out.println("\n\n3. СЕРИАЛИЗАЦИЯ С ИСПОЛЬЗОВАНИЕМ Externalizable\n");

                // Создаем функцию с Externalizable
                TabulatedFunction externalizableFunc =
                        new ArrayTabulatedFunctionExternalizable(0, 10, 11);

                // Заполняем значениями
                for (int i = 0; i < externalizableFunc.getPointsCount(); i++) {
                    double x = externalizableFunc.getPointX(i);
                    externalizableFunc.setPointY(i, composition.getFunctionValue(x));
                }

                Path externalizableFile = Paths.get("function_externalizable.dat");

                // Сериализация
                try (ObjectOutputStream out = new ObjectOutputStream(
                        Files.newOutputStream(externalizableFile))) {
                    out.writeObject(externalizableFunc);
                    System.out.println("   Функция (Externalizable) сериализована в файл: " +
                            externalizableFile.toAbsolutePath());
                }

                // Десериализация
                TabulatedFunction deserializedExternalizable;
                try (ObjectInputStream in = new ObjectInputStream(
                        Files.newInputStream(externalizableFile))) {
                    deserializedExternalizable = (TabulatedFunction) in.readObject();
                    System.out.println("   Функция десериализована из файла");
                }

                // Сравнение
                System.out.println("\n   Сравнение исходной и десериализованной функции:");
                System.out.println("   x\t\tИсходная\tДесериализ.\tРазность");
                System.out.println("   -----------------------------------------------------");

                double maxDiffExternalizable = 0;
                for (double x = 0; x <= 10; x += 1.0) {
                    double original = externalizableFunc.getFunctionValue(x);
                    double restored = deserializedExternalizable.getFunctionValue(x);
                    double diff = Math.abs(original - restored);
                    if (diff > maxDiffExternalizable) maxDiffExternalizable = diff;

                    System.out.printf("   %.1f\t\t%.6f\t%.6f\t%.10f%n",
                            x, original, restored, diff);
                }
                System.out.printf("\n   Максимальная разность: %.10f%n", maxDiffExternalizable);

                // Информация о файле
                System.out.println("\n   Информация о файле Externalizable:");
                System.out.println("   Размер файла: " + Files.size(externalizableFile) + " байт");


                // Часть 4: Очистка
                System.out.println("\n\n7. ОЧИСТКА ФАЙЛОВ\n");

                Files.deleteIfExists(serializableFile);
                Files.deleteIfExists(externalizableFile);
                System.out.println("   Временные файлы удалены");

                System.out.println("\n=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===");

            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
        }
    }

        // Дополнительный тест для проверки версионности
    private static void testVersioning () {
            System.out.println("\n\nДополнительно: проверка версионности");

            try {
                // Создаем функцию
                TabulatedFunction func = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, 5);

                // Сохраняем
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
                    out.writeObject(func);
                }

                byte[] data = baos.toByteArray();
                System.out.println("Размер сериализованных данных: " + data.length + " байт");

                // Восстанавливаем
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                try (ObjectInputStream in = new ObjectInputStream(bais)) {
                    TabulatedFunction restored = (TabulatedFunction) in.readObject();
                    System.out.println("Функция успешно восстановлена");
                    System.out.println("Класс: " + restored.getClass().getName());
                }

            } catch (Exception e) {
                System.out.println("Ошибка версионности: " + e.getMessage());
            }
        }

}


