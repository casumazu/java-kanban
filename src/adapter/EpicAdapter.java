package adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import tasks.Epic;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class EpicAdapter implements JsonSerializer<Epic> {
    @Override
    public JsonElement serialize(Epic epic, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("id", epic.getId());
        result.addProperty("type", "EPIC");
        result.addProperty("name", epic.getTitle());
        result.addProperty("description", epic.getDescription());
        result.addProperty("status", epic.getStatus().name());
        result.addProperty("subTasksId", String.valueOf(epic.getSubtasks()));
        result.addProperty("duration", epic.getDuration());
        result.addProperty("startTime", epic.getStartTime().
                format(DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss")));
        return result;
    }
}
