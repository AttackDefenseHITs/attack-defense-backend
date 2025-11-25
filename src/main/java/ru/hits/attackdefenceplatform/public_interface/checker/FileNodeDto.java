package ru.hits.attackdefenceplatform.public_interface.checker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileNodeDto {
    private String name;
    private String path;
    private boolean directory;
    private List<FileNodeDto> children;
}
