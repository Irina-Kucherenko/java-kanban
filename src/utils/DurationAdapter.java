package utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {


    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        jsonWriter.value(String.valueOf(duration.toMinutes()));
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        String next = jsonReader.nextString();
        return next == null ? null : Duration.ofMinutes(Long.parseLong(next));
    }
}

