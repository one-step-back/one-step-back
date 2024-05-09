package com.app.onestepback.domain.entity.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity @Table(name = "TBL_POST_FILE")
@SequenceGenerator(name = "SEQ_POST_FILE_GENERATOR", sequenceName = "SEQ_POST_FILE", allocationSize = 1)
@Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFile {
    @Id @GeneratedValue(generator = "SEQ_POST_FILE_GENERATOR")
    private Long id;

    @Column(nullable = false, name = "FILE_NAME")
    private String filaName;
    @Column(nullable = false, name = "FILE_PATH")
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "POST_ID")
    private Post post;

}
