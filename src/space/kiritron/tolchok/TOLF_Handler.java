/*
 * Copyright 2021 Kiritron's Space
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package space.kiritron.tolchok;

import java.io.*;

/**
 * Класс с методами для управления конфигами стандарта КС Толчок, далее TOLF.
 * @author Киритрон Стэйблкор
 */

public class TOLF_Handler {
    /**
     * Генерация данных формата TOLF(КС Толчок).
     * Чтобы добавить поле к существующим данным TOLF, используйте метод AddFieldToData.
     * @param category Категория
     * @param Fields Строки с именами полей
     * @param Params Строки со значениями полей
     * @return возвращает сгенерированные данные в формате TOLF.
     */
    public static String GenData(String category, String[] Fields, String[] Params) throws IOException {
        if (category != null && !category.equals("") && Fields != null && Params != null) {
            if (checkBannedSymbolsInCategory(category)) {
                String OUT;
                OUT = "<#!--" + "\n"
                        + "\t" + "[" + category + "]" + "\n";

                if (Fields.length == Params.length) {
                    for (int i = 0; i < Fields.length; i++) {
                        if (checkBannedSymbolsInNameFieldsAndParams(Fields[i]) && checkBannedSymbolsInNameFieldsAndParams(Params[i])) {
                            OUT += "\t\t- " + Fields[i] + ": " + Params[i] + ";" + "\n";
                        } else {
                            throw new IOException("В названии параметра или в значении параметра обнаружены запрещенные символы.");
                        }
                    }
                } else {
                    throw new IOException("Массивы, которые были отправлены методу TOLF_Handler.GenData, должны иметь одинаковое количество элементов.");
                }

                OUT += "\t" + "[/" + category + "]" + "\n"
                        + "--!#>";

                return OUT;
            } else {
                throw new IOException("В названии категории обнаружены запрещенные символы.");
            }
        } else {
            throw new IOException("Все входящие параметры должны иметь значение. Null и пустые строки недопустимы.");
        }
    }

    /**
     * Генерация данных формата TOLF(КС Толчок) и последующая запись в файл.
     * Генерируется только одна категория и одно поле. Чтобы добавить поле к существующим данным TOLF, используйте метод AddFieldToData.
     * @param category Категория
     * @param Fields Строки с именами полей
     * @param Params Строки со значениями полей
     * @param filepath Адрес файла, в который надо записать TOLF данные. Если файла нет, он будет создан.
     */
    public static void GenData_WriteToFile(String category, String[] Fields, String[] Params, String filepath) throws IOException {
        if (!SearchFile(filepath)) {
            CreateFile(filepath);
        }

        writeToFile(filepath, GenData(category, Fields, Params));
    }

    /**
     * Добавление параметра в данные TOLF.
     * @param data Данные, которые необходимо обработать
     * @param category Категория, в которой нужно добавить параметр
     * @param nameField Имя поля
     * @param Param Значение поля(параметр).
     * @return возвращает сгенерированные данные в формате TOLF, где есть новая строка.
     */
    public static String AddFieldToData(String data, String category, String nameField, String Param) throws IOException {
        String CatOp, CatCl, CacheOne, CacheTwo, CacheThree;

        if (checkBannedSymbolsInCategory(category)) {
            if (checkBannedSymbolsInNameFieldsAndParams(nameField) && checkBannedSymbolsInNameFieldsAndParams(Param)) {
                if (data.contains("[" + category + "]")) {
                    if (data.contains("[/" + category + "]")) {
                        CatOp = "[" + category + "]";
                        CatCl = "[/" + category + "]";
                        CacheOne = data.format("\\%s[^)]+\\%s", "<#!--", CatOp);
                        CacheTwo = data.format("\\%s[^)]+\\%s", CatCl, "--!#>");

                        CacheOne = CacheOne.substring(9);
                        CacheTwo = CacheTwo.substring(1, CacheTwo.length() - (CacheTwo.length() - 1 - CatCl.length()));

                        data = data.replaceAll(String.format("\\%s[^)]+\\%s", "<#!--", CatOp), "");
                        data = data.replaceAll(String.format("\\%s[^)]+\\%s", CatCl, "--!#>"), "");
                        CacheThree = data;
                        data = "\t\t- " + nameField + ": " + Param + ";" + "\n";

                        return "<#!--" + "\n"  + "\t" + CacheOne + CacheThree + data + CacheTwo + "\n"  + "--!#>";
                    } else {
                        throw new IOException("Не удалось найти закрывающий тег категории.");
                    }
                } else {
                    throw new IOException("Не удалось найти открывающий тег категории.");
                }
            } else {
                throw new IOException("В названии параметра или в значении параметра обнаружены запрещенные символы.");
            }
        } else {
            throw new IOException("В названии категории обнаружены запрещенные символы.");
        }
    }

