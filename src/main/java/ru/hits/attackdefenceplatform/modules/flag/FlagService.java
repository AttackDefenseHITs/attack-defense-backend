package ru.hits.attackdefenceplatform.modules.flag;

import ru.hits.attackdefenceplatform.modules.user.repository.UserEntity;

public interface FlagService {
    void sendFlag(String flag, UserEntity user);
}
