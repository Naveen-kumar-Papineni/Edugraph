package api;

import java.util.*;

/** Minimal JSON writer/reader - no external dependencies. */
public class Json {

    // ---------- WRITING ----------

    public static String write(Object o) {
        StringBuilder sb = new StringBuilder();
        writeVal(o, sb);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void writeVal(Object o, StringBuilder sb) {
        if (o == null) { sb.append("null"); return; }
        if (o instanceof String) { writeStr((String) o, sb); return; }
        if (o instanceof Number || o instanceof Boolean) { sb.append(o.toString()); return; }
        if (o instanceof Map) {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                if (!first) sb.append(',');
                first = false;
                writeStr(String.valueOf(e.getKey()), sb);
                sb.append(':');
                writeVal(e.getValue(), sb);
            }
            sb.append('}');
            return;
        }
        if (o instanceof Iterable) {
            sb.append('[');
            boolean first = true;
            for (Object item : (Iterable<?>) o) {
                if (!first) sb.append(',');
                first = false;
                writeVal(item, sb);
            }
            sb.append(']');
            return;
        }
        if (o instanceof int[]) {
            sb.append('[');
            int[] arr = (int[]) o;
            for (int i = 0; i < arr.length; i++) { if (i > 0) sb.append(','); sb.append(arr[i]); }
            sb.append(']');
            return;
        }
        // fallback: toString as string
        writeStr(o.toString(), sb);
    }

    private static void writeStr(String s, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        sb.append('"');
    }

    public static LinkedHashMap<String, Object> obj(Object... kv) {
        LinkedHashMap<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put(String.valueOf(kv[i]), kv[i + 1]);
        return m;
    }

    // ---------- READING (very small subset: flat objects with string/number/bool values) ----------

    public static Map<String, String> parseFlatObject(String body) {
        Map<String, String> result = new LinkedHashMap<>();
        if (body == null) return result;
        body = body.trim();
        if (body.startsWith("{")) body = body.substring(1);
        if (body.endsWith("}")) body = body.substring(0, body.length() - 1);
        int i = 0, n = body.length();
        while (i < n) {
            while (i < n && (body.charAt(i) == ' ' || body.charAt(i) == ',' || body.charAt(i) == '\n')) i++;
            if (i >= n) break;
            // key (quoted)
            if (body.charAt(i) != '"') break;
            int keyStart = ++i;
            while (i < n && body.charAt(i) != '"') i++;
            String key = body.substring(keyStart, i);
            i++; // skip closing quote
            while (i < n && (body.charAt(i) == ' ' || body.charAt(i) == ':')) i++;
            String val;
            if (i < n && body.charAt(i) == '"') {
                int valStart = ++i;
                StringBuilder vsb = new StringBuilder();
                while (i < n && body.charAt(i) != '"') {
                    if (body.charAt(i) == '\\' && i + 1 < n) { vsb.append(body.charAt(i + 1)); i += 2; }
                    else { vsb.append(body.charAt(i)); i++; }
                }
                val = vsb.toString();
                i++; // closing quote
            } else {
                int valStart = i;
                while (i < n && body.charAt(i) != ',' && body.charAt(i) != '}') i++;
                val = body.substring(valStart, i).trim();
            }
            result.put(key, val);
        }
        return result;
    }

    /** Parses a JSON array of flat objects, e.g. [{"name":"A","start":"1","end":"2"}, ...] */
    public static List<Map<String, String>> parseArrayOfFlatObjects(String body) {
        List<Map<String, String>> out = new ArrayList<>();
        if (body == null) return out;
        body = body.trim();
        if (body.startsWith("[")) body = body.substring(1);
        if (body.endsWith("]")) body = body.substring(0, body.length() - 1);
        int depth = 0, start = -1;
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            if (c == '{') { if (depth == 0) start = i; depth++; }
            else if (c == '}') { depth--; if (depth == 0 && start >= 0) { out.add(parseFlatObject(body.substring(start, i + 1))); start = -1; } }
        }
        return out;
    }
}
