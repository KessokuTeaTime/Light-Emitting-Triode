package band.kessokuteatime.lightemittingtriode.util.old;

import com.google.gson.JsonObject;

public class KeyBuilder {

    private final JsonObject json = new JsonObject();

    public KeyBuilder addItem(char key, String item) {
        JsonObject entry = new JsonObject();
        entry.addProperty("item", item);
        json.add(key + "", entry);
        return this;
    }

    public JsonObject get() {
        return json;
    }

}
