package ksh.backendserver.post.dto.response;

import ksh.backendserver.post.model.GroupShare;
import lombok.AllArgsConstructor;
import lombok.Getter;

//TODO: 직군 직무 어느 용어가 편한지 프론트랑 얘기하기
@Getter
@AllArgsConstructor
public class GroupShareResponseDto {

    private long groupId;
    private String groupName;
    private double share;

    public static GroupShareResponseDto from(GroupShare model) {
        return new GroupShareResponseDto(model.getGroupId(), model.getGroupName(), model.getShare());
    }
}