    /**
     * Добавление параметра в данные TOLF, которые потом будут записаны в файл.
     * @param category Категория, в которой нужно добавить параметр
     * @param nameField Имя поля
     * @param Param Значение поля(параметр).
     * @param filepath Адрес файла, в который надо записать TOLF данные. Если файла нет, он будет создан.
     */
    public static void AddFieldToData_WriteToFile(String category, String nameField, String Param, String filepath) throws IOException {
        if (!SearchFile(filepath)) {
            CreateFile(filepath);
        }

        String data = ReadFile(filepath);

        writeToFile(filepath, AddFieldToData(data, category, nameField, Param));
    }

    /**
     * Изменение параметра в данных TOLF.
     * @param data Данные TOLF, которые нужно обработать.
     * @param category Категория, в которой нужно обновить параметр.
     * @param field Название параметра.
     * @param newParam Новое значение параметра.
     * @return возвращает обработанные данные.
     */
    public static String EditParamInData(String data, String category, String field, String newParam) throws IOException {
        String CatOp, CatCl, CacheOne, CacheTwo;

        int lenghtOfField = field.length();

        if (checkBannedSymbolsInCategory(category)) {
            if (checkBannedSymbolsInNameFieldsAndParams(field) && checkBannedSymbolsInNameFieldsAndParams(newParam)) {
                if (data.contains("[" + category + "]") == true) {
                    if (data.contains("[/" + category + "]") == true) {
                        if (data.contains("- " + field + ":")) {
                            CatOp = "[" + category + "]";
                            CatCl = "[/" + category + "]";
                            CacheOne = data.format("\\%s[^)]+\\%s", "<#!--", CatOp);
                            CacheTwo = data.format("\\%s[^)]+\\%s", CatCl, "--!#>");

                            CacheOne = CacheOne.substring(9);
                            CacheTwo = CacheTwo.substring(1, CacheTwo.length() - (CacheTwo.length() - 1 - CatCl.length()));

                            data = data.replaceAll(String.format("\\%s[^)]+\\%s", "<#!--", CatOp), "");
                            data = data.replaceAll(String.format("\\%s[^)]+\\%s", CatCl, "--!#>"), "");
                            final int Field = data.indexOf("- " + field + ": ") + 4 + lenghtOfField;
                            String ParamOfFIELD = data.substring(Field, data.indexOf(";", Field));

                            data = data.replaceAll("- " + field + ": " + ParamOfFIELD + ";", "- " + field + ": " + newParam + ";");
                            final String DataFromFilter = data.substring(0, data.length() - 2); // Костыль

                            return "<#!--" + "\n"  + "\t" + CacheOne + DataFromFilter + "\t" + CacheTwo + "\n"  + "--!#>";
                        } else {
                            throw new IOException("Не удалось найти поле, параметр которого необходимо изменить.");
                        }
                    } else {
                        throw new IOException("Не удалось найти закрывающий тег категории.");
                    }
                } else {
                    throw new IOException("Не удалось найти открывающий тег категории.");
                }
            } else {
                throw new IOException("В названии параметра или в значении параметра обнаружены запрещенные символы.");
            }
        } else {
            throw new IOException("В названии категории обнаружены запрещенные символы.");
        }
    }

