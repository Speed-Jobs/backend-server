package ksh.backendserver.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonParserUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonParserUtil() {
        throw new UnsupportedOperationException("JsonParseUtil 클래스를 인스턴스화할 수 없습니다");
    }

    public static Map<String, Object> parseAndFilterKoreanKeys(String jsonString) {
        if (jsonString == null || jsonString.isBlank()) {
            return Collections.emptyMap();
        }

        try {
            Map<String, Object> fullMetaData = OBJECT_MAPPER.readValue(
                jsonString,
                new TypeReference<Map<String, Object>>() {
                }
            );

            return fullMetaData.entrySet().stream()
                .filter(entry -> containsKorean(entry.getKey()) && entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private static boolean containsKorean(String text) {
        if (text == null) {
            return false;
        }
        return text.chars().anyMatch(ch ->
            (ch >= 0xAC00 && ch <= 0xD7A3) ||
                (ch >= 0x1100 && ch <= 0x11FF) ||
                (ch >= 0x3130 && ch <= 0x318F)
        );
    }
}
