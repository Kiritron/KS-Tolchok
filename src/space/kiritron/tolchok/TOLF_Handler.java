package space.kiritron.tolchok;

/**
 * Класс с методами для управления конфигами стандарта КС Толчок, далее TOLF.
 * @author Киритрон Стэйблкор
 * @version 2.2
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
            if (checkBannedSymbols(category)) {
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

        if(checkBannedSymbols(category)) {
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

        if(checkBannedSymbols(category)) {
            if (data.contains("[" + category + "]") == true) {
                if (data.contains("[/" + category + "]") == true) {
                    if (data.contains("- " + field + ":")) {
                        CatOp = "[" + category + "]";
                        CatCl = "[/" + category + "]";
                        CacheOne = data.format("\\%s[^)]+\\%s", "</", CatOp);
                        CacheTwo = data.format("\\%s[^)]+\\%s", CatCl, "/>");

                        CacheOne = CacheOne.substring(9);
                        CacheTwo = CacheTwo.substring(1, CacheTwo.length() - (CacheTwo.length() - 1 - CatCl.length()));

                        data = data.replaceAll(String.format("\\%s[^)]+\\%s", "</", CatOp), "");
                        data = data.replaceAll(String.format("\\%s[^)]+\\%s", CatCl, "/>"), "");
                        final int Field = data.indexOf("- " + field + ": ") + 4 + lenghtOfField;
                        String ParamOfFIELD = data.substring(Field, data.indexOf(";", Field));

                        data = data.replaceAll("- " + field + ": " + ParamOfFIELD + ";", "- " + field + ": " + newParam + ";");
                        final String DataFromFilter = data.substring(0, data.length() - 2); // Костыль

                        return "</" + "\n"  + "\t" + CacheOne + DataFromFilter + "\t" + CacheTwo + "\n"  + "/>";
                    } else {
                        return "SEARCH_FIELD_FAILED";
                    }
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

        if(checkBannedSymbols(category)) {
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

    private static boolean checkBannedSymbols(String category) {
        int index = category.indexOf(".") + category.indexOf("?") + category.indexOf("!") + category.indexOf('"') + category.indexOf("'")
                + category.indexOf(",") + category.indexOf("&") + category.indexOf("$") + category.indexOf(";") + category.indexOf(":")
                + category.indexOf("#") + category.indexOf("№") + category.indexOf("@") + category.indexOf("~") + category.indexOf("*")
                + category.indexOf("%") + category.indexOf(")") + category.indexOf("(") + category.indexOf("|") + category.indexOf("+")
                + category.indexOf("=") + category.indexOf("{") + category.indexOf("}");
        if (index == -23) {
            return true;
        } else {
            return false;
        }
    }
}