    /**
     * Изменение параметра в данных TOLF, которые потом будут записаны в файл.
     * @param category Категория, в которой нужно изменить параметр
     * @param nameField Имя поля
     * @param Param Значение поля(параметр).
     * @param filepath Адрес файла, в который надо записать TOLF данные. Если файла нет, он будет создан.
     */
    public static void EditParamInData_WriteToFile(String category, String nameField, String Param, String filepath) throws IOException {
        if (!SearchFile(filepath)) {
            CreateFile(filepath);
        }

        String data = ReadFile(filepath);

        writeToFile(filepath, EditParamInData(data, category, nameField, Param));
    }

    /**
     * Чтение параметра из данных TOLF.
     * @param data Путь до файла.
     * @param category Категория, в которой нужно прочитать параметр.
     * @param field Название параметра.
     * @return возвращает данные из параметра.
     */
    public static String ReadParamFromData(String data, String category, String field) throws IOException {
        String cache = data;
        String CatOp, CatCl;

        int lenghtOfField = field.length();

        if (checkBannedSymbolsInCategory(category)) {
            if (checkBannedSymbolsInNameFieldsAndParams(field)) {
                if (cache.contains("[" + category + "]") == true) {
                    if (cache.contains("[/" + category + "]") == true) {
                        CatOp = "[" + category + "]";
                        CatCl = "[/" + category + "]";
                        cache = cache.replaceAll(String.format("\\%s[^)]+\\%s", "<#!--", CatOp), "");
                        cache = cache.replaceAll(String.format("\\%s[^)]+\\%s", CatCl, "--!#>"), "");

                        if (cache.contains(field)) {
                            int Field1 = cache.indexOf("- " + field + ": ") + 4 + lenghtOfField;
                            int Field2 = cache.indexOf(";", Field1);
                            return cache.substring(Field1, Field2);
                        } else {
                            throw new IOException("Не удалось найти поле, параметр которого необходимо изменить.");
                        }
                    } else {
                        throw new IOException("Не удалось найти закрывающий тег категории.");
                    }
                } else {
                    throw new IOException("Не удалось найти открывающий тег категории.");
                }
            } else {
                throw new IOException("В названии параметра или в значении параметра обнаружены запрещенные символы.");
            }
        } else {
            throw new IOException("В названии категории обнаружены запрещенные символы.");
        }
    }

    /**
     * Чтение параметра из данных TOLF, которые в свою очередь получены из файла.
     * @param filepath Адрес файла, в который надо записать TOLF данные.
     * @param category Категория, в которой нужно изменить параметр
     * @param field Имя поля. значение которого нужно получить
     * @return возвращает данные из параметра.
     */
    public static String ReadParamFromData_FromFile(String filepath, String category, String field) throws IOException {
        return ReadParamFromData(ReadFile(filepath), category, field);
    }

    private static boolean checkBannedSymbolsInCategory(String category) {
        return category.matches("[a-zA-Zа-яА-Я0-9_-]+");
    }

    private static boolean checkBannedSymbolsInNameFieldsAndParams(String data) {
        return !(data.contains("-") || data.contains(":") || data.contains(";") || data.contains("<#!--") || data.contains("--!#>"));
    }

    // Решил сунуть эти методы сюда для тех, кто не хочет пользоваться КС Пиксель, но хочет пользоваться КС Толчок. //
    private static void CreateFile(String filename) throws IOException {
        File TargetFile = new File(filename);
        TargetFile.createNewFile();
    }

    private static boolean SearchFile(String filename) {
        File TargetFile = new File(filename);
        return TargetFile.exists();
    }

    private static String ReadFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader(filename));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        if (stringBuilder.length() != 0) {
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }

        return stringBuilder.toString();
    }

    private static boolean writeToFile(String filename, String data) {
        boolean Status = false;
        File TargetFile = new File(filename);
        FileWriter fr = null;
        try {
            fr = new FileWriter(TargetFile);
            fr.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fr != null;
                fr.close();
                Status = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Status;
    }
}
