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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @param Values Строки со значениями полей
     * @return возвращает сгенерированные данные в формате TOLF.
     */
    public static String GenData(String category, String[] Fields, String[] Values) throws IOException {
        if (category != null && !category.equals("") && Fields != null && Values != null) {
            if (checkBannedSymbolsInCategory(category)) {
                String OUT;
                OUT = "<#!--" + "\n"
                        + "\t" + "[" + category + "]" + "\n";

                if (Fields.length == Values.length) {
                    if (Fields.length > 0 && Values.length > 0) {
                        for (int i = 0; i < Fields.length; i++) {
                            if (checkBannedSymbolsInNameFieldsAndValues(Fields[i]) && checkBannedSymbolsInNameFieldsAndValues(Values[i])) {
                                OUT += "\t\t- " + Fields[i] + ": " + Values[i] + ";" + "\n";
                            } else {
                                throw new IOException("В названии параметра или в значении параметра обнаружены запрещенные символы.");
                            }
                        }
                    } else {

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
     * @param Values Строки со значениями полей
     * @param filepath Адрес файла, в который надо записать TOLF данные. Если файла нет, он будет создан.
     */
    public static void GenDataToFile(String category, String[] Fields, String[] Values, String filepath) throws IOException {
        if (!checkFileExists(filepath)) {
            CreateFile(filepath);
        }

        writeToFile(filepath, GenData(category, Fields, Values));
    }

    /**
     * Генерация данных формата TOLF(КС Толчок).
     * Чтобы добавить поле к существующим данным TOLF, используйте метод AddFieldToData.
     * @param category Категория
     * @param data HashMap с параметрами и значениями
     * @return возвращает сгенерированные данные в формате TOLF.
     */
    public static String GenData(String category, HashMap data) throws IOException {
        if (category != null && !category.equals("") && data != null) {
            if (checkBannedSymbolsInCategory(category)) {
                String OUT;
                OUT = "<#!--" + "\n"
                        + "\t" + "[" + category + "]" + "\n";

                Set set = data.entrySet();
                Iterator i = set.iterator();

                while(i.hasNext()) {
                    Map.Entry me = (Map.Entry)i.next();
                    if (checkBannedSymbolsInNameFieldsAndValues((String) me.getKey()) && checkBannedSymbolsInNameFieldsAndValues((String) me.getValue())) {
                        OUT += "\t\t- " + me.getKey() + ": " + me.getValue() + ";" + "\n";
                    } else {
                        throw new IOException("В названии параметра или в значении параметра обнаружены запрещенные символы.");
                    }
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
     * @param data HashMap с параметрами и значениями
     * @param filepath Адрес файла, в который надо записать TOLF данные. Если файла нет, он будет создан.
     */
    public static void GenDataToFile(String category, HashMap data, String filepath) throws IOException {
        if (!checkFileExists(filepath)) {
            CreateFile(filepath);
        }

        writeToFile(filepath, GenData(category, data));
    }

    /**
     * Добавление параметра в данные TOLF.
     * @param data Данные, которые необходимо обработать
     * @param category Категория, в которой нужно добавить параметр
     * @param nameNewField Имя поля
     * @param Value Значение поля(параметр).
     * @return возвращает сгенерированные данные в формате TOLF, где есть новая строка.
     */
    public static String AddFieldToData(String data, String category, String nameNewField, String Value) throws IOException {
        String CatOp, CatCl, CacheOne, CacheTwo, CacheThree;

        if (checkTOLFMarkers(data)) {
            if (checkBannedSymbolsInCategory(category)) {
                if (checkBannedSymbolsInNameFieldsAndValues(nameNewField) && checkBannedSymbolsInNameFieldsAndValues(Value)) {
                    if (data.contains("[" + category + "]")) {
                        if (data.contains("[/" + category + "]")) {
                            CatOp = "[" + category + "]";
                            CatCl = "[/" + category + "]";
                            CacheOne = data.substring(data.indexOf("<#!--"), data.indexOf(CatOp) + CatOp.length());
                            data = data.replace(CacheOne, "\r");
                            CacheTwo = data.substring(data.indexOf("\t" + CatCl), data.indexOf("--!#>") + ("--!#>").length());
                            data = data.replace(CacheTwo, "\r");

                            if (!data.contains("- " + nameNewField + ": ")) {
                                System.out.println(data.length());
                                if (data.length() > 3) {
                                    data = data.substring(2, data.length() - 2) + "\t\t- " + nameNewField + ": " + Value + ";" + "\n";
                                } else {
                                    data = data.substring(2, data.length()) + "\t\t- " + nameNewField + ": " + Value + ";" + "\n";
                                }
                                return CacheOne + data + CacheTwo;
                            } else {
                                throw new IOException("Кажется, этот параметр уже существует.");
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
        } else {
            throw new IOException("Похоже, что это не TOLF данные, а следовательно обработать их не получится.");
        }
    }

    /**
     * Добавление параметра в данные TOLF внутри файла
     * @param filepath Адрес файла, в котором нужно добавить параметр.
     * @param category Категория, в которой нужно добавить параметр
     * @param nameNewField Имя поля
     * @param Value Значение поля(параметр).
     */
    public static void AddFieldToFile(String filepath, String category, String nameNewField, String Value) throws IOException {
        if (!checkFileExists(filepath)) {
            throw new IOException("Файл не существует.");
        }

        String data = ReadFile(filepath);

        writeToFile(filepath, AddFieldToData(data, category, nameNewField, Value));
    }

    /**
     * Добавление категории с содержимым в данные TOLF.
     * @param data Данные, которые необходимо обработать
     * @param category Категория, которую нужно добавить
     * @param Fields Строки с именами полей
     * @param Values Строки со значениями полей
     * @return возвращает сгенерированные данные в формате TOLF, где есть новая категория с содержимым.
     */
    public static String AddCategoryToData(String data, String category, String[] Fields, String[] Values) throws IOException {
        if (checkTOLFMarkers(data)) {
            if (checkBannedSymbolsInCategory(category)) {
                if (!(data.contains("[" + category + "]") || data.contains("[/" + category + "]"))) {
                    data = data.replace("--!#>", "");
                    data += "\t" + "[" + category + "]" + "\n";

                    if (Fields.length == Values.length) {
                        if (Fields.length > 0 && Values.length > 0) {
                            for (int i = 0; i < Fields.length; i++) {
                                if (checkBannedSymbolsInNameFieldsAndValues(Fields[i]) && checkBannedSymbolsInNameFieldsAndValues(Values[i])) {
                                    data += "\t\t- " + Fields[i] + ": " + Values[i] + ";" + "\n";
                                } else {
                                    throw new IOException("В названии параметра или в значении параметра обнаружены запрещенные символы.");
                                }
                            }
                        } else {
                            throw new IOException("Размер массивов должен быть больше 0.");
                        }
                    } else {
                        throw new IOException("Массивы, которые были отправлены методу TOLF_Handler.AddCategoryToData(или File), должны иметь одинаковое количество элементов.");
                    }

                    data += "\t" + "[/" + category + "]" + "\n"
                            + "--!#>";

                    return data;
                } else {
                    throw new IOException("Категория \"" + category + "\" уже существует.");
                }
            } else {
                throw new IOException("В названии категории обнаружены запрещенные символы.");
            }
        } else {
            throw new IOException("Похоже, что это не TOLF данные, а следовательно обработать их не получится.");
        }
    }

    /**
     * Добавление категории с содержимым в данные TOLF внутри файла
     * @param filepath Адрес файла, в котором нужно обновить TOLF данные.
     * @param category Категория, которую нужно добавить
     * @param Fields Строки с именами полей
     * @param Values Строки со значениями полей
     */
    public static void AddCategoryToFile(String filepath, String category, String[] Fields, String[] Values) throws IOException {
        if (!checkFileExists(filepath)) {
            throw new IOException("Файл не существует.");
        }

        String data = ReadFile(filepath);

        writeToFile(filepath, AddCategoryToData(data, category, Fields, Values));
    }

    /**
     * Добавление категории с содержимым в данные TOLF.
     * @param data Данные, которые необходимо обработать
     * @param category Категория, которую нужно добавить
     * @param hashMap HashMap с параметрами и значениями
     * @return возвращает сгенерированные данные в формате TOLF, где есть новая категория с содержимым.
     */
    public static String AddCategoryToData(String data, String category, HashMap hashMap) throws IOException {
        if (checkTOLFMarkers(data)) {
            if (checkBannedSymbolsInCategory(category)) {
                if (!(data.contains("[" + category + "]") || data.contains("[/" + category + "]"))) {
                    data = data.replace("--!#>", "");
                    data += "\t" + "[" + category + "]" + "\n";

                    Set set = hashMap.entrySet();
                    Iterator i = set.iterator();

                    while(i.hasNext()) {
                        Map.Entry me = (Map.Entry)i.next();
                        if (checkBannedSymbolsInNameFieldsAndValues((String) me.getKey()) && checkBannedSymbolsInNameFieldsAndValues((String) me.getValue())) {
                            data += "\t\t- " + me.getKey() + ": " + me.getValue() + ";" + "\n";
                        } else {
                            throw new IOException("В названии параметра или в значении параметра обнаружены запрещенные символы.");
                        }
                    }

                    data += "\t" + "[/" + category + "]" + "\n"
                            + "--!#>";

                    return data;
                } else {
                    throw new IOException("Категория \"" + category + "\" уже существует.");
                }
            } else {
                throw new IOException("В названии категории обнаружены запрещенные символы.");
            }
        } else {
            throw new IOException("Похоже, что это не TOLF данные, а следовательно обработать их не получится.");
        }
    }

    /**
     * Добавление категории с содержимым в данные TOLF внутри файла
     * @param filepath Адрес файла, в котором нужно обновить TOLF данные.
     * @param category Категория, которую нужно добавить
     * @param hashMap HashMap с параметрами и значениями
     */
    public static void AddCategoryToFile(String filepath, String category, HashMap hashMap) throws IOException {
        if (!checkFileExists(filepath)) {
            throw new IOException("Файл не существует.");
        }

        String data = ReadFile(filepath);

        writeToFile(filepath, AddCategoryToData(data, category, hashMap));
    }

    /**
     * Удаление категории с содержимым в данных TOLF. (ЭКСПЕРИМЕНТАЛЬНО)
     * @param data Данные, которые необходимо обработать
     * @param category Категория, которую нужно удалить
     * @return возвращает сгенерированные данные в формате TOLF, где удалена категория.
     */
    public static String RemoveCategoryFromData(String data, String category) throws IOException {
        String CatOp, CatCl;

        if (checkTOLFMarkers(data)) {
            if (checkBannedSymbolsInCategory(category)) {
                if (data.contains("[" + category + "]")) {
                    if (data.contains("[/" + category + "]")) {
                        int countCat = 0;
                        for (char element : data.toCharArray()){
                            if (element == '[') countCat++;
                        }

                        if (countCat > 2) {
                            CatOp = "[" + category + "]";
                            CatCl = "[/" + category + "]";
                            //data = data.replace(data.substring(data.indexOf(CatOp), data.indexOf(CatCl) + CatCl.length()), "\r");

                            data = data.substring(0, data.indexOf(CatOp) - 5) +
                                    data.substring(data.indexOf(CatCl) + CatCl.length(), data.length());

                            return data;
                        } else {
                            throw new IOException("Кажется, в TOLF данных только одна категория. Удаление невозможно.");
                        }
                    } else {
                        throw new IOException("Не удалось найти закрывающий тег категории.");
                    }
                } else {
                    throw new IOException("Не удалось найти открывающий тег категории.");
                }
            } else {
                throw new IOException("В названии категории обнаружены запрещенные символы.");
            }
        } else {
            throw new IOException("Похоже, что это не TOLF данные, а следовательно обработать их не получится.");
        }
    }

    /**
     * Удаление категории с содержимым в данных TOLF внутри файла (ЭКСПЕРИМЕНТАЛЬНО)
     * @param filepath Адрес файла, в котором нужно обновить TOLF данные.
     * @param category Категория, которую нужно удалить.
     */
    public static void RemoveCategoryFromFile(String filepath, String category) throws IOException {
        if (!checkFileExists(filepath)) {
            throw new IOException("Файл не существует.");
        }

        String data = ReadFile(filepath);

        writeToFile(filepath, RemoveCategoryFromData(data, category));
    }

    /**
     * Удаление поля из данных TOLF. (ЭКСПЕРИМЕНТАЛЬНО)
     * @param data Данные, которые необходимо обработать
     * @param category Категория, в которой нужно удалить поле
     * @param field Поле, которое нужно удалить
     * @return возвращает сгенерированные данные в формате TOLF, где удалено поле.
     */
    public static String RemoveFieldFromData(String data, String category, String field) throws IOException {
        String CatOp, CatCl, CacheOne, CacheTwo;
        int lenghtOfField = field.length();
        if (checkTOLFMarkers(data)) {
            if (checkBannedSymbolsInCategory(category)) {
                if (data.contains("[" + category + "]")) {
                    if (data.contains("[/" + category + "]")) {
                        CatOp = "[" + category + "]";
                        CatCl = "[/" + category + "]";
                        CacheOne = data.substring(data.indexOf("<#!--"), data.indexOf(CatOp) + CatOp.length());
                        data = data.replace(CacheOne, "\r");
                        CacheTwo = data.substring(data.indexOf("\t" + CatCl), data.indexOf("--!#>") + ("--!#>").length());
                        data = data.replace(CacheTwo, "\r");
                        if (data.contains(field)) {
                            final int Field = data.indexOf("- " + field + ": ") + 4 + lenghtOfField;
                            String ValueOfFIELD = data.substring(Field, data.indexOf(";", Field));

                            // Осторожно. Тут костыль.
                            data = data.substring(1, data.indexOf("\n\t\t- " + field + ": " + ValueOfFIELD + ";") - 2) +
                                    data.substring(data.indexOf("\n\t\t- " + field + ": " + ValueOfFIELD + ";") + ("\n\t\t- " + field + ": " + ValueOfFIELD + ";").length() - 1, data.indexOf("\n\t\t- " + field + ": " + ValueOfFIELD + ";") + ("\n\t\t- " + field + ": " + ValueOfFIELD + ";").length()) +
                                    data.substring(data.indexOf("\n\t\t- " + field + ": " + ValueOfFIELD + ";") + ("\n\t\t- " + field + ": " + ValueOfFIELD + ";").length(), data.length() - 2);

                            return CacheOne + data + CacheTwo;
                        } else {
                            throw new IOException("Не удалось найти параметр для удаления.");
                        }
                    } else {
                        throw new IOException("Не удалось найти закрывающий тег категории.");
                    }
                } else {
                    throw new IOException("Не удалось найти открывающий тег категории.");
                }
            } else {
                throw new IOException("В названии категории обнаружены запрещенные символы.");
            }
        } else {
            throw new IOException("Похоже, что это не TOLF данные, а следовательно обработать их не получится.");
        }
    }

    /**
     * Удаление категории с содержимым из данных TOLF внутри файла (ЭКСПЕРИМЕНТАЛЬНО)
     * @param filepath Адрес файла, в котором нужно обновить TOLF данные.
     * @param category Категория, в которой нужно удалить поле
     * @param field Поле, которое нужно удалить
     */
    public static void RemoveFieldFromFile(String filepath, String category, String field) throws IOException {
        if (!checkFileExists(filepath)) {
            throw new IOException("Файл не существует.");
        }

        String data = ReadFile(filepath);

        writeToFile(filepath, RemoveFieldFromData(data, category, field));
    }

    /**
     * Изменение параметра в данных TOLF.
     * @param data Данные TOLF, которые нужно обработать.
     * @param category Категория, в которой нужно обновить параметр.
     * @param field Название параметра.
     * @param newValue Новое значение параметра.
     * @return возвращает обработанные данные.
     */
    public static String EditValueInData(String data, String category, String field, String newValue) throws IOException {
        String CatOp, CatCl, CacheOne, CacheTwo;

        int lenghtOfField = field.length();

        if (checkTOLFMarkers(data)) {
            if (checkBannedSymbolsInCategory(category)) {
                if (checkBannedSymbolsInNameFieldsAndValues(field) && checkBannedSymbolsInNameFieldsAndValues(newValue)) {
                    if (data.contains("[" + category + "]")) {
                        if (data.contains("[/" + category + "]")) {
                            if (data.contains("- " + field + ":")) {
                                CatOp = "[" + category + "]";
                                CatCl = "[/" + category + "]";
                                CacheOne = data.substring(data.indexOf("<#!--"), data.indexOf(CatOp) + CatOp.length());
                                data = data.replace(CacheOne, "\r");
                                CacheTwo = data.substring(data.indexOf("\t" + CatCl), data.indexOf("--!#>") + ("--!#>").length());
                                data = data.replace(CacheTwo, "\r");

                                final int Field = data.indexOf("- " + field + ": ") + 4 + lenghtOfField;
                                String ValueOfFIELD = data.substring(Field, data.indexOf(";", Field));

                                data = data.replace("- " + field + ": " + ValueOfFIELD + ";", "- " + field + ": " + newValue + ";");
                                String DataFromFilter = data.substring(2, data.length() - 2); // Костыль

                                return CacheOne + DataFromFilter + CacheTwo;
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
        } else {
            throw new IOException("Похоже, что это не TOLF данные, а следовательно обработать их не получится.");
        }
    }

    /**
     * Изменение параметра в данных TOLF, которые потом будут записаны в файл.
     * @param filepath Адрес файла, в котором нужно обновить TOLF данные.
     * @param category Категория, в которой нужно изменить параметр
     * @param nameField Имя поля
     * @param Value Значение поля(параметр).
     */
    public static void EditValueInFile(String filepath, String category, String nameField, String Value) throws IOException {
        if (!checkFileExists(filepath)) {
            throw new IOException("Файл не существует.");
        }

        String data = ReadFile(filepath);

        writeToFile(filepath, EditValueInData(data, category, nameField, Value));
    }

    /**
     * Чтение параметра из данных TOLF.
     * @param data Данные TOLF, в которых нужно прочитать значение поля.
     * @param category Категория, в которой нужно прочитать параметр.
     * @param field Название параметра.
     * @return возвращает данные из параметра.
     */
    public static String ReadValueFromData(String data, String category, String field) throws IOException {
        String cache = data;
        String CatOp, CatCl, CacheOne, CacheTwo;

        int lenghtOfField = field.length();

        if (checkTOLFMarkers(data)) {
            if (checkBannedSymbolsInCategory(category)) {
                if (checkBannedSymbolsInNameFieldsAndValues(field)) {
                    if (cache.contains("[" + category + "]")) {
                        if (cache.contains("[/" + category + "]")) {
                            CatOp = "[" + category + "]";
                            CatCl = "[/" + category + "]";
                            CacheOne = data.substring(data.indexOf("<#!--"), data.indexOf(CatOp) + CatOp.length());
                            data = data.replace(CacheOne, "\r");
                            CacheTwo = data.substring(data.indexOf("\t" + CatCl), data.indexOf("--!#>") + ("--!#>").length());
                            data = data.replace(CacheTwo, "\r");

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
        } else {
            throw new IOException("Похоже, что это не TOLF данные, а следовательно обработать их не получится.");
        }
    }

    /**
     * Чтение параметра из данных TOLF, которые в свою очередь получены из файла.
     * @param filepath Адрес файла, из которого нужно прочитать TOLF данные и узнать значение поля в них.
     * @param category Категория, в которой нужно изменить параметр
     * @param field Имя поля. значение которого нужно получить
     * @return возвращает данные из параметра.
     */
    public static String ReadValueFromFile(String filepath, String category, String field) throws IOException {
        return ReadValueFromData(ReadFile(filepath), category, field);
    }

    /**
     * Парсинг параметров из TOLF данных и конкретной категории.
     * @param data TOLF данные, которые нужно обработать.
     * @param category Категория, данные из которой нужно извлечь
     * @return возвращает HashMap с параметрами.
     */
    public static HashMap getValuesFromData(String data, String category) throws IOException {
        HashMap TOLF_CATEGORY_DATA = new HashMap();
        String cache = data;
        String CatOp, CatCl, CacheOne, CacheTwo;


        if (checkTOLFMarkers(data)) {
            if (checkBannedSymbolsInCategory(category)) {
                if (cache.contains("[" + category + "]")) {
                    if (cache.contains("[/" + category + "]")) {
                        CatOp = "[" + category + "]";
                        CatCl = "[/" + category + "]";
                        CacheOne = data.substring(data.indexOf("<#!--"), data.indexOf(CatOp) + CatOp.length());
                        data = data.replace(CacheOne, "\r");
                        CacheTwo = data.substring(data.indexOf("\t" + CatCl), data.indexOf("--!#>") + ("--!#>").length());
                        data = data.replace(CacheTwo, "\r");

                        Matcher m = Pattern.compile("(.)\\1+").matcher(data);
                        while (m.find()) {
                            String sub = m.group();
                            if (sub.length() > 1 && (sub.contains(";") || sub.contains(":"))) {
                                throw new IOException("TOLF данные повреждены. Обнаружены лишние \":\" или \";\".");
                            }
                        }

                        int countValues = 0;
                        for (char element : data.toCharArray()) {
                            if (element == ';') {
                                countValues++;
                            }
                        }

                        for (int i = 0; i < countValues; i++) {
                            String FIELD;
                            String VALUE;

                            FIELD = data.substring(data.indexOf("- ") + 2, data.indexOf(":"));
                            VALUE = data.substring(data.indexOf("- " + FIELD) + ("- " + FIELD).length() + 2, data.indexOf(";"));

                            data = data.replace("- " + FIELD + ": " + VALUE + ";", "");

                            TOLF_CATEGORY_DATA.put(FIELD, VALUE);
                        }

                        return TOLF_CATEGORY_DATA;
                    } else {
                        throw new IOException("Не удалось найти закрывающий тег категории.");
                    }
                } else {
                    throw new IOException("Не удалось найти открывающий тег категории.");
                }
            } else {
                throw new IOException("В названии категории обнаружены запрещенные символы.");
            }
        } else {
            throw new IOException("Похоже, что это не TOLF данные, а следовательно обработать их не получится.");
        }
    }

    /**
     * Парсинг параметров из TOLF данных в файле и конкретной категории.
     * @param filepath Путь до файла с TOLF данными
     * @param category Категория, данные из которой нужно извлечь
     * @return возвращает HashMap с параметрами.
     */
    public static HashMap getValuesFromFile(String filepath, String category) throws IOException {
        return getValuesFromData(ReadFile(filepath), category);
    }

    private static boolean checkBannedSymbolsInCategory(String category) {
        return category.matches("[a-zA-Zа-яА-Я0-9_-]+");
    }

    private static boolean checkBannedSymbolsInNameFieldsAndValues(String data) {
        return !(data.contains(":") || data.contains(";") || data.contains("<#!--") || data.contains("--!#>"));
    }

    private static boolean checkTOLFMarkers(String data) {
        return data.contains(":") && data.contains(";") && data.contains("<#!--") && data.contains("--!#>") && data.contains("-");
    }

    // Решил сунуть эти методы сюда для тех, кто не хочет пользоваться КС Пиксель, но хочет пользоваться КС Толчок. //
    private static void CreateFile(String filename) throws IOException {
        File TargetFile = new File(filename);
        TargetFile.createNewFile();
    }

    private static boolean checkFileExists(String filename) {
        File TargetFile = new File(filename);
        return TargetFile.exists() && TargetFile.isFile();
    }

    private static String ReadFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader(filename));
        String line;
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

    private static void writeToFile(String filename, String data) throws IOException {
        File TargetFile = new File(filename);

        if (!(checkFileExists(filename) && TargetFile.isFile())) {
            CreateFile(filename);
        }

        try (FileWriter fr = new FileWriter(TargetFile)) {
            fr.write(data);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
