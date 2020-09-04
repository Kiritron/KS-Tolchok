package space.kiritron.tolchok;

/**
 * Класс с методами для управления конфигами стандарта КС Толчок, далее TOLF.
 * @author Киритрон Стэйблкор
 * @version 2.1
 */

public class TOLF_Handler {
    /**
     * Генерация данных формата TOLF(КС Толчок).
     * Генерируется только одна категория и одно поле. Чтобы добавить поле к существующим данным TOLF, используйте метод AddFieldToData.
     * @param category Категория
     * @param firstField Имя поля
     * @param firstParam Значение поля(параметр). Должно быть определено.
     * @return возвращает сгенерированные данные в формате TOLF. В случае неудачи, будет возвращены ошибки DATA_IS_NULL_ERROR или BLOCKED_CHARACTERS_DETECTED.
     */
    public static String GenData(String category, String firstField, String firstParam) {
        if (category != null && category != "" && firstField != null && firstField != "" && firstParam != null && firstParam != "") {
            if (checkBanSymbols(category)) {
                String Cache
                        = "</" + "\n"
                        + "\t" + "[" + category + "]" + "\n"
                        + "\t\t- " + firstField + ": " + firstParam + ";" + "\n"
                        + "\t" + "[/" + category + "]" + "\n"
                        + "/>";

                return Cache;
            } else {
                return "BLOCKED_CHARACTERS_DETECTED";
            }
        } else {
            return "DATA_IS_NULL_ERROR";
        }
    }

    /**
     * Добавление параметра в данные TOLF. (ЭКСПЕРИМЕНТАЛЬНО)
     * @param data Данные, которые необходимо обработать
     * @param category Категория, в которой нужно добавить параметр
     * @param nameField Имя поля
     * @param Param Значение поля(параметр). Должно быть определено
     * @return возвращает сгенерированные данные в формате TOLF, где есть новая строка. В случае неудачи - будет возвращена ошибка.
     */
    public static String AddFieldToData(String data, String category, String nameField, String Param) {
        String CatOp, CatCl, CacheOne, CacheTwo, CacheThree;

        if(checkBanSymbols(category)) {
            if (data.contains("[" + category + "]") == true) {
                if (data.contains("[/" + category + "]") == true) {
                    CatOp = "[" + category + "]";
                    CatCl = "[/" + category + "]";
                    CacheOne = data.format("\\%s[^)]+\\%s", "</", CatOp);
                    CacheTwo = data.format("\\%s[^)]+\\%s", CatCl, "/>");

                    CacheOne = CacheOne.substring(9);
                    CacheTwo = CacheTwo.substring(1, CacheTwo.length() - (CacheTwo.length() - 1 - CatCl.length()));

                    data = data.replaceAll(String.format("\\%s[^)]+\\%s", "</", CatOp), "");
                    data = data.replaceAll(String.format("\\%s[^)]+\\%s", CatCl, "/>"), "");
                    CacheThree = data;
                    data = "\t\t- " + nameField + ": " + Param + ";" + "\n";

                    return "</" + "\n"  + "\t" + CacheOne + CacheThree + data + CacheTwo + "\n"  + "/>";
                } else {
                    return "SEARCH_CATEGORY_FAILED";
                }
            } else {
                return "SEARCH_CATEGORY_FAILED";
            }
        } else {
            return "BLOCKED_CHARACTERS_DETECTED";
        }
    }

