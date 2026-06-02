package ru.hits.attackdefenceplatform.core.repo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RepoFileDto {
    private String path;
    private String name;
    private String type;
    private long size;
    private String sha;
}
