package ru.hits.attackdefenceplatform.public_interface.team;

import ru.hits.attackdefenceplatform.public_interface.user.UserTeamMemberDto;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.VirtualMachineDto;

import java.util.List;
import java.util.UUID;

public record TeamInfoDto(
        UUID id,
        String name,
        Long userCount,
        Long membersCount,
        Integer place,
        Integer points,
        Boolean canJoin,
        Boolean isMyTeam,
        Boolean canLeave,
        List<UserTeamMemberDto> memberList,
        VirtualMachineDto virtualMachine
) {
}