    /**
     * Изменение параметра в данных TOLF.
     * @param data Данные TOLF, которые нужно обработать.
     * @param category Категория, в которой нужно обновить параметр.
     * @param field Название параметра.
     * @param newParam Новое значение параметра.
     * @return возвращает обработанные данные, а в случае неудачи - ошибку.
     */
    public static String EditParamInData(String data, String category, String field, String newParam) {
        String CatOp, CatCl, CacheOne, CacheTwo;

        int lenghtOfField = field.length();

        if(checkBanSymbols(category)) {
            if (data.contains("[" + category + "]") == true) {
                if (data.contains("[/" + category + "]") == true) {
                    CatOp = "[" + category + "]";
                    CatCl = "[/" + category + "]";
                    CacheOne = data.format("\\%s[^)]+\\%s", "</", CatOp);
                    CacheTwo = data.format("\\%s[^)]+\\%s", CatCl, "/>");

                    CacheOne = CacheOne.substring(9);
                    CacheTwo = CacheTwo.substring(1, CacheTwo.length() - (CacheTwo.length() - 1 - CatCl.length()));

                    data = data.replaceAll(String.format("\\%s[^)]+\\%s", "</", CatOp), "");
                    data = data.replaceAll(String.format("\\%s[^)]+\\%s", CatCl, "/>"), "");
                    int Field1 = data.indexOf("- " + field + ": ") + 4 + lenghtOfField;
                    int Field2 = data.indexOf(";", Field1);
                    String ParamOfFIELD = data.substring(Field1, Field2);

                    data = data.replaceAll("- " + field + ": " + ParamOfFIELD + ";", "- " + field + ": " + newParam + ";");

                    return "</" + "\n"  + "\t" + CacheOne + data + CacheTwo + "\n"  + "/>";
                } else {
                    return "SEARCH_CATEGORY_FAILED";
                }
            } else {
                return "SEARCH_CATEGORY_FAILED";
            }
        } else {
            return "BLOCKED_CHARACTERS_DETECTED";
        }
    }

    /**
     * Чтение параметра из данных TOLF.
     * @param data Путь до файла.
     * @param category Категория, в которой нужно прочитать параметр.
     * @param field Название параметра.
     * @return возвращает данные из параметра, а в случае неудачи - ошибку.
     */
    public static String ReadParamFromData(String data, String category, String field) {
        String cache = data;
        String CatOp, CatCl;

        int lenghtOfField = field.length();

        if(checkBanSymbols(category)) {
            if (cache.contains("[" + category + "]") == true) {
                if (cache.contains("[/" + category + "]") == true) {
                    CatOp = "[" + category + "]";
                    CatCl = "[/" + category + "]";
                    cache = cache.replaceAll(String.format("\\%s[^)]+\\%s", "</", CatOp), "");
                    cache = cache.replaceAll(String.format("\\%s[^)]+\\%s", CatCl, "/>"), "");

                    if (cache.contains(field)) {
                        int Field1 = cache.indexOf("- " + field + ": ") + 4 + lenghtOfField;
                        int Field2 = cache.indexOf(";", Field1);
                        return cache.substring(Field1, Field2);
                    } else {
                        return "FIELD_NOT_FOUND_ERROR";
                    }
                } else {
                    return "DATA_FORMAT_ERROR";
                }
            } else {
                return "DATA_FORMAT_ERROR";
            }
        } else {
            return "BLOCKED_CHARACTERS_DETECTED";
        }
    }

    private static boolean checkBanSymbols(String category) {
        int index1 = category.indexOf(".");
        int index2 = category.indexOf("?");
        int index3 = category.indexOf("!");
        int index4 = category.indexOf('"');
        int index5 = category.indexOf("'");
        int index6 = category.indexOf(",");
        int index7 = category.indexOf("&");
        int index8 = category.indexOf("$");
        int index9 = category.indexOf(";");
        int index10 = category.indexOf(":");
        int index11 = category.indexOf("#");
        int index12 = category.indexOf("№");
        int index13 = category.indexOf("@");
        int index14 = category.indexOf("~");
        int index15 = category.indexOf("*");
        int index16 = category.indexOf("%");
        int index17 = category.indexOf(")");
        int index18 = category.indexOf("(");
        int index19 = category.indexOf("|");
        int index20 = category.indexOf("+");
        int index21 = category.indexOf("=");
        int index22 = category.indexOf("{");
        int index23 = category.indexOf("}");

        if ((index1 == -1) && (index2 == -1) && (index3 == -1) && (index4 == -1) && (index5 == -1) && (index6 == -1) && (index7 == -1) && (index8 == -1) && (index9 == -1) && (index10 == -1) && (index11 == -1) && (index12 == -1) && (index13 == -1) && (index14 == -1) && (index15 == -1) && (index16 == -1) && (index17 == -1) && (index18 == -1) && (index19 == -1) && (index20 == -1) && (index21 == -1) && (index22 == -1) && (index23 == -1)) {
            return true;
        } else {
            return false;
        }
    }
}
