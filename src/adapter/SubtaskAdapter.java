package adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import tasks.Subtask;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class SubtaskAdapter implements JsonSerializer<Subtask> {


    @Override
    public JsonElement serialize(Subtask subtask, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("id", subtask.getId());
        result.addProperty("type", "SUB_TASK");
        result.addProperty("name", subtask.getDescription());
        result.addProperty("description", subtask.getDescription());
        result.addProperty("status", subtask.getStatus().name());
        result.addProperty("duration", subtask.getDuration());
        result.addProperty("startTime", subtask.getStartTime().
                format(DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss")));
        result.addProperty("epicId", subtask.getEpicId());
        return result;
    }
}
