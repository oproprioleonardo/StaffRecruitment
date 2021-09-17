package com.leon.srecruit.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gamemode {

    private String name;
    private String label;
    private long roleId;
    private long categoryId;

}
