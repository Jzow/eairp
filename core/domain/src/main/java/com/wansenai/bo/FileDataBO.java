package com.wansenai.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDataBO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String uid;

    private String fileName;

    private String fileUrl;

    private String fileType;

    private Long fileSize;
}