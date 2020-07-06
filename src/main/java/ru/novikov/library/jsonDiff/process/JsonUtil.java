package ru.novikov.library.jsonDiff.process;

import com.google.gson.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author novikov_vi on 06.07.2020
 */
final class JsonUtil {
    /**
     * Строка хранящая пустой JSON
     **/
    private static final String EMPTY_JSON = "{}";

    /**
     * Преобразование строки в json объект.
     * В случае если строка является null строка преобразуется в пустой json строчного типа
     *
     * @param str - строка типа json
     * @return - json объект
     */
    static JsonObject stringToJson(String str) {
        str = str == null ? EMPTY_JSON : str;
        return JsonParser.parseString(str).getAsJsonObject();
    }

    /**
     * Получение элемента по ключу
     *
     * @return json типа элемент
     */
    static JsonElement getJsonElementByKey(JsonObject jsonObject, String key) {
        if (jsonObject != null) {
            return jsonObject.get(key);
        }
        return null;
    }

    /**
     * Получение массива json из элемента
     *
     * @return json типа массив или null
     */
    static JsonArray getJsonArray(JsonElement jsonElement) {
        JsonArray jsonArray = null;
        if (jsonElement != null) {
            jsonArray = jsonElement.getAsJsonArray();
        }
        return jsonArray;
    }

    /**
     * Получение объекта json из элемента
     *
     * @return json типа объект или null
     */
    static JsonObject getJsonObject(JsonElement jsonElement) {
        JsonObject jsonObject = null;
        if (jsonElement != null) {
            jsonObject = jsonElement.getAsJsonObject();
        }
        return jsonObject;
    }

    /**
     * Получение массива объектов
     *
     * @param jsonArray - json типа массив
     * @return - список json типа объект
     */
    static List<JsonObject> getJsonObjectList(JsonArray jsonArray) {
        return StreamSupport
                .stream(jsonArray.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .collect(Collectors.toList());
    }

    /**
     * Получение значения json примитива
     *
     * @return - значение
     */
    static Object getJsonPrimitive(JsonPrimitive primitive) {
        if (primitive == null) {
            return null;

        } else if (primitive.isBoolean()) {
            return primitive.getAsBoolean();

        } else if (primitive.isString()) {
            return primitive.getAsString();

        } else if (primitive.isNumber()) {
            return primitive.getAsNumber();

        } else return null;
    }

    /**
     * Получение значения элемента json
     *
     * @param element - нетипизированный элемент
     * @return значение элемента, в случае ненахождения null
     */
    static Object getValueElement(JsonElement element) {
        if (element.isJsonPrimitive()) {
            return getJsonPrimitive(element.getAsJsonPrimitive());
        } else if (element.isJsonObject()) {
            return element.getAsJsonObject();
        }
        return null;
    }
}
