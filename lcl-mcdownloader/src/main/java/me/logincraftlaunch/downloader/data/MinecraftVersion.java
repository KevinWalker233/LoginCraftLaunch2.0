package me.logincraftlaunch.downloader.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Data
@JsonSerialize
public class MinecraftVersion {
    private String id;
    private String type;
    private String releaseTime;
    private String url;
    private String time;
}
